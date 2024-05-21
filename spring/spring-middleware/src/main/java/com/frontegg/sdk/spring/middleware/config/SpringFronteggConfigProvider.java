package com.frontegg.sdk.spring.middleware.config;

import com.frontegg.sdk.config.BaseConfigProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(4)
@Component
public class SpringFronteggConfigProvider extends BaseConfigProvider
{
	@Value("${frontegg.config.urls.baseUrl:#{''}}")
	private String baseUrl;

	@Override
	protected String getBaseUrl(String key)
	{
		return this.baseUrl;
	}
}
