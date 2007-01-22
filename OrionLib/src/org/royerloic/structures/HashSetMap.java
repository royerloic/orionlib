package org.royerloic.structures;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class HashSetMap<K, V> extends HashMap<K, Set<V>> implements SetMap<K, V>
{

	/**
	 * @return
	 */
	public Set<V> valuesFromAllSets()
	{
		Set<V> lAllValuesSet = new HashSet<V>();
		Collection<Set<V>> lValues = super.values();
		for (Set<V> lSet : lValues)
		{
			lAllValuesSet.addAll(lSet);
		}
		return lAllValuesSet;
	}

	public Set<V> put(K pKey)
	{
		Set<V> lValueSet = new HashSet<V>();
		put(pKey, lValueSet);
		return lValueSet;
	}

	/**
	 * @param pKey
	 * @param pValue
	 * @return
	 */
	public Set<V> put(K pKey, V pValue)
	{
		Set<V> lValueSet = get(pKey);
		if (lValueSet == null)
		{
			lValueSet = new HashSet<V>();
			put(pKey, lValueSet);
		}
		lValueSet.add(pValue);
		return lValueSet;
	}

	public Set<V> put(K pKey, Set<V> pVSet)
	{
		Set<V> lValueSet = get(pKey);
		if (lValueSet == null)
		{
			lValueSet = new HashSet<V>(pVSet);
			super.put(pKey, lValueSet);
		}
		else
			lValueSet.addAll(pVSet);

		return lValueSet;
	}

	public void clear(K pKey)
	{
		Set<V> lValueSet = get(pKey);
		if (lValueSet != null)
			lValueSet.clear();
	}

}
