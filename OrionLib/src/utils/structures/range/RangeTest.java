package utils.structures.range;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the range object.
 */
public class RangeTest
{

	private Range dr;
	private Range dr1;
	private Range drsame;

	/**
	 * Setup
	 */
	@Before
	public void setUp()
	{
		dr = new Range(3, 3);
		dr1 = new Range(1);
		drsame = new Range(3, 3);
	}

	/** A test. */
	@Test
	public void testIntersectingWith()
	{
		// Tests all 4 types of intesction (containing,left,right,contained):
		Assert.assertTrue(dr.isIntersectingWith(new Range(2, 6)));
		Assert.assertTrue(dr.isIntersectingWith(new Range(2, 2)));
		Assert.assertTrue(dr.isIntersectingWith(new Range(5, 3)));
		Assert.assertTrue(dr.isIntersectingWith(new Range(4, 1)));

		// Tests non interesction at the borders:
		Assert.assertFalse(dr.isIntersectingWith(new Range(1, 2)));
		Assert.assertFalse(dr.isIntersectingWith(new Range(6, 2)));
		Assert.assertFalse(dr.isIntersectingWith(new Range(2, 1)));
		Assert.assertFalse(dr.isIntersectingWith(new Range(6, 1)));
		// Tests strict non intersection:
		Assert.assertFalse(dr.isIntersectingWith(new Range(7, 3)));
	}

	/** A test. */
	@Test
	public void testContiguousTo()
	{
		// Test contiguity on both sides:
		Assert.assertTrue(dr.isContiguousTo(new Range(1, 2)));
		Assert.assertTrue(dr.isContiguousTo(new Range(6, 2)));
		Assert.assertTrue(dr.isContiguousTo(new Range(2, 1)));
		Assert.assertTrue(dr.isContiguousTo(new Range(6, 1)));

		// test that if there is a gap between the two ranges there is no
		// contiguity.
		Assert.assertFalse(dr.isContiguousTo(new Range(0, 2)));
		Assert.assertFalse(dr.isContiguousTo(new Range(7, 2)));
		Assert.assertFalse(dr.isContiguousTo(new Range(1, 1)));
		Assert.assertFalse(dr.isContiguousTo(new Range(7, 1)));

		// test non strict contiguity - cases when ther is overlapping
		Assert.assertFalse(dr.isContiguousTo(new Range(2, 6)));
		Assert.assertFalse(dr.isContiguousTo(new Range(2, 2)));
		Assert.assertFalse(dr.isContiguousTo(new Range(5, 3)));
		Assert.assertFalse(dr.isContiguousTo(new Range(4, 1)));
	}

	/** A test. */
	@Test
	public void testExtendStart1()
	{
		dr.convexUnion(new Range(2, 2));
		Assert.assertEquals(2, dr.getStart());
		Assert.assertEquals(6, dr.getEnd());
	}

	/** A test. */
	@Test
	public void testExtendStart2()
	{
		dr.convexUnion(new Range(1, 2));
		Assert.assertEquals(1, dr.getStart());
		Assert.assertEquals(6, dr.getEnd());
	}

	/** A test. */
	@Test
	public void testExtendEnd1()
	{
		dr.convexUnion(new Range(4, 3));
		Assert.assertEquals(3, dr.getStart());
		Assert.assertEquals(7, dr.getEnd());
	}

	/** A test. */
	@Test
	public void testExtendEnd2()
	{
		dr.convexUnion(new Range(6, 2));
		Assert.assertEquals(3, dr.getStart());
		Assert.assertEquals(8, dr.getEnd());
	}

	/** A test. */
	@Test
	public void testExtendBoth()
	{
		dr.convexUnion(new Range(2, 5));
		Assert.assertEquals(2, dr.getStart());
		Assert.assertEquals(7, dr.getEnd());
	}

	/** A test. */
	@Test
	public void testNoExtension()
	{
		dr.convexUnion(new Range(4, 2));
		Assert.assertEquals(3, dr.getStart());
		Assert.assertEquals(6, dr.getEnd());
	}

	/** A test. */
	@Test
	public void testContiguousPosition()
	{
		Assert.assertFalse(dr.isContiguousPosition(1));
		Assert.assertTrue(dr.isContiguousPosition(2));
		Assert.assertTrue(dr.isContiguousPosition(3));
		Assert.assertTrue(dr.isContiguousPosition(4));
		Assert.assertTrue(dr.isContiguousPosition(5));
		Assert.assertTrue(dr.isContiguousPosition(6));
		Assert.assertFalse(dr.isContiguousPosition(7));

		Assert.assertFalse(dr1.isContiguousPosition(-1));
		Assert.assertTrue(dr1.isContiguousPosition(0));
		Assert.assertTrue(dr1.isContiguousPosition(1));
		Assert.assertTrue(dr1.isContiguousPosition(2));
		Assert.assertFalse(dr1.isContiguousPosition(3));
	}

