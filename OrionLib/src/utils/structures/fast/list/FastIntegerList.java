package utils.structures.fast.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.RandomAccess;

import utils.structures.fast.set.FastIntegerSet;

public class FastIntegerList implements
														RandomAccess,
														java.io.Serializable,
														Collection<Integer>
{
	private static final long serialVersionUID = 1L;

	private int[] elements;
	private int size;

	public FastIntegerList(int initialCapacity)
	{
		super();
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		this.elements = new int[initialCapacity];
	}

	public FastIntegerList()
	{
		this(10);
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
		int oldCapacity = elements.length;
		if (minCapacity > oldCapacity)
		{
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			// minCapacity is usually close to size, so this is a win:
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
		for (int i=0; i<size; i++)
			if (o == elements[i])
				return true;
		return false;
	}

	/**
	 * Returns the index of the first occurrence of the specified element in this
	 * list, or -1 if this list does not contain the element. More formally,
	 * returns the lowest index <tt>i</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 */
	public final int indexOf(int o)
	{
		for (int i=0; i<size; i++)
			if (o == elements[i])
				return i;
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified element in this
	 * list, or -1 if this list does not contain the element. More formally,
	 * returns the highest index <tt>i</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 */
	public final int lastIndexOf(int o)
	{
		{
			for (int i = size - 1; i >= 0; i--)
				if (o==elements[i])
					return i;
		}
		return -1;
	}

	// Positional Access Operations

	public final int get(int index)
	{
		rangeCheck(index);
		return elements[index];
	}

	public final void set(int index, int element)
	{
		rangeCheck(index);
		elements[index] = element;
	}

	public void add(int e)
	{
		ensureCapacity(size + 1); 
		elements[size++] = e;
	}

	public void add(int index, int element)
	{
		rangeCheck(index);

		ensureCapacity(size + 1); // Increments modCount!!
		System.arraycopy(elements, index, elements, index + 1, size - index);
		elements[index] = element;
		size++;
	}

	public void remove(int index)
	{
		rangeCheck(index);
		int numMoved = size - index - 1;
		if (numMoved > 0)
			System.arraycopy(elements, index + 1, elements, index, numMoved);
		size--;
	}

	public boolean removeValue(int o)
	{
		for (int index = 0; index < size; index++)
			if (o == elements[index])
			{
				remove(index);
				return true;
			}
		return false;
	}

	public boolean addAll(FastIntegerList pFastIntegerList)
	{
		ensureCapacity(size + pFastIntegerList.size);
		System.arraycopy(	pFastIntegerList.elements,
											0,
											elements,
											size,
											pFastIntegerList.size);
		size = size + pFastIntegerList.size;
		return pFastIntegerList.size > 0;
	}

	/**
	 * Removes all of the elements from this list. The list will be empty after
	 * this call returns.
	 */
	public void clear()
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
		if (index >= size || size<0)
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
		final FastIntegerList other = (FastIntegerList) obj;

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
		StringBuilder lStringBuilder = new StringBuilder();
		lStringBuilder.append("[");
		for (int i = 0; i < size; i++)
		{
			final int val = elements[i];
			lStringBuilder.append(val);
			lStringBuilder.append(",");
		}
		lStringBuilder.setCharAt(lStringBuilder.length() - 1, ']');
		return lStringBuilder.toString();
	}

	// Special methods:
	
	
	int[] getUnderlyingArray()
	{
		trimToSize();
		return elements;	
	}
	
	FastIntegerSet getSet()
	{
		trimToSize();
		FastIntegerSet lFastIntegerSet = new FastIntegerSet(elements);
		return lFastIntegerSet;
	}	
	
	
	
	// *************************************************************
	// Methods implementing interface Collection<Integer>

	public boolean add(Integer pE)
	{
		add((int) pE);
		return true;
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
		remove((int) ((Integer) pO));
		return true;
	}

	public boolean removeAll(Collection<?> pC)
	{
		boolean haschanged = false;
		for (Object element : pC)
			haschanged |= removeValue((int) ((Integer) element));
		return haschanged;
	}

	public boolean retainAll(Collection<?> pC)
	{
		boolean haschanged = false;
		for (int element : elements)
			if (!pC.contains(element))
				removeValue(element);
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
