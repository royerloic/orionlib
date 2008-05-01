package utils.structures.range;

import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the range object.
 */
public class RangeMapTest
{

	private Range r1, r12, r2, r3, r123, r4, rall;
	private RangeMap<String> rangemap;
	private RangeMap<Integer> rangemapinteger;

	/**
	 * Setup
	 */
	@Before
	public void setUp()
	{
		r1 = Range.constructRangeWithStartEnd(0, 3);
		r2 = Range.constructRangeWithStartEnd(3, 5);
		r12 = Range.constructRangeWithStartEnd(0, 5);
		r3 = Range.constructRangeWithStartEnd(5, 10);
		r123 = Range.constructRangeWithStartEnd(0, 10);
		r4 = Range.constructRangeWithStartEnd(2, 8);
		rall = Range.constructRangeWithStartEnd(0, 10);
		r4.length();
		rangemap = new RangeMap<String>();
		rangemapinteger = new RangeMap<Integer>();
	}

	@Test
	public void testSimpleSearch()
	{
		rangemap.put(r1, "r1");
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(-20) == 0);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(-1) == 0);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(0) == 1);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(1) == 1);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(2) == 1);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(3) == 2);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(4) == 2);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(20) == 2);

		Assert.assertTrue(rangemap.getLowestIndexHigherThan(-20) == 1);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(-1) == 1);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(0) == 1);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(1) == 2);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(2) == 2);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(3) == 2);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(4) == 3);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(20) == 3);

	}

	@Test
	public void testSearch()
	{
		rangemap.put(r1, "r1");
		rangemap.put(r2, "r2");
		rangemap.put(r3, "r3");
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(-20) == 0);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(-1) == 0);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(0) == 1);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(1) == 1);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(2) == 1);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(3) == 2);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(4) == 2);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(5) == 3);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(6) == 3);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(7) == 3);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(8) == 3);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(9) == 3);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(10) == 4);
		Assert.assertTrue(rangemap.getGreaterIndexLowerThan(20) == 4);

		Assert.assertTrue(rangemap.getLowestIndexHigherThan(-20) == 1);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(-1) == 1);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(0) == 1);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(1) == 2);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(2) == 2);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(3) == 2);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(4) == 3);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(5) == 3);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(6) == 4);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(7) == 4);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(8) == 4);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(9) == 4);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(10) == 4);
		Assert.assertTrue(rangemap.getLowestIndexHigherThan(20) == 5);

	}

	@Test
	public void testPutGetWithoutOverwrite()
	{
		rangemap.put(r1, "r1");
		Assert.assertTrue(rangemap.get(-10000) == null);
		Assert.assertTrue(rangemap.get(-1) == null);
		Assert.assertTrue(rangemap.get(0).equals("r1"));
		Assert.assertTrue(rangemap.get(1).equals("r1"));
		Assert.assertTrue(rangemap.get(2).equals("r1"));
		Assert.assertTrue(rangemap.get(3) == null);
		Assert.assertTrue(rangemap.get(50000) == null);

		rangemap.put(r2, "r2");
		Assert.assertTrue(rangemap.get(-10000) == null);
		Assert.assertTrue(rangemap.get(-1) == null);
		Assert.assertTrue(rangemap.get(0).equals("r1"));
		Assert.assertTrue(rangemap.get(1).equals("r1"));
		Assert.assertTrue(rangemap.get(2).equals("r1"));
		Assert.assertTrue(rangemap.get(3).equals("r2"));
		Assert.assertTrue(rangemap.get(4).equals("r2"));
		Assert.assertTrue(rangemap.get(5) == null);
		Assert.assertTrue(rangemap.get(50000) == null);

		rangemap.put(r3, "r3");
		Assert.assertTrue(rangemap.get(-10000) == null);
		Assert.assertTrue(rangemap.get(-1) == null);
		Assert.assertTrue(rangemap.get(0).equals("r1"));
		Assert.assertTrue(rangemap.get(1).equals("r1"));
		Assert.assertTrue(rangemap.get(2).equals("r1"));
		Assert.assertTrue(rangemap.get(3).equals("r2"));
		Assert.assertTrue(rangemap.get(4).equals("r2"));
		Assert.assertTrue(rangemap.get(5).equals("r3"));
		Assert.assertTrue(rangemap.get(6).equals("r3"));
		Assert.assertTrue(rangemap.get(7).equals("r3"));
		Assert.assertTrue(rangemap.get(8).equals("r3"));
		Assert.assertTrue(rangemap.get(9).equals("r3"));
		Assert.assertTrue(rangemap.get(10) == null);
		Assert.assertTrue(rangemap.get(50000) == null);

	}

	@Test
	public void testPutGetWithoutOverwriteOtherOrders()
	{
		rangemap.put(r1, "r1");
		rangemap.put(r3, "r3");
		rangemap.put(r2, "r2");
		Assert.assertTrue(rangemap.get(-10000) == null);
		Assert.assertTrue(rangemap.get(-1) == null);
		Assert.assertTrue(rangemap.get(0).equals("r1"));
		Assert.assertTrue(rangemap.get(1).equals("r1"));
		Assert.assertTrue(rangemap.get(2).equals("r1"));
		Assert.assertTrue(rangemap.get(3).equals("r2"));
		Assert.assertTrue(rangemap.get(4).equals("r2"));
		Assert.assertTrue(rangemap.get(5).equals("r3"));
		Assert.assertTrue(rangemap.get(6).equals("r3"));
		Assert.assertTrue(rangemap.get(7).equals("r3"));
		Assert.assertTrue(rangemap.get(8).equals("r3"));
		Assert.assertTrue(rangemap.get(9).equals("r3"));
		Assert.assertTrue(rangemap.get(10) == null);
		Assert.assertTrue(rangemap.get(50000) == null);

		rangemap.put(r3, "r3");
		rangemap.put(r2, "r2");
		rangemap.put(r1, "r1");
		Assert.assertTrue(rangemap.get(-10000) == null);
		Assert.assertTrue(rangemap.get(-1) == null);
		Assert.assertTrue(rangemap.get(0).equals("r1"));
		Assert.assertTrue(rangemap.get(1).equals("r1"));
		Assert.assertTrue(rangemap.get(2).equals("r1"));
		Assert.assertTrue(rangemap.get(3).equals("r2"));
		Assert.assertTrue(rangemap.get(4).equals("r2"));
		Assert.assertTrue(rangemap.get(5).equals("r3"));
		Assert.assertTrue(rangemap.get(6).equals("r3"));
		Assert.assertTrue(rangemap.get(7).equals("r3"));
		Assert.assertTrue(rangemap.get(8).equals("r3"));
		Assert.assertTrue(rangemap.get(9).equals("r3"));
		Assert.assertTrue(rangemap.get(10) == null);
		Assert.assertTrue(rangemap.get(50000) == null);

		rangemap.put(r3, "r3");
		rangemap.put(r1, "r1");
		rangemap.put(r2, "r2");
		Assert.assertTrue(rangemap.get(-10000) == null);
		Assert.assertTrue(rangemap.get(-1) == null);
		Assert.assertTrue(rangemap.get(0).equals("r1"));
		Assert.assertTrue(rangemap.get(1).equals("r1"));
		Assert.assertTrue(rangemap.get(2).equals("r1"));
		Assert.assertTrue(rangemap.get(3).equals("r2"));
		Assert.assertTrue(rangemap.get(4).equals("r2"));
		Assert.assertTrue(rangemap.get(5).equals("r3"));
		Assert.assertTrue(rangemap.get(6).equals("r3"));
		Assert.assertTrue(rangemap.get(7).equals("r3"));
		Assert.assertTrue(rangemap.get(8).equals("r3"));
		Assert.assertTrue(rangemap.get(9).equals("r3"));
		Assert.assertTrue(rangemap.get(10) == null);
		Assert.assertTrue(rangemap.get(50000) == null);

	}

	@Test
	public void testPutGetSimpleOverwrite()
	{
		rangemap.put(r1, "r1");
		Assert.assertTrue(rangemap.get(-10000) == null);
		Assert.assertTrue(rangemap.get(-1) == null);
		Assert.assertTrue(rangemap.get(0).equals("r1"));
		Assert.assertTrue(rangemap.get(1).equals("r1"));
		Assert.assertTrue(rangemap.get(2).equals("r1"));
		Assert.assertTrue(rangemap.get(3) == null);
		Assert.assertTrue(rangemap.get(50000) == null);

		rangemap.put(r1, "r1o1");
		Assert.assertTrue(rangemap.get(-10000) == null);
		Assert.assertTrue(rangemap.get(-1) == null);
		Assert.assertTrue(rangemap.get(0).equals("r1o1"));
		Assert.assertTrue(rangemap.get(1).equals("r1o1"));
		Assert.assertTrue(rangemap.get(2).equals("r1o1"));
		Assert.assertTrue(rangemap.get(3) == null);
		Assert.assertTrue(rangemap.get(50000) == null);

		rangemap.put(r1.translateRange(1), "r1o2");
		Assert.assertTrue(rangemap.get(-10000) == null);
		Assert.assertTrue(rangemap.get(-1) == null);
		Assert.assertTrue(rangemap.get(0).equals("r1o1"));
		Assert.assertTrue(rangemap.get(1).equals("r1o2"));
		Assert.assertTrue(rangemap.get(2).equals("r1o2"));
		Assert.assertTrue(rangemap.get(3).equals("r1o2"));
		Assert.assertTrue(rangemap.get(4) == null);
		Assert.assertTrue(rangemap.get(50000) == null);

		rangemap.put(r1.translateRange(-2), "r1o3");
		Assert.assertTrue(rangemap.get(-10000) == null);
		Assert.assertTrue(rangemap.get(-2) == null);
		Assert.assertTrue(rangemap.get(-1).equals("r1o3"));
		Assert.assertTrue(rangemap.get(0).equals("r1o3"));
		Assert.assertTrue(rangemap.get(1).equals("r1o3"));
		Assert.assertTrue(rangemap.get(2).equals("r1o2"));
		Assert.assertTrue(rangemap.get(3).equals("r1o2"));
		Assert.assertTrue(rangemap.get(4) == null);
		Assert.assertTrue(rangemap.get(50000) == null); /**/
	}

	@Test
	public void testPutGetComplexOverwrite()
	{
		rangemap.put(r1, "r1");
		rangemap.put(r2, "r2");
		rangemap.put(r3, "r3");
		rangemap.put(r4, "r4");
		Assert.assertTrue(rangemap.get(-10000) == null);
		Assert.assertTrue(rangemap.get(-1) == null);
		Assert.assertTrue(rangemap.get(0).equals("r1"));
		Assert.assertTrue(rangemap.get(1).equals("r1"));
		Assert.assertTrue(rangemap.get(2).equals("r4"));
		Assert.assertTrue(rangemap.get(3).equals("r4"));
		Assert.assertTrue(rangemap.get(4).equals("r4"));
		Assert.assertTrue(rangemap.get(5).equals("r4"));
		Assert.assertTrue(rangemap.get(6).equals("r4"));
		Assert.assertTrue(rangemap.get(7).equals("r4"));
		Assert.assertTrue(rangemap.get(8).equals("r3"));
		Assert.assertTrue(rangemap.get(9).equals("r3"));
		Assert.assertTrue(rangemap.get(10) == null);
		Assert.assertTrue(rangemap.get(50000) == null);

	}

	@Test
	public void testPutGetAllNull()
	{
		rangemap.put(r1, "r1");
		rangemap.put(r2, "r2");
		rangemap.put(r3, "r3");
		rangemap.put(r4, "r4");
		rangemap.put(rall, null);
		Assert.assertTrue(rangemap.get(-10000) == null);
		Assert.assertTrue(rangemap.get(-1) == null);
		Assert.assertTrue(rangemap.get(0) == null);
		Assert.assertTrue(rangemap.get(1) == null);
		Assert.assertTrue(rangemap.get(2) == null);
		Assert.assertTrue(rangemap.get(3) == null);
		Assert.assertTrue(rangemap.get(4) == null);
		Assert.assertTrue(rangemap.get(5) == null);
		Assert.assertTrue(rangemap.get(6) == null);
		Assert.assertTrue(rangemap.get(7) == null);
		Assert.assertTrue(rangemap.get(8) == null);
		Assert.assertTrue(rangemap.get(9) == null);
		Assert.assertTrue(rangemap.get(10) == null);
		Assert.assertTrue(rangemap.get(50000) == null);
	}

	@Test
	public void testItemReuse()
	{
		{
			rangemap.put(r12, "o");
			int lSizeBefore = rangemap.mList.size();
			rangemap.put(r1, "o");
			int lSizeAfter = rangemap.mList.size();

			// There should be no additional Item needed...
			Assert.assertTrue(lSizeBefore == lSizeAfter);
		}

		{
			rangemap.put(r123, "o");
			int lSizeBefore = rangemap.mList.size();
			rangemap.put(r2, "o");
			int lSizeAfter = rangemap.mList.size();

			// There should be no additional Item needed...
			Assert.assertTrue(lSizeBefore == lSizeAfter);
		}

		{
			rangemap.put(r123, "o");
			int lSizeBefore = rangemap.mList.size();
			rangemap.put(r3, "o");
			int lSizeAfter = rangemap.mList.size();

			// There should be no additional Item needed...
			Assert.assertTrue(lSizeBefore == lSizeAfter);
		}

	}

	@Test
	public void testGetFirstGetLast()
	{
		rangemap.put(r1, "r1");
		rangemap.put(r2, "r2");
		rangemap.put(r3, "r3");
		rangemap.put(r4, "r4");
		Assert.assertTrue(rangemap.getFirst().equals("r1"));
		Assert.assertTrue(rangemap.getLast().equals("r3"));
	}

	/**
	 * This Test is about checking any leaks of the tests. (Missing cases not
	 * covered in the tests) It generates a random maping and stores it in two
	 * ways, using an array and using the RangeMap. Any discrepancy is detected.
	 */
	@Test
	public void testBruteForce()
	{
		Random lRandom = new Random(System.currentTimeMillis());

		final int lNumberOfCycles = 100000;
		final int lDomain = 10000;
		final int lMaxlength = 100;

		final Integer[] lArray = new Integer[lDomain + lMaxlength];

		for (int i = 0; i < lNumberOfCycles; i++)
		{
			final Range lRange = Range.constructRangeWithStartLength(	lRandom.nextInt(lDomain),
																																lRandom.nextInt(lMaxlength));
			final Integer lInteger = lRandom.nextInt();
			rangemapinteger.put(lRange, lInteger);
			for (int j = lRange.mRangeStart; j < lRange.mRangeEnd; j++)
			{
				lArray[j] = lInteger;
			}
		}

		for (int i = 0; i < lDomain; i++)
		{
			Assert.assertTrue(lArray[i] == rangemapinteger.get(i));
		}

	}

	@Test
	public void testPutPerformance()
	{
		Random lRandom = new Random();

		final int lNumberOfCycles = 1000000;
		final int lDomain = 1000;
		final int lMaxlength = 1000;

		final long lStartTime = System.currentTimeMillis();
		for (int i = 0; i < lNumberOfCycles; i++)
		{
			final Range lRange = Range.constructRangeWithStartLength(	lRandom.nextInt(lDomain),
																																lRandom.nextInt(lMaxlength));
			rangemap.put(lRange, Integer.toString(lRandom.nextInt()));
		}
		final long lEndTime = System.currentTimeMillis();

		final long lElapsedTime = lEndTime - lStartTime;
		final double lPutsPerMilliseconds = ((double) lNumberOfCycles) / lElapsedTime;
		System.out.println("Puts per millisecond: "+lPutsPerMilliseconds);
		Assert.assertTrue(lPutsPerMilliseconds > 500);
	}

	@Test
	public void testGetPerformance()
	{
		Random lRandom = new Random();
		testPutPerformance();

		final int lNumberOfCycles = 1000000;
		final int lDomain = 1000;

		final long lStartTime = System.currentTimeMillis();
		for (int i = 0; i < lNumberOfCycles; i++)
		{
			rangemap.get(lRandom.nextInt(lDomain));
		}
		final long lEndTime = System.currentTimeMillis();

		final long lElapsedTime = lEndTime - lStartTime;
		final double lGetsPerMilliseconds = ((double) lNumberOfCycles) / lElapsedTime;
		System.out.println("Gets per milliseconds: "+lGetsPerMilliseconds);

		Assert.assertTrue(lGetsPerMilliseconds > 2500);
	}
}
