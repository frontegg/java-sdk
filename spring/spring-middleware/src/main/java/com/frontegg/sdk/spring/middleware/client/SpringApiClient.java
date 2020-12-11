package com.frontegg.sdk.spring.middleware.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.exception.FronteggHttpException;
import com.frontegg.sdk.common.model.FronteggHttpHeader;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.middleware.authenticator.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SpringApiClient implements ApiClient
{
	private static final Logger logger = LoggerFactory.getLogger(SpringApiClient.class);

	private final RestTemplate restTemplate;
	private final ObjectMapper mapper = new ObjectMapper();

	public SpringApiClient(RestTemplate restTemplate)
	{
		this.restTemplate = restTemplate;
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
	}

	@Override
	public <T> Optional<T> get(String url, Class<T> clazz)
	{
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		ResponseEntity<T> responseEntity = this.restTemplate.exchange(builder.toUriString(),
																	  HttpMethod.GET,
																	  createHttpEntity(),
																	  clazz);
		return Optional.of(responseEntity.getBody());
	}

	@Override
	public <T> Optional<T> get(String url, Map<String, String> headers, Class<T> clazz)
	{
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		ResponseEntity<T> responseEntity = this.restTemplate.exchange(builder.toUriString(),
																	  HttpMethod.GET,
																	  createHttpEntity(headers, null),
																	  clazz);
		return Optional.of(responseEntity.getBody());
	}

	@Override
	public <T, R> FronteggHttpResponse<T> post(String url, Class<T> clazz, R body)
	{
		return post(url, clazz, null, body);
	}

	@Override
	public <T, R> FronteggHttpResponse<T> post(String url, Class<T> clazz, Map<String, String> headers, R body)
	{
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		HttpEntity<Object> httpEntity = createHttpEntity(headers, body);
		ResponseEntity<T> responseEntity = this.restTemplate.exchange(builder.toUriString(),
																	  HttpMethod.POST,
																	  httpEntity,
																	  clazz);
		return convert(responseEntity);
	}

	@Override
	public <T> FronteggHttpResponse<T> service(
			String url,
			HttpServletRequest request,
			HttpServletResponse response,
			Map<String, String> headers,
			Class<T> clazz
	)
	{
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
			if (!StringHelper.isBlank(request.getQueryString())) {
				builder.query(request.getQueryString());
			}

			HttpMethod method = HttpMethod.resolve(request.getMethod());
			HttpEntity<Object> httpEntity = createHttpEntity(request, headers);
			ResponseEntity<T> responseEntity = this.restTemplate.exchange(builder.toUriString(), method, httpEntity, clazz);
			return convert(responseEntity);
		} catch (RestClientException ex) {
			if (ex instanceof HttpStatusCodeException) {

				if (((HttpStatusCodeException)ex).getStatusCode() == HttpStatus.UNAUTHORIZED) {
					throw new AuthenticationException(ex.getMessage(), ex);
				}

				throw new FronteggHttpException(
						((HttpStatusCodeException)ex).getStatusCode().value(),
						"frontegg sdk call fails with message",
						ex
				);
			}
			throw ex;
		}
	}

	//region request helper methods

	private HttpEntity<Object> createHttpEntity()
	{
		return new HttpEntity<>(buildHttpHeaders(null));
	}

	private <R> HttpEntity<Object> createHttpEntity(Map<String, String> proxyHeaders, R body)
	{
		HttpHeaders headers = buildHttpHeaders(proxyHeaders);
		return buildHttpEntity(body, headers);
	}

	private HttpEntity<Object> createHttpEntity(HttpServletRequest request, Map<String, String> proxyHeaders)
	{
		HttpHeaders headers = buildHttpHeaders(proxyHeaders);
		populateRequestHeadersToApiRequest(headers, request);
		return buildHttpEntity(getBody(request), headers);
	}

	private <T> HttpEntity<T> buildHttpEntity(T body, HttpHeaders headers)
	{
		if (body == null)
		{
			return new HttpEntity<>(headers);
		}

		if (body instanceof String)
		{
			String strBody = (String) body;
			if (!StringHelper.isBlank(strBody))
			{
				return buildHttpEntity(headers, strBody);
			}
		}
		else
		{
			try
			{
				String strBody = this.mapper.writeValueAsString(body);
				return buildHttpEntity(headers, strBody);
			}
			catch (Exception ex)
			{
				logger.error("unable to jsonify the request body of class -> {} ", body.getClass());
			}
		}

		return new HttpEntity<>(headers);
	}

	private <T> HttpEntity<T> buildHttpEntity(HttpHeaders headers, String strBody)
	{
		headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
		headers.put(HttpHeaders.CONTENT_LENGTH, Collections.singletonList(String.valueOf(strBody.length())));
		return new HttpEntity(strBody, headers); // TODO needs to be an actual type
	}

	private void populateRequestHeadersToApiRequest(HttpHeaders headers, HttpServletRequest request)
	{
		Enumeration<String> enumeration = request.getHeaderNames();
		while (enumeration.hasMoreElements())
		{
			String headerName = enumeration.nextElement();
			if (headers.containsKey(headerName))
			{
				continue;
			}

			String headerValue = request.getHeader(headerName);
			headers.add(headerName, headerValue);
		}
	}

	private HttpHeaders buildHttpHeaders(Map<String, String> headersMap)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));

		if (headersMap != null)
		{
			for (String key : headersMap.keySet())
			{
				headers.add(key, headersMap.get(key));
			}
		}

		return headers;
	}

	private String getBody(HttpServletRequest request)
	{
		HttpMethod method = HttpMethod.resolve(request.getMethod());
		if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH)
		{
			try
			{
				return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
			}
			catch (IOException e)
			{
				logger.error("Unable to read the body of request", e);
			}
		}
		return null;
	}
	//endregion

	//region response helper methods
	private <T> FronteggHttpResponse<T> convert(ResponseEntity<T> responseEntity)
	{
		FronteggHttpResponse<T> response = new FronteggHttpResponse<>();
		response.setBody(responseEntity.getBody());
		response.setStatusCode(responseEntity.getStatusCode().value());
		response.setHeaders(convertHeaders(responseEntity.getHeaders()));
		return response;
	}

	private List<FronteggHttpHeader> convertHeaders(HttpHeaders headers)
	{
		List<FronteggHttpHeader> fronteggHttpHeaders = new ArrayList<>();
		Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
		for (Map.Entry<String, List<String>> entry : entries)
		{
			String key = entry.getKey();
			String value = String.join(";", entry.getValue());
			fronteggHttpHeaders.add(new FronteggHttpHeader(key, value));
		}
		return fronteggHttpHeaders;
	}
	//endregion
}
