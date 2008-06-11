package utils.structures.map;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 * @param <K>
 * @param <V>
 */
public class BijectiveBidiHashMap<K, V> extends HashMap<K, V>	implements
																															Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3601651509551672409L;

	HashMap<V, K> mReverseHashMap = new HashMap<V, K>();

	@Override
	public void clear()
	{
		mReverseHashMap.clear();
		super.clear();
	}

	@Override
	public Object clone()
	{
		final BijectiveBidiHashMap<K, V> lMap = new BijectiveBidiHashMap<K, V>();
		lMap.putAll(this);
		return lMap;
	}

	@Override
	public boolean containsValue(final Object pValue)
	{
		return mReverseHashMap.containsKey(pValue);
	}

	public K getReverse(final V pValue)
	{
		return mReverseHashMap.get(pValue);
	}

	@Override
	public V put(final K pKey, final V pValue)
	{
		if (mReverseHashMap.containsValue(pValue))
		{
			super.remove(pKey);
		}
		mReverseHashMap.put(pValue, pKey);
		return super.put(pKey, pValue);
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> pM)
	{
		for (final Map.Entry<? extends K, ? extends V> lEntry : pM.entrySet())
		{
			this.put(lEntry.getKey(), lEntry.getValue());
		}
	}

	@Override
	public V remove(final Object pKey)
	{
		mReverseHashMap.remove(super.get(pKey));
		return super.remove(pKey);
	}

	@Override
	public Collection<V> values()
	{
		return mReverseHashMap.keySet();
	}
}
