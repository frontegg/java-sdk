package com.frontegg.sdk.sso;

public class SamlResponse {

    private String RelayState;
    private String SAMLResponse;

    public String getRelayState() {
        return this.RelayState;
    }

    public void setRelayState(String relayState) {
        this.RelayState = relayState;
    }

    public String getSAMLResponse() {
        return this.SAMLResponse;
    }

    public void setSAMLResponse(String SAMLResponse) {
        this.SAMLResponse = SAMLResponse;
    }
}
