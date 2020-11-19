package com.frontegg.sdk.middleware.spring.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.common.util.HttpUtil;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.identity.IIdentityService;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.spring.context.FronteggContextHolder;
import com.frontegg.sdk.middleware.spring.model.IdentityModel;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.rsa.RSAPublicKeyImpl;

import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

@Service
public class IdentityService implements IIdentityService {
    private static final Logger logger = LoggerFactory.getLogger(IdentityService.class);

    @Autowired
    private FronteggAuthenticator authenticator;

    @Autowired
    private IApiClient apiClient;

    @Autowired
    private FronteggConfig fronteggConfig;

    @Override
    public void verifyToken(String token) {

        logger.info("going to authenticate");
        authenticator.authenticate();
        logger.info("going to get identity service configuration");

        try {

            logger.info("got identity service configuration");
            IdentityModel identityModel = apiClient.get(
                    fronteggConfig.getUrlConfig().getIdentityService() + "/resources/configurations/v1",
                    withHeaders(),
                    IdentityModel.class
            ).get();


            // And save it as member of the class
            logger.info("going to extract public key from response");
            DecodedJWT jwt = JWT.decode(token);

            RSAPublicKey publicKey = new RSAPublicKeyImpl(Base64.decode(normalizedPublicKey(identityModel.getPublicKey())));
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
            throw new FronteggSDKException("Unable to verify token", e);
        }
    }

    private String normalizedPublicKey(String key) {
        return key.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
    }

    private Map<String, String> withHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpUtil.FRONTEGG_HEADER_ACCESS_TOKEN, authenticator.getAccessToken());
        return headers;
    }
}
