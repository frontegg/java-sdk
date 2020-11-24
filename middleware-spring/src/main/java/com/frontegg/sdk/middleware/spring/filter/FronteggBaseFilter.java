package com.frontegg.sdk.middleware.spring.filter;

import com.frontegg.sdk.middleware.spring.core.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

public abstract class FronteggBaseFilter extends GenericFilterBean {

    protected String basePath;

    protected RequestMatcher requestMatcher;

    public RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    public void setRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}
