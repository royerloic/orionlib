package org.royerloic.structures;

import java.util.HashMap;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 * @param <K>
 * @param <V>
 */
public class HashDoubleMap<K> extends HashMap<K, Double> implements DoubleMap<K>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.DoubleMap#add(java.lang.Object,
	 *      java.lang.Double)
	 */
	public Double add(K pKey, Double pValue)
	{
		Double lValue = get(pKey);
		if (lValue == null)
			lValue = pValue;
		else
			lValue = lValue + pValue;

		return put(pKey, lValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.DoubleMap#sub(java.lang.Object,
	 *      java.lang.Double)
	 */
	public Double sub(K pKey, Double pValue)
	{
		return add(pKey, -pValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.DoubleMap#mult(java.lang.Object,
	 *      java.lang.Double)
	 */
	public Double mult(K pKey, Double pValue)
	{
		Double lValue = get(pKey);
		if (lValue == null)
			lValue = pValue;
		else
			lValue = lValue * pValue;

		return put(pKey, lValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.DoubleMap#div(java.lang.Object,
	 *      java.lang.Double)
	 */
	public Double div(K pKey, Double pValue)
	{
		return mult(pKey, 1 / pValue);
	}

	public Double min(K pKey, Double pNewValue)
	{
		Double lOldValue = get(pKey);
		if (lOldValue == null)
		{
			return put(pKey, pNewValue);
		}
		else if (lOldValue > pNewValue)
		{
			return put(pKey, pNewValue);
		}
		return lOldValue;
	}

	public Double max(K pKey, Double pNewValue)
	{
		Double lOldValue = get(pKey);
		if (lOldValue == null)
		{
			return put(pKey, pNewValue);
		}
		else if (lOldValue < pNewValue)
		{
			return put(pKey, pNewValue);
		}
		return lOldValue;
	}

}
