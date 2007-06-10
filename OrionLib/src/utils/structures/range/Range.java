package utils.structures.range;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Denotes a range of integer numbers
 */
public class Range implements Comparable<Range>, Iterable<Integer>, Serializable
{
	// because in Java int are signed, we need to restrict the length of ranges
	// otherwise overflows happen.
	public static final int	cMaxRangeLength	= Integer.MAX_VALUE / 2;

	protected int						mRangeStart;
	protected int						mRangeEnd;

	/**
	 * Creates a range of length one.
	 * 
	 * @param id
	 */
	public Range(int pPosition)
	{
		mRangeStart = pPosition;
		mRangeEnd = pPosition + 1;
	}

	/**
	 * Create a range given the begin and the length. 
	 * Please use instead the two static methods: Range.constructRangeWithStartEnd and
	 * range.constructRangeWithStartLength which are more explicit. (better for
	 * avoiding confusion between end and length..)
	 * 
	 * Still better: reuse an existing Range by setting its start and end
	 * positions with setStartEnd(final int rangeStart, final int rangeEnd)
	 * 
	 * @param start
	 * @param length
	 */
	//@Deprecated
	public Range(final int start, final int length)
	{
		if (length < 0)
		{
			throw new RuntimeException("Range must have min length 0.");
		}
		if (length > cMaxRangeLength)
		{
			throw new RuntimeException("Range length is limited to: " + cMaxRangeLength);
		}
		mRangeStart = start;
		mRangeEnd = start + length;
	}

	/**
	 * Create a range given the start and end indices. NOTE: the end index is
	 * _exclusive_ following the Java convention!!!
	 * 
	 * @param start
	 * @param end
	 */
	public static Range constructRangeWithStartEnd(final int start, final int end)
	{
		if (end < start)
		{
			throw new RuntimeException("Range must have min length 0. (start<=end)");
		}
		final long lLength = (long) end - (long) start;
		if (lLength > cMaxRangeLength)
		{
			throw new RuntimeException("Range length is limited to: " + cMaxRangeLength);
		}
		return new Range(start, (int) lLength);
	}

	/**
	 * Create a range given the start and end indices. NOTE: the end index is
	 * _exclusive_ following the Java convention!!!
	 * 
	 * @param start
	 * @param end
	 */
	public static Range constructRangeWithStartLength(final int start, final int length)
	{
		return new Range(start, length);
	}

	/**
	 * Copies the given range.
	 * 
	 * @param baseRange
	 */
	public Range(final Range pRange)
	{
		mRangeStart = pRange.mRangeStart;
		mRangeEnd = pRange.mRangeEnd;
	}

	/**
	 * Changes the start and end of this range.
	 * 
	 * @param rangeStart
	 */
	public Range setStartEnd(final int rangeStart, final int rangeEnd)
	{
		if (rangeStart <= rangeEnd)
		{
			mRangeStart = rangeStart;
			mRangeEnd = rangeEnd;

			final long lLength = (long) mRangeEnd - (long) mRangeStart;
			if (lLength > cMaxRangeLength)
			{
				throw new RuntimeException("Range length is limited to: " + cMaxRangeLength);
			}
		}
		else
		{
			throw new RuntimeException("Invalid range modification.");
		}
		return this;
	}

	/**
	 * @return the first integer of this range
	 */
	public final int getStart()
	{
		return mRangeStart;
	}

	/**
	 * Changes the start of this range
	 * 
	 * @param rangeStart
	 */
	public void setStart(final int rangeStart)
	{
		if (rangeStart <= getEnd())
		{
			mRangeStart = rangeStart;

			final long lLength = (long) mRangeEnd - (long) mRangeStart;
			if (lLength > cMaxRangeLength)
			{
				throw new RuntimeException("Range length is limited to: " + cMaxRangeLength);
			}

		}
		else
		{
			throw new RuntimeException("Invalid range modification.");
		}
	}

	/**
	 * @return the integer after the last belonging to this range (END indices are
	 *         allways EXLUSIVE).
	 */
	public final int getEnd()
	{
		return mRangeEnd;
	}

	/**
	 * Changes the end of this range.
	 * 
	 * @param rangeEnd
	 */
	public void setEnd(final int pRangeEnd)
	{
		if (pRangeEnd >= mRangeStart)
		{
			mRangeEnd = pRangeEnd;

			final long lLength = (long) mRangeEnd - (long) mRangeStart;
			if (lLength > cMaxRangeLength)
			{
				throw new RuntimeException("Range length is limited to: " + cMaxRangeLength);
			}
		}
		else
		{
			throw new RuntimeException("Invalid range modification.");
		}
	}

	/**
	 * @param otherRange
	 * @return true if there is one number contained in both Ranges
	 */
	public final boolean isIntersectingWith(final Range otherRange)
	{
		final int otherStart = otherRange.getStart();
		final int otherEnd = otherRange.getEnd();
		return ((mRangeStart <= otherStart) && (otherStart < mRangeEnd))
				|| ((mRangeStart < otherEnd) && (otherEnd <= mRangeEnd))
				|| ((otherStart < mRangeStart) && (mRangeEnd < otherEnd));
	}

