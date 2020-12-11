package com.frontegg.sdk.middleware;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.HttpHelper;
import com.frontegg.sdk.config.DefaultConfigProvider;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Map;

import static com.frontegg.sdk.common.util.HttpHelper.FRONTEGG_HEADER_HOST;
import static com.frontegg.sdk.middleware.Constants.FRONTEGG_CONTEXT_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FronteggServiceTest
{

	private static final String ACCESS_TOKEN = "access_token";
	private static final String CLIENT_ID = "client-id";
	private static final String API_KEY = "api-key";
	private static final String TENANT_ID = "tenant-id";
	private static final String VENDOR_HOST = "mydomain";
	private static final String USER_ID = "user-id";
	private static final String BASE_PATH = "/frontegg";
	private static final String BASE_URL = "https://api.frontegg.com";
	private static final String REQUEST_TEST_ENDPOINT = "/some-test-endpoint";
	private final FronteggConfig config = new DefaultConfigProvider().resolveConfigs();
	private ApiClient apiClient;
	private FronteggAuthenticator authenticator;
	private FronteggOptions fronteggOptions;
	private FronteggService fronteggService;
	private FronteggContext context;

	@BeforeEach
	void setUp()
	{
		this.apiClient = mock(ApiClient.class);
		this.authenticator = mock(FronteggAuthenticator.class);
		this.fronteggOptions = new FronteggOptions(CLIENT_ID, API_KEY, false, null, 3, BASE_PATH);

		this.context = new FronteggContext(TENANT_ID, USER_ID);

		when(this.authenticator.getAccessToken()).thenReturn(ACCESS_TOKEN);

		this.fronteggService = new FronteggService(this.config,
												   this.apiClient,
												   this.authenticator,
												   this.fronteggOptions);
	}

	@Test
	public void doProcess()
	{
		Object obj = new Object();
		FronteggHttpResponse<Object> expectedResponse = new FronteggHttpResponse<>();
		expectedResponse.setBody(obj);
		expectedResponse.setStatusCode(200);
		expectedResponse.setHeaders(new ArrayList<>());
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);

		ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
		String url = BASE_URL + REQUEST_TEST_ENDPOINT;
		when(this.apiClient.service(eq(url),
									any(HttpServletRequest.class),
									any(HttpServletResponse.class),
									captor.capture(),
									eq(Object.class))).thenReturn(expectedResponse);
		when(request.getHeader(FRONTEGG_HEADER_HOST)).thenReturn(VENDOR_HOST);
		when(request.getRequestURI()).thenReturn(BASE_PATH + REQUEST_TEST_ENDPOINT);
		when(request.getAttribute(FRONTEGG_CONTEXT_KEY)).thenReturn(this.context);

		FronteggHttpResponse<Object> httpResponse = this.fronteggService.doProcess(request, response);

		Map<String, String> capturedHeaders = captor.getValue();
		assertEquals(capturedHeaders.get(HttpHelper.FRONTEGG_HEADER_TENANT_ID), TENANT_ID);
		assertEquals(capturedHeaders.get(HttpHelper.FRONTEGG_HEADER_USER_ID), USER_ID);
		assertEquals(capturedHeaders.get(HttpHelper.FRONTEGG_HEADER_ACCESS_TOKEN), ACCESS_TOKEN);
		assertEquals(capturedHeaders.get(HttpHelper.FRONTEGG_HEADER_VENDOR_HOST), VENDOR_HOST);

		assertNotNull(httpResponse);
		assertEquals(200, httpResponse.getStatusCode());
		assertEquals(obj, httpResponse.getBody());
		assertNotNull(httpResponse.getHeaders());
	}

	@Test
	public void doProcess_sdkInternalProblem()
	{
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		String url = BASE_URL + REQUEST_TEST_ENDPOINT;
		when(this.apiClient.service(eq(url),
									any(HttpServletRequest.class),
									any(HttpServletResponse.class),
									anyMap(),
									eq(Object.class))).thenThrow(new FronteggSDKException("Something went wrong"));
		when(request.getRequestURI()).thenReturn(BASE_PATH + REQUEST_TEST_ENDPOINT);
		when(request.getAttribute(FRONTEGG_CONTEXT_KEY)).thenReturn(this.context);

		try
		{
			this.fronteggService.doProcess(request, response);
			fail("Expected FronteggSDKException exception");
		}
		catch (FronteggSDKException ex)
		{
			assertEquals("Something went wrong", ex.getMessage());
		}
	}
}