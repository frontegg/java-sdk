package com.frontegg.sdk.spring.middleware.config;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.config.*;
import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.FronteggService;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.routes.IFronteggRouteService;
import com.frontegg.sdk.middleware.routes.impl.FronteggConfigRoutesService;
import com.frontegg.sdk.spring.middleware.client.SpringApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;


@Configuration
public class FronteggConfiguration
{
	@Bean
	public ConfigProvider configProvider(SpringFronteggConfigProvider springFronteggConfigProvider)
	{
		return new FronteggConfigProviderChain(springFronteggConfigProvider,
											   new EnvironmentVariableConfigProvider(),
											   new SystemPropertiesConfigProvider(),
											   new DefaultConfigProvider());
	}

	@Bean
	public FronteggConfig fronteggConfig(ConfigProvider configProvider)
	{
		return configProvider.resolveConfigs();
	}

	@Bean
	public ApiClient apiClient()
	{
		return new SpringApiClient(new RestTemplate());
	}

	@Bean
	public FronteggAuthenticator fronteggAuthenticator(
			ApiClient apiClient,
			FronteggConfig config,
			FronteggOptions options
	)
	{
		FronteggAuthenticator authenticator = new FronteggAuthenticator(options.getClientId(), options.getApiKey(), config, apiClient);
		authenticator.authenticate();
		return authenticator;
	}

	@Bean
	public IFronteggRouteService fronteggRouteService(ApiClient apiClient, FronteggConfig config, FronteggOptions options)
	{
		return new FronteggConfigRoutesService(apiClient, config, options);
	}

	@Bean
	public RetryTemplate retryTemplate(FronteggOptions options)
	{
		RetryTemplate retryTemplate = new RetryTemplate();

		FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
		fixedBackOffPolicy.setBackOffPeriod(1000L);
		retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		retryPolicy.setMaxAttempts(options.getMaxRetries());
		retryTemplate.setRetryPolicy(retryPolicy);
		return retryTemplate;
	}

	@Bean
	public FronteggService fronteggService(
			FronteggConfig config,
			ApiClient apiClient,
			FronteggAuthenticator authenticator,
			FronteggOptions fronteggOptions
	)
	{

		return new FronteggService(config, apiClient, authenticator, fronteggOptions);
	}

}
