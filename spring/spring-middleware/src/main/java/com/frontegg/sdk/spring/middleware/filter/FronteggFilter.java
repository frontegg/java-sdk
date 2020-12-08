package com.frontegg.sdk.spring.middleware.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.common.exception.InsufficientAccessException;
import com.frontegg.sdk.common.exception.InvalidParameterException;
import com.frontegg.sdk.common.model.FronteggHttpHeader;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.HttpHelper;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.authenticator.AuthenticationException;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.FronteggContextHolder;
import com.frontegg.sdk.middleware.context.FronteggContextResolver;
import com.frontegg.sdk.middleware.routes.IFronteggRouteService;
import com.frontegg.sdk.spring.middleware.FronteggServiceDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static com.frontegg.sdk.common.util.HttpHelper.*;

public class FronteggFilter extends GenericFilterBean {

    private static final String FRONTEGG_CONTEXT_KEY = "FRONTEGG_CONTEXT";
    static final String FILTER_APPLIED = "__frontegg_feggf_applied";
    private static final Logger logger = LoggerFactory.getLogger(FronteggFilter.class);
    private FronteggContextResolver fronteggContextResolver;
    private FronteggAuthenticator fronteggAuthenticator;
    private IFronteggRouteService fronteggRouteService;
    private FronteggServiceDelegate fronteggServiceDelegate;
    private FronteggOptions options;
    private ObjectMapper objectMapper = new ObjectMapper();
    private String basePath;

    public FronteggFilter(String basePath,
                          FronteggAuthenticator fronteggAuthenticator,
                          FronteggContextResolver fronteggContextResolver,
                          IFronteggRouteService fronteggRouteService,
                          FronteggServiceDelegate fronteggServiceDelegate,
                          FronteggOptions options) {
        validateOptions(options);
        this.basePath = basePath;
        this.fronteggAuthenticator = fronteggAuthenticator;
        this.fronteggContextResolver = fronteggContextResolver;
        this.fronteggRouteService = fronteggRouteService;
        this.fronteggServiceDelegate = fronteggServiceDelegate;
        this.options = options;
    }

