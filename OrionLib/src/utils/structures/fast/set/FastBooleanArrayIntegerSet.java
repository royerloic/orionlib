package utils.structures.fast.set;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;

import utils.utils.Arrays;

public final class FastBooleanArrayIntegerSet	implements
																							FastIntegerSet,
																							RandomAccess,
																							java.io.Serializable

{
	private static final long serialVersionUID = 1L;

	private int cachedsize = 0;
	private boolean sizeOutOfDate = false;
	private boolean[] elements;
	private int min = Integer.MAX_VALUE;
	private int max = 0;

	public FastBooleanArrayIntegerSet()
	{
		elements = new boolean[10];
	}

	public FastBooleanArrayIntegerSet(final int capacity)
	{
		this();
		ensureCapacity(capacity);
	}

	public FastBooleanArrayIntegerSet(final boolean test, final int... ints)
	{
		this();
		ensureCapacity(ints.length);
		for (final int i : ints)
		{
			add(i);
		}
	}

	public FastBooleanArrayIntegerSet(final boolean[] pElementData,
																		final int pMin,
																		final int pMax)
	{
		super();
		elements = pElementData;
		min = pMin;
		max = pMax;
	}

	public FastBooleanArrayIntegerSet(final FastBooleanArrayIntegerSet pFastSparseIntegerSet)
	{
		elements = Arrays.copyOf(	pFastSparseIntegerSet.elements,
															pFastSparseIntegerSet.elements.length);
		min = pFastSparseIntegerSet.min;
		max = pFastSparseIntegerSet.max;
		sizeOutOfDate = true;
	}

	public FastBooleanArrayIntegerSet(final Collection<Integer> pCollection)
	{
		this(pCollection.size());

		for (final int i : pCollection)
		{
			this.add(i);
		}
	}

	public boolean[] getUnderlyingBooleanArray()
	{
		return elements;
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
		if (max <= min) // if(isEmpty())
		{
			min = Integer.MAX_VALUE;
			max = 0;
		}
		else
		{
			int newmin = Integer.MAX_VALUE;
			int newmax = 0;
			for (int i = min; i < max; i++)
			{
				if (elements[i])
				{
					newmin = i;
					break;
				}
			}
			for (int i = max - 1; i >= min; i--)
			{
				if (elements[i])
				{
					newmax = i + 1;
					break;
				}
			}
			min = newmin;
			max = newmax;
		}

	}

	public final int size()
	{
		if (sizeOutOfDate)
		{
			int size = 0;
			for (int i = min; i < max; i++)
				if (elements[i])
				{
					size++;
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
		ensureCapacity(o + 1);
		if (elements[o])
		{
			return false;
		}
		else
		{
			elements[o] = true;
			cachedsize++;
			min = min(min, o);
			max = max(max, o + 1);
			return true;
		}
	}

	public void addAll(final int[] pIntArray)
	{
		for (int i : pIntArray)
		{
			add(i);
		}
	}

	public boolean remove(final int o)
	{
		if (o < min || o >= max)
			return false;

		if (elements[o])
		{
			elements[o] = false;
			cachedsize--;
			tightenMinMax();
			return true;
		}
		else
		{
			return false;
		}
	}

	public void removeAll(int[] pIntArray)
	{
		for (int i : pIntArray)
		{
			remove(i);
		}
	}

	public void toggle(final int o)
	{
		if (contains(o))
		{
			remove(o);
		}
		else
		{
			add(o);
		}
	}

	public void toggleAll(final int[] pIntArray)
	{
		for (int i : pIntArray)
		{
			toggle(i);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.structures.fast.set.Test#contains(int)
	 */
	public final boolean contains(final int o)
	{
		if (o < min || o >= max)
			return false;
		return elements[o];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.structures.fast.set.Test#contains(int)
	 */
	public final boolean containsAll(final int... pArray)
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

	public void clear()
	{
		cachedsize = 0;
		sizeOutOfDate = false;
		min = Integer.MAX_VALUE;
		max = 0;
		Arrays.fill(elements, false);
	}

	public void fill(int pSize, boolean pB)
	{
		clear();
		min = 0;
		max = pSize;
		cachedsize = pSize;
		sizeOutOfDate = false;
		for (int i = 0; i < max; i++)
		{
			elements[i] = true;
		}
	}

	public void invert()
	{
		for (int i = 0; i < elements.length; i++)
		{
			elements[i] ^= true;
		}
		sizeOutOfDate = true;
	}

	public void wipe()
	{
		cachedsize = 0;
		sizeOutOfDate = false;
		min = Integer.MAX_VALUE;
		max = 0;
		elements = new boolean[10];
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		for (int i = min; i < max; i++)
		{
			result = prime * result + (elements[i] ? 13 : 111);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.structures.fast.set.Test#equals(int)
	 */
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

	public boolean equals(final FastBooleanArrayIntegerSet other)
	{
		if (max != other.max || min != other.min || size() != other.size())
		{
			return false;
		}

		{
			final boolean[] a1 = this.elements;
			final boolean[] a2 = other.elements;
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
		if (this instanceof FastBooleanArrayIntegerSet)
		{
			final FastBooleanArrayIntegerSet other = (FastBooleanArrayIntegerSet) obj;
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
			int i = 0;
			for (final boolean isint : elements)
			{
				if (isint)
				{
					lStringBuilder.append(i);
					lStringBuilder.append(", ");
				}
				i++;
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
	public final boolean contains(final FastBooleanArrayIntegerSet set)
	{
		if (min <= set.min && set.max <= max)
		{
			for (int i = set.min; i < set.max; i++)
			{
				if ((!elements[i] && set.elements[i]))
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
	public final boolean intersects(final FastBooleanArrayIntegerSet set)
	{
		if (!(min >= set.max || max <= set.min))
		{
			final int mininter = max(min, set.min);
			final int maxinter = min(max, set.max);

			for (int i = mininter; i < maxinter; i++)
			{
				if ((set.elements[i] && elements[i]))
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
	public final void intersection(final FastBooleanArrayIntegerSet set)
	{
		if (!(min >= set.max || max <= set.min))
		{
			sizeOutOfDate = true;
			final boolean[] otherelements = set.elements;

			final int intermin = max(min, set.min);
			final int intermax = min(max, set.max);

			for (int i = min; i < intermin; i++)
			{
				elements[i] = false;
			}
			for (int i = intermin; i < intermax; i++)
			{
				elements[i] &= otherelements[i];
			}
			for (int i = intermax; i < max; i++)
			{
				elements[i] = false;
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

	public static FastBooleanArrayIntegerSet intersection(final FastBooleanArrayIntegerSet set1,
																												final FastBooleanArrayIntegerSet set2)
	{
		final FastBooleanArrayIntegerSet inter = new FastBooleanArrayIntegerSet();

		if (!(set1.min >= set2.max || set1.max <= set2.min))
		{
			inter.ensureCapacity(min(set1.elements.length, set2.elements.length));
			final boolean[] elements1 = set1.elements;
			final boolean[] elements2 = set2.elements;
			final boolean[] elementsi = inter.elements;

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
	public final void union(final FastBooleanArrayIntegerSet set)
	{
		if (set.size() > 0)
		{
			sizeOutOfDate = true;
			final boolean[] otherelements = set.elements;
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

	public static FastBooleanArrayIntegerSet union(	final FastBooleanArrayIntegerSet set1,
																									final FastBooleanArrayIntegerSet set2)
	{
		final FastBooleanArrayIntegerSet union = new FastBooleanArrayIntegerSet();
		if (set1.size() > 0 || set2.size() > 0)
		{
			final boolean[] elements1 = set1.elements;
			final boolean[] elements2 = set2.elements;

			union.ensureCapacity(max(elements1.length, elements2.length));
			final boolean[] elementsu = union.elements;

			final int unionmin = min(set1.min, set2.min);
			final int unionmax = max(set1.max, set2.max);
			union.sizeOutOfDate = true;
			union.min = unionmin;
			union.max = unionmax;

			for (int i = set1.min; i < set1.max; i++)
			{
				elementsu[i] |= elements1[i];
			}

			for (int i = set2.min; i < set2.max; i++)
			{
				elementsu[i] |= elements2[i];
			}

			union.tightenMinMax();
		}
		return union;
	}

	/**
	 * Computes the difference between this set and another set.
	 */
	public final void difference(final FastBooleanArrayIntegerSet set)
	{
		sizeOutOfDate = true;
		final boolean[] otherelements = set.elements;

		final int diffmin = max(min, set.min);
		final int diffmax = min(max, set.max);

		for (int i = diffmin; i < diffmax; i++)
		{
			elements[i] &= !otherelements[i];
		}

		tightenMinMax();
	}

	public static FastBooleanArrayIntegerSet difference(final FastBooleanArrayIntegerSet set1,
																											final FastBooleanArrayIntegerSet set2)
	{
		final FastBooleanArrayIntegerSet diff = new FastBooleanArrayIntegerSet();
		if (set1.size() > 0)
		{
			final boolean[] elements1 = set1.elements;
			final boolean[] elements2 = set2.elements;

			diff.ensureCapacity(elements1.length);
			final boolean[] elementsd = diff.elements;

			final int diffmin = set1.min;
			final int diffmax = set1.max;

			for (int i = set1.min; i < set1.max; i++)
			{
				elementsd[i] = elements1[i];
			}

			for (int i = set2.min; i < set2.max; i++)
			{
				elementsd[i] &= !elements2[i];
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
	public static final int relationship(	final FastBooleanArrayIntegerSet set1,
																				final FastBooleanArrayIntegerSet set2)
	{

		if (set1.min >= set2.max || set1.max <= set2.min)
		{
			return 0; // set1 and set2 disjoint
		}
		else
		{
			final boolean[] elements1 = set1.elements;
			final boolean[] elements2 = set2.elements;

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
					set1alone |= (elements1[i] && !elements2[i]);
					i++;
				}
			}

			{
				int i = mininter;
				while (!set2alone && i < maxinter)
				{
					set2alone |= (elements2[i] && !elements1[i]);
					i++;
				}
			}

			{
				int i = mininter;
				while (!intersection && i < maxinter)
				{
					intersection |= (elements1[i] && elements2[i]);
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
	public static final int dotproduct(	final FastBooleanArrayIntegerSet set1,
																			final FastBooleanArrayIntegerSet set2)
	{
		int dotproduct = 0;
		if (!(set1.min >= set2.max || set1.max <= set2.min))
		{
			final boolean[] elements1 = set1.elements;
			final boolean[] elements2 = set2.elements;

			final int intermin = max(set1.min, set2.min);
			final int intermax = min(set1.max, set2.max);

			for (int i = intermin; i < intermax; i++)
			{
				dotproduct += (elements1[i] && elements2[i]) ? 1 : 0;
			}
		}
		return dotproduct;
	}

	public FastBooleanArrayIntegerSet getRandomSubSet(final Random pRandom,
																										final double pDensity)
	{
		final FastBooleanArrayIntegerSet lFastBooleanArrayIntegerSet = new FastBooleanArrayIntegerSet();
		int i = 0;
		for (final boolean val : elements)
		{
			if (pRandom.nextDouble() < pDensity)
			{
				lFastBooleanArrayIntegerSet.add(i);
			}
			i++;
		}
		return lFastBooleanArrayIntegerSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.structures.fast.set.Test#getMin(int)
	 */
	public Integer getMin(final int pMin)
	{
		int i = max(min, pMin);
		while (!elements[i] && i < max)
		{
			i++;
		}
		return i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.structures.fast.set.Test#getMax(int)
	 */
	public Integer getMax(final int pMax)
	{
		int i = min(max, pMax);
		while (!elements[i] && i >= min)
		{
			i--;
		}
		return i;
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
		if (pC instanceof FastBooleanArrayIntegerSet)
		{
			final FastBooleanArrayIntegerSet other = (FastBooleanArrayIntegerSet) pC;
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
		final FastBooleanArrayIntegerSet thisisthis = this;
		final Iterator<Integer> lIterator = new Iterator<Integer>()
		{
			int currentint = min == Integer.MAX_VALUE ? -1 : min;

			public boolean hasNext()
			{
				if (currentint < max && currentint >= 0)
				{
					if (elements[currentint])
					{
						return true;
					}
					else
					{
						do
						{
							currentint++;
							if (currentint >= max)
								return false;
						}
						while (!elements[currentint]);
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
		if (pC instanceof FastBooleanArrayIntegerSet)
		{
			final FastBooleanArrayIntegerSet other = (FastBooleanArrayIntegerSet) pC;
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
		if (pC instanceof FastBooleanArrayIntegerSet)
		{
			final FastBooleanArrayIntegerSet other = (FastBooleanArrayIntegerSet) pC;
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
