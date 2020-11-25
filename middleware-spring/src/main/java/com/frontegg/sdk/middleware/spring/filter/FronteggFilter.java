package com.frontegg.sdk.middleware.spring.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.common.exception.InefficientAccessException;
import com.frontegg.sdk.common.exception.InvalidParameterException;
import com.frontegg.sdk.common.model.FronteggHttpHeader;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.HttpUtil;
import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.IFronteggServiceDelegate;
import com.frontegg.sdk.middleware.authentication.IFronteggAuthenticationService;
import com.frontegg.sdk.middleware.authenticator.AuthenticationException;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.FronteggContextHolder;
import com.frontegg.sdk.middleware.routes.IFronteggRouteService;
import com.frontegg.sdk.middleware.spring.core.context.FronteggContextRepository;
import com.frontegg.sdk.middleware.spring.core.context.FronteggHttpRequestResponseHolder;
import com.frontegg.sdk.middleware.spring.core.context.HttpSessionFronteggContextRepository;
import org.springframework.http.HttpStatus;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static com.frontegg.sdk.common.util.HttpUtil.*;

public class FronteggFilter extends FronteggBaseFilter {

    static final String FILTER_APPLIED = "__frontegg_feggf_applied";

    private FronteggContextRepository repo;
    private IFronteggAuthenticationService authenticationService;
    private IFronteggRouteService fronteggRouteService;
    private IFronteggServiceDelegate fronteggServiceDelegate;
    private FronteggOptions options;
    private ObjectMapper objectMapper = new ObjectMapper();

    public FronteggFilter(IFronteggAuthenticationService authenticationService,
                          IFronteggRouteService fronteggRouteService,
                          IFronteggServiceDelegate fronteggServiceDelegate,
                          FronteggOptions options) {
        this(new HttpSessionFronteggContextRepository(), authenticationService, fronteggRouteService, fronteggServiceDelegate, options);
    }

    public FronteggFilter(FronteggContextRepository repo,
                          IFronteggAuthenticationService authenticationService,
                          IFronteggRouteService fronteggRouteService,
                          IFronteggServiceDelegate fronteggServiceDelegate,
                          FronteggOptions options) {
        this.repo = repo;
        this.authenticationService = authenticationService;
        this.fronteggRouteService = fronteggRouteService;
        this.fronteggServiceDelegate = fronteggServiceDelegate;
        this.options = options;

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (requestMatcher.matches(request)) {


            if (request.getAttribute(FILTER_APPLIED) != null) {
                // ensure that filter is only applied once per request
                filterChain.doFilter(request, response);
                return;
            }

            request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

            FronteggHttpRequestResponseHolder holder = new FronteggHttpRequestResponseHolder(request, response);
            FronteggContext contextBeforeChainExecution = repo.loadContext(holder);

            try {
                contextBeforeChainExecution.setFronteggBasePath(basePath);
                FronteggContextHolder.setContext(contextBeforeChainExecution);

                authenticationService.authenticateApp();

                if (request.getMethod().equals("OPTIONS")) {
                    response.setStatus(204);
                    return;
                }

                if (!isFronteggPublicRoute(request)) {
                    logger.debug("will pass request threw the auth middleware");
                    authenticationService.withAuthentication(request);
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

                response.getWriter().flush();
                return;

            } catch (Exception ex) {
                resolverException(ex, response);
                return;
            } finally {
                FronteggContext contextAfterChainExecution = FronteggContextHolder.getContext();
                FronteggContextHolder.clearContext();
                repo.saveContext(contextAfterChainExecution, holder.getRequest(), holder.getResponse());
                request.removeAttribute(FILTER_APPLIED);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void manageCorsHeaders(HttpServletResponse response, FronteggHttpResponse<Object> fronteggHttpResponse) {
        if (options.isDisableCors()) {
            HttpUtil.deleteHeaders(response,
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
        } if (ex instanceof InefficientAccessException) {
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
        HttpUtil.replaceHeader(fronteggHttpResponse.getHeaders(), response, ACCESS_CONTROL_REQUEST_METHOD);
        HttpUtil.replaceHeader(fronteggHttpResponse.getHeaders(), response, ACCESS_CONTROL_REQUEST_HEADERS);
        HttpUtil.replaceHeader(fronteggHttpResponse.getHeaders(), response, ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN);
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

}
