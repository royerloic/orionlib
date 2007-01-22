package org.royerloic.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class CmdLine
{
	public static final Map<String, String> getMap(String[] pArguments)
	{
		Map<String, String> lParameterMap = new HashMap<String, String>();
		for (String lArgument : pArguments)
		{
			lArgument = lArgument.trim();
			if (lArgument.contains("="))
			{
				String[] lKeyValueArray = lArgument.split("=");
				String lKey = lKeyValueArray[0];
				String lValue = lKeyValueArray[1];
				lParameterMap.put(lKey, lValue);
			}
			else
			{
				lParameterMap.put(lArgument, "yes");
			}
		}
		return lParameterMap;
	}
}
