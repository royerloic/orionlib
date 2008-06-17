package utils.structures.fast.map;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import utils.structures.fast.set.FastSparseIntegerSet;

public class FastIntegerHashMap<O> implements Iterable<Integer>
{
	int capacity;
	Object[] elements;
	boolean[] isdirect;
	int[] keycache;

	FastSparseIntegerSet keyset;

	static final Random sRandom = new Random();
	int hashseed = Math.abs(sRandom.nextInt());

	public FastIntegerHashMap()
	{
		super();
		initialize(10);
	}

	public FastIntegerHashMap(final int pCapacity)
	{
		super();
		initialize(pCapacity);
	}

	private FastIntegerHashMap(final int pCapacity, final int pHashSeed)
	{
		super();
		initialize(pCapacity);
		hashseed = Math.abs(pHashSeed);
	}

	void initialize(final int pCapacity)
	{
		capacity = 2;
		while (capacity < pCapacity)
		{
			capacity *= 2;
		}
		keyset = new FastSparseIntegerSet();
		keyset.ensureCapacity(capacity);
		elements = new Object[capacity];
		isdirect = new boolean[capacity];
		Arrays.fill(isdirect, true);
		keycache = new int[capacity];
	}

	public O put(final int key, final O obj)
	{
		keyset.add(key);
		final int hash = hash(key);

		if (elements[hash] == null)
		{
			elements[hash] = obj;
			keycache[hash] = key;
			isdirect[hash] = true;
			return null;
		}
		else if (isdirect[hash])
		{
			if (keycache[hash] == key)
			{
				final O oldvalue = (O) elements[hash];
				elements[hash] = obj;
				keycache[hash] = key;
				return oldvalue;
			}
			else
			{
				// collision, need to replace direct access withr recursive map.
				int newhashseed = hashseed;
				while (newhashseed == hashseed)
				{
					newhashseed += Math.abs(sRandom.nextInt());
				}
				final FastIntegerHashMap lFastIntegerHashMap = new FastIntegerHashMap(capacity / 2,
																																							newhashseed);
				isdirect[hash] = false;
				lFastIntegerHashMap.put(keycache[hash], elements[hash]);
				elements[hash] = lFastIntegerHashMap;
				return (O) lFastIntegerHashMap.put(key, obj);
			}
		}
		else
		{
			// recursive put
			return (O) ((FastIntegerHashMap) elements[hash]).put(key, obj);
		}
	}

	public O get(final int key)
	{
		final int hash = hash(key);

		if (elements[hash] == null)
		{
			return null;
		}
		else if (isdirect[hash])
		{
			if (keycache[hash] == key)
			{
				return (O) elements[hash];
			}
			else
			{
				return null;
			}
		}
		else
		{
			return (O) ((FastIntegerHashMap) elements[hash]).get(key);
		}
	}

	public O remove(final int key)
	{
		keyset.remove(key);
		final int hash = hash(key);

		if (elements[hash] == null)
		{
			return null;
		}
		else if (isdirect[hash])
		{
			if (keycache[hash] == key)
			{
				final O oldvalue = (O) elements[hash];
				elements[hash] = null;
				return oldvalue;
			}
			else
			{
				// key is not in map, do nothing
				return null;
			}
		}
		else
		{
			// recursive remove
			return (O) ((FastIntegerHashMap) elements[hash]).remove(key);
		}
	}

	public int size()
	{
		return keyset.size();
	}

	private final int hash(int h)
	{
		h += hashseed;
		h *= hashseed;
		h ^= h >> 20 ^ h >> 12;
		h ^= h >> 7 ^ h >> 4;
		return h & capacity - 1;
	}

	public Iterator<Integer> iterator()
	{
		return keyset.iterator();
	}

}
