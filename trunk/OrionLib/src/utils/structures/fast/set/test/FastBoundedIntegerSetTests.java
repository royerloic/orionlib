package utils.structures.fast.set.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import utils.structures.fast.set.FastBooleanArrayIntegerSet;
import utils.structures.fast.set.FastBoundedIntegerSet;
import utils.structures.fast.set.FastIntegerSet;

/**
 */
public class FastBoundedIntegerSetTests
{

	@Test
	public void genericTest()
	{
		IntegerSetFactory lIntegerSetFactory = new IntegerSetFactory()
		{

			public FastIntegerSet createEmptySet()
			{
				return new FastBoundedIntegerSet();
			}

			public FastIntegerSet createSet(int... pListOfIntegers)
			{
				return new FastBoundedIntegerSet(true, pListOfIntegers);
			}

			public String getSetTypeName()
			{
				return "FastBoundedIntegerSet";
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
		if (!result)
		{
			throw new RuntimeException();
		}
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
		final Set<Integer> testset2 = new FastBoundedIntegerSet();
		final Set<Integer> set1 = new TreeSet<Integer>();
		final Set<Integer> set2 = new FastBoundedIntegerSet();
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
		final Set<Integer> testset2 = new FastBoundedIntegerSet();
		final Set<Integer> set1 = new TreeSet<Integer>();
		final Set<Integer> set2 = new FastBoundedIntegerSet();
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
		final Set<Integer> testset2 = new FastBoundedIntegerSet();
		final Set<Integer> set1 = new TreeSet<Integer>();
		final Set<Integer> set2 = new FastBoundedIntegerSet();
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
		final Set<Integer> testset2 = new FastBoundedIntegerSet();
		final Set<Integer> set1 = new TreeSet<Integer>();
		final Set<Integer> set2 = new FastBoundedIntegerSet();
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
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		4,
																																		5,
																																		6);

			assertEquals(0, FastBoundedIntegerSet.relationship(set1, set2));
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		2,
																																		3,
																																		4);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == 2);
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 2, 3);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == 1);
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3,
																																		4);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == -1);
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == 3);
		}

		// different ints:

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		104,
																																		105,
																																		106);

			assertEquals(0, FastBoundedIntegerSet.relationship(set1, set2));
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1001,
																																		1,
																																		2,
																																		103);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		2,
																																		103,
																																		4,
																																		1004);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == 2);
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		103);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 2, 103);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == 1);
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		103);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		103,
																																		4,
																																		104);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == -1);
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3,
																																		101,
																																		1001);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3,
																																		101,
																																		1001);

			assertTrue(FastBoundedIntegerSet.relationship(set1, set2) == 3);
		}

	}

	@Test
	public void testIntersectionStatic()
	{
		{
			final FastBoundedIntegerSet set0 = new FastBoundedIntegerSet();
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet();

			final FastBoundedIntegerSet lSetInter = FastBoundedIntegerSet.intersection(	set0,
																																									set1);
			assertTrue(lSetInter.isEmpty());

			final FastBoundedIntegerSet lSetInter2 = FastBoundedIntegerSet.intersection(set0,
																																									set1);
			assertTrue(lSetInter2.isEmpty());

			final FastBoundedIntegerSet lSetInter3 = FastBoundedIntegerSet.intersection(set1,
																																									set0);
			assertTrue(lSetInter3.isEmpty());
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		4,
																																		5,
																																		6);

			FastBoundedIntegerSet inter = FastBoundedIntegerSet.intersection(	set1,
																																				set2);
			assertTrue(inter.isEmpty());
			inter = FastBoundedIntegerSet.intersection(set2, set1);
			assertTrue(inter.isEmpty());
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		4,
																																		5,
																																		6);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		3,
																																		4,
																																		5);

			final FastBoundedIntegerSet inter1 = FastBoundedIntegerSet.intersection(set1,
																																							set2);
			assertSame(2, inter1.size());
			assertTrue(inter1.contains(4));
			assertTrue(inter1.contains(5));
			final FastBoundedIntegerSet inter2 = FastBoundedIntegerSet.intersection(set2,
																																							set1);
			assertSame(2, inter2.size());
			assertTrue(inter2.contains(4));
			assertTrue(inter2.contains(5));
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 2);

			final FastBoundedIntegerSet inter = FastBoundedIntegerSet.intersection(	set1,
																																							set2);
			assertEquals(1, inter.size());
			assertTrue(inter.contains(2));
			final FastBoundedIntegerSet lSetInter2 = FastBoundedIntegerSet.intersection(set2,
																																									set1);
			assertEquals(1, lSetInter2.size());
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		11,
																																		12,
																																		13);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3,
																																		12);

			final FastBoundedIntegerSet inter1 = FastBoundedIntegerSet.intersection(set1,
																																							set2);
			assertSame(1, inter1.size());
			assertTrue(inter1.contains(12));
			final FastBoundedIntegerSet inter2 = FastBoundedIntegerSet.intersection(set2,
																																							set1);
			assertSame(1, inter2.size());
			assertTrue(inter2.contains(12));
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3,
																																		4,
																																		10);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3,
																																		5,
																																		10);

			final FastBoundedIntegerSet inter1 = FastBoundedIntegerSet.intersection(set1,
																																							set2);
			assertSame(4, inter1.size());
			assertTrue(inter1.contains(10));
			final FastBoundedIntegerSet inter2 = FastBoundedIntegerSet.intersection(set2,
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
			final FastBoundedIntegerSet set0 = new FastBoundedIntegerSet();
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet();
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		4,
																																		5,
																																		6);

			final FastBoundedIntegerSet union1 = FastBoundedIntegerSet.union(	set0,
																																				set1);
			assertSame(0, union1.size());

			final FastBoundedIntegerSet union2 = FastBoundedIntegerSet.union(	set1,
																																				set2);
			assertSame(3, union2.size());
			assertTrue(union2.containsAll(4, 5, 6));

			final FastBoundedIntegerSet union3 = FastBoundedIntegerSet.union(	set2,
																																				set1);
			assertSame(3, union3.size());
			assertTrue(union3.containsAll(4, 5, 6));
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		4,
																																		5,
																																		6);

			final FastBoundedIntegerSet union1 = FastBoundedIntegerSet.union(	set1,
																																				set2);
			assertTrue(union1.containsAll(1, 2, 3, 4, 5, 6));

			final FastBoundedIntegerSet union2 = FastBoundedIntegerSet.union(	set2,
																																				set1);
			assertTrue(union2.containsAll(1, 2, 3, 4, 5, 6));
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		4,
																																		5,
																																		6);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		3,
																																		4,
																																		5);

			final FastBoundedIntegerSet union1 = FastBoundedIntegerSet.union(	set1,
																																				set2);
			assertSame(4, union1.size());
			assertTrue(union1.contains(3));
			assertTrue(union1.contains(6));
			final FastBoundedIntegerSet union2 = FastBoundedIntegerSet.union(	set2,
																																				set1);
			assertSame(4, union2.size());
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 2);

			final FastBoundedIntegerSet union1 = FastBoundedIntegerSet.union(	set1,
																																				set2);
			assertSame(3, union1.size());
			assertTrue(union1.contains(1));
			assertTrue(union1.contains(2));
			assertTrue(union1.contains(3));
			final FastBoundedIntegerSet union2 = FastBoundedIntegerSet.union(	set2,
																																				set1);
			assertSame(3, union2.size());
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		11,
																																		12,
																																		13);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3,
																																		12);

			final FastBoundedIntegerSet union1 = FastBoundedIntegerSet.union(	set1,
																																				set2);
			assertSame(6, union1.size());
			assertTrue(union1.contains(1));
			assertTrue(union1.contains(3));
			assertTrue(union1.contains(13));
			final FastBoundedIntegerSet union2 = FastBoundedIntegerSet.union(	set2,
																																				set1);
			assertSame(6, union2.size());
			assertTrue(union2.contains(1));
			assertTrue(union2.contains(3));
			assertTrue(union2.contains(13));
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3,
																																		4,
																																		10);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3,
																																		5,
																																		10);

			final FastBoundedIntegerSet union1 = FastBoundedIntegerSet.union(	set1,
																																				set2);
			assertSame(6, union1.size());
			assertTrue(union1.contains(1));
			assertTrue(union1.contains(10));
			final FastBoundedIntegerSet union2 = FastBoundedIntegerSet.union(	set2,
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
			final FastBoundedIntegerSet set0 = new FastBoundedIntegerSet();
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet();
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		4,
																																		5,
																																		6);

			final FastBoundedIntegerSet diff1 = FastBoundedIntegerSet.difference(	set0,
																																						set1);
			assertSame(0, diff1.size());

			final FastBoundedIntegerSet diff2 = FastBoundedIntegerSet.difference(	set2,
																																						set1);
			assertSame(3, diff2.size());
			assertTrue(diff2.containsAll(4, 5, 6));

			final FastBoundedIntegerSet diff3 = FastBoundedIntegerSet.difference(	set1,
																																						set2);
			assertSame(0, diff3.size());
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		4,
																																		5,
																																		6);

			final FastBoundedIntegerSet diff1 = FastBoundedIntegerSet.difference(	set1,
																																						set2);
			assertTrue(diff1.equals(1, 2, 3));

			final FastBoundedIntegerSet diff2 = FastBoundedIntegerSet.difference(	set2,
																																						set1);
			assertTrue(diff2.equals(4, 5, 6));
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		4,
																																		5,
																																		6);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		3,
																																		4,
																																		5);

			final FastBoundedIntegerSet diff1 = FastBoundedIntegerSet.difference(	set1,
																																						set2);
			assertSame(1, diff1.size());
			assertTrue(diff1.contains(6));

			final FastBoundedIntegerSet diff2 = FastBoundedIntegerSet.difference(	set2,
																																						set1);
			assertSame(1, diff2.size());
			assertTrue(diff2.contains(3));
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(true, 2);

			final FastBoundedIntegerSet diff1 = FastBoundedIntegerSet.difference(	set1,
																																						set2);
			assertSame(2, diff1.size());
			assertTrue(diff1.contains(1));
			assertTrue(diff1.contains(3));

			final FastBoundedIntegerSet diff2 = FastBoundedIntegerSet.difference(	set2,
																																						set1);
			assertSame(0, diff2.size());
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		11,
																																		12,
																																		13);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3,
																																		12);

			final FastBoundedIntegerSet diff1 = FastBoundedIntegerSet.difference(	set1,
																																						set2);
			assertSame(2, diff1.size());
			assertTrue(diff1.contains(11));
			assertTrue(diff1.contains(13));

			final FastBoundedIntegerSet diff2 = FastBoundedIntegerSet.difference(	set2,
																																						set1);
			assertSame(3, diff2.size());
			assertTrue(diff2.contains(1));
			assertTrue(diff2.contains(2));
			assertTrue(diff2.contains(3));
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3,
																																		4,
																																		10);
			final FastBoundedIntegerSet set2 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3,
																																		5,
																																		10);

			final FastBoundedIntegerSet diff1 = FastBoundedIntegerSet.difference(	set1,
																																						set2);
			assertSame(1, diff1.size());
			assertTrue(diff1.contains(4));

			final FastBoundedIntegerSet diff2 = FastBoundedIntegerSet.difference(	set2,
																																						set1);
			assertSame(1, diff2.size());
			assertTrue(diff2.contains(5));
		}

		{
			final FastBoundedIntegerSet set1 = new FastBoundedIntegerSet(	true,
																																		1,
																																		2,
																																		3,
																																		4,
																																		10);

			final FastBoundedIntegerSet diff = FastBoundedIntegerSet.difference(set1,
																																					set1);
			assertTrue(diff.isEmpty());
		}

	}

}
