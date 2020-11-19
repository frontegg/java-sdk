package com.frontegg.sdk.middleware.spring.context;

import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.spring.context.FronteggContextHolder;
import com.frontegg.sdk.middleware.spring.context.util.OnCommittedResponseWrapper;

import javax.servlet.http.HttpServletResponse;

public abstract class SaveContextOnUpdateOrErrorResponseWrapper extends OnCommittedResponseWrapper {

    private boolean contextSaved = false;
    private final boolean disableUrlRewriting;

    public SaveContextOnUpdateOrErrorResponseWrapper(HttpServletResponse response,
                                                     boolean disableUrlRewriting) {
        super(response);
        this.disableUrlRewriting = disableUrlRewriting;
    }

    public void disableSaveOnResponseCommitted() {
        disableOnResponseCommitted();
    }

    protected abstract void saveContext(FronteggContext context);

    @Override
    protected void onResponseCommitted() {
        saveContext(FronteggContextHolder.getContext());
        this.contextSaved = true;
    }

    @Override
    public final String encodeRedirectUrl(String url) {
        if (this.disableUrlRewriting) {
            return url;
        }
        return super.encodeRedirectUrl(url);
    }

    @Override
    public final String encodeRedirectURL(String url) {
        if (this.disableUrlRewriting) {
            return url;
        }
        return super.encodeRedirectURL(url);
    }

    @Override
    public final String encodeUrl(String url) {
        if (this.disableUrlRewriting) {
            return url;
        }
        return super.encodeUrl(url);
    }

    @Override
    public final String encodeURL(String url) {
        if (this.disableUrlRewriting) {
            return url;
        }
        return super.encodeURL(url);
    }

    public final boolean isContextSaved() {
        return this.contextSaved;
    }
}
