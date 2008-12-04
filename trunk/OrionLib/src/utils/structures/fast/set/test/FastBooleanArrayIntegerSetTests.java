package utils.structures.fast.set.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import utils.structures.fast.set.FastBooleanArrayIntegerSet;
import utils.structures.fast.set.FastIntegerSet;

/**
 */
public class FastBooleanArrayIntegerSetTests
{

	@Test
	public void genericTest()
	{
		IntegerSetFactory lIntegerSetFactory = new IntegerSetFactory()
		{

			public FastIntegerSet createEmptySet()
			{
				return new FastBooleanArrayIntegerSet();
			}

			public FastIntegerSet createSet(int... pListOfIntegers)
			{
				return new FastBooleanArrayIntegerSet(true, pListOfIntegers);
			}

			public String getSetTypeName()
			{
				return "FastBooleanArrayIntegerSet";
			}
		};

		GenericIntegerSetTest.testAll(lIntegerSetFactory);
	}

	private static void add(final Set<Integer> set1,
													final Set<Integer> set2,
													final int i)
	{
		set1.add(i);
		set2.add(i);
	}

	private static void del(final Set<Integer> set1,
													final Set<Integer> set2,
													final int i)
	{
		set1.remove(i);
		set2.remove(i);
	}

	private static void union(final Set<Integer> set1,
														final Set<Integer> set2,
														final Set<Integer> addedset1,
														final Set<Integer> addedset2)
	{
		set1.addAll(addedset1);
		set2.addAll(addedset2);
	}

	private static void intersection(	final Set<Integer> set1,
																		final Set<Integer> set2,
																		final Set<Integer> retainedset1,
																		final Set<Integer> retainedset2)
	{
		set1.retainAll(retainedset1);
		set2.retainAll(retainedset2);
	}

	private static void difference(	final Set<Integer> set1,
																	final Set<Integer> set2,
																	final Set<Integer> removedset1,
																	final Set<Integer> removedset2)
	{
		set1.removeAll(removedset1);
		set2.removeAll(removedset2);
	}

	private static void contains(	final Set<Integer> set1,
																final Set<Integer> set2,
																final Set<Integer> containedset1,
																final Set<Integer> containedset2)
	{
		boolean result = set1.containsAll(containedset1) == set2.containsAll(containedset1);
		assertTrue(result);
		result = set1.containsAll(containedset2) == set2.containsAll(containedset2);
		if (!result)
		{
			throw new RuntimeException();
		}
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
		final int iterations = 100000;
		final Set<Integer> testset1 = new TreeSet<Integer>();
		final Set<Integer> testset2 = new FastBooleanArrayIntegerSet();
		final Set<Integer> set1 = new TreeSet<Integer>();
		final Set<Integer> set2 = new FastBooleanArrayIntegerSet();
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
		final int iterations = 100000;
		final Set<Integer> testset1 = new TreeSet<Integer>();
		final Set<Integer> testset2 = new FastBooleanArrayIntegerSet();
		final TreeSet<Integer> set1 = new TreeSet<Integer>();
		final Set<Integer> set2 = new FastBooleanArrayIntegerSet();
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
		final int iterations = 100000;
		final Set<Integer> testset1 = new TreeSet<Integer>();
		final Set<Integer> testset2 = new FastBooleanArrayIntegerSet();
		final Set<Integer> set1 = new TreeSet<Integer>();
		final Set<Integer> set2 = new FastBooleanArrayIntegerSet();
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
		final int iterations = 100000;
		final Set<Integer> testset1 = new TreeSet<Integer>();
		final Set<Integer> testset2 = new FastBooleanArrayIntegerSet();
		final Set<Integer> set1 = new TreeSet<Integer>();
		final Set<Integer> set2 = new FastBooleanArrayIntegerSet();
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
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							4,
																																							5,
																																							6);

