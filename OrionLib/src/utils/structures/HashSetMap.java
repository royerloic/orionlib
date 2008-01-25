package utils.structures;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class HashSetMap<K, V> extends HashMap<K, Set<V>> implements
																												SetMap<K, V>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8584828916052243423L;

	/**
	 * @return
	 */
	public Set<V> valuesFromAllSets()
	{
		final Set<V> lAllValuesSet = new HashSet<V>();
		final Collection<Set<V>> lValues = super.values();
		for (final Set<V> lSet : lValues)
			lAllValuesSet.addAll(lSet);
		return lAllValuesSet;
	}

	public Set<V> put(final K pKey)
	{
		Set<V> lValueSet = get(pKey);
		if (lValueSet == null)
		{
			lValueSet = new HashSet<V>();
			super.put(pKey, lValueSet);
		}
		return lValueSet;
	}

	/**
	 * @param pKey
	 * @param pValue
	 * @return
	 */
	public Set<V> put(final K pKey, final V pValue)
	{
		Set<V> lValueSet = get(pKey);
		if (lValueSet == null)
		{
			lValueSet = new HashSet<V>();
			super.put(pKey, lValueSet);
		}
		lValueSet.add(pValue);
		return lValueSet;
	}

	public Set<V> putAll(final K pKey, final Collection<V> pVSet)
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

	public void addAll(final Collection<K> pKSet, final Collection<V> pVSet)
	{
		for (final K lK : pKSet)
			this.putAll(lK, pVSet);
	}

	public void addAll(final SetMap<K, V> pSetMap)
	{
		for (final Entry<K, Set<V>> lEntry : pSetMap.entrySet())
		{
			final Set<V> lSet = get(lEntry.getKey());
			if (lSet == null)
				super.put(lEntry.getKey(), new HashSet<V>(lEntry.getValue()));
			else
				lSet.addAll(lEntry.getValue());
		}

	}

	public void clear(final K pKey)
	{
		final Set<V> lValueSet = get(pKey);
		if (lValueSet != null)
			lValueSet.clear();
	}

}
