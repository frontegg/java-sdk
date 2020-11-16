package com.frontegg.sdk.middleware.spring.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.IIdentityService;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.RequestContext;
import com.frontegg.sdk.middleware.spring.context.ContextHolder;
import com.frontegg.sdk.middleware.spring.model.IdentityModel;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.rsa.RSAPublicKeyImpl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
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

        // Get the public key
        RSAPublicKey publicKey = null;
        try {

            logger.info("got identity service configuration");
            IdentityModel identityModel = apiClient.get(
                    fronteggConfig.getUrlConfig().getIdentityService() + "/resources/configurations/v1",
                    withHeaders(),
                    IdentityModel.class
            ).get();

            DecodedJWT jwt = JWT.decode(token);

            publicKey = new RSAPublicKeyImpl(Base64.decode(normalizedPublicKey(identityModel.getPublicKey())));
            JWTVerifier verifier = JWT.require(Algorithm.RSA256(publicKey, null)).build();
            verifier.verify(token);

            Map<String, Claim> claimMap = jwt.getClaims();
            String userID = claimMap.get("sub").asString();
            String tenantID = claimMap.get("tenantId").asString();

            // And save it as member of the class
            logger.info("going to extract public key from response");

            RequestContext context = ContextHolder.getRequestContext();
            FronteggContext fronteggContext = context.getFronteggContext();
            fronteggContext.setUserId(userID);
            fronteggContext.setTenantId(tenantID);
            //fronteggContext.setUser(claimMap);
            ContextHolder.setRequestContext(context);

        } catch (Exception e) {
            throw new FronteggSDKException("Unable to verify token", e);
        }
    }

    private String normalizedPublicKey(String key) {
        return key.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
    }

    private RSAPublicKey convert(IdentityModel model) throws InvalidKeySpecException, NoSuchAlgorithmException, CertificateException {

        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        InputStream is = new ByteArrayInputStream(model.getPublicKey().getBytes());
        Certificate certificate = certFactory.generateCertificate(is);
        certificate.getPublicKey();

        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(model.getPublicKey().getBytes());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);
        System.out.println(publicKey);
        return (RSAPublicKey)publicKey;
    }

    private Map<String, String> withHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-access-token", authenticator.getAccessToken());
        return headers;
    }
}
