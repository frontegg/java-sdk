package com.frontegg.sdk.middleware.identity.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.common.util.HttpUtil;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.FronteggContextHolder;
import com.frontegg.sdk.middleware.identity.IIdentityService;
import com.frontegg.sdk.middleware.identity.model.IdentityModel;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.rsa.RSAPublicKeyImpl;

import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

public class IdentityService implements IIdentityService {
    private static final Logger logger = LoggerFactory.getLogger(IdentityService.class);

    private FronteggAuthenticator authenticator;
    private IApiClient apiClient;
    private FronteggConfig fronteggConfig;

    private RSAPublicKey publicKey;
    private static final String PUBLIC_KEY_PATH = "/resources/configurations/v1";

    public IdentityService(FronteggAuthenticator authenticator, IApiClient apiClient, FronteggConfig fronteggConfig) {
        this.authenticator = authenticator;
        this.apiClient = apiClient;
        this.fronteggConfig = fronteggConfig;
    }

    private void init() {
        publicKey = getPublicKey();
    }

    @Override
    public void verifyToken(String token) {
        init();

        logger.info("going to authenticate");
        authenticator.authenticate();
        logger.info("going to get identity service configuration");

        try {

            // And save it as member of the class
            DecodedJWT jwt = JWT.decode(token);

            JWTVerifier verifier = JWT.require(Algorithm.RSA256(publicKey, null)).build();
            verifier.verify(token);

            Map<String, Claim> claimMap = jwt.getClaims();
            String userID = claimMap.get("sub").asString();
            String tenantID = claimMap.get("tenantId").asString();

            FronteggContext fronteggContext = FronteggContextHolder.getContext();
            fronteggContext.setUserId(userID);
            fronteggContext.setTenantId(tenantID);
            //fronteggContext.setUser(claimMap);
            FronteggContextHolder.setContext(fronteggContext);

        } catch (Exception e) {
            logger.error("Unable to verify token", e);
            throw new FronteggSDKException("Unable to verify token", e);
        }
    }

    private RSAPublicKey getPublicKey() {
        if (publicKey != null) return publicKey;

        logger.info("got identity service configuration");
        String urlPath = fronteggConfig.getUrlConfig().getIdentityService() + PUBLIC_KEY_PATH;
        try {
            IdentityModel identityModel = apiClient.get(urlPath,
                    withHeaders(),
                    IdentityModel.class
            ).get();

            logger.info("going to extract public key from response");
            return new RSAPublicKeyImpl(Base64.decode(normalizedPublicKey(identityModel.getPublicKey())));
        } catch (Exception ex) {
            logger.error("Unable to get frontegg public key from url = " + urlPath, ex);
            throw new FronteggSDKException(ex.getMessage(), ex);
        }
    }

    private String normalizedPublicKey(String key) {
        return key.replaceAll("\\n", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "");
    }

    private Map<String, String> withHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpUtil.FRONTEGG_HEADER_ACCESS_TOKEN, authenticator.getAccessToken());
        return headers;
    }
}
