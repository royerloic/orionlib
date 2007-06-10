package utils.structures;

import java.util.ArrayList;
import java.util.HashSet;

public class IterableToCollection
{
	public static final<T> ArrayList<T> iterableToList(final Iterable<T> pIterable)
	{
		final ArrayList<T> lArrayList = new ArrayList<T>();
		for (final T lT : pIterable)
		{
			lArrayList.add(lT);
		}
		return lArrayList;
	}

	public static final<T> HashSet<T> iterableToSet(final Iterable<T> pIterable)
	{
		final HashSet<T> lHashSet = new HashSet<T>();
		for (final T lT : pIterable)
		{
			lHashSet.add(lT);
		}
		return lHashSet;
	}

}
