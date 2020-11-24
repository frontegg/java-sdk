package com.frontegg.sdk.sso;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.common.model.FronteggHttpHeader;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.HttpUtil;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;

import java.util.HashMap;
import java.util.Map;

public class SsoClient implements ISsoClient {
    private static final String PRE_LOGIN_PATH = "/resources/sso/v1/prelogin";
    private static final String POST_LOGIN_PATH = "/resources/sso/v1/postlogin";

    private FronteggAuthenticator authenticator;
    private IApiClient apiClient;
    private FronteggConfig fronteggConfig;

    public SsoClient(FronteggAuthenticator authenticator, IApiClient apiClient, FronteggConfig fronteggConfig) {
        this.authenticator = authenticator;
        this.apiClient = apiClient;
        this.fronteggConfig = fronteggConfig;
    }

    @Override
    public String preLogin(String payload) {
        String urlPath = fronteggConfig.getUrlConfig().getTeamService() + PRE_LOGIN_PATH;
        FronteggHttpResponse<Object> response = apiClient.post(urlPath, Object.class, withHeaders(), payload);
        validateStatus(urlPath, response);
        FronteggHttpHeader locationHeader = response.getHeaders().stream().filter(fh -> fh.getName().equals("location")).findFirst().orElse(null);
        return locationHeader != null ? locationHeader.getValue() : null;
    }

    @Override
    public Object postLogin(SamlResponse samlResponse) {
        String urlPath = fronteggConfig.getUrlConfig().getTeamService() + POST_LOGIN_PATH;
        FronteggHttpResponse<Object> response = apiClient.post(urlPath, Object.class, withHeaders(), samlResponse);
        validateStatus(urlPath, response);
        return response.getBody();
    }

    private Map<String, String> withHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpUtil.FRONTEGG_HEADER_ACCESS_TOKEN, authenticator.getAccessToken());
        return headers;
    }

    private void validateStatus(String url, FronteggHttpResponse<?> response) {
        if (response.getStatusCode() < 200 || response.getStatusCode() >= 400)
            throw new FronteggSDKException("SSO request to " + url + " fails. invalid response status  = " + response.getStatusCode());
    }
}
