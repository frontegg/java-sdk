package com.frontegg.sdk.middleware.spring.core.util.matcher;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * An interface for extracting URI variables from the {@link HttpServletRequest}.
 *
 * @author Rob Winch
 * @since 4.1.1
 * @deprecated use {@link RequestMatcher.MatchResult} from {@link RequestMatcher#matcher(HttpServletRequest)}
 */
public interface RequestVariablesExtractor {

    /**
     * Extract URL template variables from the request.
     *
     * @param request the HttpServletRequest to obtain a URL to extract the variables from
     * @return the URL variables or empty if no variables are found
     */
    Map<String, String> extractUriTemplateVariables(HttpServletRequest request);
}
