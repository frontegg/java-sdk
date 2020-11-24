package com.frontegg.sdk.middleware.spring.core.util.matcher;


import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * {@link RequestMatcher} that will return true if any of the passed in
 * {@link RequestMatcher} instances match.
 *
 * @author Rob Winch
 * @since 3.2
 */
public final class OrRequestMatcher implements RequestMatcher {
    private final Log logger = LogFactory.getLog(getClass());
    private final List<RequestMatcher> requestMatchers;

    /**
     * Creates a new instance
     *
     * @param requestMatchers the {@link RequestMatcher} instances to try
     */
    public OrRequestMatcher(List<RequestMatcher> requestMatchers) {
        Assert.notEmpty(requestMatchers, "requestMatchers must contain a value");
        if (requestMatchers.contains(null)) {
            throw new IllegalArgumentException(
                    "requestMatchers cannot contain null values");
        }
        this.requestMatchers = requestMatchers;
    }

    /**
     * Creates a new instance
     *
     * @param requestMatchers the {@link RequestMatcher} instances to try
     */
    public OrRequestMatcher(RequestMatcher... requestMatchers) {
        this(Arrays.asList(requestMatchers));
    }

    public boolean matches(HttpServletRequest request) {
        for (RequestMatcher matcher : requestMatchers) {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to match using " + matcher);
            }
            if (matcher.matches(request)) {
                logger.debug("matched");
                return true;
            }
        }
        logger.debug("No matches found");
        return false;
    }

    @Override
    public String toString() {
        return "OrRequestMatcher [requestMatchers=" + requestMatchers + "]";
    }
}