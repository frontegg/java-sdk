package com.frontegg.sdk.spring.middleware.config;

import com.frontegg.sdk.config.BaseConfigProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Order(4)
@Component
public class SpringFronteggConfigProvider extends BaseConfigProvider
{
	@Value("${frontegg.config.urls.baseUrl:#{null}}")
	private String baseUrl;

	@Override
	protected Optional<String> getBaseUrl(String key)
	{
		return Optional.ofNullable(this.baseUrl);
	}
}
