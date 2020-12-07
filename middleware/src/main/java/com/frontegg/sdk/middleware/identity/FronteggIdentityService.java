package com.frontegg.sdk.middleware.identity;

public interface FronteggIdentityService {

    void verifyToken(String token);
}
