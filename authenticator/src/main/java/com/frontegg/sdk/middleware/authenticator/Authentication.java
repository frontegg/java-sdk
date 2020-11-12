package com.frontegg.sdk.middleware.authenticator;

import java.time.LocalDateTime;

public interface Authentication {
    LocalDateTime getAccessTokenExpiry();
    String getAccessToken();

}
