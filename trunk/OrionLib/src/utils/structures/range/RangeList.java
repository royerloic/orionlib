package utils.structures.range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loic Royer (c)May 11, 2007.
 * 
 */
public class RangeList
{
	private static final long serialVersionUID = -1996997948395271914L;

	List<Range> mList;

	public RangeList()
	{
		super();
		mList = new ArrayList<Range>();
	}

	public RangeList(List<Range> pList)
	{
		super();
		mList = pList;
		Collections.sort(mList);
	}

	int addRange(final Range pRange)
	{
		// TODO: find insertion position
		final int lInsertionPosition = 0;

		mList.add(lInsertionPosition, pRange);
		return lInsertionPosition;
	}

	void removeRange(final Range pRange)
	{
		// TODO: optimize by finding the range index first
		mList.remove(pRange);
	}

	Range getSupport()
	{
		final Range firstRange = mList.get(0);
		final Range lastRange = mList.get(mList.size() - 1);
		return UnmodifiableRange.constructRangeWithStartEnd(firstRange.mRangeStart,
																												lastRange.mRangeEnd);
	}

	public Range getGreaterRangeAt(final int pPosition)
	{
		final int lRangeIndex = getGreaterRangeIndexAt(pPosition);
		final Range lRangeFound = lRangeIndex == -1 ? null : mList.get(lRangeIndex);
		return lRangeFound;
	}

	public int getGreaterRangeIndexAt(final int pPosition)
	{
		// Check if there is anything to find at all...
		if (mList.size() == 0)
		{
			return -1;
		}

		// Check if we are not searching outside of the support of the RangeSet...
		if (!getSupport().isInside(pPosition))
		{
			return -1;
		}

		return getGreaterRangeIndexWithinIndices(pPosition, 0, mList.size());
	}

	/**
	 * Given a range of indices (begin inckusive, end _exclusive_) find the
	 * greatest (in the sense of the natural ordering of Ranges) Range that
	 * contains a given position.
	 * 
	 * @param pPosition
	 * @param pBeginIndex
	 * @param pEndIndex
	 * @return
	 */
	public int getGreaterRangeIndexWithinIndices(	final int pPosition,
																								final int pBeginIndex,
																								final int pEndIndex)
	{
		// There is just one element within pBeginIndex and pEndIndex, we return its
		// index.
		if (pEndIndex - pBeginIndex == 1)
		{
			return pBeginIndex;
		}

		// Computes the median index and the corresponding Range:
		final int lMedianIndex = (pBeginIndex + pEndIndex) / 2;
		final Range lMedianRange = mList.get(lMedianIndex);

		if (pPosition < lMedianRange.mRangeStart)
		{
			return getGreaterRangeIndexWithinIndices(	pPosition,
																								pBeginIndex,
																								lMedianIndex);
		}
		else if (pPosition >= lMedianRange.mRangeEnd)
		{
			return getGreaterRangeIndexWithinIndices(	pPosition,
																								lMedianIndex,
																								pEndIndex);
		}
		else
		{
			final int lAfterMedianIndex = lMedianIndex + 1;
			final Range lAfterMedianRange = lAfterMedianIndex >= mList.size()	? null
																																				: mList.get(lAfterMedianIndex);
			if (lAfterMedianRange == null || !lAfterMedianRange.isInside(pPosition))
			{
				return lMedianIndex;
			}
			else
			{
				return getGreaterRangeIndexWithinIndices(	pPosition,
																									lMedianIndex,
																									pEndIndex);
			}
		}
	}

	public RangeList flattenRangeSet()
	{
		// TODO: implement a flattening of a RangeSet
		return null;
	}

	/**
	 * Builds a list of ranges from a list of integer positions NOTE: The list of
	 * positions must be sorted !!!!
	 * 
	 * @param Ids
	 * @return a list of ranges constituted
	 */
	public static RangeList buildRangesFromPositions(final List<Integer> pPositionList)
	{
		final List<Range> lRangeList = new ArrayList<Range>();
		Range lCurrentRange = null;
		for (final Integer lPositions : pPositionList)
		{
			if (lCurrentRange == null)
			{
				lCurrentRange = new Range(lPositions);
			}
			else
			{
				if (lCurrentRange.getEnd() == lPositions)
				{
					lCurrentRange.setEnd(lPositions + 1);
				}
				else
				{
					lRangeList.add(lCurrentRange);
					lCurrentRange = new Range(lPositions);
				}
			}
		}
		lRangeList.add(lCurrentRange);
		return new RangeList(lRangeList);
	}

}
