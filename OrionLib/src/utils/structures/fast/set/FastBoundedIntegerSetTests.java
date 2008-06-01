package utils.structures.fast.set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

/**
 */
public class FastBoundedIntegerSetTests
{

	@Test
	public void testAdd()
	{
		FastBoundedIntegerSet set = new FastBoundedIntegerSet();
		set.add(0);
		set.add(1);
		set.add(31);
		set.add(32);
		assertTrue(set.size() == 4);
		assertTrue(set.contains(0));
		assertTrue(set.contains(1));
		assertTrue(set.contains(31));
		assertTrue(set.contains(32));

		for (int i = 0; i < 1000000; i++)
		{
			set.add(i);
		}
		for (int i = 0; i < 1000000; i++)
		{
			assertTrue(set.contains(i));
		}
	}

	@Test
	public void testDel()
	{
		FastBoundedIntegerSet set = new FastBoundedIntegerSet();
		set.add(0);
		set.add(1);
		set.add(31);
		set.add(32);
		set.remove(0);
		set.remove(31);
		assertTrue(set.size() == 2);
		assertTrue(set.contains(1));
		assertTrue(set.contains(32));

		for (int i = 0; i < 1000000; i++)
		{
			set.add(i);
		}
		for (int i = 0; i < 1000000; i++)
		{
			set.remove(i);
		}
		for (int i = 0; i < 1000000; i++)
		{
			assertFalse(set.contains(i));
		}
	}

	@Test
	public void testContains()
	{
		FastBoundedIntegerSet set1 = new FastBoundedIntegerSet();
		set1.add(0);
		set1.add(1);
		set1.add(2);
		set1.add(3);

		FastBoundedIntegerSet set2 = new FastBoundedIntegerSet();
		set2.add(4);
		set2.add(5);

		FastBoundedIntegerSet set3 = new FastBoundedIntegerSet();
		set3.add(2);
		set3.add(3);

		assertFalse(set1.contains(set2));
		assertFalse(set2.contains(set1));
		assertTrue(set1.contains(set3));

	}

	private static void add(Set<Integer> set1, Set<Integer> set2, int i)
	{
		set1.add(i);
		set2.add(i);
	}

	private static void del(Set<Integer> set1, Set<Integer> set2, int i)
	{
		set1.remove(i);
		set2.remove(i);
	}

	private static void union(Set<Integer> set1,
														Set<Integer> set2,
														Set<Integer> addedset1,
														Set<Integer> addedset2)
	{
		set1.addAll(addedset1);
		set2.addAll(addedset2);
	}

	private static void intersection(	Set<Integer> set1,
																		Set<Integer> set2,
																		Set<Integer> retainedset1,
																		Set<Integer> retainedset2)
	{
		set1.retainAll(retainedset1);
		set2.retainAll(retainedset2);
	}

	private static void difference(	Set<Integer> set1,
																	Set<Integer> set2,
																	Set<Integer> removedset1,
																	Set<Integer> removedset2)
	{
		set1.removeAll(removedset1);
		set2.removeAll(removedset2);
	}

	private static void contains(	Set<Integer> set1,
																Set<Integer> set2,
																Set<Integer> containedset1,
																Set<Integer> containedset2)
	{
		boolean result = set1.containsAll(containedset1) == set2.containsAll(containedset1);
		if (!result)
			throw new RuntimeException();
		assertTrue(result);
		result = set1.containsAll(containedset2) == set2.containsAll(containedset2);
		if (!result)
			throw new RuntimeException();
		assertTrue(result);
	}

	private static boolean equals(Set<Integer> set1, Set<Integer> set2)
	{
		return set1.containsAll(set2) && set2.containsAll(set1);
	}

	private static void assertSetEquals(Set<Integer> set1, Set<Integer> set2)
	{
		boolean result = set1.containsAll(set2) && set2.containsAll(set1);
		if (!result)
			throw new RuntimeException();
		assertTrue(result);
	}

	static Random sRandom = new Random(System.currentTimeMillis());

	static final int nextInt()
	{
		return sRandom.nextInt(100);
	}

