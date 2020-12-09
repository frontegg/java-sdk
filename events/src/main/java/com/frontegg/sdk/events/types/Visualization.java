package com.frontegg.sdk.events.types;

public enum Visualization {
    BUTTON("Button"),
    LINK("Link");

    private final String name;

    Visualization(String name) {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }
}
