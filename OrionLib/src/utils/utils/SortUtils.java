package utils.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortUtils
{
	public static final class ListComparator<O extends Comparable<O>> implements
																																		Comparator<List<O>>
	{

		private final int mColumn;
		private final boolean mAscendingOrder;

		public ListComparator(final int pColumn, final boolean pAscendingOrder)
		{
			super();
			// TODO Auto-generated constructor stub
			mColumn = pColumn;
			mAscendingOrder = pAscendingOrder;
		}

		public int compare(final List<O> pO1, final List<O> pO2)
		{
			final O lComparable1 = pO1.get(mColumn);
			final O lComparable2 = pO2.get(mColumn);
			return (mAscendingOrder ? 1 : -1) * lComparable1.compareTo(lComparable2);
		}
	}

	public static <O extends Comparable<O>> void sortMatrix(final List<List<O>> pMatrix,
																													final int pColumn,
																													final boolean pAscendingOrder)
	{
		Collections.sort(pMatrix, new ListComparator<O>(pColumn, pAscendingOrder));
	}

	public static <O> void sortMatrix(final Comparator<List<O>> pListComprarator,
																		final List<List<O>> pMatrix)
	{
		Collections.sort(pMatrix, pListComprarator);
	}
}
