package utils.structures.map;

import java.util.Map;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 * @param <K>
 * @param <V>
 */
public interface DoubleMap<K> extends Map<K, Double>
{

	/**
	 * @param pKey
	 * @param pValue
	 * @return
	 */
	public Double add(K pKey, Double pValue);

	/**
	 * @param pKey
	 * @param pValue
	 * @return
	 */
	public Double sub(K pKey, Double pValue);

	/**
	 * @param pKey
	 * @param pValue
	 * @return
	 */
	public Double mult(K pKey, Double pValue);

	/**
	 * @param pKey
	 * @param pValue
	 * @return
	 */
	public Double div(K pKey, Double pValue);

	/**
	 * @param pKey
	 * @param pValue
	 * @return
	 */
	public Double min(K pKey, Double pValue);

	/**
	 * @param pKey
	 * @param pValue
	 * @return
	 */
	public Double max(K pKey, Double pValue);
}
