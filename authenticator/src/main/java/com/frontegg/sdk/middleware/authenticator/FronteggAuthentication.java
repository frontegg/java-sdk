package com.frontegg.sdk.middleware.authenticator;

import java.time.LocalDateTime;

public interface FronteggAuthentication {
    LocalDateTime getAccessTokenExpiry();
    String getAccessToken();

}
