package utils.structures.fast.set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Random;

import org.junit.Test;

/**
 */
public class FastSparseIntegerSetTests
{
	@Test
	public void testArrayConstructor()
	{
		final FastSparseIntegerSet set1 = new FastSparseIntegerSet(	0,
																																0,
																																1,
																																2,
																																3,
																																4,
																																5,
																																6,
																																7,
																																8,
																																9);

		assertEquals(10, set1.size());
		for (int i = 0; i < 10; i++)
		{
			assertTrue(set1.contains(i));
		}

		final FastSparseIntegerSet set2 = new FastSparseIntegerSet(	1,
																																0,
																																1,
																																2,
																																3,
																																0,
																																5,
																																5,
																																6,
																																7,
																																8,
																																9,
																																4,
																																3);
		assertEquals(10, set2.size());
		for (int i = 0; i < 10; i++)
		{
			assertTrue(set2.contains(i));
		}

	}

	@Test
	public void testCopyConstructor()
	{
		final FastSparseIntegerSet set1 = new FastSparseIntegerSet(	0,
																																0,
																																1,
																																2,
																																3,
																																4,
																																5,
																																6,
																																7,
																																8,
																																9);
		final FastSparseIntegerSet copyset1 = new FastSparseIntegerSet(set1);
		assertEquals(set1, copyset1);
	}

	@Test
	public void testContains()
	{
		final FastSparseIntegerSet set2 = new FastSparseIntegerSet(	1,
																																0,
																																1,
																																2,
																																3,
																																0,
																																5,
																																5,
																																6,
																																7,
																																8,
																																9,
																																4,
																																3);
		assertEquals(10, set2.size());
		for (int i = 0; i < 10; i++)
		{
			assertTrue(set2.contains(i));
		}
	}

	@Test
	public void testAdd()
	{

		final FastSparseIntegerSet set1 = new FastSparseIntegerSet();
		assertEquals(0, set1.size());
		for (int i = 0; i < 100; i++)
		{
			assertTrue(set1.add(i));
		}
		assertEquals(100, set1.size());
		for (int i = 0; i < 100; i++)
		{
			assertFalse(set1.add(i));
		}
		assertEquals(100, set1.size());

		set1.clear();
		for (int i = 0; i < 101; i++)
		{
			assertTrue(set1.add(i * 10 % 101));
		}
		assertEquals(101, set1.size());
		for (int i = 0; i < 101; i++)
		{
			assertFalse(set1.add(i * 10 % 101));
		}
		assertEquals(101, set1.size());
		System.out.println(set1);
		for (int i = 0; i < 101; i++)
		{
			assertTrue(set1.contains(i));
		}

	}

	@Test
	public void testDel()
	{

		final FastSparseIntegerSet set1 = new FastSparseIntegerSet();
		assertEquals(0, set1.size());
		for (int i = 0; i < 100; i++)
		{
			assertTrue(set1.add(i));
		}
		assertEquals(100, set1.size());
		for (int i = 0; i < 100; i++)
		{
			assertTrue(set1.del(i));
		}
		assertEquals(0, set1.size());

		set1.clear();
		for (int i = 0; i < 101; i++)
		{
			assertTrue(set1.add(i * 10 % 101));
		}
		assertEquals(101, set1.size());
		for (int i = 0; i < 101; i++)
		{
			assertTrue(set1.del(i * 10 % 101));
		}
		assertEquals(0, set1.size());
		System.out.println(set1);
		for (int i = 0; i < 101; i++)
		{
			assertFalse(set1.contains(i));
		}

	}

	@Test
	public void testIntersection()
	{
		{
			final FastSparseIntegerSet set0 = new FastSparseIntegerSet();
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet();

			final FastSparseIntegerSet lSetInter = FastSparseIntegerSet.intersection(	set0,
																																								set1);
			assertTrue(lSetInter.isEmpty());

			final FastSparseIntegerSet lSetInter2 = FastSparseIntegerSet.intersection(set0,
																																								set1);
			assertTrue(lSetInter2.isEmpty());

			final FastSparseIntegerSet lSetInter3 = FastSparseIntegerSet.intersection(set1,
																																								set0);
			assertTrue(lSetInter3.isEmpty());
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3);
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(4, 5, 6);

			FastSparseIntegerSet inter = FastSparseIntegerSet.intersection(set1, set2);
			assertTrue(inter.isEmpty());
			inter = FastSparseIntegerSet.intersection(set2, set1);
			assertTrue(inter.isEmpty());
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(4, 5, 6);
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(3, 4, 5);

			final FastSparseIntegerSet inter1 = FastSparseIntegerSet.intersection(set1,
																																						set2);
			assertSame(2, inter1.size());
			assertSame(4, inter1.getUnderlyingArray()[0]);
			assertSame(5, inter1.getUnderlyingArray()[1]);
			final FastSparseIntegerSet inter2 = FastSparseIntegerSet.intersection(set2,
																																						set1);
			assertSame(2, inter2.size());
			assertSame(4, inter2.getUnderlyingArray()[0]);
			assertSame(5, inter2.getUnderlyingArray()[1]);
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3);
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(2);

