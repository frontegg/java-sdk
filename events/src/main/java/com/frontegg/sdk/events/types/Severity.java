package com.frontegg.sdk.events.types;

public enum Severity {

    Info,
    Medium,
    High,
    Critical;

    public static Severity getDefault() {
        return Severity.Info;
    }
}
