package com.frontegg.sdk.middleware.spring.context;

import com.frontegg.sdk.middleware.authenticator.Authentication;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.FronteggContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HttpSessionFronteggContextRepository implements FronteggContextRepository {

    private static Logger logger = LoggerFactory.getLogger(HttpSessionFronteggContextRepository.class);
    private static final String FRONTEGG_CONTEXT_KEY = "FRONTEGG_CONTEXT";

    private final Object contextObject = FronteggContextHolder.createEmptyContext();
    private boolean allowSessionCreation = true;
    private boolean disableUrlRewriting = false;

    @Override
    public FronteggContext loadContext(FronteggHttpRequestResponseHolder holder) {
        HttpServletRequest request = holder.getRequest();
        HttpServletResponse response = holder.getResponse();
        HttpSession httpSession = request.getSession(false);

        FronteggContext context = readFronteggContextFromSession(httpSession);

        if (context == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("No FronteggContext was available from the HttpSession: "
                        + httpSession + ". " + "A new one will be created.");
            }
            context = generateNewContext();

        }

        SaveToSessionResponseWrapper wrappedResponse = new SaveToSessionResponseWrapper(response, request, httpSession != null, context);
        holder.setResponse(wrappedResponse);
        holder.setRequest(new SaveToSessionRequestWrapper(request, wrappedResponse));

        return context;
    }



    @Override
    public void saveContext(FronteggContext context, HttpServletRequest request, HttpServletResponse response) {
        SaveContextOnUpdateOrErrorResponseWrapper responseWrapper = WebUtils.getNativeResponse(response, SaveContextOnUpdateOrErrorResponseWrapper.class);
        if (responseWrapper == null) {
            throw new IllegalStateException(
                    "Cannot invoke saveContext on response "
                            + response
                            + ". You must use the HttpRequestResponseHolder.response after invoking loadContext");
        }
        // saveContext() might already be called by the response wrapper
        // if something in the chain called sendError() or sendRedirect(). This ensures we
        // only call it
        // once per request.
        if (!responseWrapper.isContextSaved()) {
            responseWrapper.saveContext(context);
        }
    }

    private FronteggContext readFronteggContextFromSession(HttpSession httpSession) {
        final boolean debug = logger.isDebugEnabled();

        if (httpSession == null) {
            if (debug) {
                logger.debug("No HttpSession currently exists");
            }

            return null;
        }

        // Session exists, so try to obtain a context from it.

        Object contextFromSession = httpSession.getAttribute(FRONTEGG_CONTEXT_KEY);

        if (contextFromSession == null) {
            if (debug) {
                logger.debug("HttpSession returned null object for SPRING_SECURITY_CONTEXT");
            }

            return null;
        }

        // We now have the frontegg context object from the session.
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

        if (debug) {
            logger.debug("Obtained a valid FronteggContext from " + FRONTEGG_CONTEXT_KEY + ": '" + contextFromSession + "'");
        }

        // Everything OK. The only non-null return from this method.
        return (FronteggContext) contextFromSession;
    }

    private FronteggContext generateNewContext() {
        return FronteggContextHolder.createEmptyContext();
    }

    final class SaveToSessionResponseWrapper extends SaveContextOnUpdateOrErrorResponseWrapper {
        private final HttpServletRequest request;
        private final boolean httpSessionExistedAtStartOfRequest;
        private final FronteggContext contextBeforeExecution;
        private final Authentication authBeforeExecution;

        /**
         * Takes the parameters required to call <code>saveContext()</code> successfully
         * in addition to the request and the response object we are wrapping.
         *
         * @param request the request object (used to obtain the session, if one exists).
         * @param httpSessionExistedAtStartOfRequest indicates whether there was a session
         * in place before the filter chain executed. If this is true, and the session is
         * found to be null, this indicates that it was invalidated during the request and
         * a new session will now be created.
         * @param context the context before the filter chain executed. The context will
         * only be stored if it or its contents changed during the request.
         */
        SaveToSessionResponseWrapper(HttpServletResponse response,
                                     HttpServletRequest request, boolean httpSessionExistedAtStartOfRequest,
                                     FronteggContext context) {
            super(response, disableUrlRewriting);
            this.request = request;
            this.httpSessionExistedAtStartOfRequest = httpSessionExistedAtStartOfRequest;
            this.contextBeforeExecution = context;
            this.authBeforeExecution = context.getAuthentication();
        }

        @Override
        protected void saveContext(FronteggContext context) {
            final Authentication authentication = context.getAuthentication();
            HttpSession httpSession = request.getSession(false);

            if (authentication == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("FronteggContext is empty or contents are anonymous - context will not be stored in HttpSession.");
                }

                if (httpSession != null && authBeforeExecution != null) {
                    httpSession.removeAttribute(FRONTEGG_CONTEXT_KEY);
                }
                return;
            }

            if (httpSession == null) {
                httpSession = createNewSessionIfAllowed(context);
            }

            if (httpSession != null) {
                if (contextChanged(context)
                        || httpSession.getAttribute(FRONTEGG_CONTEXT_KEY) == null) {
                    httpSession.setAttribute(FRONTEGG_CONTEXT_KEY, context);

                    if (logger.isDebugEnabled()) {
                        logger.debug("FronteggContext '" + context + "' stored to HttpSession: '" + httpSession);
                    }
                }
            }
        }

        private boolean contextChanged(FronteggContext context) {
            return context != contextBeforeExecution || context.getAuthentication() != authBeforeExecution;
        }

        private HttpSession createNewSessionIfAllowed(FronteggContext context) {

            if (httpSessionExistedAtStartOfRequest) {
                if (logger.isDebugEnabled()) {
                    logger.debug("HttpSession is now null, but was not null at start of request; "
                            + "session was invalidated, so do not create a new session");
                }

                return null;
            }

            if (!allowSessionCreation) {
                if (logger.isDebugEnabled()) {
                    logger.debug("The HttpSession is currently null, and the "
                            + HttpSessionFronteggContextRepository.class.getSimpleName()
                            + " is prohibited from creating an HttpSession "
                            + "(because the allowSessionCreation property is false) - FronteggContext thus not "
                            + "stored for next request");
                }

                return null;
            }
            // Generate a HttpSession only if we need to

            if (contextObject.equals(context)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("HttpSession is null, but FronteggContext has not changed from default empty context: ' "
                            + context
                            + "'; not creating HttpSession or storing FronteggContext");
                }

                return null;
            }

            if (logger.isDebugEnabled()) {
                logger.debug("HttpSession being created as FronteggContext is non-default");
            }

            try {
                return request.getSession(true);
            }
            catch (IllegalStateException e) {
                logger.warn("Failed to create a session, as response has been committed. Unable to store FronteggContext.");
            }

            return null;
        }
    }

    private static class SaveToSessionRequestWrapper extends HttpServletRequestWrapper {
        private final SaveContextOnUpdateOrErrorResponseWrapper response;

        SaveToSessionRequestWrapper(HttpServletRequest request, SaveContextOnUpdateOrErrorResponseWrapper response) {
            super(request);
            this.response = response;
        }

        @Override
        public AsyncContext startAsync() {
            response.disableSaveOnResponseCommitted();
            return super.startAsync();
        }

        @Override
        public AsyncContext startAsync(ServletRequest servletRequest,
                                       ServletResponse servletResponse) throws IllegalStateException {
            response.disableSaveOnResponseCommitted();
            return super.startAsync(servletRequest, servletResponse);
        }
    }
}
