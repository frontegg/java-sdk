package com.frontegg.sdk.spring.middleware.context.resolver;

public class IdentityModel {
    private String id;
    private Long defaultTokenExpiration;
    private Long defaultRefreshTokenExpiration;
    private String publicKey;
    private String cookieSameSite;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDefaultTokenExpiration() {
        return defaultTokenExpiration;
    }

    public void setDefaultTokenExpiration(Long defaultTokenExpiration) {
        this.defaultTokenExpiration = defaultTokenExpiration;
    }

    public Long getDefaultRefreshTokenExpiration() {
        return defaultRefreshTokenExpiration;
    }

    public void setDefaultRefreshTokenExpiration(Long defaultRefreshTokenExpiration) {
        this.defaultRefreshTokenExpiration = defaultRefreshTokenExpiration;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getCookieSameSite() {
        return cookieSameSite;
    }

    public void setCookieSameSite(String cookieSameSite) {
        this.cookieSameSite = cookieSameSite;
    }
}