	@Test
	public void testUnion()
	{
		int iterations = 100000;
		Set<Integer> testset1 = new TreeSet<Integer>();
		Set<Integer> testset2 = new FastBoundedIntegerSet();
		Set<Integer> set1 = new TreeSet<Integer>();
		Set<Integer> set2 = new FastBoundedIntegerSet();
		for (int i = 0; i < iterations; i++)
		{
			final int a = nextInt();
			final int b = nextInt();

			testset1.add(a);
			testset1.remove(b);
			testset2.add(a);
			testset2.remove(b);

			add(set1, set2, nextInt());
			del(set1, set2, nextInt());
			union(set1, set2, testset1, testset2);
			union(set1, set2, testset2, testset1);

			assertEquals(set1, set2);
		}
	}

	@Test
	public void testDifference()
	{
		int iterations = 100000;
		Set<Integer> testset1 = new TreeSet<Integer>();
		Set<Integer> testset2 = new FastBoundedIntegerSet();
		Set<Integer> set1 = new TreeSet<Integer>();
		Set<Integer> set2 = new FastBoundedIntegerSet();
		for (int i = 0; i < iterations; i++)
		{
			final int a = nextInt();
			final int b = nextInt();

			testset1.add(a);
			testset1.remove(b);
			testset2.add(a);
			testset2.remove(b);

			add(set1, set2, nextInt());
			del(set1, set2, nextInt());
			difference(set1, set2, testset1, testset2);
			difference(set1, set2, testset2, testset1);
			assertEquals(set1, set2);
		}
	}

	@Test
	public void testIntersection()
	{
		int iterations = 100000;
		Set<Integer> testset1 = new TreeSet<Integer>();
		Set<Integer> testset2 = new FastBoundedIntegerSet();
		Set<Integer> set1 = new TreeSet<Integer>();
		Set<Integer> set2 = new FastBoundedIntegerSet();
		for (int i = 0; i < iterations; i++)
		{
			final int a = nextInt();
			final int b = nextInt();

			testset1.add(a);
			testset1.remove(b);
			testset2.add(a);
			testset2.remove(b);

			add(set1, set2, nextInt());
			del(set1, set2, nextInt());
			intersection(set1, set2, testset1, testset2);
			intersection(set1, set2, testset2, testset1);
			assertEquals(set1, set2);
		}
	}

	@Test
	public void testContainsAll()
	{
		int iterations = 100000;
		Set<Integer> testset1 = new TreeSet<Integer>();
		Set<Integer> testset2 = new FastBoundedIntegerSet();
		Set<Integer> set1 = new TreeSet<Integer>();
		Set<Integer> set2 = new FastBoundedIntegerSet();
		for (int i = 0; i < iterations; i++)
		{
			final int a = nextInt();
			final int b = nextInt();

			testset1.add(a);
			testset1.remove(b);
			testset2.add(a);
			testset2.remove(b);

			add(set1, set2, nextInt());
			del(set1, set2, nextInt());
			contains(set1, set2, testset2, testset1);
			contains(set1, set2, testset1, testset2);
			assertEquals(set1, set2);
		}
	}

