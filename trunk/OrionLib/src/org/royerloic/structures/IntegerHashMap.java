package org.royerloic.structures;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 * @param <K>
 * @param <V>
 */
public class IntegerHashMap<K> extends HashMap<K, Integer> implements IntegerMap<K>
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5619054777054023067L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.DoubleMap#add(java.lang.Object,
	 *      java.lang.Double)
	 */
	public Integer add(final K pKey, final Integer pValue)
	{
		Integer lValue = get(pKey);
		if (lValue == null)
			lValue = pValue;
		else
			lValue = lValue + pValue;

		return put(pKey, lValue);
	}

	public IntegerMap<K> addAllWith(final Collection<K> pCollection, final Integer pValue)
	{
		for (final K lK : pCollection)
			add(lK, pValue);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.DoubleMap#sub(java.lang.Object,
	 *      java.lang.Double)
	 */
	public Integer sub(final K pKey, Integer pValue)
	{
		return add(pKey, -pValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.DoubleMap#mult(java.lang.Object,
	 *      java.lang.Double)
	 */
	public Integer mult(final K pKey, final Integer pValue)
	{
		Integer lValue = get(pKey);
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
	public Integer div(final K pKey, final Integer pValue)
	{
		return mult(pKey, 1 / pValue);
	}

	public Integer min(final K pKey, final Integer pNewValue)
	{
		final Integer lOldValue = get(pKey);
		if (lOldValue == null)
			return put(pKey, pNewValue);
		else if (lOldValue > pNewValue)
			return put(pKey, pNewValue);
		return lOldValue;
	}

	public Integer max(final K pKey, final Integer pNewValue)
	{
		final Integer lOldValue = get(pKey);
		if (lOldValue == null)
			return put(pKey, pNewValue);
		else if (lOldValue < pNewValue)
			return put(pKey, pNewValue);
		return lOldValue;
	}

	public IntegerMap<K> minAll(final Map<K, Integer> pMap)
	{
		for (final Map.Entry<K, Integer> lEntry : pMap.entrySet())
			min(lEntry.getKey(), lEntry.getValue());
		return this;
	}

	public IntegerMap<K> maxAll(final Map<K, Integer> pMap)
	{
		for (final Map.Entry<K, Integer> lEntry : pMap.entrySet())
			max(lEntry.getKey(), lEntry.getValue());
		return this;
	}

}