			assertEquals(0, FastBooleanArrayIntegerSet.relationship(set1, set2));
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							2,
																																							3,
																																							4);

			assertTrue(FastBooleanArrayIntegerSet.relationship(set1, set2) == 2);
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							2,
																																							3);

			assertTrue(FastBooleanArrayIntegerSet.relationship(set1, set2) == 1);
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3,
																																							4);

			assertTrue(FastBooleanArrayIntegerSet.relationship(set1, set2) == -1);
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3);

			assertTrue(FastBooleanArrayIntegerSet.relationship(set1, set2) == 3);
		}

		// different ints:

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							104,
																																							105,
																																							106);

			assertEquals(0, FastBooleanArrayIntegerSet.relationship(set1, set2));
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1001,
																																							1,
																																							2,
																																							103);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							2,
																																							103,
																																							4,
																																							1004);

			assertTrue(FastBooleanArrayIntegerSet.relationship(set1, set2) == 2);
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							103);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							2,
																																							103);

			assertTrue(FastBooleanArrayIntegerSet.relationship(set1, set2) == 1);
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							103);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							103,
																																							4,
																																							104);

			assertTrue(FastBooleanArrayIntegerSet.relationship(set1, set2) == -1);
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3,
																																							101,
																																							1001);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3,
																																							101,
																																							1001);

			assertTrue(FastBooleanArrayIntegerSet.relationship(set1, set2) == 3);
		}

	}

	@Test
	public void testIntersectionStatic()
	{
		{
			final FastBooleanArrayIntegerSet set0 = new FastBooleanArrayIntegerSet();
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet();

			final FastBooleanArrayIntegerSet lSetInter = FastBooleanArrayIntegerSet.intersection(	set0,
																																														set1);
			assertTrue(lSetInter.isEmpty());

			final FastBooleanArrayIntegerSet lSetInter2 = FastBooleanArrayIntegerSet.intersection(set0,
																																														set1);
			assertTrue(lSetInter2.isEmpty());

			final FastBooleanArrayIntegerSet lSetInter3 = FastBooleanArrayIntegerSet.intersection(set1,
																																														set0);
			assertTrue(lSetInter3.isEmpty());
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							4,
																																							5,
																																							6);

			FastBooleanArrayIntegerSet inter = FastBooleanArrayIntegerSet.intersection(	set1,
																																									set2);
			assertTrue(inter.isEmpty());
			inter = FastBooleanArrayIntegerSet.intersection(set2, set1);
			assertTrue(inter.isEmpty());
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							4,
																																							5,
																																							6);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							3,
																																							4,
																																							5);

			final FastBooleanArrayIntegerSet inter1 = FastBooleanArrayIntegerSet.intersection(set1,
																																												set2);
			assertSame(2, inter1.size());
			assertTrue(inter1.contains(4));
			assertTrue(inter1.contains(5));
			final FastBooleanArrayIntegerSet inter2 = FastBooleanArrayIntegerSet.intersection(set2,
																																												set1);
			assertSame(2, inter2.size());
			assertTrue(inter2.contains(4));
			assertTrue(inter2.contains(5));
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							2);

			final FastBooleanArrayIntegerSet inter = FastBooleanArrayIntegerSet.intersection(	set1,
																																												set2);
			assertEquals(1, inter.size());
			assertTrue(inter.contains(2));
			final FastBooleanArrayIntegerSet lSetInter2 = FastBooleanArrayIntegerSet.intersection(set2,
																																														set1);
			assertEquals(1, lSetInter2.size());
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							11,
																																							12,
																																							13);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3,
																																							12);

			final FastBooleanArrayIntegerSet inter1 = FastBooleanArrayIntegerSet.intersection(set1,
																																												set2);
			assertSame(1, inter1.size());
			assertTrue(inter1.contains(12));
			final FastBooleanArrayIntegerSet inter2 = FastBooleanArrayIntegerSet.intersection(set2,
																																												set1);
			assertSame(1, inter2.size());
			assertTrue(inter2.contains(12));
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3,
																																							4,
																																							10);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3,
																																							5,
																																							10);

			final FastBooleanArrayIntegerSet inter1 = FastBooleanArrayIntegerSet.intersection(set1,
																																												set2);
			assertSame(4, inter1.size());
			assertTrue(inter1.contains(10));
			final FastBooleanArrayIntegerSet inter2 = FastBooleanArrayIntegerSet.intersection(set2,
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
			final FastBooleanArrayIntegerSet set0 = new FastBooleanArrayIntegerSet();
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet();
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							4,
																																							5,
																																							6);

			final FastBooleanArrayIntegerSet union1 = FastBooleanArrayIntegerSet.union(	set0,
																																									set1);
			assertSame(0, union1.size());

			final FastBooleanArrayIntegerSet union2 = FastBooleanArrayIntegerSet.union(	set1,
																																									set2);
			assertSame(3, union2.size());
			assertTrue(union2.containsAll(4, 5, 6));

			final FastBooleanArrayIntegerSet union3 = FastBooleanArrayIntegerSet.union(	set2,
																																									set1);
			assertSame(3, union3.size());
			assertTrue(union3.containsAll(4, 5, 6));
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							4,
																																							5,
																																							6);

			final FastBooleanArrayIntegerSet union1 = FastBooleanArrayIntegerSet.union(	set1,
																																									set2);
			assertTrue(union1.containsAll(1, 2, 3, 4, 5, 6));

			final FastBooleanArrayIntegerSet union2 = FastBooleanArrayIntegerSet.union(	set2,
																																									set1);
			assertTrue(union2.containsAll(1, 2, 3, 4, 5, 6));
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							4,
																																							5,
																																							6);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							3,
																																							4,
																																							5);

			final FastBooleanArrayIntegerSet union1 = FastBooleanArrayIntegerSet.union(	set1,
																																									set2);
			assertSame(4, union1.size());
			assertTrue(union1.contains(3));
			assertTrue(union1.contains(6));
			final FastBooleanArrayIntegerSet union2 = FastBooleanArrayIntegerSet.union(	set2,
																																									set1);
			assertSame(4, union2.size());
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							2);

			final FastBooleanArrayIntegerSet union1 = FastBooleanArrayIntegerSet.union(	set1,
																																									set2);
			assertSame(3, union1.size());
			assertTrue(union1.contains(1));
			assertTrue(union1.contains(2));
			assertTrue(union1.contains(3));
			final FastBooleanArrayIntegerSet union2 = FastBooleanArrayIntegerSet.union(	set2,
																																									set1);
			assertSame(3, union2.size());
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							11,
																																							12,
																																							13);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3,
																																							12);

			final FastBooleanArrayIntegerSet union1 = FastBooleanArrayIntegerSet.union(	set1,
																																									set2);
			assertSame(6, union1.size());
			assertTrue(union1.contains(1));
			assertTrue(union1.contains(3));
			assertTrue(union1.contains(13));
			final FastBooleanArrayIntegerSet union2 = FastBooleanArrayIntegerSet.union(	set2,
																																									set1);
			assertSame(6, union2.size());
			assertTrue(union2.contains(1));
			assertTrue(union2.contains(3));
			assertTrue(union2.contains(13));
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3,
																																							4,
																																							10);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3,
																																							5,
																																							10);

			final FastBooleanArrayIntegerSet union1 = FastBooleanArrayIntegerSet.union(	set1,
																																									set2);
			assertSame(6, union1.size());
			assertTrue(union1.contains(1));
			assertTrue(union1.contains(10));
			final FastBooleanArrayIntegerSet union2 = FastBooleanArrayIntegerSet.union(	set2,
																																									set1);
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
			final FastBooleanArrayIntegerSet set0 = new FastBooleanArrayIntegerSet();
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet();
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							4,
																																							5,
																																							6);

			final FastBooleanArrayIntegerSet diff1 = FastBooleanArrayIntegerSet.difference(	set0,
																																											set1);
			assertSame(0, diff1.size());

			final FastBooleanArrayIntegerSet diff2 = FastBooleanArrayIntegerSet.difference(	set2,
																																											set1);
			assertSame(3, diff2.size());
			assertTrue(diff2.containsAll(4, 5, 6));

			final FastBooleanArrayIntegerSet diff3 = FastBooleanArrayIntegerSet.difference(	set1,
																																											set2);
			assertSame(0, diff3.size());
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							4,
																																							5,
																																							6);

			final FastBooleanArrayIntegerSet diff1 = FastBooleanArrayIntegerSet.difference(	set1,
																																											set2);
			assertTrue(diff1.equals(1, 2, 3));

			final FastBooleanArrayIntegerSet diff2 = FastBooleanArrayIntegerSet.difference(	set2,
																																											set1);
			assertTrue(diff2.equals(4, 5, 6));
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							4,
																																							5,
																																							6);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							3,
																																							4,
																																							5);

			final FastBooleanArrayIntegerSet diff1 = FastBooleanArrayIntegerSet.difference(	set1,
																																											set2);
			assertSame(1, diff1.size());
			assertTrue(diff1.contains(6));

			final FastBooleanArrayIntegerSet diff2 = FastBooleanArrayIntegerSet.difference(	set2,
																																											set1);
			assertSame(1, diff2.size());
			assertTrue(diff2.contains(3));
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							2);

			final FastBooleanArrayIntegerSet diff1 = FastBooleanArrayIntegerSet.difference(	set1,
																																											set2);
			assertSame(2, diff1.size());
			assertTrue(diff1.contains(1));
			assertTrue(diff1.contains(3));

			final FastBooleanArrayIntegerSet diff2 = FastBooleanArrayIntegerSet.difference(	set2,
																																											set1);
			assertSame(0, diff2.size());
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							11,
																																							12,
																																							13);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3,
																																							12);

			final FastBooleanArrayIntegerSet diff1 = FastBooleanArrayIntegerSet.difference(	set1,
																																											set2);
			assertSame(2, diff1.size());
			assertTrue(diff1.contains(11));
			assertTrue(diff1.contains(13));

			final FastBooleanArrayIntegerSet diff2 = FastBooleanArrayIntegerSet.difference(	set2,
																																											set1);
			assertSame(3, diff2.size());
			assertTrue(diff2.contains(1));
			assertTrue(diff2.contains(2));
			assertTrue(diff2.contains(3));
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3,
																																							4,
																																							10);
			final FastBooleanArrayIntegerSet set2 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3,
																																							5,
																																							10);

			final FastBooleanArrayIntegerSet diff1 = FastBooleanArrayIntegerSet.difference(	set1,
																																											set2);
			assertSame(1, diff1.size());
			assertTrue(diff1.contains(4));

			final FastBooleanArrayIntegerSet diff2 = FastBooleanArrayIntegerSet.difference(	set2,
																																											set1);
			assertSame(1, diff2.size());
			assertTrue(diff2.contains(5));
		}

		{
			final FastBooleanArrayIntegerSet set1 = new FastBooleanArrayIntegerSet(	true,
																																							1,
																																							2,
																																							3,
																																							4,
																																							10);

			final FastBooleanArrayIntegerSet diff = FastBooleanArrayIntegerSet.difference(set1,
																																										set1);
			assertTrue(diff.isEmpty());
		}

	}

}
