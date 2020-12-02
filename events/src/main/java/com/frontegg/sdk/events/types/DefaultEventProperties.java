package com.frontegg.sdk.events.types;

public class DefaultEventProperties implements EventProperties {

    private String title;
    private String description;

    public DefaultEventProperties(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
