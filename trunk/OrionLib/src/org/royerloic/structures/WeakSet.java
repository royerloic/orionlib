package org.royerloic.structures;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;

public class WeakSet<K> implements Set<K>
{

	WeakHashMap<K, Object>	mWeakHashMap	= new WeakHashMap<K, Object>();

	public int size()
	{
		return mWeakHashMap.size();
	}

	public boolean isEmpty()
	{
		return mWeakHashMap.isEmpty();
	}

	public boolean contains(Object o)
	{
		return mWeakHashMap.containsKey(o);
	}

	public Iterator iterator()
	{
		return mWeakHashMap.keySet().iterator();
	}

	public boolean add(K o)
	{
		mWeakHashMap.put(o, null);
		return true;
	}

	public Object[] toArray()
	{
		return mWeakHashMap.keySet().toArray();
	}

	public <T> T[] toArray(T[] a)
	{
		return mWeakHashMap.keySet().toArray(a);
	}

	public boolean remove(Object o)
	{
		mWeakHashMap.remove(o);
		return true;
	}

	public boolean containsAll(Collection<?> c)
	{
		mWeakHashMap.keySet().containsAll(c);
		return true;
	}

	public boolean addAll(Collection<? extends K> c)
	{
		for (K k : c)
		{
			mWeakHashMap.put(k, null);
		}
		return true;
	}

	public boolean retainAll(Collection<?> c)
	{
		for (Entry<K, Object> lEntry : mWeakHashMap.entrySet())
		{
			K lK = lEntry.getKey();
			if (!c.contains(lK))
			{
				remove(lK);
			}
		}
		return true;
	}

	public boolean removeAll(Collection<?> c)
	{
		for (Object k : c)
		{
			mWeakHashMap.remove(k);
		}
		return true;
	}

	public void clear()
	{
		mWeakHashMap.clear();
	}

}
