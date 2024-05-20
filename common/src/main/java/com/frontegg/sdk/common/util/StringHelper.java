package com.frontegg.sdk.common.util;

import java.util.Optional;

public class StringHelper
{

	public static boolean isBlank(String val)
	{
		return isBlank(val, false);
	}

	public static boolean isBlank(String val, boolean trim)
	{
		return Optional.ofNullable(val).map(v -> trim ? v.isBlank() : val.isEmpty()).orElse(true);
	}

	public static String stringValueOf(Object o)
	{
		if (o instanceof String)
		{
			return (String) o;
		}

		if (o instanceof Object[] array)
		{
			if (array.length > 1)
			{
				StringBuilder builder = new StringBuilder();
				for (Object o1 : array)
				{
					builder.append(stringValueOf(o1)).append(",");
				}
				return builder.toString();
			}
			return stringValueOf(array[0]);
		}

		return o.toString();
	}
}
