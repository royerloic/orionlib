package utils.structures.fast.set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.Test;

import utils.structures.fast.map.FastIntegerHashMap;

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
		if(!result) throw new RuntimeException();
		assertTrue(result);
		result = set1.containsAll(containedset2) == set2.containsAll(containedset2);
		if(!result) throw new RuntimeException();
		assertTrue(result);
	}

	private static boolean equals(Set<Integer> set1, Set<Integer> set2)
	{
		return set1.containsAll(set2) && set2.containsAll(set1);
	}

	private static void assertEquals(Set<Integer> set1, Set<Integer> set2)
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
			union(set1, set2, testset1,testset2);
			union(set1, set2, testset2,testset1);
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
			difference(set1, set2, testset1,testset2);
			difference(set1, set2, testset2,testset1);
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
			intersection(set1, set2, testset1,testset2);
			intersection(set1, set2, testset2,testset1);
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
			contains(set1, set2, testset2,testset1);
			contains(set1, set2, testset1,testset2);
			assertEquals(set1, set2);
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
		for(int i=0; i<size; i++)
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
		System.out.println("add:FastSparseIntegerSet is " + fold
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
		System.out.println("union: FastSparseIntegerSet is " + fold
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
		System.out.println("intersection: FastSparseIntegerSet is " + fold
												+ " times faster than HashSet<Integer> ");

	}

}
