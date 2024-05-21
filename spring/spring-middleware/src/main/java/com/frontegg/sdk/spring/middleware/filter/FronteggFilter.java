package com.frontegg.sdk.spring.middleware.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontegg.sdk.common.exception.FronteggHttpException;
import com.frontegg.sdk.common.model.FronteggHttpHeader;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.HttpHelper;
import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.FronteggContextResolver;
import com.frontegg.sdk.middleware.routes.IFronteggRouteService;
import com.frontegg.sdk.spring.middleware.FronteggServiceDelegate;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static com.frontegg.sdk.common.util.HttpHelper.*;
import static com.frontegg.sdk.middleware.Constants.FRONTEGG_CONTEXT_KEY;

public class FronteggFilter extends GenericFilterBean
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FronteggFilter.class);

	private static final String FILTER_APPLIED = "__frontegg_feggf_applied";

	private final FronteggContextResolver fronteggContextResolver;
	private final FronteggAuthenticator fronteggAuthenticator;
	private final IFronteggRouteService fronteggRouteService;
	private final FronteggServiceDelegate fronteggServiceDelegate;
	private final FronteggOptions options;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public FronteggFilter(
			FronteggAuthenticator fronteggAuthenticator,
			FronteggContextResolver fronteggContextResolver,
			IFronteggRouteService fronteggRouteService,
			FronteggServiceDelegate fronteggServiceDelegate,
			FronteggOptions options
	)
	{
		this.fronteggAuthenticator = fronteggAuthenticator;
		this.fronteggContextResolver = fronteggContextResolver;
		this.fronteggRouteService = fronteggRouteService;
		this.fronteggServiceDelegate = fronteggServiceDelegate;
		this.options = options;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
	throws IOException, ServletException
	{
		var request = (HttpServletRequest) servletRequest;
		var response = (HttpServletResponse) servletResponse;

		var matcher = new AntPathMatcher();
		var isRequestMatches = matcher.match(this.options.getBasePath() + "/**", request.getRequestURI());

		if (isRequestMatches)
		{
			if (request.getAttribute(FILTER_APPLIED) != null)
			{
				// ensure that filter is only applied once per request
				filterChain.doFilter(request, response);
				return;
			}

			request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
			try
			{
				this.fronteggAuthenticator.authenticate();

				if (request.getMethod().equals("OPTIONS"))
				{
					response.setStatus(204);
					return;
				}

				if (!isFronteggPublicRoute(request))
				{
					this.saveContextToRequest(request, this.fronteggContextResolver.resolveContext(request));
				}

				FronteggHttpResponse<Object> fronteggHttpResponse = this.fronteggServiceDelegate.doProcess(request,
				                                                                                           response);
				populateHeaders(fronteggHttpResponse.getHeaders(), response);

				manageCorsHeaders(response, fronteggHttpResponse);

				response.setStatus(fronteggHttpResponse.getStatusCode());

				Object object = fronteggHttpResponse.getBody();
				if (object != null)
				{
					response.setContentType("application/json");
					response.getWriter().write(this.objectMapper.writeValueAsString(object));
				}

				return;

			}
			catch (Exception ex)
			{
				resolverException(ex, response);
				return;
			}
			finally
			{
				request.removeAttribute(FILTER_APPLIED);
				response.getWriter().flush();
			}
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	private void saveContextToRequest(HttpServletRequest request, FronteggContext context)
	{
		request.setAttribute(FRONTEGG_CONTEXT_KEY, context);
	}

	private void manageCorsHeaders(HttpServletResponse response, FronteggHttpResponse<Object> fronteggHttpResponse)
	{
		if (this.options.isDisableCors())
		{
			HttpHelper.deleteHeaders(response,
			                         ACCESS_CONTROL_REQUEST_METHOD,
			                         ACCESS_CONTROL_REQUEST_HEADERS,
			                         ACCESS_CONTROL_ALLOW_ORIGIN,
			                         ACCESS_CONTROL_ALLOW_CREDENTIALS);
		}
		else
		{
			enableCors(fronteggHttpResponse, response);
		}
	}

	private void resolverException(Exception ex, HttpServletResponse response) throws IOException
	{
		PrintWriter printWriter = response.getWriter();
		if (ex instanceof FronteggHttpException exception)
		{
			response.setStatus(exception.getStatus());
			printWriter.write(exception.getMessage());
		}
		else if (ex instanceof HttpClientErrorException restException)
		{
			response.setStatus(restException.getStatusCode().value());
			printWriter.write(restException.getResponseBodyAsString());
		}
		else
		{
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			printWriter.write(this.objectMapper.writeValueAsString(new FronteggExternalizedException(ex.getMessage())));
		}

		printWriter.flush();
	}

	void enableCors(FronteggHttpResponse<Object> fronteggHttpResponse, HttpServletResponse response)
	{
		HttpHelper.replaceHeader(fronteggHttpResponse.getHeaders(), response, ACCESS_CONTROL_REQUEST_METHOD);
		HttpHelper.replaceHeader(fronteggHttpResponse.getHeaders(), response, ACCESS_CONTROL_REQUEST_HEADERS);
		HttpHelper.replaceHeader(fronteggHttpResponse.getHeaders(), response, ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN);
	}

	private boolean isFronteggPublicRoute(HttpServletRequest request)
	{
		return this.fronteggRouteService.isFronteggPublicRoute(request);
	}

	public void populateHeaders(List<FronteggHttpHeader> headers, HttpServletResponse response)
	{
		headers.forEach(header -> response.setHeader(header.getName(), header.getValue()));
	}

	public static class FronteggExternalizedException
	{
		private String message;

		public FronteggExternalizedException(String message)
		{
			this.message = message;
		}

		public String getMessage()
		{
			return this.message;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}
	}
}
