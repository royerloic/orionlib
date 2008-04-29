package utils.structures.fast.set;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.RandomAccess;

import utils.structures.fast.list.FastIntegerList;

public final class FastIntegerSet	implements
																	RandomAccess,
																	java.io.Serializable,
																	Collection<Integer>,
																	Iterable<Integer>
{
	private static final long serialVersionUID = 1L;

	private int[] elements;
	private int size = 0;

	public FastIntegerSet()
	{
		elements = new int[10];
	}

	public FastIntegerSet(final int[] pElementData, final int pSize)
	{
		super();
		elements = pElementData;
		size = pSize;
	}

	public FastIntegerSet(int... pElementData)
	{
		super();
		elements = Arrays.copyOf(pElementData, pElementData.length);
		Arrays.sort(elements);

		int writeindex = 0;
		int readindex = 0;
		final int length = elements.length;

		while (readindex < length)
			if (readindex == length - 1)
			{
				elements[writeindex] = elements[readindex];
				writeindex++;
				break;
			}
			else if ((elements[readindex] == elements[readindex + 1]))
			{
				readindex++;
				continue;
			}
			else
			{
				elements[writeindex] = elements[readindex];
				readindex++;
				writeindex++;
			}

		size = writeindex;
	}

	public FastIntegerSet(FastIntegerSet pFastIntegerSet)
	{
		size = pFastIntegerSet.size;
		elements = Arrays.copyOf(pFastIntegerSet.elements, size);
	}

	public final void trimToSize()
	{
		if (elements.length > size)
		{
			elements = Arrays.copyOf(elements, size);
		}
	}

	/**
	 * Increases the capacity of this <tt>ArrayList</tt> instance, if necessary,
	 * to ensure that it can hold at least the number of elements specified by the
	 * minimum capacity argument.
	 * 
	 * @param minCapacity
	 *          the desired minimum capacity
	 */
	public final void ensureCapacity(int minCapacity)
	{
		final int oldCapacity = elements.length;
		if (minCapacity > oldCapacity)
		{
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			elements = Arrays.copyOf(elements, newCapacity);
		}
	}

	public final int size()
	{
		return size;
	}

	public final boolean isEmpty()
	{
		return size == 0;
	}

	public final boolean contains(int o)
	{
		return Arrays.binarySearch(elements, 0, size, o) >= 0;
	}

	public final boolean contains(int... pArray)
	{
		for (int val : pArray)
			if (!contains(val))
				return false;
		return true;
	}

	public final boolean contains(FastIntegerSet pFastIntegerSet)
	{
		final int[] otherElements = pFastIntegerSet.elements;
		// we first check last element, might lead to early failure...
		if (!contains(otherElements[pFastIntegerSet.size - 1]))
			return false;
		// then we check first and all following
		for (int i = 0; i < pFastIntegerSet.size - 1; i++)
			if (!contains(otherElements[i]))
				return false;
		return true;
	}

	public boolean equals(int... pArray)
	{
		FastIntegerSet lFastIntegerSet = new FastIntegerSet(pArray);
		return lFastIntegerSet.equals(this);
	}

	/**
	 * Removes all of the elements from this set. The set will be empty after his
	 * call returns.
	 */
	public void clear()
	{
		size = 0;
	}

	/**
	 * Removes all of the elements from this list. The set will be empty after
	 * this call returns. Additionally to clear, this call releases the reference
	 * to the underlying array thus potentially freeing memory. (after garbage
	 * collection)
	 */
	public void wipe()
	{
		size = 0;
	}

	/**
	 * Checks if the given index is in range. If not, throws an appropriate
	 * runtime exception. This method does *not* check if the index is negative:
	 * It is always used immediately prior to an array access, which throws an
	 * ArrayIndexOutOfBoundsException if index is negative.
	 */
	private final void rangeCheck(int index)
	{
		if (index >= size || size < 0)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		for (int i = 0; i < size; i++)
			result = prime * result + elements[i];
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FastIntegerSet other = (FastIntegerSet) obj;

		if (size != other.size)
			return false;

		{
			final int[] a1 = this.elements;
			final int[] a2 = other.elements;
			if (a1 == a2)
				return true;
			if (a1 == null || a2 == null)
				return false;

			for (int i = 0; i < size; i++)
				if (a1[i] != a2[i])
					return false;

			return true;
		}
	}

	@Override
	public String toString()
	{
		if (size == 0)
		{
			return "[] as Set";
		}
		else
		{
			StringBuilder lStringBuilder = new StringBuilder();
			lStringBuilder.append("[");
			for (int i = 0; i < size; i++)
			{
				final int val = elements[i];
				lStringBuilder.append(val);
				lStringBuilder.append(",");
			}
			lStringBuilder.setCharAt(lStringBuilder.length() - 1, ']');
			lStringBuilder.append(" as Set");
			return lStringBuilder.toString();
		}
	}

	public boolean add(int o)
	{
		final int index = Arrays.binarySearch(elements, 0, size, o);

		if (index >= 0)
		{
			return false;
		}
		else
		{
			final int insertionindex = -index - 1;
			ensureCapacity(size + 1);
			System.arraycopy(	elements,
												insertionindex,
												elements,
												insertionindex + 1,
												size - insertionindex);
			elements[insertionindex] = o;
			size++;
			return true;
		}
	}

	public boolean del(int o)
	{
		final int index = Arrays.binarySearch(elements, 0, size, o);

		if (index < 0)
		{
			return false;
		}
		else
		{
			final int deletionindex = index;
			ensureCapacity(size);
			System.arraycopy(	elements,
												deletionindex + 1,
												elements,
												deletionindex,
												size - deletionindex - 1);
			size--;
			return true;
		}
	}

	// Static Intersection, Union, Difference, Symetric Difference

	/**
	 * Computes the intersection of two sets: set1 Inter set2
	 */
	public static final FastIntegerSet intersection(final FastIntegerSet set1,
																									final FastIntegerSet set2)
	{
		final int[] array1 = set1.elements;
		final int size1 = set1.size;
		final int[] array2 = set2.elements;
		final int size2 = set2.size;

		final int lMaximalSize = max(size1, size2);
		final int[] lNewArray = new int[lMaximalSize];

		int i = 0;
		int j = 0;
		int k = 0;

		while (i < size1 && j < size2)
		{
			final int lA = array1[i];
			final int lB = array2[j];

			if (lA == lB)
			{
				lNewArray[k] = lA;
				k++;
				i++;
				j++;
			}
			else if (lA > lB)
			{
				j++;
			}
			else if (lA < lB)
			{
				i++;
			}
		}

		FastIntegerSet lFastIntegerSet = new FastIntegerSet(lNewArray, k);

		return lFastIntegerSet;
	}

	/**
	 * Computes the union of two sets: set1 Union set2
	 */
	public static final FastIntegerSet union(	final FastIntegerSet set1,
																						final FastIntegerSet set2)
	{
		final int[] array1 = set1.elements;
		final int size1 = set1.size;
		final int[] array2 = set2.elements;
		final int size2 = set2.size;

		if (size1 == 0 && size2 != 0)
		{
			return new FastIntegerSet(set2);
		}
		if (size2 == 0 && size1 != 0)
		{
			return new FastIntegerSet(set1);
		}
		if (size2 == 0 && size1 == 0)
		{
			return new FastIntegerSet();
		}

		final int lMaximalSize = size1 + size2;
		final int[] lNewArray = new int[lMaximalSize];

		int i = 0;
		int j = 0;
		int k = 0;

		int lA = array1[i];
		int lB = array2[j];
		while (true)
		{

			if (i < size1)
			{
				lA = array1[i];

				if (j < size2)
				{
					lB = array2[j];
				}
				else
				{
					lB = Integer.MAX_VALUE;
				}
			}
			else
			{
				lA = Integer.MAX_VALUE;

				if (j < size2)
				{
					lB = array2[j];
				}
				else
				{
					break;
				}
			}

			if (lA == lB)
			{
				lNewArray[k] = lA;
				k++;
				i++;
				j++;
			}
			else if (lA > lB)
			{
				lNewArray[k] = lB;
				k++;
				j++;
			}
			else if (lA < lB)
			{
				lNewArray[k] = lA;
				k++;
				i++;
			}
		}

		return new FastIntegerSet(lNewArray, k);
	}

	/**
	 * Computes the difference set1 Minus set2 (not the symmetric one!!!)
	 */
	public static final FastIntegerSet difference(final FastIntegerSet set1,
																								final FastIntegerSet set2)
	{
		final int[] array1 = set1.elements;
		final int size1 = set1.size;
		final int[] array2 = set2.elements;
		final int size2 = set2.size;

		if (size1 == 0 && size2 != 0)
		{
			return new FastIntegerSet();
		}
		if (size1 != 0 && size2 == 0)
		{
			return new FastIntegerSet(set1);
		}
		if (size2 == 0 && size1 == 0)
		{
			return new FastIntegerSet();
		}

		final int lMaximalSize = size1;
		final int[] lNewArray = new int[lMaximalSize];

		int i = 0;
		int j = 0;
		int k = 0;
		int lA = array1[i];
		int lB = array2[j];
		while (true)
		{

			if (i < size1)
			{
				lA = array1[i];

				if (j < size2)
				{
					lB = array2[j];
				}
				else
				{
					lB = Integer.MAX_VALUE;
				}
			}
			else
			{
				lA = Integer.MAX_VALUE;

				if (j < size2)
				{
					lB = array2[j];
				}
				else
				{
					break;
				}
			}

			if (lA == lB)
			{
				i++;
				j++;
			}
			else if (lA > lB)
			{
				j++;
			}
			else if (lA < lB)
			{
				lNewArray[k] = lA;
				k++;
				i++;
			}
		}

		return new FastIntegerSet(lNewArray, k);
	}

	// Special methods:

	public int[] getUnderlyingArray()
	{
		trimToSize();
		return elements;
	}

	public FastIntegerList getList()
	{
		FastIntegerList lFastIntegerList = new FastIntegerList(elements, size);
		return lFastIntegerList;
	}

	public FastIntegerSet getRandomSubSet(Random pRandom, double pDensity)
	{
		FastIntegerSet lFastIntegerSet = new FastIntegerSet();
		for (int val : elements)
		{
			if (pRandom.nextDouble() < pDensity)
			{
				lFastIntegerSet.add(val);
			}
		}
		return lFastIntegerSet;
	}

	// Special static methods:

	private static final int max(int a, int b)
	{
		return (a >= b) ? a : b;
	}

	// *************************************************************
	// Methods implementing interfaces
	
	public boolean add(Integer pE)
	{
		return add((int) pE);
	}

	public boolean addAll(Collection<? extends Integer> c)
	{
		Integer[] a = (Integer[]) c.toArray();
		final int numNew = a.length;
		ensureCapacity(size + numNew); // Increments modCount
		System.arraycopy(a, 0, elements, size, numNew);
		for (int i = 0; i < numNew; i++)
			elements[i] = a[size + i];
		size += numNew;
		return numNew != 0;
	}

	public boolean contains(Object pO)
	{
		return contains((int) ((Integer) pO));
	}

	public boolean containsAll(Collection<?> pC)
	{
		for (Object element : pC)
			if (!contains(element))
				return false;
		return true;
	}

	public Iterator<Integer> iterator()
	{
		Iterator<Integer> lIterator = new Iterator<Integer>()
		{
			int mPosition = 0;

			public boolean hasNext()
			{
				return mPosition < size - 1;
			}

			public Integer next()
			{
				if (hasNext())
					mPosition++;
				return elements[mPosition];
			}

			public void remove()
			{
				throw new UnsupportedOperationException("Cannot remove");
			}
		};
		return lIterator;
	}

	public boolean remove(Object pO)
	{
		del((int) ((Integer) pO));
		return true;
	}

	public boolean removeAll(Collection<?> pC)
	{
		boolean haschanged = false;
		for (Object element : pC)
			haschanged |= del((int) ((Integer) element));
		return haschanged;
	}

	public boolean retainAll(Collection<?> pC)
	{
		boolean haschanged = false;
		for (int element : elements)
			if (!pC.contains(element))
				del(element);
		return haschanged;
	}

	public Object[] toArray()
	{
		Integer[] lArray = new Integer[size];
		for (int i = 0; i < size; i++)
		{
			lArray[i] = (Integer) elements[i];
		}
		return lArray;
	}

	public <T> T[] toArray(T[] pA)
	{
		throw new UnsupportedOperationException("unsupported, use: Object[] toArray()");
	}
}
