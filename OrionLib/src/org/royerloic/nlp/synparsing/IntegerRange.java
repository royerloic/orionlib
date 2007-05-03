package org.royerloic.nlp.synparsing;

import java.util.ArrayList;
import java.util.List;

public class IntegerRange
{
	private int	rangeStart;
	private int	rangeEnd;

	public IntegerRange(final int id)
	{
		rangeStart = id;
		rangeEnd = id;
	}

	public IntegerRange(final int start, final int end)
	{
		rangeStart = start;
		rangeEnd = end;
	}

	public IntegerRange(final IntegerRange baseRange)
	{
		rangeStart = baseRange.rangeStart;
		rangeEnd = baseRange.rangeEnd;
	}

	@Override
	public String toString()
	{
		return (new StringBuffer()).append("start:").append(rangeStart).append(" end: ").append(rangeEnd)
				.toString();
	}

	public int getRangeEnd()
	{
		return rangeEnd;
	}

	public void setRangeEnd(final int rangeEnd)
	{
		this.rangeEnd = rangeEnd;
	}

	public int getRangeStart()
	{
		return rangeStart;
	}

	public void setRangeStart(final int rangeStart)
	{
		this.rangeStart = rangeStart;
	}

	public boolean isContiguousTo(final IntegerRange otherRange)
	{
		final int otherStart = otherRange.getRangeStart();
		final int otherEnd = otherRange.getRangeEnd();
		return ((rangeStart - 1 <= otherStart) && (otherStart <= rangeEnd + 1)) || ((rangeStart - 1 <= otherEnd)
				&& (otherEnd <= rangeEnd + 1)) || ((otherStart < rangeStart) && (rangeEnd < otherEnd));
	}

	public boolean isContiguousDot(final int i)
	{
		return (rangeStart - 1 <= i) && (i <= rangeEnd + 1);
	}

	public boolean isContiuousOrHigherDot(final int i)
	{
		return rangeStart - 1 <= i;
	}

	public void extendBy(final IntegerRange otherRange)
	{
		final int otherStart = otherRange.getRangeStart();
		final int otherEnd = otherRange.getRangeEnd();

		rangeStart = otherStart < rangeStart ? otherStart : rangeStart;
		rangeEnd = otherEnd > rangeEnd ? otherEnd : rangeEnd;
	}

	public static List<IntegerRange> extractDocRange(final List<Integer> Ids)
	{
		final List<IntegerRange> docRanges = new ArrayList<IntegerRange>();
		IntegerRange currentRange = null;
		for (final Integer id : Ids)
			if (null == currentRange)
				currentRange = new IntegerRange(id);
			else if (currentRange.getRangeEnd() + 1 == id)
				currentRange.setRangeEnd(id);
			else
			{
				docRanges.add(currentRange);
				currentRange = new IntegerRange(id);
			}
		docRanges.add(currentRange);
		return docRanges;
	}

	public static void addDocRange(final List<IntegerRange> list, final IntegerRange range)
	{
		final int i = getNearestRangeIdx(list, range);
		list.add(i, new IntegerRange(range));
		adjustDocRanges(list, i);
	}

	public static int getNearestRangeIdx(final List<IntegerRange> list, final IntegerRange range)
	{
		final int listSize = list.size();
		if (0 == listSize)
			return 0;

		final IntegerRange firstRange = list.get(0);
		if ((range.getRangeEnd() < firstRange.getRangeStart()) || firstRange.isContiguousTo(range))
			return 0;

		final int rangeStart = range.getRangeStart();
		final IntegerRange lastRange = list.get(listSize - 1);
		if (lastRange.isContiguousDot(rangeStart))
			return listSize - 1;

		if (lastRange.getRangeEnd() + 1 < rangeStart)
			return listSize;

		return quickSearchDocRanges(list, rangeStart, 0, list.size() - 1);
	}

	private static int quickSearchDocRanges(final List<IntegerRange> list, final int rangeStart, final int begin, final int end)
	{

		final int i = (end + begin) / 2;
		final IntegerRange median = list.get(i);

		if (median.isContiguousDot(rangeStart))
			return i;

		// Just to avoid eternal loops
		if ((i == end) || (i == begin))
			return end;

		if (rangeStart < median.getRangeStart())
			return quickSearchDocRanges(list, rangeStart, begin, i);

		return quickSearchDocRanges(list, rangeStart, i, end);

		/*
		 * int i = (end+begin)/2; Range beginRange = list.get(begin); Range endRange =
		 * list.get(end); Range median = list.get(i);
		 * 
		 * if (median.isContiguousDot(rangeStart)){ return i; }
		 * 
		 * if (beginRange.getRangeEnd() < rangeStart && rangeStart <
		 * median.getRangeStart()){ //Just to avoid eternal loops if (i == end){
		 * return end; } return quickSearchDocRanges(list, rangeStart, begin, i); }
		 * else if (median.getRangeEnd() < rangeStart && rangeStart <
		 * endRange.getRangeStart()) { //Just to avoid eternal loops if (i ==
		 * begin){ return end; } return quickSearchDocRanges(list, rangeStart, i,
		 * end); } return -1;
		 */
	}

	private static void adjustDocRanges(final List<IntegerRange> list, final int i)
	{
		final IntegerRange currentRange = list.get(i);
		final int currentRangeEnd = currentRange.getRangeEnd();
		final int further = i + 1;
		int listSize = list.size();
		for (; further < listSize;)
		{
			final IntegerRange furtherRange = list.get(further);
			if (furtherRange.isContiuousOrHigherDot(currentRangeEnd))
			{
				currentRange.extendBy(furtherRange);
				list.remove(further);
				listSize = list.size();
			}
			else
				break;
		}
	}

	public static void mergeBaseWithUpdate(final List<IntegerRange> base, final List<IntegerRange> update)
	{
		for (final IntegerRange range : update)
			addDocRange(base, range);
	}

}
/*
 * public static void addDocRange(List<Range> list, Range range){ int listSize =
 * list.size();
 * 
 * if (0 == listSize){ list.add(range); } else { int i =
 * getNearestRangeIdx(list, range);
 * 
 * if (i == listSize){ list.add(i,range); } else { Range currentRange =
 * list.get(i); if (currentRange.isHigherThan(range)) { list.add(i, range); }
 * else { //if (currentRange.isContiguousTo(range))
 * currentRange.extendBy(range); adjustDocRanges(list, i); } } }
 * 
 * return list; }
 */
