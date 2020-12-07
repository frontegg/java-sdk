package com.frontegg.samples;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.sso.SamlResponse;
import com.frontegg.sdk.sso.SsoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class SSO
{
	private final FronteggConfig config;
	private final ApiClient apiClient;
	private final FronteggAuthenticator authenticator;

	private final SsoClient ssoClient;

	@Autowired
	public SSO(ApiClient apiClient, FronteggAuthenticator authenticator, FronteggConfig config)
	{
		this.apiClient = apiClient;
		this.authenticator = authenticator;
		this.config = config;

		this.ssoClient = new SsoClient(this.authenticator, this.apiClient, this.config);
	}

	@PostMapping("/login")
	public void login(@RequestParam("email") String email, HttpServletResponse response) throws IOException
	{
		String location = this.ssoClient.preLogin(email);
		response.sendRedirect(location);
	}

	@PostMapping("/auth/saml/callback")
	public Object samlCallback(@RequestBody() SamlResponse samlResponse)
	{
		return this.ssoClient.postLogin(samlResponse);
	}

}
