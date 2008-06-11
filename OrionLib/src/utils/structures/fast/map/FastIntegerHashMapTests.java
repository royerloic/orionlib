package utils.structures.fast.map;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Random;

import org.junit.Test;

/**
 **/
public class FastIntegerHashMapTests
{
	@Test
	public void testPutGet()
	{
		final FastIntegerHashMap<String> map = new FastIntegerHashMap<String>(2);
		map.put(0, "00");
		map.put(0, "0");
		map.put(1, "1");
		map.put(2, "2");

		assertEquals(map.size(), 3);

		assertEquals(map.get(0), "0");
		assertEquals(map.get(1), "1");
		assertEquals(map.get(2), "2");
	}

	@Test
	public void testPutRemoveGet()
	{
		final FastIntegerHashMap<String> map = new FastIntegerHashMap<String>();
		map.put(0, "00");
		map.remove(0);
		assertEquals(map.size(), 0);
		map.put(0, "0");
		map.put(1, "1");
		map.put(2, "2");
		assertEquals(map.size(), 3);

		map.remove(2);
		assertEquals(map.size(), 2);

		assertEquals(map.get(0), "0");
		assertEquals(map.get(1), "1");
		assertEquals(map.get(2), null);
	}

	@Test
	public void testLargeScale()
	{
		final FastIntegerHashMap<String> map = new FastIntegerHashMap<String>(100);
		final HashMap<Integer, String> mapref = new HashMap<Integer, String>(100);

		final Random rnd = new Random();
		for (int i = 0; i < 10000; i++)
		{
			final int key = rnd.nextInt();
			final String value = "" + rnd.nextFloat();
			map.put(key, value);
			mapref.put(key, value);
		}

		for (final Integer key : map)
		{
			final String value = map.get(key);
			assertEquals(mapref.get(key), value);
		}
	}

	@Test
	public void testPerformance()
	{
		final FastIntegerHashMap<String> map = new FastIntegerHashMap<String>(1000);
		final HashMap<Integer, String> mapref = new HashMap<Integer, String>(1000);

		double timeref;
		double time;
		double fold;

		{
			final long start = System.nanoTime();
			final Random rnd = new Random();
			for (int i = 0; i < 1000; i++)
			{
				final int key = rnd.nextInt();
				final String value = "";
				mapref.put(key, value);
			}
			final long stop = System.nanoTime();
			timeref = (stop - start);
		}
		System.out.println("timeref=" + timeref);

		{
			final long start = System.nanoTime();
			final Random rnd = new Random();
			for (int i = 0; i < 1000; i++)
			{
				final int key = rnd.nextInt();
				final String value = "";
				map.put(key, value);
			}
			final long stop = System.nanoTime();
			time = (stop - start);
		}
		System.out.println("time=" + time);

		fold = timeref / time;
		System.out.println("put: FastIntegerHashMap is " + fold
												+ " times faster than HashMap<Integer,O> ");

		{
			final long start = System.nanoTime();
			final Random rnd = new Random();
			for (int i = 0; i < 1000; i++)
			{
				final int key = rnd.nextInt();
				mapref.get(key);
			}
			final long stop = System.nanoTime();
			timeref = (stop - start);
		}
		System.out.println("timeref=" + timeref);

		{
			final long start = System.nanoTime();
			final Random rnd = new Random();
			for (int i = 0; i < 1000; i++)
			{
				final int key = rnd.nextInt();
				map.get(key);
			}
			final long stop = System.nanoTime();
			time = (stop - start);
		}
		System.out.println("time=" + time);

		fold = timeref / time;
		System.out.println("get: FastIntegerHashMap is " + fold
												+ " times faster than HashMap<Integer,O> ");
	}

	/**/
}
