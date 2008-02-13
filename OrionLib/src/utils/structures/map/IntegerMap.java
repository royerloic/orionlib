package utils.structures.map;

import java.util.Collection;
import java.util.Map;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 * @param <K>
 * @param <V>
 */
public interface IntegerMap<K> extends Map<K, Integer>
{

	/**
	 * @param pKey
	 * @param pValue
	 * @return
	 */
	public Integer add(K pKey, Integer pValue);

	/**
	 * @param pMap
	 * @return
	 */
	public IntegerMap<K> addAllWith(Collection<K> pCollection, Integer pValue);

	/**
	 * @param pKey
	 * @param pValue
	 * @return
	 */
	public Integer sub(K pKey, Integer pValue);

	/**
	 * @param pKey
	 * @param pValue
	 * @return
	 */
	public Integer mult(K pKey, Integer pValue);

	/**
	 * @param pKey
	 * @param pValue
	 * @return
	 */
	public Integer div(K pKey, Integer pValue);

	/**
	 * @param pKey
	 * @param pValue
	 * @return
	 */
	public Integer min(K pKey, Integer pValue);

	/**
	 * @param pKey
	 * @param pValue
	 * @return
	 */
	public Integer max(K pKey, Integer pValue);

	/**
	 * @param pMap
	 * @return
	 */
	public IntegerMap<K> minAll(Map<K, Integer> pMap);

	/**
	 * @param pMap
	 * @return
	 */
	public IntegerMap<K> maxAll(Map<K, Integer> pMap);
}
