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
		FastSparseIntegerSet set1 = new FastSparseIntegerSet(	0,
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
			assertTrue(set1.contains(i));

		FastSparseIntegerSet set2 = new FastSparseIntegerSet(	1,
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
			assertTrue(set2.contains(i));

	}

	@Test
	public void testCopyConstructor()
	{
		FastSparseIntegerSet set1 = new FastSparseIntegerSet(	0,
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
		FastSparseIntegerSet copyset1 = new FastSparseIntegerSet(set1);
		assertEquals(set1, copyset1);
	}

	@Test
	public void testContains()
	{
		FastSparseIntegerSet set2 = new FastSparseIntegerSet(	1,
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
			assertTrue(set2.contains(i));
	}

	@Test
	public void testAdd()
	{

		FastSparseIntegerSet set1 = new FastSparseIntegerSet();
		assertEquals(0, set1.size());
		for (int i = 0; i < 100; i++)
			assertTrue(set1.add(i));
		assertEquals(100, set1.size());
		for (int i = 0; i < 100; i++)
			assertFalse(set1.add(i));
		assertEquals(100, set1.size());

		set1.clear();
		for (int i = 0; i < 101; i++)
			assertTrue(set1.add((i * 10) % 101));
		assertEquals(101, set1.size());
		for (int i = 0; i < 101; i++)
			assertFalse(set1.add((i * 10) % 101));
		assertEquals(101, set1.size());
		System.out.println(set1);
		for (int i = 0; i < 101; i++)
			assertTrue(set1.contains(i));

	}

	@Test
	public void testDel()
	{

		FastSparseIntegerSet set1 = new FastSparseIntegerSet();
		assertEquals(0, set1.size());
		for (int i = 0; i < 100; i++)
			assertTrue(set1.add(i));
		assertEquals(100, set1.size());
		for (int i = 0; i < 100; i++)
			assertTrue(set1.del(i));
		assertEquals(0, set1.size());

		set1.clear();
		for (int i = 0; i < 101; i++)
			assertTrue(set1.add((i * 10) % 101));
		assertEquals(101, set1.size());
		for (int i = 0; i < 101; i++)
			assertTrue(set1.del((i * 10) % 101));
		assertEquals(0, set1.size());
		System.out.println(set1);
		for (int i = 0; i < 101; i++)
			assertFalse(set1.contains(i));

	}

	@Test
	public void testIntersection()
	{
		{
			FastSparseIntegerSet set0 = new FastSparseIntegerSet();
			FastSparseIntegerSet set1 = new FastSparseIntegerSet();

			FastSparseIntegerSet lSetInter = FastSparseIntegerSet.intersection(	set0,
																																					set1);
			assertTrue(lSetInter.isEmpty());

			FastSparseIntegerSet lSetInter2 = FastSparseIntegerSet.intersection(set0,
																																					set1);
			assertTrue(lSetInter2.isEmpty());

			FastSparseIntegerSet lSetInter3 = FastSparseIntegerSet.intersection(set1,
																																					set0);
			assertTrue(lSetInter3.isEmpty());
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3);
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(4, 5, 6);

			FastSparseIntegerSet inter = FastSparseIntegerSet.intersection(set1, set2);
			assertTrue(inter.isEmpty());
			inter = FastSparseIntegerSet.intersection(set2, set1);
			assertTrue(inter.isEmpty());
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(4, 5, 6);
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(3, 4, 5);

			FastSparseIntegerSet inter1 = FastSparseIntegerSet.intersection(set1,
																																			set2);
			assertSame(2, inter1.size());
			assertSame(4, inter1.getUnderlyingArray()[0]);
			assertSame(5, inter1.getUnderlyingArray()[1]);
			FastSparseIntegerSet inter2 = FastSparseIntegerSet.intersection(set2,
																																			set1);
			assertSame(2, inter2.size());
			assertSame(4, inter2.getUnderlyingArray()[0]);
			assertSame(5, inter2.getUnderlyingArray()[1]);
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3);
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(2);

			FastSparseIntegerSet lSetInter = FastSparseIntegerSet.intersection(	set1,
																																					set2);
			assertEquals(1, lSetInter.size());
			assertSame(2, lSetInter.getUnderlyingArray()[0]);
			FastSparseIntegerSet lSetInter2 = FastSparseIntegerSet.intersection(set2,
																																					set1);
			assertEquals(1, lSetInter2.size());
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(11, 12, 13);
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(1, 2, 3, 12);

			FastSparseIntegerSet inter1 = FastSparseIntegerSet.intersection(set1,
																																			set2);
			assertSame(1, inter1.size());
			assertSame(12, inter1.getUnderlyingArray()[0]);
			FastSparseIntegerSet inter2 = FastSparseIntegerSet.intersection(set2,
																																			set1);
			assertSame(1, inter2.size());
			assertSame(12, inter2.getUnderlyingArray()[0]);
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3, 4, 10);
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(1, 2, 3, 5, 10);

			FastSparseIntegerSet inter1 = FastSparseIntegerSet.intersection(set1,
																																			set2);
			assertSame(4, inter1.size());
			assertSame(10, inter1.getUnderlyingArray()[inter1.size() - 1]);
			FastSparseIntegerSet inter2 = FastSparseIntegerSet.intersection(set2,
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
			FastSparseIntegerSet set0 = new FastSparseIntegerSet();
			FastSparseIntegerSet set1 = new FastSparseIntegerSet();
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(4, 5, 6);

			FastSparseIntegerSet union1 = FastSparseIntegerSet.union(set0, set1);
			assertSame(0, union1.size());

			FastSparseIntegerSet union2 = FastSparseIntegerSet.union(set1, set2);
			assertSame(3, union2.size());
			assertTrue(union2.contains(4, 5, 6));

			FastSparseIntegerSet union3 = FastSparseIntegerSet.union(set2, set1);
			assertSame(3, union3.size());
			assertTrue(union3.contains(4, 5, 6));
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3);
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(4, 5, 6);

			FastSparseIntegerSet union1 = FastSparseIntegerSet.union(set1, set2);
			assertTrue(union1.contains(1, 2, 3, 4, 5, 6));

			FastSparseIntegerSet union2 = FastSparseIntegerSet.union(set2, set1);
			assertTrue(union2.contains(1, 2, 3, 4, 5, 6));
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(4, 5, 6);
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(3, 4, 5);

			FastSparseIntegerSet union1 = FastSparseIntegerSet.union(set1, set2);
			assertSame(4, union1.size());
			assertSame(3, union1.getUnderlyingArray()[0]);
			assertSame(6, union1.getUnderlyingArray()[3]);
			FastSparseIntegerSet union2 = FastSparseIntegerSet.union(set2, set1);
			assertSame(4, union2.size());
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3);
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(2);

			FastSparseIntegerSet union1 = FastSparseIntegerSet.union(set1, set2);
			assertSame(3, union1.size());
			assertSame(1, union1.getUnderlyingArray()[0]);
			assertSame(2, union1.getUnderlyingArray()[1]);
			assertSame(3, union1.getUnderlyingArray()[2]);
			FastSparseIntegerSet union2 = FastSparseIntegerSet.union(set2, set1);
			assertSame(3, union2.size());
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(11, 12, 13);
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(1, 2, 3, 12);

			FastSparseIntegerSet union1 = FastSparseIntegerSet.union(set1, set2);
			assertSame(6, union1.size());
			assertSame(1, union1.getUnderlyingArray()[0]);
			assertSame(3, union1.getUnderlyingArray()[2]);
			assertSame(13, union1.getUnderlyingArray()[5]);
			FastSparseIntegerSet union2 = FastSparseIntegerSet.union(set2, set1);
			assertSame(6, union2.size());
			assertSame(1, union2.getUnderlyingArray()[0]);
			assertSame(3, union2.getUnderlyingArray()[2]);
			assertSame(13, union2.getUnderlyingArray()[5]);
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3, 4, 10);
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(1, 2, 3, 5, 10);

			FastSparseIntegerSet union1 = FastSparseIntegerSet.union(set1, set2);
			assertSame(6, union1.size());
			assertSame(1, union1.getUnderlyingArray()[0]);
			assertSame(10, union1.getUnderlyingArray()[5]);
			FastSparseIntegerSet union2 = FastSparseIntegerSet.union(set2, set1);
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
			FastSparseIntegerSet set0 = new FastSparseIntegerSet();
			FastSparseIntegerSet set1 = new FastSparseIntegerSet();
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(4, 5, 6);

			FastSparseIntegerSet diff1 = FastSparseIntegerSet.difference(set0, set1);
			assertSame(0, diff1.size());

			FastSparseIntegerSet diff2 = FastSparseIntegerSet.difference(set2, set1);
			assertSame(3, diff2.size());
			assertTrue(diff2.contains(4, 5, 6));

			FastSparseIntegerSet diff3 = FastSparseIntegerSet.difference(set1, set2);
			assertSame(0, diff3.size());
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3);
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(4, 5, 6);

			FastSparseIntegerSet diff1 = FastSparseIntegerSet.difference(set1, set2);
			assertTrue(diff1.equals(1, 2, 3));

			FastSparseIntegerSet diff2 = FastSparseIntegerSet.difference(set2, set1);
			assertTrue(diff2.equals(4, 5, 6));
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(4, 5, 6);
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(3, 4, 5);

			FastSparseIntegerSet diff1 = FastSparseIntegerSet.difference(set1, set2);
			assertSame(1, diff1.size());
			assertSame(6, diff1.getUnderlyingArray()[0]);

			FastSparseIntegerSet diff2 = FastSparseIntegerSet.difference(set2, set1);
			assertSame(1, diff2.size());
			assertSame(3, diff2.getUnderlyingArray()[0]);
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3);
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(2);

			FastSparseIntegerSet diff1 = FastSparseIntegerSet.difference(set1, set2);
			assertSame(2, diff1.size());
			assertSame(1, diff1.getUnderlyingArray()[0]);
			assertSame(3, diff1.getUnderlyingArray()[1]);

			FastSparseIntegerSet diff2 = FastSparseIntegerSet.difference(set2, set1);
			assertSame(0, diff2.size());
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(11, 12, 13);
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(1, 2, 3, 12);

			FastSparseIntegerSet diff1 = FastSparseIntegerSet.difference(set1, set2);
			assertSame(2, diff1.size());
			assertSame(11, diff1.getUnderlyingArray()[0]);
			assertSame(13, diff1.getUnderlyingArray()[1]);

			FastSparseIntegerSet diff2 = FastSparseIntegerSet.difference(set2, set1);
			assertSame(3, diff2.size());
			assertSame(1, diff2.getUnderlyingArray()[0]);
			assertSame(2, diff2.getUnderlyingArray()[1]);
			assertSame(3, diff2.getUnderlyingArray()[2]);
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3, 4, 10);
			FastSparseIntegerSet set2 = new FastSparseIntegerSet(1, 2, 3, 5, 10);

			FastSparseIntegerSet diff1 = FastSparseIntegerSet.difference(set1, set2);
			assertSame(1, diff1.size());
			assertSame(4, diff1.getUnderlyingArray()[0]);

			FastSparseIntegerSet diff2 = FastSparseIntegerSet.difference(set2, set1);
			assertSame(1, diff2.size());
			assertTrue(diff2.contains(5));
		}

		{
			FastSparseIntegerSet set1 = new FastSparseIntegerSet(1, 2, 3, 4, 10);

			FastSparseIntegerSet diff = FastSparseIntegerSet.difference(set1, set1);
			assertTrue(diff.isEmpty());
		}

	}

	@Test
	public void testPerformance()
	{
		int size = 20000;

		FastSparseIntegerSet set = new FastSparseIntegerSet();
		set.ensureCapacity(size);
		HashSet<Integer> setref = new HashSet<Integer>(size);

		double timeref;
		double time;
		double fold;

		{
			long start = System.nanoTime();
			Random rnd = new Random();
			for (int i = 0; i < size; i++)
			{
				final int key = rnd.nextInt();
				setref.add(key);
			}
			long stop = System.nanoTime();
			timeref = ((double) (stop - start));
		}
		System.out.println("timeref=" + timeref);

		{
			long start = System.nanoTime();
			Random rnd = new Random();
			for (int i = 0; i < size; i++)
			{
				final int key = rnd.nextInt();
				set.add(key);
			}
			long stop = System.nanoTime();
			time = ((double) (stop - start));
		}
		System.out.println("time=" + time);

		fold = timeref / time;
		System.out.println("add:FastSparseIntegerSet is " + fold
												+ " times faster than HashSet<Integer> ");

		{
			long start = System.nanoTime();
			Random rnd = new Random();
			for (int i = 0; i < size; i++)
			{
				final int key = rnd.nextInt();
				setref.addAll(setref);
			}
			long stop = System.nanoTime();
			timeref = ((double) (stop - start));
		}
		System.out.println("timeref=" + timeref);

		{
			long start = System.nanoTime();
			Random rnd = new Random();
			for (int i = 0; i < size; i++)
			{
				final int key = rnd.nextInt();
				FastSparseIntegerSet.union(set, set);
			}
			long stop = System.nanoTime();
			time = ((double) (stop - start));
		}
		System.out.println("time=" + time);

		fold = timeref / time;
		System.out.println("union: FastSparseIntegerSet is " + fold
												+ " times faster than HashSet<Integer> ");

	}

}
