package org.royerloic.structures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.royerloic.structures.Map3.Entry;

public class HashMap3<K1, K2, K3, V> extends HashMap<K1, Map<K2, Map<K3, V>>> implements Map3<K1, K2, K3, V>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.MapMap#put(java.lang.Object,
	 *      java.lang.Object, java.lang.Object)
	 */
	public V put(K1 pKey1, K2 pKey2, K3 pKey3, V pValue)
	{
		Map<K2, Map<K3, V>> lMap1 = get(pKey1);
		if (lMap1 == null)
		{
			lMap1 = new HashMap<K2, Map<K3, V>>();
			put(pKey1, lMap1);
		}

		Map<K3, V> lMap2 = lMap1.get(pKey2);
		if (lMap2 == null)
		{
			lMap2 = new HashMap<K3, V>();
			lMap1.put(pKey2, lMap2);
		}

		return lMap2.put(pKey3, pValue);
	}

	public void putAll(Map3<K1, K2, K3, V> pMapMap)
	{
		for (Map.Entry<K1, Map<K2, Map<K3, V>>> lEntry : pMapMap.entrySet())
		{
			for (Map.Entry<K2, Map<K3, V>> lEntry2 : lEntry.getValue().entrySet())
			{
				for (Map.Entry<K3, V> lEntry3 : lEntry2.getValue().entrySet())
				{
					put(lEntry.getKey(), lEntry2.getKey(), lEntry3.getKey(), lEntry3.getValue());
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.MapMap#get(java.lang.Object,
	 *      java.lang.Object)
	 */
	public V get(K1 pKey1, K2 pKey2, K2 pKey3)
	{
		return get(pKey1).get(pKey2).get(pKey3);
	}

	public static class HashMap3Entry<K1, K2, K3, V> implements Entry<K1, K2, K3, V>
	{
		private final K1	mKey1;
		private final K2	mKey2;
		private final K3	mKey3;
		private final V		mValue;

		public HashMap3Entry(K1 pKey1, K2 pKey2, K3 pKey3, V pValue)
		{
			mKey1 = pKey1;
			mKey2 = pKey2;
			mKey3 = pKey3;
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

		public K3 getKey3()
		{
			return mKey3;
		}

		public V getValue()
		{
			return mValue;
		}

	}

	public Set<Entry<K1, K2, K3, V>> allKeyEntrySet()
	{
		Set<Entry<K1, K2, K3, V>> lEntrySet = new HashSet<Entry<K1, K2, K3, V>>();
		for (Map.Entry<K1, Map<K2, Map<K3, V>>> lEntry1 : entrySet())
		{
			for (Map.Entry<K2, Map<K3, V>> lEntry2 : lEntry1.getValue().entrySet())
			{
				for (Map.Entry<K3, V> lEntry3 : lEntry2.getValue().entrySet())
				{
					Entry<K1, K2, K3, V> lAllKeyEntry = new HashMap3Entry<K1, K2, K3, V>(lEntry1.getKey(), lEntry2
							.getKey(), lEntry3.getKey(), lEntry3.getValue());
					lEntrySet.add(lAllKeyEntry);
				}
			}
		}
		return lEntrySet;
	}

}
