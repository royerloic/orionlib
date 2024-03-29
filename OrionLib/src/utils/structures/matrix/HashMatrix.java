package utils.structures.matrix;

import utils.structures.map.HashMapMap;

public class HashMatrix<V> extends HashMapMap<Integer, Integer, V>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void put(final int pX, final int pY, final V pV)
	{
		super.put(pX, pY, pV);
	}

	public V get(final int pX, final int pY)
	{
		return super.get(pX, pY);
	}

}
