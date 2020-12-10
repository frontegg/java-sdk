package com.frontegg.sdk.middleware.context;

import com.frontegg.sdk.common.exception.FronteggSDKException;

import javax.servlet.http.HttpServletRequest;

public class NoOpContextResolver implements FronteggContextResolver
{
	@Override
	public FronteggContext resolveContext(HttpServletRequest request)
	{
		throw new FronteggSDKException("You should provide context resolver");
	}
}
