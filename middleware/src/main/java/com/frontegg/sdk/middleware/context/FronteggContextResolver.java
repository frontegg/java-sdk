package com.frontegg.sdk.middleware.context;

import jakarta.servlet.http.HttpServletRequest;

public interface FronteggContextResolver
{
	FronteggContext resolveContext(HttpServletRequest request);
}
