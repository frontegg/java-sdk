package com.frontegg.sdk.middleware.spring;

import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.middleware.IFronteggService;
import com.frontegg.sdk.middleware.IFronteggServiceDelegate;
import com.frontegg.sdk.middleware.authenticator.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class FronteggServiceDelegate implements IFronteggServiceDelegate {
    private static final Logger logger = LoggerFactory.getLogger(FronteggServiceDelegate.class);

    @Autowired
    private IFronteggService fronteggService;
    @Autowired
    private RetryTemplate retryTemplate;

    @Override
    public FronteggHttpResponse<Object> doProcess(HttpServletRequest request, HttpServletResponse response) {
        return retryTemplate.execute(
                context -> {
                    if (context.getLastThrowable() instanceof AuthenticationException) {
                        fronteggService.authorizeApplication();
                    }
                    return fronteggService.doProcess(request, response);
                });
    }
}
