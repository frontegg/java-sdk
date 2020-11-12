package com.frontegg.ws.sample;

import com.frontegg.sdk.middleware.context.IFronteggContextResolver;
import com.frontegg.sdk.middleware.spring.context.ContextHolder;
import com.frontegg.sdk.middleware.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RequestContextHolderHandlerFilter extends OncePerRequestFilter {

    @Autowired
    private IFronteggContextResolver fronteggContextResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            RequestContext context = fronteggContextResolver.resolveContext(httpServletRequest);
            ContextHolder.setRequestContext(context);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            ContextHolder.removeRequestContext();
        }

    }
}