    private void validateOptions(FronteggOptions options) {
        if (StringHelper.isBlank(options.getClientId())) {
            throw new InvalidParameterException("Missing client ID");
        }
        if (StringHelper.isBlank(options.getApiKey())) {
            throw new InvalidParameterException("Missing api key");
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        AntPathMatcher matcher = new AntPathMatcher();
        boolean isRequestMatches = matcher.match(getBasePath() + "/**", request.getRequestURI());

        if (isRequestMatches) {

            if (request.getAttribute(FILTER_APPLIED) != null) {
                // ensure that filter is only applied once per request
                filterChain.doFilter(request, response);
                return;
            }

            request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

            HttpSession httpSession = request.getSession(false);

            FronteggContext context = loadContextFromSession(request, httpSession);
            if (context == null) context = FronteggContextHolder.createEmptyContext();

            context.setFronteggBasePath(basePath);
            FronteggContextHolder.setContext(context);

            try {
                fronteggAuthenticator.authenticate();

                if (request.getMethod().equals("OPTIONS")) {
                    response.setStatus(204);
                    return;
                }

                if (!isFronteggPublicRoute(request)) {
                    logger.debug("will pass request threw the auth middleware");
                    fronteggContextResolver.resolveContext(request);
                }


                FronteggHttpResponse<Object> fronteggHttpResponse = fronteggServiceDelegate.doProcess(request, response);
                populateHeaders(fronteggHttpResponse.getHeaders(), response);

                manageCorsHeaders(response, fronteggHttpResponse);

                response.setStatus(fronteggHttpResponse.getStatusCode());

                Object object = fronteggHttpResponse.getBody();
                if (object != null) {
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(object));
                }

                return;

            } catch (Exception ex) {
                resolverException(ex, response);
                return;
            } finally {
                FronteggContext contextAfterChainExecution = FronteggContextHolder.getContext();
                FronteggContextHolder.clearContext();
                saveContextToSession(httpSession, contextAfterChainExecution);
                request.removeAttribute(FILTER_APPLIED);
                response.getWriter().flush();
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void saveContextToSession(HttpSession httpSession, FronteggContext context) {
        if (httpSession != null) {
            httpSession.setAttribute(FRONTEGG_CONTEXT_KEY, context);
        }
    }

    private HttpSession createNewSessionIfAllowed(HttpServletRequest request) {
        try {
            logger.info("HttpSession being created as FronteggContext is non-default");
            return request.getSession(true);
        } catch (IllegalStateException e) {
            logger.warn("Failed to create a session, as response has been committed. Unable to store FronteggContext.");
        }

        return null;
    }

    private FronteggContext loadContextFromSession(HttpServletRequest request, HttpSession httpSession) {
        if (httpSession == null) {
            httpSession = createNewSessionIfAllowed(request);
        }

        Object contextFromSession = httpSession.getAttribute(FRONTEGG_CONTEXT_KEY);

        if (!(contextFromSession instanceof FronteggContext)) {
            if (logger.isWarnEnabled()) {
                logger.warn(FRONTEGG_CONTEXT_KEY
                        + " did not contain a FronteggContext but contained: '"
                        + contextFromSession
                        + "'; are you improperly modifying the HttpSession directly "
                        + "(you should always use FronteggContextHolder) or using the HttpSession attribute "
                        + "reserved for this class?");
            }

            return null;
        }

        return (FronteggContext) contextFromSession;
    }

    private void manageCorsHeaders(HttpServletResponse response, FronteggHttpResponse<Object> fronteggHttpResponse) {
        if (options.isDisableCors()) {
            HttpHelper.deleteHeaders(response,
                    ACCESS_CONTROL_REQUEST_METHOD,
                    ACCESS_CONTROL_REQUEST_HEADERS,
                    ACCESS_CONTROL_ALLOW_ORIGIN,
                    ACCESS_CONTROL_ALLOW_CREDENTIALS
            );
        } else {
            enableCors(fronteggHttpResponse, response);
        }
    }

    private void resolverException(Exception ex, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        if (ex instanceof AuthenticationException) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            printWriter.write(objectMapper.writeValueAsString(new Error("Unauthorized")));
        } if (ex instanceof InsufficientAccessException) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            printWriter.write(objectMapper.writeValueAsString(new Error("Permission Denied")));
        } if (ex instanceof InvalidParameterException) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            printWriter.write(objectMapper.writeValueAsString(new Error(ex.getMessage())));
        } if (ex instanceof FronteggSDKException) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            printWriter.write(objectMapper.writeValueAsString(new Error("Something went wrong, please try again.")));
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            printWriter.write(objectMapper.writeValueAsString(new Error(ex.getMessage())));
        }

        printWriter.flush();
    }

    void enableCors(FronteggHttpResponse<Object> fronteggHttpResponse, HttpServletResponse response) {
        HttpHelper.replaceHeader(fronteggHttpResponse.getHeaders(), response, ACCESS_CONTROL_REQUEST_METHOD);
        HttpHelper.replaceHeader(fronteggHttpResponse.getHeaders(), response, ACCESS_CONTROL_REQUEST_HEADERS);
        HttpHelper.replaceHeader(fronteggHttpResponse.getHeaders(), response, ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    class Error {
        private String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    private boolean isFronteggPublicRoute(HttpServletRequest request) {
        return fronteggRouteService.isFronteggPublicRoute(request);
    }

    public void populateHeaders(List<FronteggHttpHeader> headers, HttpServletResponse response) {
        for (FronteggHttpHeader header : headers) {
            response.setHeader(header.getName(), header.getValue());
        }
    }

    public String getBasePath() {
        return basePath;
    }
}
