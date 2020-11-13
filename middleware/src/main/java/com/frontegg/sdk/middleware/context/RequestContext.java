package com.frontegg.sdk.middleware.context;

public class RequestContext {

    private String requestPath;
    private byte[] body;
    private Object contextUser;
    private FronteggContext fronteggContext;

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Object getContextUser() {
        return contextUser;
    }

    public void setContextUser(Object contextUser) {
        this.contextUser = contextUser;
    }

    public FronteggContext getFronteggContext() {
        return fronteggContext;
    }

    public void setFronteggContext(FronteggContext fronteggContext) {
        this.fronteggContext = fronteggContext;
    }
}
