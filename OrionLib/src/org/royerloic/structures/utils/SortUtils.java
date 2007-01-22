package org.royerloic.structures.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortUtils
{
	public static final class ListComparator<O extends Comparable<O>> implements Comparator<List<O>>
	{

		private final int			mColumn;
		private final boolean	mAscendingOrder;

		public ListComparator(int pColumn, boolean pAscendingOrder)
		{
			super();
			// TODO Auto-generated constructor stub
			mColumn = pColumn;
			mAscendingOrder = pAscendingOrder;
		}

		public int compare(List<O> pO1, List<O> pO2)
		{
			O lComparable1 = pO1.get(mColumn);
			O lComparable2 = pO2.get(mColumn);
			return (mAscendingOrder ? 1 : -1) * lComparable1.compareTo(lComparable2);
		}
	}

	public static <O extends Comparable<O>> void sortMatrix(List<List<O>> pMatrix,
																													int pColumn,
																													boolean pAscendingOrder)
	{
		Collections.sort(pMatrix, new ListComparator<O>(pColumn, pAscendingOrder));
	}

	public static <O> void sortMatrix(Comparator<List<O>> pListComprarator, List<List<O>> pMatrix)
	{
		Collections.sort(pMatrix, pListComprarator);
	}
}
