package utils.structures.range;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Loic Royer (c)May 11, 2007.
 * 
 */
public class RangeMap<O> implements Serializable
{
	private static final long serialVersionUID = -1996997948395271914L;

	/**
	 * MapItem(s) are used to store the mapping information. A mapping Range is
	 * defined by a MapItem to its left and a transition to another item (possibly
	 * null) at its right
	 * 
	 */
	private static class MapItem<O> implements Serializable
	{
		int mPosition;
		O mObject;

		public MapItem(final int pPosition, final O pObject)
		{
			super();
			mPosition = pPosition;
			mObject = pObject;
		}

		@Override
		public String toString()
		{
			return "[" + mPosition + "->" + mObject + "]";
		}

		@Override
		public int hashCode()
		{
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + ((mObject == null) ? 0 : mObject.hashCode());
			result = PRIME * result + mPosition;
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
			final MapItem<O> other = (MapItem<O>) obj;
			if (mObject == null)
			{
				if (other.mObject != null)
					return false;
			}
			else if (!mObject.equals(other.mObject))
				return false;
			if (mPosition != other.mPosition)
				return false;
			return true;
		}
	}

	/**
	 * This lists holds MapItems and must be read from Left To Right, each mapItem
	 * signiofies a transition from one mapped value to another. (In essence this
	 * map stores mapping using run length compression)
	 */
	List<MapItem<O>> mList;

	/**
	 * Creates an empty RangeMap
	 */
	public RangeMap()
	{
		super();
		mList = new ArrayList<MapItem<O>>();
		clear();
	}

