package utils.structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class ArrayListMap<K, V> extends HashMap<K, List<V>> implements ListMap<K, V>
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2439064125823640434L;

	/**
	 * @return
	 */
	public Set<V> valuesFromAllLists()
	{
		final Set<V> lAllValuesSet = new HashSet<V>();
		final Collection<List<V>> lValues = super.values();
		for (final List<V> lSet : lValues)
			lAllValuesSet.addAll(lSet);
		return lAllValuesSet;
	}

	/**
	 * @param pKey
	 * @param pValue
	 * @return
	 */
	public List<V> put(final K pKey, final V pValue)
	{
		List<V> lValueList = get(pKey);
		if (lValueList == null)
		{
			lValueList = new ArrayList<V>();
			super.put(pKey, lValueList);
		}
		lValueList.add(pValue);
		return lValueList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public List<V> put(final K pKey, final List<V> pVList)
	{
		List<V> lValueList = get(pKey);
		if (lValueList == null)
		{
			lValueList = new ArrayList<V>(pVList);
			super.put(pKey, lValueList);
		}
		else
			lValueList.addAll(pVList);

		return lValueList;
	}

}
