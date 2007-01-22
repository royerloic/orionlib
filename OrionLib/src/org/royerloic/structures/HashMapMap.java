package org.royerloic.structures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.royerloic.structures.MapMap.Entry;

public class HashMapMap<K1, K2, V> extends HashMap<K1, Map<K2, V>> implements MapMap<K1, K2, V>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.MapMap#put(java.lang.Object,
	 *      java.lang.Object, java.lang.Object)
	 */
	public V put(K1 pKey1, K2 pKey2, V pValue)
	{
		Map<K2, V> lMap = get(pKey1);
		if (lMap == null)
		{
			lMap = new HashMap<K2, V>();
			put(pKey1, lMap);
		}
		return lMap.put(pKey2, pValue);
	}

	public void putAll(MapMap<K1, K2, V> pMapMap)
	{
		for (Map.Entry<K1, Map<K2, V>> lEntry : pMapMap.entrySet())
		{
			for (Map.Entry<K2, V> lEntry2 : lEntry.getValue().entrySet())
			{
				put(lEntry.getKey(), lEntry2.getKey(), lEntry2.getValue());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.MapMap#get(java.lang.Object,
	 *      java.lang.Object)
	 */
	public V get(K1 pKey1, K2 pKey2)
	{
		return get(pKey1).get(pKey2);
	}

	public static class HashMapMapEntry<K1, K2, V> implements Entry<K1, K2, V>
	{
		private final K1	mKey1;
		private final K2	mKey2;
		private final V		mValue;

		public HashMapMapEntry(K1 pKey1, K2 pKey2, V pValue)
		{
			mKey1 = pKey1;
			mKey2 = pKey2;
			mValue = pValue;
		}

		public K1 getKey1()
		{
			return mKey1;
		}

		public K2 getKey2()
		{
			return mKey2;
		}

		public V getValue()
		{
			return mValue;
		}

	}

	public Set<Entry<K1, K2, V>> allKeyEntrySet()
	{
		Set<Entry<K1, K2, V>> lEntrySet = new HashSet<Entry<K1, K2, V>>();
		for (Map.Entry<K1, Map<K2, V>> lEntry : entrySet())
		{
			for (Map.Entry<K2, V> lEntry2 : lEntry.getValue().entrySet())
			{
				Entry<K1, K2, V> lAllKeyEntry = new HashMapMapEntry<K1, K2, V>(lEntry.getKey(), lEntry2.getKey(),
						lEntry2.getValue());
				lEntrySet.add(lAllKeyEntry);
			}
		}
		return lEntrySet;
	}

}