	@Test
	public void testRelationship()
	{
		// same int:
		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1, 2, 3);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 4, 5, 6);

			assertEquals(0,FastBoundedIntegerSet.relationship(set1, set2));
		}
		
		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1, 2, 3);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 2, 3, 4);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == 2);
		}
		
		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1, 2, 3);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 2, 3);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == 1);
		}
		
		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1, 2, 3);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 1, 2, 3 ,4);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == -1);
		}
		
		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1, 2, 3);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 1, 2, 3);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == 3);
		}
		
    // different ints:
		
		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1, 2, 3);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 104, 105, 106);

			assertEquals(0,FastBoundedIntegerSet.relationship(set1, set2));
		}
		
		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1001 ,1, 2, 103);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 2, 103, 4, 1004);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == 2);
		}
		
		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1, 2, 103);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 2, 103);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == 1);
		}
		
		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1, 2, 103);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 1, 2, 103 ,4, 104);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == -1);
		}
		
		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1, 2, 3, 101, 1001);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 1, 2, 3, 101, 1001);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == 3);
		}

	}

	@Test
	public void testIntersectionStatic()
	{
		{
			FastBoundedIntegerSet set0 = new FastBoundedIntegerSet();
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet();

			FastBoundedIntegerSet lSetInter = FastBoundedIntegerSet.intersection(	set0,
																																						set1);
			assertTrue(lSetInter.isEmpty());

			FastBoundedIntegerSet lSetInter2 = FastBoundedIntegerSet.intersection(set0,
																																						set1);
			assertTrue(lSetInter2.isEmpty());

			FastBoundedIntegerSet lSetInter3 = FastBoundedIntegerSet.intersection(set1,
																																						set0);
			assertTrue(lSetInter3.isEmpty());
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1, 2, 3);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 4, 5, 6);

			FastBoundedIntegerSet inter = FastBoundedIntegerSet.intersection(	set1,
																																				set2);
			assertTrue(inter.isEmpty());
			inter = FastBoundedIntegerSet.intersection(set2, set1);
			assertTrue(inter.isEmpty());
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 4, 5, 6);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 3, 4, 5);

			FastBoundedIntegerSet inter1 = FastBoundedIntegerSet.intersection(set1,
																																				set2);
			assertSame(2, inter1.size());
			assertTrue(inter1.contains(4));
			assertTrue(inter1.contains(5));
			FastBoundedIntegerSet inter2 = FastBoundedIntegerSet.intersection(set2,
																																				set1);
			assertSame(2, inter2.size());
			assertTrue(inter2.contains(4));
			assertTrue(inter2.contains(5));
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1, 2, 3);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 2);

			FastBoundedIntegerSet inter = FastBoundedIntegerSet.intersection(	set1,
																																				set2);
			assertEquals(1, inter.size());
			assertTrue(inter.contains(2));
			FastBoundedIntegerSet lSetInter2 = FastBoundedIntegerSet.intersection(set2,
																																						set1);
			assertEquals(1, lSetInter2.size());
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 11, 12, 13);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 1, 2, 3, 12);

			FastBoundedIntegerSet inter1 = FastBoundedIntegerSet.intersection(set1,
																																				set2);
			assertSame(1, inter1.size());
			assertTrue(inter1.contains(12));
			FastBoundedIntegerSet inter2 = FastBoundedIntegerSet.intersection(set2,
																																				set1);
			assertSame(1, inter2.size());
			assertTrue(inter2.contains(12));
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																															1,
																															2,
																															3,
																															4,
																															10);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																															1,
																															2,
																															3,
																															5,
																															10);

			FastBoundedIntegerSet inter1 = FastBoundedIntegerSet.intersection(set1,
																																				set2);
			assertSame(4, inter1.size());
			assertTrue(inter1.contains(10));
			FastBoundedIntegerSet inter2 = FastBoundedIntegerSet.intersection(set2,
																																				set1);
			assertSame(4, inter2.size());
			assertTrue(inter2.contains(10));
		}
		/***/
	}

	@Test
	public void testUnionStatic()
	{
		{
			FastBoundedIntegerSet set0 = new FastBoundedIntegerSet();
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet();
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 4, 5, 6);

			FastBoundedIntegerSet union1 = FastBoundedIntegerSet.union(set0, set1);
			assertSame(0, union1.size());

			FastBoundedIntegerSet union2 = FastBoundedIntegerSet.union(set1, set2);
			assertSame(3, union2.size());
			assertTrue(union2.contains(4, 5, 6));

			FastBoundedIntegerSet union3 = FastBoundedIntegerSet.union(set2, set1);
			assertSame(3, union3.size());
			assertTrue(union3.contains(4, 5, 6));
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1, 2, 3);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 4, 5, 6);

			FastBoundedIntegerSet union1 = FastBoundedIntegerSet.union(set1, set2);
			assertTrue(union1.contains(1, 2, 3, 4, 5, 6));

			FastBoundedIntegerSet union2 = FastBoundedIntegerSet.union(set2, set1);
			assertTrue(union2.contains(1, 2, 3, 4, 5, 6));
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 4, 5, 6);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 3, 4, 5);

			FastBoundedIntegerSet union1 = FastBoundedIntegerSet.union(set1, set2);
			assertSame(4, union1.size());
			assertTrue(union1.contains(3));
			assertTrue(union1.contains(6));
			FastBoundedIntegerSet union2 = FastBoundedIntegerSet.union(set2, set1);
			assertSame(4, union2.size());
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1, 2, 3);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 2);

			FastBoundedIntegerSet union1 = FastBoundedIntegerSet.union(set1, set2);
			assertSame(3, union1.size());
			assertTrue(union1.contains(1));
			assertTrue(union1.contains(2));
			assertTrue(union1.contains(3));
			FastBoundedIntegerSet union2 = FastBoundedIntegerSet.union(set2, set1);
			assertSame(3, union2.size());
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 11, 12, 13);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 1, 2, 3, 12);

			FastBoundedIntegerSet union1 = FastBoundedIntegerSet.union(set1, set2);
			assertSame(6, union1.size());
			assertTrue(union1.contains(1));
			assertTrue(union1.contains(3));
			assertTrue(union1.contains(13));
			FastBoundedIntegerSet union2 = FastBoundedIntegerSet.union(set2, set1);
			assertSame(6, union2.size());
			assertTrue(union2.contains(1));
			assertTrue(union2.contains(3));
			assertTrue(union2.contains(13));
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																															1,
																															2,
																															3,
																															4,
																															10);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																															1,
																															2,
																															3,
																															5,
																															10);

			FastBoundedIntegerSet union1 = FastBoundedIntegerSet.union(set1, set2);
			assertSame(6, union1.size());
			assertTrue(union1.contains(1));
			assertTrue(union1.contains(10));
			FastBoundedIntegerSet union2 = FastBoundedIntegerSet.union(set2, set1);
			assertSame(6, union2.size());
			assertTrue(union2.contains(1));
			assertTrue(union2.contains(10));
		}
		/**/
	}

	@Test
	public void testDifferenceStatic()
	{

		{
			FastBoundedIntegerSet set0 = new FastBoundedIntegerSet();
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet();
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 4, 5, 6);

			FastBoundedIntegerSet diff1 = FastBoundedIntegerSet.difference(set0, set1);
			assertSame(0, diff1.size());

			FastBoundedIntegerSet diff2 = FastBoundedIntegerSet.difference(set2, set1);
			assertSame(3, diff2.size());
			assertTrue(diff2.contains(4, 5, 6));

			FastBoundedIntegerSet diff3 = FastBoundedIntegerSet.difference(set1, set2);
			assertSame(0, diff3.size());
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1, 2, 3);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 4, 5, 6);

			FastBoundedIntegerSet diff1 = FastBoundedIntegerSet.difference(set1, set2);
			assertTrue(diff1.equals(1, 2, 3));

			FastBoundedIntegerSet diff2 = FastBoundedIntegerSet.difference(set2, set1);
			assertTrue(diff2.equals(4, 5, 6));
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 4, 5, 6);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 3, 4, 5);

			FastBoundedIntegerSet diff1 = FastBoundedIntegerSet.difference(set1, set2);
			assertSame(1, diff1.size());
			assertTrue(diff1.contains(6));

			FastBoundedIntegerSet diff2 = FastBoundedIntegerSet.difference(set2, set1);
			assertSame(1, diff2.size());
			assertTrue(diff2.contains(3));
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 1, 2, 3);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 2);

			FastBoundedIntegerSet diff1 = FastBoundedIntegerSet.difference(set1, set2);
			assertSame(2, diff1.size());
			assertTrue(diff1.contains(1));
			assertTrue(diff1.contains(3));

			FastBoundedIntegerSet diff2 = FastBoundedIntegerSet.difference(set2, set1);
			assertSame(0, diff2.size());
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(true, 11, 12, 13);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 1, 2, 3, 12);

			FastBoundedIntegerSet diff1 = FastBoundedIntegerSet.difference(set1, set2);
			assertSame(2, diff1.size());
			assertTrue(diff1.contains(11));
			assertTrue(diff1.contains(13));

			FastBoundedIntegerSet diff2 = FastBoundedIntegerSet.difference(set2, set1);
			assertSame(3, diff2.size());
			assertTrue(diff2.contains(1));
			assertTrue(diff2.contains(2));
			assertTrue(diff2.contains(3));
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																															1,
																															2,
																															3,
																															4,
																															10);
			FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																															1,
																															2,
																															3,
																															5,
																															10);

			FastBoundedIntegerSet diff1 = FastBoundedIntegerSet.difference(set1, set2);
			assertSame(1, diff1.size());
			assertTrue(diff1.contains(4));

			FastBoundedIntegerSet diff2 = FastBoundedIntegerSet.difference(set2, set1);
			assertSame(1, diff2.size());
			assertTrue(diff2.contains(5));
		}

		{
			FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																															1,
																															2,
																															3,
																															4,
																															10);

			FastBoundedIntegerSet diff = FastBoundedIntegerSet.difference(set1, set1);
			assertTrue(diff.isEmpty());
		}

	}

	static int size = 20000;
	static Random rnd = new Random();

	private int rndint()
	{
		return Math.abs(rnd.nextInt(size));
	}

	private void rndset(Set<Integer> set)
	{
		for (int i = 0; i < size; i++)
		{
			set.add(rndint());
		}
	}

	@Test
	public void testPerformance()
	{

		FastBoundedIntegerSet set = new FastBoundedIntegerSet();
		FastBoundedIntegerSet set2 = new FastBoundedIntegerSet();
		HashSet<Integer> setref = new HashSet<Integer>();
		HashSet<Integer> setref2 = new HashSet<Integer>();

		double timeref;
		double time;
		double fold;

		{
			long start = System.nanoTime();
			for (int i = 0; i < size; i++)
			{
				final int key = rndint();
				setref.add(key);
			}
			long stop = System.nanoTime();
			timeref = ((double) (stop - start));
		}
		System.out.println("timeref=" + timeref);

		{
			long start = System.nanoTime();
			for (int i = 0; i < size; i++)
			{
				final int key = rndint();
				set.add(key);
			}
			long stop = System.nanoTime();
			time = ((double) (stop - start));
		}
		System.out.println("time=" + time);

		fold = timeref / time;
		System.out.println("add:FastBoundedIntegerSet is " + fold
												+ " times faster than HashSet<Integer> ");

		{
			setref.clear();
			setref2.clear();
			rndset(setref);
			rndset(setref2);
			long start = System.nanoTime();

			for (int i = 0; i < size; i++)
			{
				final int key = rndint();
				setref2.add(key);
				setref.addAll(setref2);
			}
			long stop = System.nanoTime();
			timeref = ((double) (stop - start));
		}
		System.out.println("timeref=" + timeref);

		{
			set.clear();
			set2.clear();
			rndset(set);
			rndset(set2);
			long start = System.nanoTime();
			for (int i = 0; i < size; i++)
			{
				final int key = rndint();
				set2.add(key);
				set.union(set2);
			}
			long stop = System.nanoTime();
			time = ((double) (stop - start));
		}
		System.out.println("time=" + time);

		fold = timeref / time;
		System.out.println("union: FastBoundedIntegerSet is " + fold
												+ " times faster than HashSet<Integer> ");

		{
			setref.clear();
			setref2.clear();
			rndset(setref);
			rndset(setref2);
			long start = System.nanoTime();
			for (int i = 0; i < size; i++)
			{
				final int key = rndint();
				setref2.add(key);
				setref.retainAll(setref2);
			}
			long stop = System.nanoTime();
			timeref = ((double) (stop - start));
		}
		System.out.println("timeref=" + timeref);

		{
			set.clear();
			set2.clear();
			rndset(set);
			rndset(set2);
			long start = System.nanoTime();
			for (int i = 0; i < size; i++)
			{
				final int key = rndint();
				set2.add(key);
				set.intersection(set2);
			}
			long stop = System.nanoTime();
			time = ((double) (stop - start));
		}
		System.out.println("time=" + time);

		fold = timeref / time;
		System.out.println("intersection: FastBoundedIntegerSet is " + fold
												+ " times faster than HashSet<Integer> ");

	}

}
