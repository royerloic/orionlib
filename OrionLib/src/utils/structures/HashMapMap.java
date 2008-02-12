package utils.structures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HashMapMap<K1, K2, V> extends HashMap<K1, Map<K2, V>> implements
																																	MapMap<K1, K2, V>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3031690841057866903L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.collections.MapMap#put(java.lang.Object,
	 *      java.lang.Object, java.lang.Object)
	 */
	public V put(final K1 pKey1, final K2 pKey2, final V pValue)
	{
		Map<K2, V> lMap = get(pKey1);
		if (lMap == null)
		{
			lMap = new HashMap<K2, V>();
			put(pKey1, lMap);
		}
		return lMap.put(pKey2, pValue);
	}

	public void putAll(final MapMap<K1, K2, V> pMapMap)
	{
		for (final Map.Entry<K1, Map<K2, V>> lEntry : pMapMap.entrySet())
			for (final Map.Entry<K2, V> lEntry2 : lEntry.getValue().entrySet())
				put(lEntry.getKey(), lEntry2.getKey(), lEntry2.getValue());
	}

	public V get(final K1 pKey1, final K2 pKey2)
	{
		Map<K2, V> lMap = get(pKey1);
		if (lMap == null)
			return null;
		return lMap.get(pKey2);
	}

	public static class HashMapMapEntry<K1, K2, V> implements Entry<K1, K2, V>
	{
		private final K1 mKey1;
		private final K2 mKey2;
		private final V mValue;

		public HashMapMapEntry(final K1 pKey1, final K2 pKey2, final V pValue)
		{
			this.mKey1 = pKey1;
			this.mKey2 = pKey2;
			this.mValue = pValue;
		}

		public K1 getKey1()
		{
			return this.mKey1;
		}

		public K2 getKey2()
		{
			return this.mKey2;
		}

		public V getValue()
		{
			return this.mValue;
		}

	}

	public Set<Entry<K1, K2, V>> allKeyEntrySet()
	{
		final Set<Entry<K1, K2, V>> lEntrySet = new HashSet<Entry<K1, K2, V>>();
		for (final Map.Entry<K1, Map<K2, V>> lEntry : entrySet())
			for (final Map.Entry<K2, V> lEntry2 : lEntry.getValue().entrySet())
			{
				final Entry<K1, K2, V> lAllKeyEntry = new HashMapMapEntry<K1, K2, V>(	lEntry.getKey(),
																																							lEntry2.getKey(),
																																							lEntry2.getValue());
				lEntrySet.add(lAllKeyEntry);
			}
		return lEntrySet;
	}

}