	/**
	 * Contiguity between two ranges is_not_ intersection of two ranges, but
	 * whether the sequence of integer positions in uninterupted.
	 * 
	 * @param otherRange
	 * @return true if the two ranges are just touching without overlapp and their
	 *         union is a Range.
	 */
	public final boolean isContiguousTo(final Range otherRange)
	{
		final int otherStart = otherRange.getStart();
		final int otherEnd = otherRange.getEnd();
		return ((mRangeStart == otherEnd) || (mRangeEnd == otherStart));
	}

	/**
	 * @param i
	 * @return true if all numbers of this range and the given integer are
	 *         contigous
	 */
	public final boolean isContiguousPosition(final int i)
	{
		return (mRangeStart - 1 <= i) && (i <= mRangeEnd);
	}

	/**
	 * @param i
	 * @return true if begin of this range is right next to the integer or left of
	 *         it
	 */
	public final boolean isContiguousOrHigherPosition(final int i)
	{
		return mRangeStart - 1 <= i;
	}

	/**
	 * Current range is changed to the convex union of itself and of the given
	 * Range
	 * 
	 * @param otherRange
	 */
	public void convexUnion(final Range otherRange)
	{
		final int otherStart = otherRange.getStart();
		final int otherEnd = otherRange.getEnd();

		mRangeStart = otherStart < mRangeStart ? otherStart : mRangeStart;
		mRangeEnd = otherEnd > mRangeEnd ? otherEnd : mRangeEnd;

		final long lLength = (long) mRangeEnd - (long) mRangeStart;
		if (lLength > cMaxRangeLength)
		{
			throw new RuntimeException("Range length is limited to: " + cMaxRangeLength);
		}
	}

	/**
	 * @return the length of the range
	 */
	public final int length()
	{
		return mRangeEnd - mRangeStart;
	}

	/**
	 * Changes the range by copying start and end from the given range
	 * 
	 * @param range
	 */
	public void setRange(final Range range)
	{
		mRangeStart = range.getStart();
		mRangeEnd = range.getEnd();
	}

	/**
	 * Translates the range to left or right. NOTE: this _modifies_ the range it
	 * is called upon !!
	 * 
	 * @param by
	 */
	public Range translateRange(final int by)
	{
		mRangeStart += by;
		mRangeEnd += by;
		return this;
	}

	/**
	 * @param otherRange
	 * @return true if the given range has no higher or lower numbers as contained
	 *         in this range
	 */
	public final boolean isInside(final Range otherRange)
	{
		return (mRangeStart <= otherRange.getStart()) && (mRangeEnd >= otherRange.getEnd());
	}

	/**
	 * @param pos
	 * @return true if the given number is one of the numbers in this range
	 */
	public final boolean isInside(final int pos)
	{
		return (mRangeStart <= pos) && (mRangeEnd > pos);
	}

	@Override
	public final String toString()
	{
		return "[" + mRangeStart + "(" + (length()) + ")]";
	}

	public boolean equals(int pAbsoluteStart, int pAbsoluteEnd)
	{
		return (mRangeStart == pAbsoluteStart) && (mRangeEnd == pAbsoluteEnd);
	}
	
	/**
	 * @param range
	 * @return true if start and end are equal
	 */
	@Override
	public final boolean equals(final Object range)
	{
		if (this == range)
		{
			return true;
		}
		if (range == null)
		{
			return false;
		}
		return (mRangeStart == ((Range) range).mRangeStart) && (mRangeEnd == ((Range) range).mRangeEnd);
	}

	@Override
	public final int hashCode()
	{
		return 7 ^ mRangeStart ^ mRangeEnd;
	}

	/**
	 * order: [5-3]>[5-2]>[4-4]
	 * 
	 * @param baserange
	 * @return an ordering integer
	 */
	public final int compareTo(final Range range)
	{
		if (mRangeStart < range.mRangeStart)
		{
			return -1;
		}
		else if (mRangeStart > range.mRangeStart)
		{
			return 1;
		}
		else
		{
			if (mRangeEnd < range.mRangeEnd)
			{
				return 1;
			}
			else if (mRangeEnd > range.mRangeEnd)
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}
	}

	private static final class RangeIterator implements Iterator<Integer>
	{
		final Range	range;
		int					index;

		public RangeIterator(final Range pRange)
		{
			super();
			range = pRange;
			index = range.mRangeStart - 1;
		}

		public final boolean hasNext()
		{
			return index < range.getEnd() - 1;
		}

		public final Integer next()
		{
			index++;
			return index;
		}

		public final void remove()
		{
			throw new UnsupportedOperationException("Cannot remove !");
		}

	}

	public Iterator<Integer> iterator()
	{
		return new RangeIterator(this);
	}



}
