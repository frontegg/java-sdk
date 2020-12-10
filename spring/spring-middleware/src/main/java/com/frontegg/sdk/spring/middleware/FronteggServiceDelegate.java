package com.frontegg.sdk.spring.middleware;

import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.middleware.FronteggService;
import com.frontegg.sdk.middleware.authenticator.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class FronteggServiceDelegate
{
	private static final Logger logger = LoggerFactory.getLogger(FronteggServiceDelegate.class);

	@Autowired private FronteggService fronteggService;

	@Autowired private RetryTemplate retryTemplate;

	public FronteggHttpResponse<Object> doProcess(HttpServletRequest request, HttpServletResponse response)
	{
		return this.retryTemplate.execute(context -> {
			if (context.getLastThrowable() instanceof AuthenticationException)
			{
				logger.warn("Application is not authorized. Trying to authorize and then retry to perform the request.");
				this.fronteggService.authorizeApplication();
			}
			return this.fronteggService.doProcess(request, response);
		});
	}
}
