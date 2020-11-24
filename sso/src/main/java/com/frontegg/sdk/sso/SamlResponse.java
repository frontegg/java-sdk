package com.frontegg.sdk.sso;

public class SamlResponse {

    private String relayState;
    private String sAMLResponse;

    public String getRelayState() {
        return relayState;
    }

    public void setRelayState(String relayState) {
        this.relayState = relayState;
    }

    public String getsAMLResponse() {
        return sAMLResponse;
    }

    public void setsAMLResponse(String sAMLResponse) {
        this.sAMLResponse = sAMLResponse;
    }
}
