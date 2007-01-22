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
public interface Map3<K1, K2, K3, V> extends Map<K1, Map<K2, Map<K3, V>>>
{

	/**
	 * @param pMapMap
	 */
	public void putAll(Map3<K1, K2, K3, V> pMapMap);

	/**
	 * @param pKey1
	 * @param pKey2
	 * @param pValue
	 * @return
	 */
	public V put(K1 pKey1, K2 pKey2, K3 pKey3, V pValue);

	/**
	 * @param pKey1
	 * @param pKey2
	 * @return
	 */
	public V get(K1 pKey1, K2 pKey2, K2 pKey3);

	public static interface Entry<K1, K2, K3, V>
	{
		public K1 getKey1();

		public K2 getKey2();

		public K3 getKey3();

		public V getValue();
	}

	public Set<Entry<K1, K2, K3, V>> allKeyEntrySet();

}
