package com.frontegg.sdk.common.model;

public class FronteggHttpHeader implements NameValuePair {

    private String name;
    private String value;

    public FronteggHttpHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "FronteggHttpHeader{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
