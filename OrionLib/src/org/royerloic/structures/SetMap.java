package org.royerloic.structures;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 * @param <K>
 * @param <V>
 */
public interface SetMap<K, V> extends Map<K, Set<V>>
{

	public Set<V> valuesFromAllSets();

	public Set<V> put(K pKey);

	public Set<V> put(K pKey, V pValue);
	
	public Set<V> putAll(K pKey, Collection<V> pVSet);
	
	public void addAll(Collection<K> pKSet, Collection<V> pVSet);
	
	public void addAll(SetMap<K, V> pSetMap);

	public void clear(K pKey);

}
