package utils.structures.fast.set;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;

public final class FastBoundedIntegerSet implements
																				RandomAccess,
																				java.io.Serializable,
																				Set<Integer>,
																				Iterable<Integer>
{
	private static final long serialVersionUID = 1L;

	private int cachedsize = 0;
	private boolean sizeOutOfDate = false;
	private int[] elements;
	private int min = Integer.MAX_VALUE;
	private int max = Integer.MIN_VALUE;

	public FastBoundedIntegerSet()
	{
		elements = new int[10];
	}

	public FastBoundedIntegerSet(final int capacity)
	{
		this();
		ensureCapacity(capacity / 32 + 1);
	}

	public FastBoundedIntegerSet(final boolean test, final int... ints)
	{
		this();
		ensureCapacity(ints.length / 32 + 1);
		for (final int i : ints)
		{
			add(i);
		}
	}

	public FastBoundedIntegerSet(	final int[] pElementData,
																final int pMin,
																final int pMax)
	{
		super();
		elements = pElementData;
		min = pMin;
		max = pMax;
	}

	public FastBoundedIntegerSet(final FastBoundedIntegerSet pFastSparseIntegerSet)
	{
		elements = Arrays.copyOf(	pFastSparseIntegerSet.elements,
															pFastSparseIntegerSet.elements.length);
		min = pFastSparseIntegerSet.min;
		max = pFastSparseIntegerSet.max;
	}

	public FastBoundedIntegerSet(final Collection<Integer> pCollection)
	{
		this(pCollection.size());

		for (final int i : pCollection)
		{
			this.add(i);
		}
	}

	public final void trimToSize()
	{
		if (elements.length > max)
		{
			elements = Arrays.copyOf(elements, max);
		}
	}

	private final void ensureCapacity(final int minCapacity)
	{
		final int oldCapacity = elements.length;
		if (minCapacity > oldCapacity)
		{
			int newCapacity = oldCapacity * 3 / 2 + 1;
			if (newCapacity < minCapacity)
			{
				newCapacity = minCapacity;
			}
			elements = Arrays.copyOf(elements, newCapacity);
		}
	}

	private void tightenMinMax()
	{
		{
			int newmin = Integer.MAX_VALUE;
			int newmax = Integer.MIN_VALUE;
			for (int i = min; i < max; i++)
			{
				if (elements[i] != 0)
				{
					newmin = i;
					break;
				}
			}
			for (int i = max - 1; i >= min; i--)
			{
				if (elements[i] != 0)
				{
					newmax = i + 1;
					break;
				}
			}
			min = newmin;
			max = newmax;
		}
		/***************************************************************************
		 * else { min = Integer.MAX_VALUE; max = Integer.MIN_VALUE; }/
		 **************************************************************************/
	}

	static int[] bitsInWord = new int[256 * 256];
	static
	{
		for (int i = 0; i < 256 * 256; i++)
		{
			bitsInWord[i] = slowbitcount(i);
		}
	}

	private static final int slowbitcount(int n)
	{
		int count = 0;
		while (n > 0)
		{
			count += n & 0x1;
			n >>= 1;
		}
		return count;
	}

	private static final int bitcount(final int n)
	{
		// works only for 32-bit ints
		return bitsInWord[n & 0xffff] + bitsInWord[n >> 16 & 0xffff];
	}

	public final int size()
	{
		if (sizeOutOfDate)
		{
			int size = 0;
			for (int i = min; i < max; i++)
			{
				size += bitcount(elements[i]);
			}
			cachedsize = size;
			sizeOutOfDate = false;
		}
		return cachedsize;
	}

	public final boolean isEmpty()
	{
		return max <= min;
	}

	public boolean add(final int o)
	{
		final int bitindex = o % 32;
		final int intindex = o >> 5;
		ensureCapacity(intindex + 1);
		final boolean isnotin = (elements[intindex] & 1 << bitindex) == 0;
		if (isnotin)
		{
			elements[intindex] |= 1 << bitindex;
			cachedsize++;
			min = min(min, intindex);
			max = max(max, intindex + 1);
			return true;
		}
		return false;
	}

	public boolean remove(final int o)
	{
		final int bitindex = o % 32;
		final int intindex = o >> 5;

		final boolean isin = (elements[intindex] & 1 << bitindex) != 0;
		if (isin)
		{
			ensureCapacity(intindex + 1);
			elements[intindex] ^= 1 << bitindex;
			cachedsize--;
			if (min == intindex && elements[intindex] == 0)
			{
				min++;
			}
			if (max == intindex + 1 && elements[intindex] == 0)
			{
				max--;
			}/**/
			return true;
		}
		return false;
	}

	public final boolean contains(final int o)
	{
		final int intindex = o >> 5;
		if(intindex>=elements.length)
			return false;
		final int bitindex = o % 32;


			
		return (elements[intindex] & 1 << bitindex) != 0;
	}

	public final boolean contains(final int... pArray)
	{
		for (final int val : pArray)
		{
			if (!contains(val))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Removes all of the elements from this set. The set will be empty after his
	 * call returns.
	 */
	public void clear()
	{
		cachedsize = 0;
		min = Integer.MAX_VALUE;
		max = Integer.MIN_VALUE;
		Arrays.fill(elements, 0);
	}

	/**
	 * Removes all of the elements from this list. The set will be empty after
	 * this call returns. Additionally to clear, this call releases the reference
	 * to the underlying array thus potentially freeing memory. (after garbage
	 * collection)
	 */
	public void wipe()
	{
		cachedsize = 0;
		min = Integer.MAX_VALUE;
		max = Integer.MIN_VALUE;
		elements = new int[10];
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		for (int i = min; i < max; i++)
		{
			result = prime * result + elements[i];
		}
		return result;
	}

	public boolean equals(final int... intarray)
	{
		if (size() != intarray.length)
		{
			return false;
		}
		for (final int i : intarray)
		{
			if (!contains(i))
			{
				return false;
			}
		}
		return true;
	}

	public boolean equals(final FastBoundedIntegerSet other)
	{
		if (max != other.max || min != other.min || size() != other.size())
		{
			return false;
		}

		{
			final int[] a1 = this.elements;
			final int[] a2 = other.elements;
			if (a1 == a2)
			{
				return true;
			}

			for (int i = min; i < max; i++)
			{
				if (a1[i] != a2[i])
				{
					return false;
				}
			}

			return true;
		}
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (this instanceof FastBoundedIntegerSet)
		{
			final FastBoundedIntegerSet other = (FastBoundedIntegerSet) obj;
			return equals(other);
		}
		else if (this instanceof Set)
		{
			final Set<Integer> other = (Set<Integer>) obj;
			return other.containsAll(this) && this.containsAll(other);
		}
		return false;
	}

	@Override
	public String toString()
	{
		if (size() == 0)
		{
			return "[] as Set";
		}
		else
		{
			final StringBuilder lStringBuilder = new StringBuilder();
			lStringBuilder.append("[");
			for (final int i : this)
			{
				lStringBuilder.append(i);
				lStringBuilder.append(", ");
			}
			lStringBuilder.setLength(lStringBuilder.length() - 2);
			lStringBuilder.append(']');
			lStringBuilder.append(" as Set");
			return lStringBuilder.toString();
		}
	}

	// Static Inclusion, Intersection, Union, Difference, Symetric Difference

	/**
	 * Computes whether this set contains another set.
	 */
	public final boolean contains(final FastBoundedIntegerSet set)
	{
		if (min <= set.min && set.max <= max)
		{
			for (int i = set.min; i < set.max; i++)
			{
				if ((~elements[i] & set.elements[i]) != 0)
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Computes whether this set intersects with another set.
	 */
	public final boolean intersects(final FastBoundedIntegerSet set)
	{
		if (!(min >= set.max || max <= set.min))
		{
			final int mininter = max(min, set.min);
			final int maxinter = min(max, set.max);

			for (int i = mininter; i < maxinter; i++)
			{
				if ((set.elements[i] & elements[i]) != 0)
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Computes the intersection between this set and another set.
	 */
	public final void intersection(final FastBoundedIntegerSet set)
	{
		if (!(min >= set.max || max <= set.min))
		{
			sizeOutOfDate = true;
			final int[] otherelements = set.elements;

			final int intermin = max(min, set.min);
			final int intermax = min(max, set.max);

			for (int i = min; i < intermin; i++)
			{
				elements[i] = 0;
			}
			for (int i = intermin; i < intermax; i++)
			{
				elements[i] &= otherelements[i];
			}
			for (int i = intermax; i < max; i++)
			{
				elements[i] = 0;
			}

			min = intermin;
			max = intermax;
			tightenMinMax();
		}
		else
		{
			clear();
		}
	}

	public static FastBoundedIntegerSet intersection(	final FastBoundedIntegerSet set1,
																										final FastBoundedIntegerSet set2)
	{
		final FastBoundedIntegerSet inter = new FastBoundedIntegerSet();

		if (!(set1.min >= set2.max || set1.max <= set2.min))
		{
			inter.ensureCapacity(min(set1.elements.length, set2.elements.length));
			final int[] elements1 = set1.elements;
			final int[] elements2 = set2.elements;
			final int[] elementsi = inter.elements;

			final int intermin = max(set1.min, set2.min);
			final int intermax = min(set1.max, set2.max);

			for (int i = intermin; i < intermax; i++)
			{
				elementsi[i] = elements1[i] & elements2[i];
			}

			inter.sizeOutOfDate = true;
			inter.min = intermin;
			inter.max = intermax;
			inter.tightenMinMax();
		}
		return inter;
	}

	/**
	 * Computes the union between this set and another set.
	 */
	public final void union(final FastBoundedIntegerSet set)
	{
		if (set.size() > 0)
		{
			sizeOutOfDate = true;
			final int[] otherelements = set.elements;
			ensureCapacity(set.max);

			for (int i = set.min; i < set.max; i++)
			{
				elements[i] |= otherelements[i];
			}

			min = min(min, set.min);
			max = max(max, set.max);
			tightenMinMax();
		}
	}

	public static FastBoundedIntegerSet union(final FastBoundedIntegerSet set1,
																						final FastBoundedIntegerSet set2)
	{
		final FastBoundedIntegerSet union = new FastBoundedIntegerSet();
		if (set1.size() > 0 || set2.size() > 0)
		{
			final int[] elements1 = set1.elements;
			final int[] elements2 = set2.elements;
			final int[] elementsu = union.elements;

			union.ensureCapacity(max(elements1.length, elements2.length));

			final int unionmin = min(set1.min, set2.min);
			final int unionmax = max(set1.max, set2.max);

			for (int i = unionmin; i < unionmax; i++)
			{
				elementsu[i] = elements1[i] | elements2[i];
			}

			union.sizeOutOfDate = true;
			union.min = unionmin;
			union.max = unionmax;
			union.tightenMinMax();
		}
		return union;
	}

	/**
	 * Computes the difference between this set and another set.
	 */
	public final void difference(final FastBoundedIntegerSet set)
	{
		sizeOutOfDate = true;
		final int[] otherelements = set.elements;

		for (int i = set.min; i < set.max; i++)
		{
			elements[i] &= ~otherelements[i];
		}

		tightenMinMax();
	}

	public static FastBoundedIntegerSet difference(	final FastBoundedIntegerSet set1,
																									final FastBoundedIntegerSet set2)
	{
		final FastBoundedIntegerSet diff = new FastBoundedIntegerSet();
		if (set1.size() > 0)
		{
			final int[] elements1 = set1.elements;
			final int[] elements2 = set2.elements;
			final int[] elementsd = diff.elements;

			diff.ensureCapacity(elements1.length);

			final int diffmin = set1.min;
			final int diffmax = set1.max;

			for (int i = diffmin; i < diffmax; i++)
			{
				elementsd[i] = elements1[i] & ~elements2[i];
			}

			diff.sizeOutOfDate = true;
			diff.min = diffmin;
			diff.max = diffmax;
			diff.tightenMinMax();
		}
		return diff;
	}

	/**
	 * Computes the relationship between set1 and set2: 0 if set1 is disjoint of
	 * set2, 1 if set1 constains set2, -1 if set2 contains set1, 2 if set1 and
	 * set2 are strictly intersecting, 3 if set1 and set2 are equal,
	 * 
	 * @param set1
	 * @param set2
	 * @return
	 */
	public static final int relationship(	final FastBoundedIntegerSet set1,
																				final FastBoundedIntegerSet set2)
	{

		if (set1.min >= set2.max || set1.max <= set2.min)
		{
			return 0; // set1 and set2 disjoint
		}
		else
		{
			final int[] elements1 = set1.elements;
			final int[] elements2 = set2.elements;

			boolean intersection = false;
			boolean set1alone = false;
			boolean set2alone = false;

			set1alone |= set1.min < set2.min || set2.max < set1.max;
			set2alone |= set2.min < set1.min || set1.max < set2.max;

			final int mininter = max(set1.min, set2.min);
			final int maxinter = min(set1.max, set2.max);

			{
				int i = mininter;
				while (!set1alone && i < maxinter)
				{
					set1alone |= (elements1[i] & ~elements2[i]) != 0;
					i++;
				}
			}

			{
				int i = mininter;
				while (!set2alone && i < maxinter)
				{
					set2alone |= (elements2[i] & ~elements1[i]) != 0;
					i++;
				}
			}

			{
				int i = mininter;
				while (!intersection && i < maxinter)
				{
					intersection |= (elements1[i] & elements2[i]) != 0;
					i++;
				}
			}

			if (intersection)
			{
				if (set1alone && set2alone)
				{
					return 2; // set1 and set2 strictly intersecting;
				}
				else if (set1alone)
				{
					return 1; // set1 contains set2
				}
				else if (set2alone)
				{
					return -1; // set2 contains set1
				}
				else
				{
					return 3; // equal
				}
			}
			else
			{
				return 0; // set1 and set2 disjoint
			}
		}
	}

	/**
	 * Computes the dot product of the two bit vectors equivalent to the two sets
	 * 
	 * @param set1
	 * @param set2
	 * @return
	 */
	public static final int dotproduct(	final FastBoundedIntegerSet set1,
																			final FastBoundedIntegerSet set2)
	{
		int dotproduct = 0;
		if (!(set1.min >= set2.max || set1.max <= set2.min))
		{
			final int[] elements1 = set1.elements;
			final int[] elements2 = set2.elements;

			final int intermin = max(set1.min, set2.min);
			final int intermax = min(set1.max, set2.max);

			for (int i = intermin; i < intermax; i++)
			{
				dotproduct += bitcount(elements1[i] & elements2[i]);
			}
		}
		return dotproduct;
	}

	public FastBoundedIntegerSet getRandomSubSet(	final Random pRandom,
																								final double pDensity)
	{
		final FastBoundedIntegerSet lFastSparseIntegerSet = new FastBoundedIntegerSet();
		for (final int val : elements)
		{
			if (pRandom.nextDouble() < pDensity)
			{
				lFastSparseIntegerSet.add(val);
			}
		}
		return lFastSparseIntegerSet;
	}

	public Integer getMin(final int pMin)
	{
		int i = max(min, pMin / 32);
		while (elements[i] == 0 && i < max)
		{
			i++;
		}

		int bit = pMin % 32;
		while ((elements[i] & 1 << bit) == 0 && bit < 32)
		{
			bit++;
		}

		if ((elements[i] & 1 << bit) != 0)
		{
			return 32 * i + bit;
		}
		else
		{
			return null;
		}
	}

	public Integer getMax(final int pMax)
	{
		int i = min(max, pMax / 32);
		while (elements[i] == 0 && i >= min)
		{
			i--;
		}

		int bit = pMax % 32;
		while ((elements[i] & 1 << bit) == 0 && bit >= 0)
		{
			bit--;
		}

		if ((elements[i] & 1 << bit) != 0)
		{
			return 32 * i + bit;
		}
		else
		{
			return null;
		}
	}

	// Special static methods:

	private static final int max(final int a, final int b)
	{
		return a >= b ? a : b;
	}

	private static final int min(final int a, final int b)
	{
		return a <= b ? a : b;
	}

	// *************************************************************
	// Methods implementing interfaces

	public boolean add(final Integer pE)
	{
		return add((int) pE);
	}

	public boolean addAll(final Collection<? extends Integer> c)
	{
		boolean haschanged = false;
		for (final Integer i : c)
		{
			haschanged |= add(i);
		}
		return haschanged;
	}

	public boolean contains(final Object pO)
	{
		return contains((int) (Integer) pO);
	}

	public boolean containsAll(final Collection<?> pC)
	{
		if (pC instanceof FastBoundedIntegerSet)
		{
			final FastBoundedIntegerSet other = (FastBoundedIntegerSet) pC;
			return this.contains(other);
		}
		else
		{

			for (final Object element : pC)
			{
				if (!contains(element))
				{
					return false;
				}
			}
			return true;
		}
	}

	public Iterator<Integer> iterator()
	{
		final FastBoundedIntegerSet thisisthis = this;
		final Iterator<Integer> lIterator = new Iterator<Integer>()
		{
			int currentint = min == Integer.MAX_VALUE ? -1 : 32 * min;

			public boolean hasNext()
			{
				if (currentint < max << 5 && currentint >= 0)
				{
					if ((elements[currentint >> 5] & 1 << currentint % 32) != 0)
					{
						return true;
					}
					else
					{
						boolean iszero;
						do
						{
							iszero = elements[currentint >> 5] == 0;
							if (iszero)
							{
								currentint += 32;
							}
							else
							{
								iszero = (elements[currentint >> 5] & 1 << currentint % 32) == 0;
								if (iszero)
								{
									currentint++;
								}
							}
							if (currentint >= max << 5)
							{
								return false;
							}
						}
						while (iszero);
						return true;
					}
				}
				return false;
			}

			public Integer next()
			{
				final int i = currentint;
				if (hasNext())
				{
					currentint++;
					return i;
				}
				else
				{
					throw new NoSuchElementException();
				}
			}

			public void remove()
			{
				thisisthis.remove(currentint);
				// throw new UnsupportedOperationException("Cannot remove");
			}
		};
		return lIterator;
	}

	public boolean remove(final Object pO)
	{
		return remove((int) (Integer) pO);
	}

	public boolean removeAll(final Collection<?> pC)
	{
		if (pC instanceof FastBoundedIntegerSet)
		{
			final FastBoundedIntegerSet other = (FastBoundedIntegerSet) pC;
			this.difference(other);
			return true; // might be a problem, supposed to know if something
			// changed!!
		}
		else
		{
			boolean haschanged = false;
			for (final Object element : pC)
			{
				haschanged |= remove((int) (Integer) element);
			}
			return haschanged;
		}
	}

	public boolean retainAll(final Collection<?> pC)
	{
		if (pC instanceof FastBoundedIntegerSet)
		{
			final FastBoundedIntegerSet other = (FastBoundedIntegerSet) pC;
			this.intersection(other);
			return true; // might be a problem, supposed to know if something
			// changed!!
		}
		else
		{
			final boolean haschanged = false;
			for (final int element : this)
			{
				if (!pC.contains(element))
				{
					remove(element);
				}
			}
			return haschanged;
		}
	}

	public Object[] toArray()
	{
		final Integer[] lArray = new Integer[size()];
		int i = 0;
		for (final int element : this)
		{
			lArray[i] = element;
			i++;
		}
		return lArray;
	}

	public <T> T[] toArray(final T[] pA)
	{
		throw new UnsupportedOperationException("unsupported, use: Object[] toArray()");
	}

	public int[] toIntArray()
	{
		final int[] lArray = new int[size()];
		int i = 0;
		for (final int element : this)
		{
			lArray[i] = element;
			i++;
		}
		return lArray;
	}

	public static int[] copyOf(final int[] original, final int newLength)
	{
		final int[] copy = new int[newLength];
		System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
		return copy;
	}

}