	/* =========================================== */
	/*****************************************************************************
	 * 
	 * @Test public void testAddOne() { final Range docRange = new Range(2); final
	 * List<Range> ranges = new ArrayList<Range>(); Range.addDocRange(ranges,
	 * docRange);
	 * 
	 * Assert.assertEquals(1, ranges.size()); Assert.assertEquals(2,
	 * ranges.get(0).getStart()); Assert.assertEquals(3, ranges.get(0).getEnd()); }
	 * 
	 * 
	 * @Test public void testAddInBetween() { final Range docRange = new Range(2);
	 * final List<Range> ranges = new ArrayList<Range>(); ranges.add(new
	 * Range(1)); ranges.add(new Range(3));
	 * 
	 * Range.addDocRange(ranges, docRange);
	 * 
	 * Assert.assertEquals(1, ranges.size()); Assert.assertEquals(1,
	 * ranges.get(0).getStart()); Assert.assertEquals(4, ranges.get(0).getEnd()); }
	 * 
	 * 
	 * @Test public void testAddBefore() { final Range docRange = new Range(2);
	 * final List<Range> ranges = new ArrayList<Range>(); ranges.add(new
	 * Range(3));
	 * 
	 * Range.addDocRange(ranges, docRange);
	 * 
	 * Assert.assertEquals(1, ranges.size()); Assert.assertEquals(2,
	 * ranges.get(0).getStart()); Assert.assertEquals(4, ranges.get(0).getEnd()); }
	 * 
	 * 
	 * @Test public void testAddAfter() { final Range docRange = new Range(2);
	 * final List<Range> ranges = new ArrayList<Range>(); ranges.add(new
	 * Range(1));
	 * 
	 * Range.addDocRange(ranges, docRange);
	 * 
	 * Assert.assertEquals(1, ranges.size()); Assert.assertEquals(1,
	 * ranges.get(0).getStart()); Assert.assertEquals(3, ranges.get(0).getEnd()); }
	 * 
	 * @Test public void testAddExisting() { final Range docRange = new Range(1);
	 * final List<Range> ranges = new ArrayList<Range>(); ranges.add(new
	 * Range(1));
	 * 
	 * Range.addDocRange(ranges, docRange);
	 * 
	 * Assert.assertEquals(1, ranges.size()); Assert.assertEquals(1,
	 * ranges.get(0).getStart()); Assert.assertEquals(2, ranges.get(0).getEnd()); }
	 * 
	 * 
	 * @Test public void testAppendNew() { final Range docRange = new Range(3);
	 * final List<Range> ranges = new ArrayList<Range>(); ranges.add(new
	 * Range(1));
	 * 
	 * Range.addDocRange(ranges, docRange);
	 * 
	 * Assert.assertEquals(2, ranges.size()); Assert.assertEquals(1,
	 * ranges.get(0).getStart()); Assert.assertEquals(2, ranges.get(0).getEnd());
	 * 
	 * Assert.assertEquals(3, ranges.get(1).getStart()); Assert.assertEquals(4,
	 * ranges.get(1).getEnd()); }
	 * 
	 * 
	 * @Test public void testInsertNew() { final Range docRange = new Range(1);
	 * final List<Range> ranges = new ArrayList<Range>(); ranges.add(new
	 * Range(3));
	 * 
	 * Range.addDocRange(ranges, docRange);
	 * 
	 * Assert.assertEquals(2, ranges.size()); Assert.assertEquals(1,
	 * ranges.get(0).getStart()); Assert.assertEquals(2, ranges.get(0).getEnd());
	 * 
	 * Assert.assertEquals(3, ranges.get(1).getStart()); Assert.assertEquals(4,
	 * ranges.get(1).getEnd()); }
	 * 
	 * 
	 * @Test public void testAddSuperRange() { final Range docRange = new Range(1,
	 * 5); final List<Range> ranges = new ArrayList<Range>(); ranges.add(new
	 * Range(2, 3));
	 * 
	 * Range.addDocRange(ranges, docRange);
	 * 
	 * Assert.assertEquals(1, ranges.size()); Assert.assertEquals(1,
	 * ranges.get(0).getStart()); Assert.assertEquals(6, ranges.get(0).getEnd()); }
	 * 
	 * 
	 * @Test public void testAddSubRange() { final Range docRange = new Range(2,
	 * 3); final List<Range> ranges = new ArrayList<Range>(); ranges.add(new
	 * Range(1, 5));
	 * 
	 * Range.addDocRange(ranges, docRange);
	 * 
	 * Assert.assertEquals(1, ranges.size()); Assert.assertEquals(1,
	 * ranges.get(0).getStart()); Assert.assertEquals(6, ranges.get(0).getEnd()); }
	 * 
	 * 
	 * @Test public void testMergeSuperAndSubRanges() { final List<Range>
	 * subRanges = new ArrayList<Range>(); subRanges.add(new Range(2, 3));
	 * 
	 * final List<Range> superRanges = new ArrayList<Range>();
	 * superRanges.add(new Range(1, 5));
	 * 
	 * Range.mergeBaseWithUpdate(superRanges, subRanges);
	 * 
	 * Assert.assertEquals(1, superRanges.size()); Assert.assertEquals(1,
	 * superRanges.get(0).getStart()); Assert.assertEquals(6,
	 * superRanges.get(0).getEnd());
	 *  }
	 * 
	 * 
	 * @Test public void testMergeOddAndEvenRanges() { final List<Range>
	 * oddRanges = new ArrayList<Range>(); oddRanges.add(new Range(1));
	 * oddRanges.add(new Range(3));
	 * 
	 * final List<Range> evenRanges = new ArrayList<Range>(); evenRanges.add(new
	 * Range(2)); evenRanges.add(new Range(4)); evenRanges.add(new Range(6));
	 * 
	 * Range.mergeBaseWithUpdate(oddRanges, evenRanges);
	 * 
	 * Assert.assertEquals(2, oddRanges.size()); Assert.assertEquals(1,
	 * oddRanges.get(0).getStart()); Assert.assertEquals(5,
	 * oddRanges.get(0).getEnd());
	 * 
	 * Assert.assertEquals(6, oddRanges.get(1).getStart()); Assert.assertEquals(7,
	 * oddRanges.get(1).getEnd()); }
	 * 
	 * 
	 * @Test public void testDocRangesExtraction() { final List<Integer> docIDs =
	 * new ArrayList<Integer>(); docIDs.add(new Integer(1)); docIDs.add(new
	 * Integer(3)); docIDs.add(new Integer(5)); docIDs.add(new Integer(6));
	 * docIDs.add(new Integer(7)); docIDs.add(new Integer(9));
	 * 
	 * final List<Range> docRanges = Range.buildRanges(docIDs);
	 * 
	 * Assert.assertEquals(4, docRanges.size());
	 * 
	 * Assert.assertEquals(1, docRanges.get(0).getStart()); Assert.assertEquals(2,
	 * docRanges.get(0).getEnd());
	 * 
	 * Assert.assertEquals(3, docRanges.get(1).getStart()); Assert.assertEquals(4,
	 * docRanges.get(1).getEnd());
	 * 
	 * Assert.assertEquals(5, docRanges.get(2).getStart()); Assert.assertEquals(8,
	 * docRanges.get(2).getEnd());
	 * 
	 * Assert.assertEquals(9, docRanges.get(3).getStart());
	 * Assert.assertEquals(10, docRanges.get(3).getEnd()); }
	 * 
	 * 
	 * @Test public void testSearchForSubRange() { final List<Range> ranges = new
	 * ArrayList<Range>(); ranges.add(new Range(1, 2)); ranges.add(new Range(5));
	 * 
	 * final Range subRange1 = new Range(1);
	 * 
	 * final Range subRange2 = new Range(2);
	 * 
	 * final Range subRange3 = new Range(5);
	 * 
	 * Assert.assertEquals(0, Range.getNearestRangeIndex(ranges, subRange1));
	 * Assert.assertEquals(0, Range.getNearestRangeIndex(ranges, subRange2));
	 * 
	 * Assert.assertEquals(1, Range.getNearestRangeIndex(ranges, subRange3)); }
	 * 
	 * 
	 * @Test public void testSearchForContiguousRange() { final List<Range>
	 * ranges = new ArrayList<Range>(); ranges.add(new Range(1, 2));
	 * ranges.add(new Range(5));
	 * 
	 * final Range contRange1 = new Range(0); final Range contRange2 = new
	 * Range(3); final Range contRange3 = new Range(6);
	 * 
	 * Assert.assertEquals(0, Range.getNearestRangeIndex(ranges, contRange1));
	 * Assert.assertEquals(0, Range.getNearestRangeIndex(ranges, contRange2));
	 * 
	 * Assert.assertEquals(1, Range.getNearestRangeIndex(ranges, contRange3)); }
	 * 
	 * 
	 * @Test public void testSearchingForSuperRange() { final List<Range> ranges =
	 * new ArrayList<Range>(); ranges.add(new Range(429)); ranges.add(new
	 * Range(447, 451 - 447 + 1)); ranges.add(new Range(740)); ranges.add(new
	 * Range(1083)); ranges.add(new Range(1637)); ranges.add(new Range(4354));
	 * 
	 * Assert.assertEquals(0, Range.getNearestRangeIndex(ranges, new Range(426,
	 * 432 - 426 + 1))); }
	 * 
	 * 
	 * @Test public void testSearchForNonExistingRange1() { final List<Range>
	 * ranges = new ArrayList<Range>(); ranges.add(new Range(2)); ranges.add(new
	 * Range(6));
	 * 
	 * final Range contRange1 = new Range(0); final Range contRange2 = new
	 * Range(4); final Range contRange3 = new Range(90);
	 * 
	 * Assert.assertEquals(0, Range.getNearestRangeIndex(ranges, contRange1));
	 * 
	 * Assert.assertEquals(1, Range.getNearestRangeIndex(ranges, contRange2));
	 * Assert.assertEquals(2, Range.getNearestRangeIndex(ranges, contRange3)); }
	 * 
	 * 
	 * @Test public void testSearchForNonExistingRange2() { final List<Range>
	 * ranges = new ArrayList<Range>(); ranges.add(new Range(2)); ranges.add(new
	 * Range(6)); ranges.add(new Range(8));
	 * 
	 * final Range contRange = new Range(4);
	 * 
	 * Assert.assertEquals(1, Range.getNearestRangeIndex(ranges, contRange)); }
	 * 
	 * 
	 * @Test public void testCrashMergeEmptyAndChild() { final List<Range>
	 * childList = new ArrayList<Range>(); for (int i = 1; i < 100000; i = i + 2) {
	 * childList.add(new Range(i)); } final List<Range> empty = new ArrayList<Range>(); //
	 * long start = System.currentTimeMillis(); Range.mergeBaseWithUpdate(empty,
	 * childList); // long end = System.currentTimeMillis();
	 *  // System.out.println("testCrashMergeEmptyAndChild took "+(end-start)); }
	 * 
	 * 
	 * @Test public void testCrashMergeParentAndChild() { final List<Range>
	 * childList = new ArrayList<Range>(); final List<Range> parentList = new
	 * ArrayList<Range>(); for (int i = 1; i < 100000; i = i + 2) {
	 * childList.add(new Range(i)); } for (int i = 1; i < 100000; i = i + 7) {
	 * parentList.add(new Range(i)); }
	 *  // long start = System.currentTimeMillis();
	 * Range.mergeBaseWithUpdate(parentList, childList); // long end =
	 * System.currentTimeMillis();
	 *  // System.out.println("testCrashMergeEmptyAndChild took "+(end-start)); } /
	 ****************************************************************************/

