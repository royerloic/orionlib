package org.royerloic.nlp.synparsing;

import java.util.ArrayList;
import java.util.List;

public class IntegerRange
{
	private int	rangeStart;
	private int	rangeEnd;

	public IntegerRange(int id)
	{
		rangeStart = id;
		rangeEnd = id;
	}

	public IntegerRange(int start, int end)
	{
		rangeStart = start;
		rangeEnd = end;
	}

	public IntegerRange(IntegerRange baseRange)
	{
		this.rangeStart = baseRange.rangeStart;
		this.rangeEnd = baseRange.rangeEnd;
	}

	public String toString()
	{
		return (new StringBuffer()).append("start:").append(rangeStart).append(" end: ").append(rangeEnd)
				.toString();
	}

	public int getRangeEnd()
	{
		return rangeEnd;
	}

	public void setRangeEnd(int rangeEnd)
	{
		this.rangeEnd = rangeEnd;
	}

	public int getRangeStart()
	{
		return rangeStart;
	}

	public void setRangeStart(int rangeStart)
	{
		this.rangeStart = rangeStart;
	}

	public boolean isContiguousTo(IntegerRange otherRange)
	{
		int otherStart = otherRange.getRangeStart();
		int otherEnd = otherRange.getRangeEnd();
		return (rangeStart - 1 <= otherStart) && (otherStart <= rangeEnd + 1) || (rangeStart - 1 <= otherEnd)
				&& (otherEnd <= rangeEnd + 1) || (otherStart < rangeStart) && (rangeEnd < otherEnd);
	}

	public boolean isContiguousDot(int i)
	{
		return rangeStart - 1 <= i && i <= rangeEnd + 1;
	}

	public boolean isContiuousOrHigherDot(int i)
	{
		return rangeStart - 1 <= i;
	}

	public void extendBy(IntegerRange otherRange)
	{
		int otherStart = otherRange.getRangeStart();
		int otherEnd = otherRange.getRangeEnd();

		rangeStart = otherStart < rangeStart ? otherStart : rangeStart;
		rangeEnd = otherEnd > rangeEnd ? otherEnd : rangeEnd;
	}

	public static List<IntegerRange> extractDocRange(List<Integer> Ids)
	{
		List<IntegerRange> docRanges = new ArrayList<IntegerRange>();
		IntegerRange currentRange = null;
		for (Integer id : Ids)
		{
			if (null == currentRange)
			{
				currentRange = new IntegerRange(id);
			}
			else
			{
				if (currentRange.getRangeEnd() + 1 == id)
				{
					currentRange.setRangeEnd(id);
				}
				else
				{
					docRanges.add(currentRange);
					currentRange = new IntegerRange(id);
				}

			}
		}
		docRanges.add(currentRange);
		return docRanges;
	}

	public static void addDocRange(List<IntegerRange> list, IntegerRange range)
	{
		int i = getNearestRangeIdx(list, range);
		list.add(i, new IntegerRange(range));
		adjustDocRanges(list, i);
	}

	public static int getNearestRangeIdx(List<IntegerRange> list, IntegerRange range)
	{
		int listSize = list.size();
		if (0 == listSize)
		{
			return 0;
		}

		IntegerRange firstRange = list.get(0);
		if (range.getRangeEnd() < firstRange.getRangeStart() || firstRange.isContiguousTo(range))
		{
			return 0;
		}

		int rangeStart = range.getRangeStart();
		IntegerRange lastRange = list.get(listSize - 1);
		if (lastRange.isContiguousDot(rangeStart))
		{
			return listSize - 1;
		}

		if (lastRange.getRangeEnd() + 1 < rangeStart)
		{
			return listSize;
		}

		return quickSearchDocRanges(list, rangeStart, 0, list.size() - 1);
	}

	private static int quickSearchDocRanges(List<IntegerRange> list, int rangeStart, int begin, int end)
	{

		int i = (end + begin) / 2;
		IntegerRange median = list.get(i);

		if (median.isContiguousDot(rangeStart))
		{
			return i;
		}

		// Just to avoid eternal loops
		if (i == end || i == begin)
		{
			return end;
		}

		if (rangeStart < median.getRangeStart())
		{
			return quickSearchDocRanges(list, rangeStart, begin, i);
		}

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

	private static void adjustDocRanges(List<IntegerRange> list, int i)
	{
		IntegerRange currentRange = list.get(i);
		int currentRangeEnd = currentRange.getRangeEnd();
		int further = i + 1;
		int listSize = list.size();
		for (; further < listSize;)
		{
			IntegerRange furtherRange = list.get(further);
			if (furtherRange.isContiuousOrHigherDot(currentRangeEnd))
			{
				currentRange.extendBy(furtherRange);
				list.remove(further);
				listSize = list.size();
			}
			else
			{
				break;
			}
		}
	}

	public static void mergeBaseWithUpdate(List<IntegerRange> base, List<IntegerRange> update)
	{
		for (IntegerRange range : update)
		{
			addDocRange(base, range);
		}
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
