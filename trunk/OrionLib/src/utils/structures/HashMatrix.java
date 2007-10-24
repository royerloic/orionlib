package utils.structures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HashMatrix<V> extends HashMapMap<Integer, Integer, V> 
{

	public void put(final int pX, final int pY, final V pV)
	{
		super.put(pX,pY,pV);
	}
	
	public V get(final int pX, final int pY)
	{
		return super.get(pX,pY);
	}

}