	public RangeMap(int pInitialCapacity)
	{
		mList = new ArrayList<MapItem<O>>(pInitialCapacity < 2 ? 2
																													: pInitialCapacity);
		clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see range.RangeMapInterface#clear()
	 */
	public void clear()
	{
		if (mList.size() > 0)
			mList.clear();
		mList.add(new MapItem<O>(Integer.MIN_VALUE, null));
		mList.add(new MapItem<O>(Integer.MAX_VALUE, null));/**/
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see range.RangeMapInterface#put(range.Range, O)
	 */
	public final void put(final Range pRange, final O pObject)
	{

		final int lStart = pRange.mRangeStart;
		final int lEnd = pRange.mRangeEnd;

		final int lBeforeStartItemIndex = getGreaterIndexLowerThan(lStart);
		final MapItem<O> lBeforeStartItem = mList.get(lBeforeStartItemIndex);
		final int lBeforeEndItemIndex = getGreaterIndexLowerThan(lEnd);
		final MapItem<O> lBeforeEndItem = mList.get(lBeforeEndItemIndex);
		int lAfterEndItemIndex = lBeforeEndItemIndex + 1; // getLowestIndexHigherThan(lEnd);

		// We need to determine which will be the mapping just after the given
		// Range.
		final O lObjectAfterEnd;
		// If we are overwriting at at the same boundary than an existing Range, we
		if (lBeforeEndItem.mPosition == lStart)
		{
			if (lBeforeEndItem.mPosition == lBeforeStartItem.mPosition)
				lObjectAfterEnd = lBeforeStartItem.mObject;
			else
				lObjectAfterEnd = null;
		}
		else
		{
			lObjectAfterEnd = lBeforeEndItem.mObject;
		}

		// if there are items in between, we remove them:
		// Note: a performance issue is the succesive removal of items in a list
		// which is not batched !
		{
			if (lAfterEndItemIndex - lBeforeStartItemIndex > 1)
			{
				final int lNumberOfItemsRemoved = removeRange(mList,
																											lBeforeStartItemIndex + 1,
																											lAfterEndItemIndex);
				lAfterEndItemIndex -= lNumberOfItemsRemoved;
			}
		}

		// We first deal with the start item:
		{
			// If there is a marker with same object we use it, unless it is one
			// of the boundary items:
			if (lBeforeStartItem.mObject == pObject)
			{
				if (lBeforeStartItem.mPosition != Integer.MIN_VALUE)
				{
					// just do nothing...
					// (DO NOT REMOVE THIS CASE, USEFULL FOR UNDERSTANDING THE CODE)
				}
				else
				{
					mList.add(lBeforeStartItemIndex + 1, new MapItem<O>(lStart, pObject));
					lAfterEndItemIndex++;
				}

			}
			// if there is an object at same position we reuse it:
			else if (lBeforeStartItem.mPosition == lStart)
			{
				lBeforeStartItem.mObject = pObject;
			}
			// else we just create a new item:
			else
			{
				mList.add(lBeforeStartItemIndex + 1, new MapItem<O>(lStart, pObject));
				lAfterEndItemIndex++;
			}
		}

		// We then insert the end marker
		{
			// If there is a marker with same object we merge it:
			if (pObject == lObjectAfterEnd)
			{
				// if it is not a boundary item we reuse the item:
				if (mList.get(lAfterEndItemIndex).mPosition != Integer.MAX_VALUE)
				{
					// do nothing
				}
				else
				{
					mList.add(lAfterEndItemIndex, new MapItem<O>(lEnd, lObjectAfterEnd));
				}
			}
			// If an item exists at the same location we reuse it:
			else if (mList.get(lAfterEndItemIndex).mPosition == lEnd)
			{
				mList.get(lAfterEndItemIndex).mObject = lObjectAfterEnd;
			}
			// Else we just create another item:
			else
			{
				mList.add(lAfterEndItemIndex, new MapItem<O>(lEnd, lObjectAfterEnd));
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see range.RangeMapInterface#get(int)
	 */
	public final O get(final int pPosition)
	{
		final int lItemIndex = getGreaterIndexLowerThan(pPosition);
		final O lObject = mList.get(lItemIndex).mObject;
		return lObject;
	}

	/**
	 * Returns the highest index in the MapItem List for which the MapItem
	 * position is lower than pPosition
	 * 
	 * @param pPosition
	 * @return
	 */
	public final int getGreaterIndexLowerThan(final int pPosition)
	{
		return getGreaterIndexLowerThanAndWithinIndices(pPosition, 0, mList.size());
	}

	/**
	 * @See getGreaterIndexLowerThan
	 * 
	 * @param pPosition
	 * @param pBeginIndex
	 * @param pEndIndex
	 * @return
	 */
	private final int getGreaterIndexLowerThanAndWithinIndices(	final int pPosition,
																															final int pBeginIndex,
																															final int pEndIndex)
	{
		// There is just one element within pBeginIndex and pEndIndex, we return its
		// index.
		if (pEndIndex - pBeginIndex <= 1)
		{
			return pBeginIndex;
		}

		// Computes the median index and the corresponding Range:
		final int lMedianIndex = (pBeginIndex + pEndIndex) / 2;
		final MapItem<O> lMedianMapItem = mList.get(lMedianIndex);

		if (pPosition < lMedianMapItem.mPosition)
		{
			return getGreaterIndexLowerThanAndWithinIndices(pPosition,
																											pBeginIndex,
																											lMedianIndex);
		}
		else
		{
			return getGreaterIndexLowerThanAndWithinIndices(pPosition,
																											lMedianIndex,
																											pEndIndex);
		}
	}

	/**
	 * Returns the Lowest index in the MapItem List for which the MapItem position
	 * is higher than pPosition
	 * 
	 * @param pPosition
	 * @return
	 */
	public final int getLowestIndexHigherThan(final int pPosition)
	{
		return getLowestIndexHigherThanAndWithinIndices(pPosition, 0, mList.size());
	}

	/**
	 * @See getLowestIndexHigherThan.
	 * 
	 * @param pPosition
	 * @param pBeginIndex
	 * @param pEndIndex
	 * @return
	 */
	private final int getLowestIndexHigherThanAndWithinIndices(	final int pPosition,
																															final int pBeginIndex,
																															final int pEndIndex)
	{
		// There is just one element within pBeginIndex and pEndIndex, we return its
		// index.
		if (pEndIndex - pBeginIndex <= 1)
		{
			return pEndIndex;
		}

		// Computes the median index and the corresponding Range:
		final int lMedianIndex = (pBeginIndex + pEndIndex) / 2;
		final MapItem<O> lMedianMapItem = mList.get(lMedianIndex);

		if (pPosition <= lMedianMapItem.mPosition)
		{
			return getLowestIndexHigherThanAndWithinIndices(pPosition,
																											pBeginIndex,
																											lMedianIndex);
		}
		else
		{
			return getLowestIndexHigherThanAndWithinIndices(pPosition,
																											lMedianIndex,
																											pEndIndex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see range.RangeMapInterface#getFirst()
	 */
	public O getFirst()
	{
		for (MapItem<O> lMapItem : mList)
		{
			final O lO = lMapItem.mObject;
			if (lO != null)
				return lO;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see range.RangeMapInterface#getLast()
	 */
	public O getLast()
	{
		for (int i = mList.size() - 1; i >= 0; i--)
		{
			final O lO = mList.get(i).mObject;
			if (lO != null)
				return lO;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see range.RangeMapInterface#translate(int)
	 */
	public void translate(int pOffset)
	{
		for (int i = 1; i < mList.size() - 1; i++)
		{
			mList.get(i).mPosition += pOffset;
		}
	}

	/**
	 * Returns the number of non null range spans. Note: If two non contiguous
	 * ranges have the same object mapped, this counts for two !!! [[1-2]->o1,
	 * [4-5]->o1] --> 2 non null ranges.
	 * 
	 * @return Number of non null range spans
	 */
	public int getNumberOfNonNullRangeSpans()
	{
		int lNumber = 0;
		for (MapItem<O> lMapItem : mList)
		{
			final O lO = lMapItem.mObject;
			if (lO != null)
				lNumber++;
		}
		return lNumber;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return mList.subList(1, mList.size() - 1).toString();
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((mList == null) ? 0 : mList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RangeMap other = (RangeMap) obj;
		if (mList == null)
		{
			if (other.mList != null)
				return false;
		}
		else if (!mList.equals(other.mList))
			return false;
		return true;
	}

	private static final <Obj> int removeRange(	final List<Obj> pList,
																							final int pStart,
																							final int pEnd)
	{
		final int lEnd = pEnd >= pList.size() ? pList.size() - 1 : pEnd;
		final int lStart = pStart <= 0 ? 1 : pStart;
		final int lNumberOfTimes = lEnd - lStart;
		for (int i = 0; i < lNumberOfTimes; i++)
		{
			pList.remove(lStart);
		}
		return lNumberOfTimes;
	}

	/**
	 * Do not use this method unless you really know what it is for! does not
	 * return what you really expect,
	 * 
	 * @deprectated
	 * @return
	 */
	public int getInternalListSize()
	{
		return mList.size();
	}

}
