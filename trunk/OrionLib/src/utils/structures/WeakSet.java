package utils.structures;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;

public class WeakSet<K> implements Set<K>
{

	WeakHashMap<K, Object> mWeakHashMap = new WeakHashMap<K, Object>();

	public int size()
	{
		return this.mWeakHashMap.size();
	}

	public boolean isEmpty()
	{
		return this.mWeakHashMap.isEmpty();
	}

	public boolean contains(final Object o)
	{
		return this.mWeakHashMap.containsKey(o);
	}

	public Iterator<K> iterator()
	{
		return this.mWeakHashMap.keySet().iterator();
	}

	public boolean add(final K o)
	{
		this.mWeakHashMap.put(o, null);
		return true;
	}

	public Object[] toArray()
	{
		return this.mWeakHashMap.keySet().toArray();
	}

	public <T> T[] toArray(final T[] a)
	{
		return this.mWeakHashMap.keySet().toArray(a);
	}

	public boolean remove(final Object o)
	{
		this.mWeakHashMap.remove(o);
		return true;
	}

	public boolean containsAll(final Collection<?> c)
	{
		this.mWeakHashMap.keySet().containsAll(c);
		return true;
	}

	public boolean addAll(final Collection<? extends K> c)
	{
		for (final K k : c)
			this.mWeakHashMap.put(k, null);
		return true;
	}

	public boolean retainAll(final Collection<?> c)
	{
		for (final Entry<K, Object> lEntry : this.mWeakHashMap.entrySet())
		{
			final K lK = lEntry.getKey();
			if (!c.contains(lK))
				remove(lK);
		}
		return true;
	}

	public boolean removeAll(final Collection<?> c)
	{
		for (final Object k : c)
			this.mWeakHashMap.remove(k);
		return true;
	}

	public void clear()
	{
		this.mWeakHashMap.clear();
	}

}
