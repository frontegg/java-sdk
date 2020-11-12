package com.frontegg.ws.sample;

import com.frontegg.sdk.middleware.AuthMiddleware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyCustomeMiddleware implements AuthMiddleware {

    private static Logger logger = LoggerFactory.getLogger(MyCustomeMiddleware.class);

    @Override
    public void callMiddleware(HttpServletRequest request, HttpServletResponse response) {
        logger.info("-- Custom middleware invoked");
    }
}
