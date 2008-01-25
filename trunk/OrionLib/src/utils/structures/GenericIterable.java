package utils.structures;

import java.util.Iterator;

public class GenericIterable<T> implements Iterable<T>
{
	private Iterator<T> mIterator;

	public GenericIterable(final Iterator<T> pIterator)
	{
		super();
		mIterator = pIterator;
	}

	public final Iterator<T> iterator()
	{
		if (mIterator == null)
		{
			throw new UnsupportedOperationException("this is a _single use_ Iterable !!");
		}
		final Iterator<T> lIterator = mIterator;
		mIterator = null;
		return lIterator;
	}

}
