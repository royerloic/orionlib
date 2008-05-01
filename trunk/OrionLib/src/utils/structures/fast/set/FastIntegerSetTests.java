package utils.structures.fast.set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import utils.structures.fast.map.FastIntegerHashMap;

/**
 */
public class FastIntegerSetTests
{
	@Test
	public void testArrayConstructor()
	{
		FastIntegerSet set1 = new FastIntegerSet(0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

		assertEquals(10, set1.size());
		for (int i = 0; i < 10; i++)
			assertTrue(set1.contains(i));

		FastIntegerSet set2 = new FastIntegerSet(	1,
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
		FastIntegerSet set1 = new FastIntegerSet(0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		FastIntegerSet copyset1 = new FastIntegerSet(set1);
		assertEquals(set1, copyset1);
	}

	@Test
	public void testContains()
	{
		FastIntegerSet set2 = new FastIntegerSet(	1,
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

		FastIntegerSet set1 = new FastIntegerSet();
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

		FastIntegerSet set1 = new FastIntegerSet();
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
			FastIntegerSet set0 = new FastIntegerSet();
			FastIntegerSet set1 = new FastIntegerSet();

			FastIntegerSet lSetInter = FastIntegerSet.intersection(set0, set1);
			assertTrue(lSetInter.isEmpty());

			FastIntegerSet lSetInter2 = FastIntegerSet.intersection(set0, set1);
			assertTrue(lSetInter2.isEmpty());

			FastIntegerSet lSetInter3 = FastIntegerSet.intersection(set1, set0);
			assertTrue(lSetInter3.isEmpty());
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(1, 2, 3);
			FastIntegerSet set2 = new FastIntegerSet(4, 5, 6);

			FastIntegerSet inter = FastIntegerSet.intersection(set1, set2);
			assertTrue(inter.isEmpty());
			inter = FastIntegerSet.intersection(set2, set1);
			assertTrue(inter.isEmpty());
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(4, 5, 6);
			FastIntegerSet set2 = new FastIntegerSet(3, 4, 5);

			FastIntegerSet inter1 = FastIntegerSet.intersection(set1, set2);
			assertSame(2, inter1.size());
			assertSame(4, inter1.getUnderlyingArray()[0]);
			assertSame(5, inter1.getUnderlyingArray()[1]);
			FastIntegerSet inter2 = FastIntegerSet.intersection(set2, set1);
			assertSame(2, inter2.size());
			assertSame(4, inter2.getUnderlyingArray()[0]);
			assertSame(5, inter2.getUnderlyingArray()[1]);
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(1, 2, 3);
			FastIntegerSet set2 = new FastIntegerSet(2);

			FastIntegerSet lSetInter = FastIntegerSet.intersection(set1, set2);
			assertEquals(1, lSetInter.size());
			assertSame(2, lSetInter.getUnderlyingArray()[0]);
			FastIntegerSet lSetInter2 = FastIntegerSet.intersection(set2, set1);
			assertEquals(1, lSetInter2.size());
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(11, 12, 13);
			FastIntegerSet set2 = new FastIntegerSet(1, 2, 3, 12);

			FastIntegerSet inter1 = FastIntegerSet.intersection(set1, set2);
			assertSame(1, inter1.size());
			assertSame(12, inter1.getUnderlyingArray()[0]);
			FastIntegerSet inter2 = FastIntegerSet.intersection(set2, set1);
			assertSame(1, inter2.size());
			assertSame(12, inter2.getUnderlyingArray()[0]);
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(1, 2, 3, 4, 10);
			FastIntegerSet set2 = new FastIntegerSet(1, 2, 3, 5, 10);

			FastIntegerSet inter1 = FastIntegerSet.intersection(set1, set2);
			assertSame(4, inter1.size());
			assertSame(10, inter1.getUnderlyingArray()[inter1.size() - 1]);
			FastIntegerSet inter2 = FastIntegerSet.intersection(set2, set1);
			assertSame(4, inter2.size());
			assertSame(10, inter2.getUnderlyingArray()[inter2.size() - 1]);
		}
		/***/
	}

	@Test
	public void testUnion()
	{
		{
			FastIntegerSet set0 = new FastIntegerSet();
			FastIntegerSet set1 = new FastIntegerSet();
			FastIntegerSet set2 = new FastIntegerSet(4, 5, 6);

			FastIntegerSet union1 = FastIntegerSet.union(set0, set1);
			assertSame(0, union1.size());

			FastIntegerSet union2 = FastIntegerSet.union(set1, set2);
			assertSame(3, union2.size());
			assertTrue(union2.contains(4, 5, 6));

			FastIntegerSet union3 = FastIntegerSet.union(set2, set1);
			assertSame(3, union3.size());
			assertTrue(union3.contains(4, 5, 6));
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(1, 2, 3);
			FastIntegerSet set2 = new FastIntegerSet(4, 5, 6);

			FastIntegerSet union1 = FastIntegerSet.union(set1, set2);
			assertTrue(union1.contains(1, 2, 3, 4, 5, 6));

			FastIntegerSet union2 = FastIntegerSet.union(set2, set1);
			assertTrue(union2.contains(1, 2, 3, 4, 5, 6));
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(4, 5, 6);
			FastIntegerSet set2 = new FastIntegerSet(3, 4, 5);

			FastIntegerSet union1 = FastIntegerSet.union(set1, set2);
			assertSame(4, union1.size());
			assertSame(3, union1.getUnderlyingArray()[0]);
			assertSame(6, union1.getUnderlyingArray()[3]);
			FastIntegerSet union2 = FastIntegerSet.union(set2, set1);
			assertSame(4, union2.size());
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(1, 2, 3);
			FastIntegerSet set2 = new FastIntegerSet(2);

			FastIntegerSet union1 = FastIntegerSet.union(set1, set2);
			assertSame(3, union1.size());
			assertSame(1, union1.getUnderlyingArray()[0]);
			assertSame(2, union1.getUnderlyingArray()[1]);
			assertSame(3, union1.getUnderlyingArray()[2]);
			FastIntegerSet union2 = FastIntegerSet.union(set2, set1);
			assertSame(3, union2.size());
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(11, 12, 13);
			FastIntegerSet set2 = new FastIntegerSet(1, 2, 3, 12);

			FastIntegerSet union1 = FastIntegerSet.union(set1, set2);
			assertSame(6, union1.size());
			assertSame(1, union1.getUnderlyingArray()[0]);
			assertSame(3, union1.getUnderlyingArray()[2]);
			assertSame(13, union1.getUnderlyingArray()[5]);
			FastIntegerSet union2 = FastIntegerSet.union(set2, set1);
			assertSame(6, union2.size());
			assertSame(1, union2.getUnderlyingArray()[0]);
			assertSame(3, union2.getUnderlyingArray()[2]);
			assertSame(13, union2.getUnderlyingArray()[5]);
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(1, 2, 3, 4, 10);
			FastIntegerSet set2 = new FastIntegerSet(1, 2, 3, 5, 10);

			FastIntegerSet union1 = FastIntegerSet.union(set1, set2);
			assertSame(6, union1.size());
			assertSame(1, union1.getUnderlyingArray()[0]);
			assertSame(10, union1.getUnderlyingArray()[5]);
			FastIntegerSet union2 = FastIntegerSet.union(set2, set1);
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
			FastIntegerSet set0 = new FastIntegerSet();
			FastIntegerSet set1 = new FastIntegerSet();
			FastIntegerSet set2 = new FastIntegerSet(4, 5, 6);

			FastIntegerSet diff1 = FastIntegerSet.difference(set0, set1);
			assertSame(0, diff1.size());

			FastIntegerSet diff2 = FastIntegerSet.difference(set2, set1);
			assertSame(3, diff2.size());
			assertTrue(diff2.contains(4, 5, 6));

			FastIntegerSet diff3 = FastIntegerSet.difference(set1, set2);
			assertSame(0, diff3.size());
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(1, 2, 3);
			FastIntegerSet set2 = new FastIntegerSet(4, 5, 6);

			FastIntegerSet diff1 = FastIntegerSet.difference(set1, set2);
			assertTrue(diff1.equals(1, 2, 3));

			FastIntegerSet diff2 = FastIntegerSet.difference(set2, set1);
			assertTrue(diff2.equals(4, 5, 6));
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(4, 5, 6);
			FastIntegerSet set2 = new FastIntegerSet(3, 4, 5);

			FastIntegerSet diff1 = FastIntegerSet.difference(set1, set2);
			assertSame(1, diff1.size());
			assertSame(6, diff1.getUnderlyingArray()[0]);

			FastIntegerSet diff2 = FastIntegerSet.difference(set2, set1);
			assertSame(1, diff2.size());
			assertSame(3, diff2.getUnderlyingArray()[0]);
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(1, 2, 3);
			FastIntegerSet set2 = new FastIntegerSet(2);

			FastIntegerSet diff1 = FastIntegerSet.difference(set1, set2);
			assertSame(2, diff1.size());
			assertSame(1, diff1.getUnderlyingArray()[0]);
			assertSame(3, diff1.getUnderlyingArray()[1]);

			FastIntegerSet diff2 = FastIntegerSet.difference(set2, set1);
			assertSame(0, diff2.size());
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(11, 12, 13);
			FastIntegerSet set2 = new FastIntegerSet(1, 2, 3, 12);

			FastIntegerSet diff1 = FastIntegerSet.difference(set1, set2);
			assertSame(2, diff1.size());
			assertSame(11, diff1.getUnderlyingArray()[0]);
			assertSame(13, diff1.getUnderlyingArray()[1]);

			FastIntegerSet diff2 = FastIntegerSet.difference(set2, set1);
			assertSame(3, diff2.size());
			assertSame(1, diff2.getUnderlyingArray()[0]);
			assertSame(2, diff2.getUnderlyingArray()[1]);
			assertSame(3, diff2.getUnderlyingArray()[2]);
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(1, 2, 3, 4, 10);
			FastIntegerSet set2 = new FastIntegerSet(1, 2, 3, 5, 10);

			FastIntegerSet diff1 = FastIntegerSet.difference(set1, set2);
			assertSame(1, diff1.size());
			assertSame(4, diff1.getUnderlyingArray()[0]);

			FastIntegerSet diff2 = FastIntegerSet.difference(set2, set1);
			assertSame(1, diff2.size());
			assertSame(5, diff2.getUnderlyingArray()[0]);
		}

		{
			FastIntegerSet set1 = new FastIntegerSet(1, 2, 3, 4, 10);

			FastIntegerSet diff = FastIntegerSet.difference(set1, set1);
			assertTrue(diff.isEmpty());
		}

	}
	
	@Test
	public void testPerformance()
	{
		int size = 20000;
		
		FastIntegerSet set = new FastIntegerSet();
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
		System.out.println("timeref="+timeref);		
		
		
		{
			long start = System.nanoTime();
			Random rnd = new Random();
			for (int i = 0; i < size; i++)
			{
				final int key = rnd.nextInt();
				set.add(key);
			}
			long stop = System.nanoTime();
			time = ((double) (stop - start)) ;
		}
		System.out.println("time="+time);
				
		fold = timeref/time;
		System.out.println("add:FastIntegerSet is "+fold+" times faster than HashSet<Integer> ");
		
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
		System.out.println("timeref="+timeref);		
		
		
		{
			long start = System.nanoTime();
			Random rnd = new Random();
			for (int i = 0; i < size; i++)
			{
				final int key = rnd.nextInt();
				FastIntegerSet.union(set, set);
			}
			long stop = System.nanoTime();
			time = ((double) (stop - start)) ;
		}
		System.out.println("time="+time);
				
		fold = timeref/time;
		System.out.println("union: FastIntegerSet is "+fold+" times faster than HashSet<Integer> ");

	}
	
}
