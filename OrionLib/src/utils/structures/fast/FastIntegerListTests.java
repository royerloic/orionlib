// ©2006 Transinsight GmbH - www.transinsight.com - All rights reserved.
package utils.structures.fast;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import utils.structures.fast.FastIntegerSet;

/**
 */
public class FastIntegerListTests
{

	@Test
	public void testLocate()
	{
		int[] lSet0 = new int[]
		{};

		assertSame(0, FastIntegerSet.locate(lSet0, 0));

		int[] lSet1 = new int[]
		{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		assertSame(0, FastIntegerSet.locate(lSet1, 0));
		assertSame(1, FastIntegerSet.locate(lSet1, 1));
		assertSame(2, FastIntegerSet.locate(lSet1, 2));
		assertSame(3, FastIntegerSet.locate(lSet1, 3));
		assertSame(4, FastIntegerSet.locate(lSet1, 4));
		assertSame(5, FastIntegerSet.locate(lSet1, 5));
		assertSame(6, FastIntegerSet.locate(lSet1, 6));
		assertSame(7, FastIntegerSet.locate(lSet1, 7));
		assertSame(8, FastIntegerSet.locate(lSet1, 8));
		assertSame(9, FastIntegerSet.locate(lSet1, 9));

		int[] lSet2 = new int[]
		{ 5, 6 };

		assertSame(0, FastIntegerSet.locate(lSet2, 1));
		assertSame(0, FastIntegerSet.locate(lSet2, 4));
		assertSame(1, FastIntegerSet.locate(lSet2, 7));
		assertSame(1, FastIntegerSet.locate(lSet2, 9));

	}

	@Test
	public void testAdd()
	{

		int[] lSet0 = new int[]
		{};

		lSet0 = FastIntegerSet.add(lSet0, 4);
		assertSame(lSet0[0], 4);
		assertSame(lSet0.length, 1);

		int[] lSet1 = new int[]
		{ 1, 2, 6, 7 };

		lSet1 = FastIntegerSet.add(lSet1, 4);
		assertSame(lSet1[2], 4);
		assertSame(lSet1.length, 5);

		lSet1 = FastIntegerSet.add(lSet1, 3);
		assertSame(lSet1[2], 3);
		assertSame(lSet1.length, 6);

		lSet1 = FastIntegerSet.add(lSet1, 5);
		assertSame(lSet1[4], 5);
		assertSame(lSet1.length, 7);
	}

	@Test
	public void testAddBorder()
	{
		int[] lSet1 = new int[]
		{ 1, 2, 3 };

		lSet1 = FastIntegerSet.add(lSet1, 4);
		assertSame(lSet1[3], 4);
		assertSame(lSet1.length, 4);
		lSet1 = FastIntegerSet.add(lSet1, 4);
		assertSame(lSet1[3], 4);
		assertSame(lSet1.length, 4);

		lSet1 = FastIntegerSet.add(lSet1, 0);
		assertSame(lSet1[0], 0);
		assertSame(lSet1.length, 5);
	}

	@Test
	public void testIntersection()
	{
		{
			int[] lSet0 = new int[]
			{};
			int[] lSet1 = new int[]
			{};
			int[] lSet2 = new int[]
			{ 4, 5, 6 };

			int[] lSetInter = FastIntegerSet.intersection(lSet0, lSet1);
			assertSame(0, lSetInter.length);

			int[] lSetInter2 = FastIntegerSet.intersection(lSet0, lSet1);
			assertSame(0, lSetInter2.length);

			int[] lSetInter3 = FastIntegerSet.intersection(lSet1, lSet0);
			assertSame(0, lSetInter3.length);
		}

		{
			int[] lSet1 = new int[]
			{ 1, 2, 3 };
			int[] lSet2 = new int[]
			{ 4, 5, 6 };

			int[] lSetInter = FastIntegerSet.intersection(lSet1, lSet2);
			assertSame(0, lSetInter.length);
			int[] lSetInter2 = FastIntegerSet.intersection(lSet2, lSet1);
			assertSame(0, lSetInter2.length);
		}

		{
			int[] lSet1 = new int[]
			{ 4, 5, 6 };
			int[] lSet2 = new int[]
			{ 3, 4, 5 };
			int[] lSetInter = FastIntegerSet.intersection(lSet1, lSet2);
			assertSame(2, lSetInter.length);
			assertSame(4, lSetInter[0]);
			assertSame(5, lSetInter[1]);
			int[] lSetInter2 = FastIntegerSet.intersection(lSet2, lSet1);
			assertSame(2, lSetInter2.length);
		}

		{
			int[] lSet1 = new int[]
			{ 1, 2, 3 };
			int[] lSet2 = new int[]
			{ 2 };

			int[] lSetInter = FastIntegerSet.intersection(lSet1, lSet2);
			assertSame(1, lSetInter.length);
			assertSame(2, lSetInter[0]);
			int[] lSetInter2 = FastIntegerSet.intersection(lSet2, lSet1);
			assertSame(1, lSetInter2.length);
		}

		{
			int[] lSet1 = new int[]
			{ 11, 12, 13 };
			int[] lSet2 = new int[]
			{ 1, 2, 3, 12 };

			int[] lSetInter = FastIntegerSet.intersection(lSet1, lSet2);
			assertSame(1, lSetInter.length);
			assertSame(12, lSetInter[0]);
			int[] lSetInter2 = FastIntegerSet.intersection(lSet2, lSet1);
			assertSame(1, lSetInter2.length);
			assertSame(12, lSetInter2[0]);
		}

		{
			int[] lSet1 = new int[]
			{ 1, 2, 3, 4, 10 };
			int[] lSet2 = new int[]
			{ 1, 2, 3, 5, 10 };

			int[] lSetInter = FastIntegerSet.intersection(lSet1, lSet2);
			assertSame(4, lSetInter.length);
			assertSame(10, lSetInter[lSetInter.length - 1]);
			int[] lSetInter2 = FastIntegerSet.intersection(lSet2, lSet1);
			assertSame(4, lSetInter2.length);
			assertSame(10, lSetInter2[lSetInter2.length - 1]);
		}
	}

	@Test
	public void testUnion()
	{
		{
			int[] lSet0 = new int[]
			{};
			int[] lSet1 = new int[]
			{};
			int[] lSet2 = new int[]
			{ 4, 5, 6 };

			int[] lSetUnion1 = FastIntegerSet.union(lSet0, lSet1);
			assertSame(0, lSetUnion1.length);
			assertTrue(FastIntegerSet.equals(lSetUnion1, new int[]
			{}));

			int[] lSetUnion2 = FastIntegerSet.union(lSet1, lSet2);
			assertSame(3, lSetUnion2.length);
			assertTrue(FastIntegerSet.equals(lSetUnion2, new int[]
			{ 4, 5, 6 }));

			int[] lSetUnion3 = FastIntegerSet.union(lSet2, lSet1);
			assertSame(3, lSetUnion3.length);
			assertTrue(FastIntegerSet.equals(lSetUnion3, new int[]
			{ 4, 5, 6 }));
		}

		{
			int[] lSet1 = new int[]
			{ 1, 2, 3 };
			int[] lSet2 = new int[]
			{ 4, 5, 6 };

			int[] lSetUnion = FastIntegerSet.union(lSet1, lSet2);
			assertTrue(FastIntegerSet.equals(lSetUnion, new int[]
			{ 1, 2, 3, 4, 5, 6 }));

			int[] lSetUnion2 = FastIntegerSet.union(lSet2, lSet1);
			assertTrue(FastIntegerSet.equals(lSetUnion2, new int[]
			{ 1, 2, 3, 4, 5, 6 }));
		}

		{
			int[] lSet1 = new int[]
			{ 4, 5, 6 };
			int[] lSet2 = new int[]
			{ 3, 4, 5 };
			int[] lSetUnion = FastIntegerSet.union(lSet1, lSet2);
			assertSame(4, lSetUnion.length);
			assertSame(3, lSetUnion[0]);
			assertSame(6, lSetUnion[3]);
			int[] lSetUnion2 = FastIntegerSet.union(lSet2, lSet1);
			assertSame(4, lSetUnion2.length);
		}

		{
			int[] lSet1 = new int[]
			{ 1, 2, 3 };
			int[] lSet2 = new int[]
			{ 2 };

			int[] lSetUnion = FastIntegerSet.union(lSet1, lSet2);
			assertSame(3, lSetUnion.length);
			assertSame(1, lSetUnion[0]);
			assertSame(2, lSetUnion[1]);
			assertSame(3, lSetUnion[2]);
			int[] lSetUnion2 = FastIntegerSet.union(lSet2, lSet1);
			assertSame(3, lSetUnion2.length);
		}

		{
			int[] lSet1 = new int[]
			{ 11, 12, 13 };
			int[] lSet2 = new int[]
			{ 1, 2, 3, 12 };

			int[] lSetUnion = FastIntegerSet.union(lSet1, lSet2);
			assertSame(6, lSetUnion.length);
			assertSame(1, lSetUnion[0]);
			assertSame(3, lSetUnion[2]);
			assertSame(13, lSetUnion[5]);
			int[] lSetUnion2 = FastIntegerSet.union(lSet2, lSet1);
			assertSame(6, lSetUnion2.length);
			assertSame(1, lSetUnion2[0]);
			assertSame(3, lSetUnion2[2]);
			assertSame(13, lSetUnion2[5]);
		}

		{
			int[] lSet1 = new int[]
			{ 1, 2, 3, 4, 10 };
			int[] lSet2 = new int[]
			{ 1, 2, 3, 5, 10 };

			int[] lSetUnion = FastIntegerSet.union(lSet1, lSet2);
			assertSame(6, lSetUnion.length);
			assertSame(1, lSetUnion[0]);
			assertSame(10, lSetUnion[5]);
			int[] lSetUnion2 = FastIntegerSet.union(lSet2, lSet1);
			assertSame(6, lSetUnion2.length);
			assertSame(1, lSetUnion2[0]);
			assertSame(10, lSetUnion2[5]);
		}
		/**/
	}

	@Test
	public void testDifference()
	{

		{
			int[] lSet0 = new int[]
			{};
			int[] lSet1 = new int[]
			{};
			int[] lSet2 = new int[]
			{ 4, 5, 6 };

			int[] lSetDiff1 = FastIntegerSet.difference(lSet0, lSet1);
			assertSame(0, lSetDiff1.length);
			assertTrue(FastIntegerSet.equals(lSetDiff1, new int[]
			{}));

			int[] lSetDiff2 = FastIntegerSet.difference(lSet2, lSet1);
			assertSame(3, lSetDiff2.length);
			assertTrue(FastIntegerSet.equals(lSetDiff2, new int[]
			{ 4, 5, 6 }));
			
			int[] lSetDiff3 = FastIntegerSet.difference(lSet1, lSet2);
			assertSame(0, lSetDiff3.length);
			assertTrue(FastIntegerSet.equals(lSetDiff3, new int[]
			{}));
		}

		{
			int[] lSet1 = new int[]
			{ 1, 2, 3 };
			int[] lSet2 = new int[]
			{ 4, 5, 6 };

			int[] lSetDiff = FastIntegerSet.difference(lSet1, lSet2);
			assertTrue(FastIntegerSet.equals(lSetDiff, new int[]
			{ 1, 2, 3 }));

			int[] lSetDiff2 = FastIntegerSet.difference(lSet2, lSet1);
			assertTrue(FastIntegerSet.equals(lSetDiff2, new int[]
			{ 4, 5, 6 }));
		}

		{
			int[] lSet1 = new int[]
			{ 4, 5, 6 };
			int[] lSet2 = new int[]
			{ 3, 4, 5 };
			int[] lSetDiff = FastIntegerSet.difference(lSet1, lSet2);
			assertSame(1, lSetDiff.length);
			assertSame(6, lSetDiff[0]);

			int[] lSetDiff2 = FastIntegerSet.difference(lSet2, lSet1);
			assertSame(1, lSetDiff2.length);
			assertSame(3, lSetDiff2[0]);
		}

		{
			int[] lSet1 = new int[]
			{ 1, 2, 3 };
			int[] lSet2 = new int[]
			{ 2 };

			int[] lSetDiff = FastIntegerSet.difference(lSet1, lSet2);
			assertSame(2, lSetDiff.length);
			assertSame(1, lSetDiff[0]);
			assertSame(3, lSetDiff[1]);

			int[] lSetDiff2 = FastIntegerSet.difference(lSet2, lSet1);
			assertSame(0, lSetDiff2.length);
		}

		{
			int[] lSet1 = new int[]
			{ 11, 12, 13 };
			int[] lSet2 = new int[]
			{ 1, 2, 3, 12 };

			int[] lSetDiff = FastIntegerSet.difference(lSet1, lSet2);
			assertSame(2, lSetDiff.length);
			assertSame(11, lSetDiff[0]);
			assertSame(13, lSetDiff[1]);

			int[] lSetDiff2 = FastIntegerSet.difference(lSet2, lSet1);
			assertSame(3, lSetDiff2.length);
			assertSame(1, lSetDiff2[0]);
			assertSame(2, lSetDiff2[1]);
			assertSame(3, lSetDiff2[2]);
		}

		{
			int[] lSet1 = new int[]
			{ 1, 2, 3, 4, 10 };
			int[] lSet2 = new int[]
			{ 1, 2, 3, 5, 10 };

			int[] lSetDiff = FastIntegerSet.difference(lSet1, lSet2);
			assertSame(1, lSetDiff.length);
			assertSame(4, lSetDiff[0]);

			int[] lSetDiff2 = FastIntegerSet.difference(lSet2, lSet1);
			assertSame(1, lSetDiff2.length);
			assertSame(5, lSetDiff2[0]);
		}

		{
			int[] lSet1 = new int[]
			{ 1, 2, 3, 4, 10 };

			int[] lSetDiff = FastIntegerSet.difference(lSet1, lSet1);
			assertSame(0, lSetDiff.length);
		}
		/**/
	}

	static final int	cDomainSize			= 1000000;
	static final int	cNumberOfSets		= 20;
	static final int	cNumberOfCycles	= 100;

	@Test
	@Ignore
	// call from main methode only
	public void testPerformance()
	{
		Random lRandom = new Random();

		// System.out.println("generating sets");
		int[][] lList = new int[cNumberOfSets][];
		for (int i = 0; i < cNumberOfSets; i++)
		{
			lList[i] = FastIntegerSet.random(lRandom, cDomainSize, lRandom.nextDouble() * 0.01);
			// System.out.println(lList[i].length);
		}

		{
			// System.out.println("Start Union Perf test");
			long lStartTime = System.currentTimeMillis();

			for (int c = 0; c < cNumberOfCycles; c++)
			{
				for (int i = 0; i < cNumberOfSets; i++)
					for (int j = 0; j < cNumberOfSets; j++)
					{
						// System.out.println(".");
						final int[] lSet1 = lList[i];
						final int[] lSet2 = lList[j];
						FastIntegerSet.union(lSet1, lSet2);
					}
				// System.out.print(".");
			}

			long lEndTime = System.currentTimeMillis();
			// System.out.println("End");
			long lElapsedTime = lEndTime - lStartTime;

			final double lUnionsPerMillisecond = ((double) (cNumberOfCycles * cNumberOfSets * cNumberOfSets))
					/ lElapsedTime;

			System.out.println("Unions per Milliseconds = " + lUnionsPerMillisecond);
			assertTrue(lUnionsPerMillisecond > 4);
		}

		{
			// System.out.println("Start Intersection Perf test");
			long lStartTime = System.currentTimeMillis();

			for (int c = 0; c < cNumberOfCycles; c++)
			{
				for (int i = 0; i < cNumberOfSets; i++)
					for (int j = 0; j < cNumberOfSets; j++)
					{
						// System.out.println(".");
						final int[] lSet1 = lList[i];
						final int[] lSet2 = lList[j];
						FastIntegerSet.intersection(lSet1, lSet2);
					}
				// System.out.print(".");
			}

			long lEndTime = System.currentTimeMillis();
			// System.out.println("End");
			long lElapsedTime = lEndTime - lStartTime;

			final double lIntersectionsPerMillisecond = ((double) (cNumberOfCycles * cNumberOfSets * cNumberOfSets))
					/ lElapsedTime;

			System.out.println("Intersections per Milliseconds = " + lIntersectionsPerMillisecond);

			assertTrue(lIntersectionsPerMillisecond > 8);
		}

		{
			// System.out.println("Start Difference Perf test");
			long lStartTime = System.currentTimeMillis();

			for (int c = 0; c < cNumberOfCycles; c++)
			{
				for (int i = 0; i < cNumberOfSets; i++)
					for (int j = 0; j < cNumberOfSets; j++)
					{
						// System.out.println(".");
						final int[] lSet1 = lList[i];
						final int[] lSet2 = lList[j];
						FastIntegerSet.difference(lSet1, lSet2);
					}
				// System.out.print(".");
			}

			long lEndTime = System.currentTimeMillis();
			// System.out.println("End");
			long lElapsedTime = lEndTime - lStartTime;

			final double lDifferencesPerMillisecond = ((double) (cNumberOfCycles * cNumberOfSets * cNumberOfSets))
					/ lElapsedTime;

			System.out.println("Differences per Milliseconds = " + lDifferencesPerMillisecond);

			assertTrue(lDifferencesPerMillisecond > 6);
		}

		/**/
	}

}
