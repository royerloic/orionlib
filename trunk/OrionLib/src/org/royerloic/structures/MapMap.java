package org.royerloic.structures;

import java.util.Map;
import java.util.Set;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 * @param <K1>
 * @param <K2>
 * @param <V>
 */
public interface MapMap<K1, K2, V> extends Map<K1, Map<K2, V>>
{

	/**
	 * @param pKey1
	 * @param pKey2
	 * @param pValue
	 * @return
	 */
	public V put(K1 pKey1, K2 pKey2, V pValue);

	/**
	 * @param pMapMap
	 */
	public void putAll(MapMap<K1, K2, V> pMapMap);

	/**
	 * @param pKey1
	 * @param pKey2
	 * @return
	 */
	public V get(K1 pKey1, K2 pKey2);

	public static interface Entry<K1, K2, V>
	{
		public K1 getKey1();

		public K2 getKey2();

		public V getValue();
	}

	public Set<Entry<K1, K2, V>> allKeyEntrySet();

}
