package utils.structures.map;

import java.util.HashMap;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 * @param <K>
 * @param <V>
 */
public class HashDoubleMap<K> extends HashMap<K, Double> implements
																												DoubleMap<K>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4035713289800645393L;

	
	
	
	public HashDoubleMap(HashDoubleMap<K> pPowerNodePValues)
	{
		super(pPowerNodePValues);
	}

	public HashDoubleMap()
	{
		super();
	}

	public Double putIfNull(final K pKey, final Double pValue)
	{
		Double lValue = get(pKey);
		if (lValue == null)
		{
			return put(pKey, pValue);
		}
		return lValue;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.DoubleMap#add(java.lang.Object,
	 * java.lang.Double)
	 */
	public Double add(final K pKey, final Double pValue)
	{
		Double lValue = get(pKey);
		if (lValue == null)
		{
			lValue = pValue;
		}
		else
		{
			lValue = lValue + pValue;
		}

		return put(pKey, lValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.DoubleMap#sub(java.lang.Object,
	 * java.lang.Double)
	 */
	public Double sub(final K pKey, Double pValue)
	{
		return add(pKey, -pValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.DoubleMap#mult(java.lang.Object,
	 * java.lang.Double)
	 */
	public Double mult(final K pKey, final Double pValue)
	{
		Double lValue = get(pKey);
		if (lValue == null)
		{
			lValue = pValue;
		}
		else
		{
			lValue = lValue * pValue;
		}

		return put(pKey, lValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.DoubleMap#div(java.lang.Object,
	 * java.lang.Double)
	 */
	public Double div(final K pKey, final Double pValue)
	{
		return mult(pKey, 1 / pValue);
	}

	public Double min(final K pKey, final Double pNewValue)
	{
		final Double lOldValue = get(pKey);
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

	public Double max(final K pKey, final Double pNewValue)
	{
		final Double lOldValue = get(pKey);
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
