package com.frontegg.sdk.middleware.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;

public class FronteggListenerSupport extends RetryListenerSupport {
    private static Logger logger = LoggerFactory.getLogger(FronteggListenerSupport.class);

    @Override
    public <T, E extends Throwable> void close(RetryContext context,
                                               RetryCallback<T, E> callback, Throwable throwable) {
        logger.info("onClose");
        super.close(context, callback, throwable);
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context,
                                                 RetryCallback<T, E> callback, Throwable throwable) {

        //logger.error("Failed proxy request to - " + url);
        super.onError(context, callback, throwable);
    }

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context,
                                                 RetryCallback<T, E> callback) {


        //logger.info("retry count of " + url + " = " + context.getRetryCount());
        return super.open(context, callback);
    }
}