	@Test
	public void testLength()
	{
		Assert.assertEquals(3, dr.length());
		Assert.assertEquals(1, dr1.length());
	}

	/** A test. */
	@Test
	public void testEquals()
	{
		Assert.assertEquals(drsame, dr);
		Assert.assertFalse(dr1.equals(dr));
	}

	/** A test. */
	@Test
	public void testHashCode()
	{
		Assert.assertEquals(drsame.hashCode(), dr.hashCode());
		// equivalent to: dr1.equals(dr) -> drsame.hashCode()==dr.hashCode()
		Assert.assertTrue(!dr1.equals(dr) || (drsame.hashCode() == dr.hashCode()));
	}

	/** A test. */
	@Test
	public void testCompare()
	{
		List<Range> lList = new ArrayList<Range>();
		lList.add(Range.constructRangeWithStartEnd(2, 4));
		lList.add(Range.constructRangeWithStartEnd(1, 4));
		lList.add(Range.constructRangeWithStartEnd(1, 2));
		Collections.sort(lList);

		assertEquals(lList.get(0), Range.constructRangeWithStartEnd(1, 4));
		assertEquals(lList.get(1), Range.constructRangeWithStartEnd(1, 2));
		assertEquals(lList.get(2), Range.constructRangeWithStartEnd(2, 4));

		assertTrue(dr.compareTo(new Range(1, 2)) == 1);
		assertTrue(dr.compareTo(new Range(1, 3)) == 1);
		assertTrue(dr.compareTo(new Range(3, 2)) == -1);
		assertTrue(dr.compareTo(new Range(3, 3)) == 0);
		assertTrue(dr.compareTo(new Range(3, 4)) == +1);
		assertTrue(dr.compareTo(new Range(4, 4)) == -1);
	}

	/** A test. */
	@Test
	public void testIsInside()
	{

		Assert.assertFalse(dr.isInside(2));
		Assert.assertTrue(dr.isInside(3));
		Assert.assertTrue(dr.isInside(4));
		Assert.assertTrue(dr.isInside(5));
		Assert.assertFalse(dr.isInside(6));

		Assert.assertTrue(dr.isInside(new Range(4, 1)));
		Assert.assertFalse(dr.isInside(new Range(1, 4)));
		Assert.assertFalse(dr.isInside(new Range(4, 4)));
		Assert.assertFalse(dr.isInside(new Range(6, 1)));
		Assert.assertFalse(dr.isInside(new Range(2, 1)));

	}
}
