package com.frontegg.sdk.events.types;

public class BellAction {

    /**
     *  Display name of the action.
     */
    private String name;

    /**
     *  Url that the request will be sent to when clicking on the action.
     */
    private String url;

    /**
     *  Request method.
     */
    private String method;

    /**
     *  Determent how to render the action.
     */
    private Visualization Visualization;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public com.frontegg.sdk.events.types.Visualization getVisualization() {
        return Visualization;
    }

    public void setVisualization(com.frontegg.sdk.events.types.Visualization visualization) {
        Visualization = visualization;
    }
}
