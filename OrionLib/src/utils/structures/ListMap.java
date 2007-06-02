package utils.structures;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 * @param <K>
 * @param <V>
 */
public interface ListMap<K, V> extends Map<K, List<V>>
{

	public Set<V> valuesFromAllLists();

	public List<V> put(K pKey, V pValue);

}
