package utils.structures.fast.set.old;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

/**
 */
public class FastIntegerSetStaticTests
{

	@Test
	public void testLocate()
	{
		final int[] lSet0 = new int[] {};
		assertSame(0, FastIntegerSetStatic.locate(lSet0, 0));

		final int[] lSet1 = new int[]
		{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		assertSame(0, FastIntegerSetStatic.locate(lSet1, 0));
		assertSame(1, FastIntegerSetStatic.locate(lSet1, 1));
		assertSame(2, FastIntegerSetStatic.locate(lSet1, 2));
		assertSame(3, FastIntegerSetStatic.locate(lSet1, 3));
		assertSame(4, FastIntegerSetStatic.locate(lSet1, 4));
		assertSame(5, FastIntegerSetStatic.locate(lSet1, 5));
		assertSame(6, FastIntegerSetStatic.locate(lSet1, 6));
		assertSame(7, FastIntegerSetStatic.locate(lSet1, 7));
		assertSame(8, FastIntegerSetStatic.locate(lSet1, 8));
		assertSame(9, FastIntegerSetStatic.locate(lSet1, 9));

		final int[] lSet2 = new int[]
		{ 5, 6 };

		assertSame(0, FastIntegerSetStatic.locate(lSet2, 1));
		assertSame(0, FastIntegerSetStatic.locate(lSet2, 4));
		assertSame(1, FastIntegerSetStatic.locate(lSet2, 7));
		assertSame(1, FastIntegerSetStatic.locate(lSet2, 9));

	}

	@Test
	public void testContains()
	{
		final int[] lSet0 = new int[] {};

		assertFalse(FastIntegerSetStatic.contains(lSet0, 0));

		final int[] lSet1 = new int[]
		{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		assertTrue(FastIntegerSetStatic.contains(lSet1, 0));
		assertTrue(FastIntegerSetStatic.contains(lSet1, 1));
		assertTrue(FastIntegerSetStatic.contains(lSet1, 2));
		assertTrue(FastIntegerSetStatic.contains(lSet1, 3));
		assertTrue(FastIntegerSetStatic.contains(lSet1, 4));
		assertTrue(FastIntegerSetStatic.contains(lSet1, 5));
		assertTrue(FastIntegerSetStatic.contains(lSet1, 6));
		assertTrue(FastIntegerSetStatic.contains(lSet1, 7));
		assertTrue(FastIntegerSetStatic.contains(lSet1, 8));
		assertTrue(FastIntegerSetStatic.contains(lSet1, 9));

		final int[] lSet2 = new int[]
		{ 5, 6 };

		assertFalse(FastIntegerSetStatic.contains(lSet2, 1));
		assertFalse(FastIntegerSetStatic.contains(lSet2, 4));
		assertFalse(FastIntegerSetStatic.contains(lSet2, 7));
		assertFalse(FastIntegerSetStatic.contains(lSet2, 9));

	}

	@Test
	public void testAdd()
	{

		int[] lSet0 = new int[] {};

		lSet0 = FastIntegerSetStatic.add(lSet0, 4);
		assertSame(lSet0[0], 4);
		assertSame(lSet0.length, 1);

		int[] lSet1 = new int[]
		{ 1, 2, 6, 7 };

		lSet1 = FastIntegerSetStatic.add(lSet1, 4);
		assertSame(lSet1[2], 4);
		assertSame(lSet1.length, 5);

		lSet1 = FastIntegerSetStatic.add(lSet1, 3);
		assertSame(lSet1[2], 3);
		assertSame(lSet1.length, 6);

		lSet1 = FastIntegerSetStatic.add(lSet1, 5);
		assertSame(lSet1[4], 5);
		assertSame(lSet1.length, 7);
	}

	@Test
	public void testDel()
	{

		int[] lSet0 = new int[] {};

		lSet0 = FastIntegerSetStatic.del(lSet0, 4);
		assertSame(lSet0.length, 0);

		int[] lSet1 = new int[]
		{ 1, 2, 6, 7 };

		lSet1 = FastIntegerSetStatic.del(lSet1, 2);
		assertSame(lSet1[2], 7);
		assertSame(lSet1.length, 3);

		lSet1 = FastIntegerSetStatic.del(lSet1, 6);
		assertSame(lSet1[1], 7);
		assertSame(lSet1.length, 2);

		lSet1 = FastIntegerSetStatic.del(lSet1, 10);
		assertSame(lSet1[0], 1);
		assertSame(lSet1.length, 2);

		lSet1 = FastIntegerSetStatic.del(lSet1, 1);
		assertSame(lSet1[0], 7);
		assertSame(lSet1.length, 1);

		lSet1 = FastIntegerSetStatic.del(lSet1, 7);
		assertSame(lSet1.length, 0);
	}

	@Test
	public void testAddBorder()
	{
		int[] lSet1 = new int[]
		{ 1, 2, 3 };

		lSet1 = FastIntegerSetStatic.add(lSet1, 4);
		assertSame(lSet1[3], 4);
		assertSame(lSet1.length, 4);
		lSet1 = FastIntegerSetStatic.add(lSet1, 4);
		assertSame(lSet1[3], 4);
		assertSame(lSet1.length, 4);

		lSet1 = FastIntegerSetStatic.add(lSet1, 0);
		assertSame(lSet1[0], 0);
		assertSame(lSet1.length, 5);
	}

	@Test
	public void testIntersection()
	{
		{
			final int[] lSet0 = new int[] {};
			final int[] lSet1 = new int[] {};
			final int[] lSetInter = FastIntegerSetStatic.intersection(lSet0, lSet1);
			assertSame(0, lSetInter.length);

			final int[] lSetInter2 = FastIntegerSetStatic.intersection(lSet0, lSet1);
			assertSame(0, lSetInter2.length);

			final int[] lSetInter3 = FastIntegerSetStatic.intersection(lSet1, lSet0);
			assertSame(0, lSetInter3.length);
		}

		{
			final int[] lSet1 = new int[]
			{ 1, 2, 3 };
			final int[] lSet2 = new int[]
			{ 4, 5, 6 };

			final int[] lSetInter = FastIntegerSetStatic.intersection(lSet1, lSet2);
			assertSame(0, lSetInter.length);
			final int[] lSetInter2 = FastIntegerSetStatic.intersection(lSet2, lSet1);
			assertSame(0, lSetInter2.length);
		}

		{
			final int[] lSet1 = new int[]
			{ 4, 5, 6 };
			final int[] lSet2 = new int[]
			{ 3, 4, 5 };
			final int[] lSetInter = FastIntegerSetStatic.intersection(lSet1, lSet2);
			assertSame(2, lSetInter.length);
			assertSame(4, lSetInter[0]);
			assertSame(5, lSetInter[1]);
			final int[] lSetInter2 = FastIntegerSetStatic.intersection(lSet2, lSet1);
			assertSame(2, lSetInter2.length);
		}

		{
			final int[] lSet1 = new int[]
			{ 1, 2, 3 };
			final int[] lSet2 = new int[]
			{ 2 };

			final int[] lSetInter = FastIntegerSetStatic.intersection(lSet1, lSet2);
			assertSame(1, lSetInter.length);
			assertSame(2, lSetInter[0]);
			final int[] lSetInter2 = FastIntegerSetStatic.intersection(lSet2, lSet1);
			assertSame(1, lSetInter2.length);
		}

		{
			final int[] lSet1 = new int[]
			{ 11, 12, 13 };
			final int[] lSet2 = new int[]
			{ 1, 2, 3, 12 };

			final int[] lSetInter = FastIntegerSetStatic.intersection(lSet1, lSet2);
			assertSame(1, lSetInter.length);
			assertSame(12, lSetInter[0]);
			final int[] lSetInter2 = FastIntegerSetStatic.intersection(lSet2, lSet1);
			assertSame(1, lSetInter2.length);
			assertSame(12, lSetInter2[0]);
		}

		{
			final int[] lSet1 = new int[]
			{ 1, 2, 3, 4, 10 };
			final int[] lSet2 = new int[]
			{ 1, 2, 3, 5, 10 };

			final int[] lSetInter = FastIntegerSetStatic.intersection(lSet1, lSet2);
			assertSame(4, lSetInter.length);
			assertSame(10, lSetInter[lSetInter.length - 1]);
			final int[] lSetInter2 = FastIntegerSetStatic.intersection(lSet2, lSet1);
			assertSame(4, lSetInter2.length);
			assertSame(10, lSetInter2[lSetInter2.length - 1]);
		}
	}

	@Test
	public void testUnion()
	{
		{
			final int[] lSet0 = new int[] {};
			final int[] lSet1 = new int[] {};
			final int[] lSet2 = new int[]
			{ 4, 5, 6 };

			final int[] lSetUnion1 = FastIntegerSetStatic.union(lSet0, lSet1);
			assertSame(0, lSetUnion1.length);
			assertTrue(FastIntegerSetStatic.equals(lSetUnion1, new int[] {}));

			final int[] lSetUnion2 = FastIntegerSetStatic.union(lSet1, lSet2);
			assertSame(3, lSetUnion2.length);
			assertTrue(FastIntegerSetStatic.equals(lSetUnion2, new int[]
			{ 4, 5, 6 }));

			final int[] lSetUnion3 = FastIntegerSetStatic.union(lSet2, lSet1);
			assertSame(3, lSetUnion3.length);
			assertTrue(FastIntegerSetStatic.equals(lSetUnion3, new int[]
			{ 4, 5, 6 }));
		}

		{
			final int[] lSet1 = new int[]
			{ 1, 2, 3 };
			final int[] lSet2 = new int[]
			{ 4, 5, 6 };

			final int[] lSetUnion = FastIntegerSetStatic.union(lSet1, lSet2);
			assertTrue(FastIntegerSetStatic.equals(lSetUnion, new int[]
			{ 1, 2, 3, 4, 5, 6 }));

			final int[] lSetUnion2 = FastIntegerSetStatic.union(lSet2, lSet1);
			assertTrue(FastIntegerSetStatic.equals(lSetUnion2, new int[]
			{ 1, 2, 3, 4, 5, 6 }));
		}

		{
			final int[] lSet1 = new int[]
			{ 4, 5, 6 };
			final int[] lSet2 = new int[]
			{ 3, 4, 5 };
			final int[] lSetUnion = FastIntegerSetStatic.union(lSet1, lSet2);
			assertSame(4, lSetUnion.length);
			assertSame(3, lSetUnion[0]);
			assertSame(6, lSetUnion[3]);
			final int[] lSetUnion2 = FastIntegerSetStatic.union(lSet2, lSet1);
			assertSame(4, lSetUnion2.length);
		}

		{
			final int[] lSet1 = new int[]
			{ 1, 2, 3 };
			final int[] lSet2 = new int[]
			{ 2 };

			final int[] lSetUnion = FastIntegerSetStatic.union(lSet1, lSet2);
			assertSame(3, lSetUnion.length);
			assertSame(1, lSetUnion[0]);
			assertSame(2, lSetUnion[1]);
			assertSame(3, lSetUnion[2]);
			final int[] lSetUnion2 = FastIntegerSetStatic.union(lSet2, lSet1);
			assertSame(3, lSetUnion2.length);
		}

		{
			final int[] lSet1 = new int[]
			{ 11, 12, 13 };
			final int[] lSet2 = new int[]
			{ 1, 2, 3, 12 };

			final int[] lSetUnion = FastIntegerSetStatic.union(lSet1, lSet2);
			assertSame(6, lSetUnion.length);
			assertSame(1, lSetUnion[0]);
			assertSame(3, lSetUnion[2]);
			assertSame(13, lSetUnion[5]);
			final int[] lSetUnion2 = FastIntegerSetStatic.union(lSet2, lSet1);
			assertSame(6, lSetUnion2.length);
			assertSame(1, lSetUnion2[0]);
			assertSame(3, lSetUnion2[2]);
			assertSame(13, lSetUnion2[5]);
		}

		{
			final int[] lSet1 = new int[]
			{ 1, 2, 3, 4, 10 };
			final int[] lSet2 = new int[]
			{ 1, 2, 3, 5, 10 };

			final int[] lSetUnion = FastIntegerSetStatic.union(lSet1, lSet2);
			assertSame(6, lSetUnion.length);
			assertSame(1, lSetUnion[0]);
			assertSame(10, lSetUnion[5]);
			final int[] lSetUnion2 = FastIntegerSetStatic.union(lSet2, lSet1);
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
			final int[] lSet0 = new int[] {};
			final int[] lSet1 = new int[] {};
			final int[] lSet2 = new int[]
			{ 4, 5, 6 };

			final int[] lSetDiff1 = FastIntegerSetStatic.difference(lSet0, lSet1);
			assertSame(0, lSetDiff1.length);
			assertTrue(FastIntegerSetStatic.equals(lSetDiff1, new int[] {}));

			final int[] lSetDiff2 = FastIntegerSetStatic.difference(lSet2, lSet1);
			assertSame(3, lSetDiff2.length);
			assertTrue(FastIntegerSetStatic.equals(lSetDiff2, new int[]
			{ 4, 5, 6 }));

			final int[] lSetDiff3 = FastIntegerSetStatic.difference(lSet1, lSet2);
			assertSame(0, lSetDiff3.length);
			assertTrue(FastIntegerSetStatic.equals(lSetDiff3, new int[] {}));
		}

		{
			final int[] lSet1 = new int[]
			{ 1, 2, 3 };
			final int[] lSet2 = new int[]
			{ 4, 5, 6 };

			final int[] lSetDiff = FastIntegerSetStatic.difference(lSet1, lSet2);
			assertTrue(FastIntegerSetStatic.equals(lSetDiff, new int[]
			{ 1, 2, 3 }));

			final int[] lSetDiff2 = FastIntegerSetStatic.difference(lSet2, lSet1);
			assertTrue(FastIntegerSetStatic.equals(lSetDiff2, new int[]
			{ 4, 5, 6 }));
		}

		{
			final int[] lSet1 = new int[]
			{ 4, 5, 6 };
			final int[] lSet2 = new int[]
			{ 3, 4, 5 };
			final int[] lSetDiff = FastIntegerSetStatic.difference(lSet1, lSet2);
			assertSame(1, lSetDiff.length);
			assertSame(6, lSetDiff[0]);

			final int[] lSetDiff2 = FastIntegerSetStatic.difference(lSet2, lSet1);
			assertSame(1, lSetDiff2.length);
			assertSame(3, lSetDiff2[0]);
		}

		{
			final int[] lSet1 = new int[]
			{ 1, 2, 3 };
			final int[] lSet2 = new int[]
			{ 2 };

			final int[] lSetDiff = FastIntegerSetStatic.difference(lSet1, lSet2);
			assertSame(2, lSetDiff.length);
			assertSame(1, lSetDiff[0]);
			assertSame(3, lSetDiff[1]);

			final int[] lSetDiff2 = FastIntegerSetStatic.difference(lSet2, lSet1);
			assertSame(0, lSetDiff2.length);
		}

		{
			final int[] lSet1 = new int[]
			{ 11, 12, 13 };
			final int[] lSet2 = new int[]
			{ 1, 2, 3, 12 };

			final int[] lSetDiff = FastIntegerSetStatic.difference(lSet1, lSet2);
			assertSame(2, lSetDiff.length);
			assertSame(11, lSetDiff[0]);
			assertSame(13, lSetDiff[1]);

			final int[] lSetDiff2 = FastIntegerSetStatic.difference(lSet2, lSet1);
			assertSame(3, lSetDiff2.length);
			assertSame(1, lSetDiff2[0]);
			assertSame(2, lSetDiff2[1]);
			assertSame(3, lSetDiff2[2]);
		}

		{
			final int[] lSet1 = new int[]
			{ 1, 2, 3, 4, 10 };
			final int[] lSet2 = new int[]
			{ 1, 2, 3, 5, 10 };

			final int[] lSetDiff = FastIntegerSetStatic.difference(lSet1, lSet2);
			assertSame(1, lSetDiff.length);
			assertSame(4, lSetDiff[0]);

			final int[] lSetDiff2 = FastIntegerSetStatic.difference(lSet2, lSet1);
			assertSame(1, lSetDiff2.length);
			assertSame(5, lSetDiff2[0]);
		}

		{
			final int[] lSet1 = new int[]
			{ 1, 2, 3, 4, 10 };

			final int[] lSetDiff = FastIntegerSetStatic.difference(lSet1, lSet1);
			assertSame(0, lSetDiff.length);
		}
		/**/
	}

	static final int cDomainSize = 1000000;
	static final int cNumberOfSets = 20;
	static final int cNumberOfCycles = 100;

	@Test
	@Ignore
	// call from main methode only
	public void testPerformance()
	{
		final Random lRandom = new Random();

		// System.out.println("generating sets");
		final int[][] lList = new int[cNumberOfSets][];
		for (int i = 0; i < cNumberOfSets; i++)
		{
			lList[i] = FastIntegerSetStatic.random(	lRandom,
																							cDomainSize,
																							lRandom.nextDouble() * 0.01);
			// System.out.println(lList[i].length);
		}

		{
			// System.out.println("Start Union Perf Test");
			final long lStartTime = System.currentTimeMillis();

			for (int c = 0; c < cNumberOfCycles; c++)
			{
				for (int i = 0; i < cNumberOfSets; i++)
				{
					for (int j = 0; j < cNumberOfSets; j++)
					{
						// System.out.println(".");
						final int[] lSet1 = lList[i];
						final int[] lSet2 = lList[j];
						FastIntegerSetStatic.union(lSet1, lSet2);
					}
					// System.out.print(".");
				}
			}

			final long lEndTime = System.currentTimeMillis();
			// System.out.println("End");
			final long lElapsedTime = lEndTime - lStartTime;

			final double lUnionsPerMillisecond = (double) (cNumberOfCycles * cNumberOfSets * cNumberOfSets) / lElapsedTime;

			System.out.println("Unions per Milliseconds = " + lUnionsPerMillisecond);
			assertTrue(lUnionsPerMillisecond > 4);
		}

		{
			// System.out.println("Start Intersection Perf Test");
			final long lStartTime = System.currentTimeMillis();

			for (int c = 0; c < cNumberOfCycles; c++)
			{
				for (int i = 0; i < cNumberOfSets; i++)
				{
					for (int j = 0; j < cNumberOfSets; j++)
					{
						// System.out.println(".");
						final int[] lSet1 = lList[i];
						final int[] lSet2 = lList[j];
						FastIntegerSetStatic.intersection(lSet1, lSet2);
					}
					// System.out.print(".");
				}
			}

			final long lEndTime = System.currentTimeMillis();
			// System.out.println("End");
			final long lElapsedTime = lEndTime - lStartTime;

			final double lIntersectionsPerMillisecond = (double) (cNumberOfCycles * cNumberOfSets * cNumberOfSets) / lElapsedTime;

			System.out.println("Intersections per Milliseconds = " + lIntersectionsPerMillisecond);

			assertTrue(lIntersectionsPerMillisecond > 8);
		}

		{
			// System.out.println("Start Difference Perf Test");
			final long lStartTime = System.currentTimeMillis();

			for (int c = 0; c < cNumberOfCycles; c++)
			{
				for (int i = 0; i < cNumberOfSets; i++)
				{
					for (int j = 0; j < cNumberOfSets; j++)
					{
						// System.out.println(".");
						final int[] lSet1 = lList[i];
						final int[] lSet2 = lList[j];
						FastIntegerSetStatic.difference(lSet1, lSet2);
					}
					// System.out.print(".");
				}
			}

			final long lEndTime = System.currentTimeMillis();
			// System.out.println("End");
			final long lElapsedTime = lEndTime - lStartTime;

			final double lDifferencesPerMillisecond = (double) (cNumberOfCycles * cNumberOfSets * cNumberOfSets) / lElapsedTime;

			System.out.println("Differences per Milliseconds = " + lDifferencesPerMillisecond);

			assertTrue(lDifferencesPerMillisecond > 6);
		}

		/**/
	}

}
