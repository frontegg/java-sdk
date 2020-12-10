package com.frontegg.sdk.common.exception;

public class InvalidParameterException extends FronteggSDKException
{

	public InvalidParameterException()
	{
		super();
	}

	public InvalidParameterException(String message)
	{
		super(message);
	}

	public InvalidParameterException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
