package com.frontegg.sdk.spring.middleware.context.resolver;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.common.util.HttpHelper;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.AuthenticationException;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.FronteggContextHolder;
import com.frontegg.sdk.middleware.context.FronteggContextResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.rsa.RSAPublicKeyImpl;

import javax.servlet.http.HttpServletRequest;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class FronteggIdentityServiceImpl implements FronteggContextResolver
{
	private static final Logger logger = LoggerFactory.getLogger(FronteggIdentityServiceImpl.class);
	private static final String PUBLIC_KEY_PATH = "/resources/configurations/v1";
	private FronteggAuthenticator authenticator;
	private ApiClient apiClient;
	private FronteggConfig fronteggConfig;
	private RSAPublicKey publicKey;

	public FronteggIdentityServiceImpl(
			FronteggAuthenticator authenticator, ApiClient apiClient, FronteggConfig fronteggConfig
	)
	{
		this.authenticator = authenticator;
		this.apiClient = apiClient;
		this.fronteggConfig = fronteggConfig;
	}

	private void init()
	{
		publicKey = getPublicKey();
	}

	public Map<String, Claim> verifyToken(String token)
	{
		init();

		try
		{
			// And save it as member of the class
			DecodedJWT jwt = JWT.decode(token);

			JWTVerifier verifier = JWT.require(Algorithm.RSA256(publicKey, null)).build();
			verifier.verify(token);

			return jwt.getClaims();
		}
		catch (Exception e)
		{
			logger.error("Unable to verify token", e);
			throw new FronteggSDKException("Unable to verify token", e);
		}
	}

	@Override
	public void resolveContext(HttpServletRequest request)
	{
		String authorizationHeader = request.getHeader("authorization");
		if (StringHelper.isBlank(authorizationHeader))
		{
			throw new AuthenticationException("FronteggAuthentication is required URL - " + request.getRequestURI());
		}

		String token = authorizationHeader.replace("Bearer ", "");

		Map<String, Claim> claimMap = verifyToken(token);

		String userID = claimMap.get("sub").asString();
		String tenantID = claimMap.get("tenantId").asString();

		FronteggContext fronteggContext = FronteggContextHolder.getContext();
		fronteggContext.setTenantId(tenantID);
		fronteggContext.setUserId(userID);
		FronteggContextHolder.setContext(fronteggContext);
	}

	private RSAPublicKey getPublicKey()
	{
		if (publicKey != null)
		{
			return publicKey;
		}

		logger.info("going to authenticate");
		authenticator.authenticate();
		logger.info("going to get identity service configuration");

		String urlPath = fronteggConfig.getUrlConfig().getIdentityService() + PUBLIC_KEY_PATH;
		try
		{
			IdentityModel identityModel = apiClient.get(urlPath, withHeaders(), IdentityModel.class).get();
			logger.info("got identity service configuration");

			logger.debug("going to extract public key from response");
			return RSAPublicKeyImpl.newKey(Base64.getDecoder()
												 .decode(normalizedPublicKey(identityModel.getPublicKey())));
		}
		catch (Exception ex)
		{
			logger.error("Unable to get frontegg public key from url {} ", urlPath, ex);
			throw new FronteggSDKException(ex.getMessage(), ex);
		}
	}

	private String normalizedPublicKey(String key)
	{
		return key.replaceAll("\\n", "")
				  .replace("-----BEGIN PUBLIC KEY-----", "")
				  .replace("-----END PUBLIC KEY-----", "");
	}

	private Map<String, String> withHeaders()
	{
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpHelper.FRONTEGG_HEADER_ACCESS_TOKEN, authenticator.getAccessToken());
		return headers;
	}
}
