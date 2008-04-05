package utils.structures.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class HashListMap<K, V> extends HashMap<K, List<V>> implements
																													ListMap<K, V>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8584828916052243423L;

	/**
	 * @return
	 */
	public List<V> valuesFromAllLists()
	{
		final List<V> lAllValuesList = new ArrayList<V>();
		final Collection<List<V>> lValues = super.values();
		for (final List<V> lList : lValues)
			lAllValuesList.addAll(lList);
		return lAllValuesList;
	}

	public List<V> put(final K pKey)
	{
		List<V> lValueList = get(pKey);
		if (lValueList == null)
		{
			lValueList = new ArrayList<V>();
			super.put(pKey, lValueList);
		}
		return lValueList;
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

	public List<V> putAll(final K pKey, final Collection<V> pVCollection)
	{
		List<V> lValueList = get(pKey);
		if (lValueList == null)
		{
			lValueList = new ArrayList<V>(pVCollection);
			super.put(pKey, lValueList);
		}
		else
			lValueList.addAll(pVCollection);

		return lValueList;
	}

	public void addAll(	final Collection<K> pKCollection,
											final Collection<V> pVCollection)
	{
		for (final K lK : pKCollection)
			this.putAll(lK, pVCollection);
	}

	public void addAll(final ListMap<K, V> pListMap)
	{
		for (final Entry<K, List<V>> lEntry : pListMap.entrySet())
		{
			final List<V> lListSet = get(lEntry.getKey());
			if (lListSet == null)
				super.put(lEntry.getKey(), new ArrayList<V>(lEntry.getValue()));
			else
				lListSet.addAll(lEntry.getValue());
		}

	}

	public void clear(final K pKey)
	{
		final List<V> lValueSet = get(pKey);
		if (lValueSet != null)
			lValueSet.clear();
	}

}
