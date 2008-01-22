package utils.structures.fast;

import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;

public class FastIntegerList implements RandomAccess, java.io.Serializable
{
	private static final long serialVersionUID = 8683452581122892189L;

	public int[] elementData;
	public int size;

	public FastIntegerList(int initialCapacity)
	{
		super();
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		this.elementData = new int[initialCapacity];
	}

	public FastIntegerList()
	{
		this(10);
	}

	private final void trimToSize()
	{
		int oldCapacity = elementData.length;
		if (size < oldCapacity)
		{
			elementData = Arrays.copyOf(elementData, size);
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
	private final void ensureCapacity(int minCapacity)
	{
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity)
		{
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			// minCapacity is usually close to size, so this is a win:
			elementData = Arrays.copyOf(elementData, newCapacity);
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
		for (int i : elementData)
			if (o == i)
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
		for (int i : elementData)
			if (o == i)
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
	public final int lastIndexOf(Object o)
	{
		{
			for (int i = size - 1; i >= 0; i--)
				if (o.equals(elementData[i]))
					return i;
		}
		return -1;
	}

	// Positional Access Operations

	public final int get(int index)
	{
		return elementData[index];
	}

	public final void set(int index, int element)
	{
		elementData[index] = element;
	}

	public void add(int e)
	{
		ensureCapacity(size + 1); // Increments modCount!!
		elementData[size++] = e;
	}

	public void add(int index, int element)
	{
		if (index > size || index < 0)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);

		ensureCapacity(size + 1); // Increments modCount!!
		System.arraycopy(elementData, index, elementData, index + 1, size - index);
		elementData[index] = element;
		size++;
	}

	public void remove(int index)
	{
		int numMoved = size - index - 1;
		if (numMoved > 0)
			System.arraycopy(elementData, index + 1, elementData, index, numMoved);
		size--;
	}

	public boolean removeValue(int o)
	{
		for (int index = 0; index < size; index++)
			if (o == elementData[index])
			{
				remove(index);
				return true;
			}
		return false;
	}

	/**
	 * Removes all of the elements from this list. The list will be empty after
	 * this call returns.
	 */
	public void clear()
	{
		size = 0;
	}


	public boolean addAll(Collection<? extends Integer> c)
	{
		Integer[] a = (Integer[]) c.toArray();
		final int numNew = a.length;
		ensureCapacity(size + numNew); // Increments modCount
		System.arraycopy(a, 0, elementData, size, numNew);
		for (int i = 0; i < numNew; i++)
			elementData[i] = a[size + i];
		size += numNew;
		return numNew != 0;
	}

	/**
	 * Checks if the given index is in range. If not, throws an appropriate
	 * runtime exception. This method does *not* check if the index is negative:
	 * It is always used immediately prior to an array access, which throws an
	 * ArrayIndexOutOfBoundsException if index is negative.
	 */
	private void RangeCheck(int index)
	{
		if (index >= size)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
	}

}
