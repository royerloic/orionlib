package utils.structures.fast.set.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 */
public class GenericIntegerSetTest
{

	static int lScale = 10000;
	
	public static void testAll(IntegerSetFactory pIntegerSetFactory)
	{
		testAdd(pIntegerSetFactory);
		testRemove(pIntegerSetFactory);
		testContains(pIntegerSetFactory);
		testIterator(pIntegerSetFactory);
		//testPerformance(pIntegerSetFactory);
	}

	public static void testAdd(IntegerSetFactory pIntegerSetFactory)
	{
		final Set<Integer> set = pIntegerSetFactory.createEmptySet();
		set.add(0);
		set.add(1);
		set.add(31);
		set.add(32);
		assertTrue(set.size() == 4);
		assertTrue(set.contains(0));
		assertTrue(set.contains(1));
		assertTrue(set.contains(31));
		assertTrue(set.contains(32));

		for (int i = 0; i < lScale; i++)
		{
			set.add(i);
		}
		for (int i = 0; i < lScale; i++)
		{
			assertTrue(set.contains(i));
		}
	}

	public static void testRemove(IntegerSetFactory pIntegerSetFactory)
	{
		final Set<Integer> set = pIntegerSetFactory.createEmptySet();
		set.add(0);
		set.add(1);
		set.add(31);
		set.add(32);
		set.remove(0);
		set.remove(31);
		assertTrue(set.size() == 2);
		assertTrue(set.contains(1));
		assertTrue(set.contains(32));

		for (int i = 0; i < lScale; i++)
		{
			set.add(i);
		}
		for (int i = 0; i < lScale; i++)
		{
			set.remove(i);
		}
		for (int i = 0; i < lScale; i++)
		{
			assertFalse(set.contains(i));
		}
	}

	public static void testContains(IntegerSetFactory pIntegerSetFactory)
	{
		final Set<Integer> set1 = pIntegerSetFactory.createEmptySet();
		set1.add(0);
		set1.add(1);
		set1.add(2);
		set1.add(3);

		final Set<Integer> set2 = pIntegerSetFactory.createEmptySet();
		set2.add(4);
		set2.add(5);

		final Set<Integer> set3 = pIntegerSetFactory.createEmptySet();
		set3.add(2);
		set3.add(3);

		assertFalse(set1.containsAll(set2));
		assertFalse(set2.containsAll(set1));
		assertTrue(set1.containsAll(set3));

	}

	public static void testIterator(IntegerSetFactory pIntegerSetFactory)
	{
		final Set<Integer> set1 = pIntegerSetFactory.createEmptySet();
		set1.add(0);
		set1.add(2);
		set1.add(5);
		set1.add(8);
		set1.remove(2);
		set1.add(12);

		final Set<Integer> set2 = pIntegerSetFactory.createEmptySet();
		set2.add(1);
		set2.add(20);
		set2.add(21);
		set2.add(22);

		set1.addAll(set2);

		final Set<Integer> set3 = pIntegerSetFactory.createEmptySet();
		set3.add(5);
		set3.add(22);

		set1.removeAll(set3);

		String lString = set1.toString();
		System.out.println(lString);

		int count = 0;
		for (int i : set1)
		{
			System.out.println(i);
			assertTrue(lString.contains("" + i + ",") || lString.contains("" + i
																																		+ "]"));
			count++;
		}
		assertEquals(6, count);
	}

	static Random rnd = new Random();

	private static int rndint()
	{
		return Math.abs(rnd.nextInt(lScale));
	}

	private static void rndset(final Set<Integer> set)
	{
		for (int i = 0; i < lScale; i++)
		{
			set.add(rndint());
		}
	}

	public static void testPerformance(IntegerSetFactory pIntegerSetFactory)
	{

		final Set<Integer> set = pIntegerSetFactory.createEmptySet();
		final Set<Integer> set2 = pIntegerSetFactory.createEmptySet();
		final Set<Integer> setref = new HashSet<Integer>();
		final Set<Integer> setref2 = new HashSet<Integer>();

		double timeref;
		double time;
		double fold;

		{
			final long start = System.nanoTime();
			for (int i = 0; i < lScale; i++)
			{
				final int key = rndint();
				setref.add(key);
			}
			final long stop = System.nanoTime();
			timeref = (stop - start);
		}
		System.out.println("timeref=" + timeref);

		{
			final long start = System.nanoTime();
			for (int i = 0; i < lScale; i++)
			{
				final int key = rndint();
				set.add(key);
			}
			final long stop = System.nanoTime();
			time = (stop - start);
		}
		System.out.println("time=" + time);

		fold = timeref / time;
		System.out.println("add:" + pIntegerSetFactory.getSetTypeName()
												+ " is "
												+ fold
												+ " times faster than HashSet<Integer> ");

		{
			setref.clear();
			setref2.clear();
			rndset(setref);
			rndset(setref2);
			final long start = System.nanoTime();

			for (int i = 0; i < lScale; i++)
			{
				final int key = rndint();
				setref2.add(key);
				setref.addAll(setref2);
			}
			final long stop = System.nanoTime();
			timeref = (stop - start);
		}
		System.out.println("timeref=" + timeref);

		{
			set.clear();
			set2.clear();
			rndset(set);
			rndset(set2);
			final long start = System.nanoTime();
			for (int i = 0; i < lScale; i++)
			{
				final int key = rndint();
				set2.add(key);
				set.addAll(set2);
			}
			final long stop = System.nanoTime();
			time = (stop - start);
		}
		System.out.println("time=" + time);

		fold = timeref / time;
		System.out.println("union: " + pIntegerSetFactory.getSetTypeName()
												+ " is "
												+ fold
												+ " times faster than HashSet<Integer> ");

		{
			setref.clear();
			setref2.clear();
			rndset(setref);
			rndset(setref2);
			final long start = System.nanoTime();
			for (int i = 0; i < lScale; i++)
			{
				final int key = rndint();
				setref2.add(key);
				setref.retainAll(setref2);
			}
			final long stop = System.nanoTime();
			timeref = (stop - start);
		}
		System.out.println("timeref=" + timeref);

		{
			set.clear();
			set2.clear();
			rndset(set);
			rndset(set2);
			final long start = System.nanoTime();
			for (int i = 0; i < lScale; i++)
			{
				final int key = rndint();
				set2.add(key);
				set.retainAll(set2);
			}
			final long stop = System.nanoTime();
			time = (stop - start);
		}
		System.out.println("time=" + time);

		fold = timeref / time;
		System.out.println("intersection: " + pIntegerSetFactory.getSetTypeName()
												+ " is "
												+ fold
												+ " times faster than HashSet<Integer> ");

	}

}
