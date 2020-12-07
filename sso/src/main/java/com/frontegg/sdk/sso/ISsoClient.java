package com.frontegg.sdk.sso;

public interface ISsoClient {

    String preLogin(String payload);

    Object postLogin(SamlResponse samlResponse);
}
