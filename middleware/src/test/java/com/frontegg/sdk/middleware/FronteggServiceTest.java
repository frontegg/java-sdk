package com.frontegg.sdk.middleware;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.HttpHelper;
import com.frontegg.sdk.config.DefaultConfigProvider;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Map;

import static com.frontegg.sdk.common.util.HttpHelper.FRONTEGG_HEADER_HOST;
import static com.frontegg.sdk.middleware.Constants.FRONTEGG_CONTEXT_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
	private static final String URL = BASE_URL + REQUEST_TEST_ENDPOINT;
	private static final FronteggConfig config = new DefaultConfigProvider().resolveConfigs();
	private static final FronteggOptions fronteggOptions = new FronteggOptions(CLIENT_ID,
	                                                                           API_KEY,
	                                                                           false,
	                                                                           null,
	                                                                           3,
	                                                                           BASE_PATH);
	private static final FronteggContext context = new FronteggContext(TENANT_ID, USER_ID);

	public void setup(FronteggAuthenticator authenticator)
	{
		when(authenticator.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}

	@Test
	public void doProcess(
			@Mock HttpServletRequest mockRequest,
			@Mock HttpServletResponse mockResponse,
			@Mock ApiClient mockApiClient,
			@Mock FronteggAuthenticator mockAuthenticator
	)
	{
		setup(mockAuthenticator);
		var cut = new FronteggService(config, mockApiClient, mockAuthenticator, fronteggOptions);

		var obj = new Object();
		var expectedResponse = new FronteggHttpResponse<>();
		expectedResponse.setBody(obj);
		expectedResponse.setStatusCode(200);
		expectedResponse.setHeaders(new ArrayList<>());

		var captor = ArgumentCaptor.forClass(Map.class);

		when(mockApiClient.service(eq(URL),
		                           any(HttpServletRequest.class),
		                           any(HttpServletResponse.class),
		                           captor.capture(),
		                           eq(Object.class))).thenReturn(expectedResponse);
		when(mockRequest.getHeader(FRONTEGG_HEADER_HOST)).thenReturn(VENDOR_HOST);
		when(mockRequest.getRequestURI()).thenReturn(BASE_PATH + REQUEST_TEST_ENDPOINT);
		when(mockRequest.getAttribute(FRONTEGG_CONTEXT_KEY)).thenReturn(context);

		var httpResponse = cut.doProcess(mockRequest, mockResponse);

		var capturedHeaders = captor.getValue();
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
	public void doProcess_sdkInternalProblem(
			@Mock HttpServletRequest mockRequest,
			@Mock HttpServletResponse mockResponse,
			@Mock ApiClient mockApiClient,
			@Mock FronteggAuthenticator mockAuthenticator
	)
	{
		setup(mockAuthenticator);
		var cut = new FronteggService(config, mockApiClient, mockAuthenticator, fronteggOptions);

		when(mockApiClient.service(eq(URL),
		                            any(HttpServletRequest.class),
		                            any(HttpServletResponse.class),
		                            anyMap(),
		                            eq(Object.class))).thenThrow(new FronteggSDKException("Something went wrong"));
		when(mockRequest.getRequestURI()).thenReturn(BASE_PATH + REQUEST_TEST_ENDPOINT);
		when(mockRequest.getAttribute(FRONTEGG_CONTEXT_KEY)).thenReturn(context);

		try
		{
			cut.doProcess(mockRequest, mockResponse);
			fail("Expected FronteggSDKException exception");
		}
		catch (FronteggSDKException ex)
		{
			assertEquals("Something went wrong", ex.getMessage());
		}
	}
}