			final FastSparseIntegerSet lSetInter = FastSparseIntegerSet.intersection(	set1,
																																								set2);
			assertEquals(1, lSetInter.size());
			assertSame(2, lSetInter.getUnderlyingArray()[0]);
			final FastSparseIntegerSet lSetInter2 = FastSparseIntegerSet.intersection(set2,
																																								set1);
			assertEquals(1, lSetInter2.size());
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(11, 12, 13);
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(1, 2, 3, 12);

			final FastSparseIntegerSet inter1 = FastSparseIntegerSet.intersection(set1,
																																						set2);
			assertSame(1, inter1.size());
			assertSame(12, inter1.getUnderlyingArray()[0]);
			final FastSparseIntegerSet inter2 = FastSparseIntegerSet.intersection(set2,
																																						set1);
			assertSame(1, inter2.size());
			assertSame(12, inter2.getUnderlyingArray()[0]);
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3, 4, 10);
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(1, 2, 3, 5, 10);

			final FastSparseIntegerSet inter1 = FastSparseIntegerSet.intersection(set1,
																																						set2);
			assertSame(4, inter1.size());
			assertSame(10, inter1.getUnderlyingArray()[inter1.size() - 1]);
			final FastSparseIntegerSet inter2 = FastSparseIntegerSet.intersection(set2,
																																						set1);
			assertSame(4, inter2.size());
			assertSame(10, inter2.getUnderlyingArray()[inter2.size() - 1]);
		}
		/***/
	}

	@Test
	public void testUnion()
	{
		{
			final FastSparseIntegerSet set0 = new FastSparseIntegerSet();
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet();
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(4, 5, 6);

			final FastSparseIntegerSet union1 = FastSparseIntegerSet.union(set0, set1);
			assertSame(0, union1.size());

			final FastSparseIntegerSet union2 = FastSparseIntegerSet.union(set1, set2);
			assertSame(3, union2.size());
			assertTrue(union2.contains(4, 5, 6));

			final FastSparseIntegerSet union3 = FastSparseIntegerSet.union(set2, set1);
			assertSame(3, union3.size());
			assertTrue(union3.contains(4, 5, 6));
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3);
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(4, 5, 6);

			final FastSparseIntegerSet union1 = FastSparseIntegerSet.union(set1, set2);
			assertTrue(union1.contains(1, 2, 3, 4, 5, 6));

			final FastSparseIntegerSet union2 = FastSparseIntegerSet.union(set2, set1);
			assertTrue(union2.contains(1, 2, 3, 4, 5, 6));
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(4, 5, 6);
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(3, 4, 5);

			final FastSparseIntegerSet union1 = FastSparseIntegerSet.union(set1, set2);
			assertSame(4, union1.size());
			assertSame(3, union1.getUnderlyingArray()[0]);
			assertSame(6, union1.getUnderlyingArray()[3]);
			final FastSparseIntegerSet union2 = FastSparseIntegerSet.union(set2, set1);
			assertSame(4, union2.size());
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3);
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(2);

			final FastSparseIntegerSet union1 = FastSparseIntegerSet.union(set1, set2);
			assertSame(3, union1.size());
			assertSame(1, union1.getUnderlyingArray()[0]);
			assertSame(2, union1.getUnderlyingArray()[1]);
			assertSame(3, union1.getUnderlyingArray()[2]);
			final FastSparseIntegerSet union2 = FastSparseIntegerSet.union(set2, set1);
			assertSame(3, union2.size());
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(11, 12, 13);
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(1, 2, 3, 12);

			final FastSparseIntegerSet union1 = FastSparseIntegerSet.union(set1, set2);
			assertSame(6, union1.size());
			assertSame(1, union1.getUnderlyingArray()[0]);
			assertSame(3, union1.getUnderlyingArray()[2]);
			assertSame(13, union1.getUnderlyingArray()[5]);
			final FastSparseIntegerSet union2 = FastSparseIntegerSet.union(set2, set1);
			assertSame(6, union2.size());
			assertSame(1, union2.getUnderlyingArray()[0]);
			assertSame(3, union2.getUnderlyingArray()[2]);
			assertSame(13, union2.getUnderlyingArray()[5]);
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3, 4, 10);
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(1, 2, 3, 5, 10);

			final FastSparseIntegerSet union1 = FastSparseIntegerSet.union(set1, set2);
			assertSame(6, union1.size());
			assertSame(1, union1.getUnderlyingArray()[0]);
			assertSame(10, union1.getUnderlyingArray()[5]);
			final FastSparseIntegerSet union2 = FastSparseIntegerSet.union(set2, set1);
			assertSame(6, union2.size());
			assertSame(1, union2.getUnderlyingArray()[0]);
			assertSame(10, union2.getUnderlyingArray()[5]);
		}
		/**/
	}

	@Test
	public void testDifference()
	{

		{
			final FastSparseIntegerSet set0 = new FastSparseIntegerSet();
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet();
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(4, 5, 6);

			final FastSparseIntegerSet diff1 = FastSparseIntegerSet.difference(	set0,
																																					set1);
			assertSame(0, diff1.size());

			final FastSparseIntegerSet diff2 = FastSparseIntegerSet.difference(	set2,
																																					set1);
			assertSame(3, diff2.size());
			assertTrue(diff2.contains(4, 5, 6));

			final FastSparseIntegerSet diff3 = FastSparseIntegerSet.difference(	set1,
																																					set2);
			assertSame(0, diff3.size());
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3);
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(4, 5, 6);

			final FastSparseIntegerSet diff1 = FastSparseIntegerSet.difference(	set1,
																																					set2);
			assertTrue(diff1.equals(1, 2, 3));

			final FastSparseIntegerSet diff2 = FastSparseIntegerSet.difference(	set2,
																																					set1);
			assertTrue(diff2.equals(4, 5, 6));
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(4, 5, 6);
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(3, 4, 5);

			final FastSparseIntegerSet diff1 = FastSparseIntegerSet.difference(	set1,
																																					set2);
			assertSame(1, diff1.size());
			assertSame(6, diff1.getUnderlyingArray()[0]);

			final FastSparseIntegerSet diff2 = FastSparseIntegerSet.difference(	set2,
																																					set1);
			assertSame(1, diff2.size());
			assertSame(3, diff2.getUnderlyingArray()[0]);
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3);
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(2);

			final FastSparseIntegerSet diff1 = FastSparseIntegerSet.difference(	set1,
																																					set2);
			assertSame(2, diff1.size());
			assertSame(1, diff1.getUnderlyingArray()[0]);
			assertSame(3, diff1.getUnderlyingArray()[1]);

			final FastSparseIntegerSet diff2 = FastSparseIntegerSet.difference(	set2,
																																					set1);
			assertSame(0, diff2.size());
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(11, 12, 13);
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(1, 2, 3, 12);

			final FastSparseIntegerSet diff1 = FastSparseIntegerSet.difference(	set1,
																																					set2);
			assertSame(2, diff1.size());
			assertSame(11, diff1.getUnderlyingArray()[0]);
			assertSame(13, diff1.getUnderlyingArray()[1]);

			final FastSparseIntegerSet diff2 = FastSparseIntegerSet.difference(	set2,
																																					set1);
			assertSame(3, diff2.size());
			assertSame(1, diff2.getUnderlyingArray()[0]);
			assertSame(2, diff2.getUnderlyingArray()[1]);
			assertSame(3, diff2.getUnderlyingArray()[2]);
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3, 4, 10);
			final FastSparseIntegerSet set2 = new FastSparseIntegerSet(1, 2, 3, 5, 10);

			final FastSparseIntegerSet diff1 = FastSparseIntegerSet.difference(	set1,
																																					set2);
			assertSame(1, diff1.size());
			assertSame(4, diff1.getUnderlyingArray()[0]);

			final FastSparseIntegerSet diff2 = FastSparseIntegerSet.difference(	set2,
																																					set1);
			assertSame(1, diff2.size());
			assertTrue(diff2.contains(5));
		}

		{
			final FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3, 4, 10);

			final FastSparseIntegerSet diff = FastSparseIntegerSet.difference(set1,
																																				set1);
			assertTrue(diff.isEmpty());
		}

	}

	@Test
	public void testPerformance()
	{
		final int size = 20000;

		final FastSparseIntegerSet set = new FastSparseIntegerSet();
		set.ensureCapacity(size);
		final HashSet<Integer> setref = new HashSet<Integer>(size);

		double timeref;
		double time;
		double fold;

		{
			final long start = System.nanoTime();
			final Random rnd = new Random();
			for (int i = 0; i < size; i++)
			{
				final int key = rnd.nextInt();
				setref.add(key);
			}
			final long stop = System.nanoTime();
			timeref = stop - start;
		}
		System.out.println("timeref=" + timeref);

		{
			final long start = System.nanoTime();
			final Random rnd = new Random();
			for (int i = 0; i < size; i++)
			{
				final int key = rnd.nextInt();
				set.add(key);
			}
			final long stop = System.nanoTime();
			time = stop - start;
		}
		System.out.println("time=" + time);

		fold = timeref / time;
		System.out.println("add:FastSparseIntegerSet is " + fold
												+ " times faster than HashSet<Integer> ");

		{
			final long start = System.nanoTime();
			final Random rnd = new Random();
			for (int i = 0; i < size; i++)
			{
				final int key = rnd.nextInt();
				setref.addAll(setref);
			}
			final long stop = System.nanoTime();
			timeref = stop - start;
		}
		System.out.println("timeref=" + timeref);

		{
			final long start = System.nanoTime();
			final Random rnd = new Random();
			for (int i = 0; i < size; i++)
			{
				final int key = rnd.nextInt();
				FastSparseIntegerSet.union(set, set);
			}
			final long stop = System.nanoTime();
			time = stop - start;
		}
		System.out.println("time=" + time);

		fold = timeref / time;
		System.out.println("union: FastSparseIntegerSet is " + fold
												+ " times faster than HashSet<Integer> ");

	}

}
