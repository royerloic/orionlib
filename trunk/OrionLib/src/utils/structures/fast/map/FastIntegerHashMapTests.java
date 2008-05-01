package utils.structures.fast.map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

/**
 **/
public class FastIntegerHashMapTests
{
	@Test
	public void testPutGet()
	{
		FastIntegerHashMap<String> map = new FastIntegerHashMap<String>(2);
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
		FastIntegerHashMap<String> map = new FastIntegerHashMap<String>();
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
		FastIntegerHashMap<String> map = new FastIntegerHashMap<String>(100);
		HashMap<Integer, String> mapref = new HashMap<Integer, String>(100);

		Random rnd = new Random();
		for (int i = 0; i < 10000; i++)
		{
			final int key = rnd.nextInt();
			final String value = "" + rnd.nextFloat();
			map.put(key, value);
			mapref.put(key, value);
		}

		for (Integer key : map)
		{
			final String value = map.get(key);
			assertEquals(mapref.get(key), value);
		}
	}

	@Test
	public void testPerformance()
	{
		FastIntegerHashMap<String> map = new FastIntegerHashMap<String>(1000);
		HashMap<Integer, String> mapref = new HashMap<Integer, String>(1000);

		
		double timeref;
		double time;
		double fold;
		
		{
			long start = System.nanoTime();
			Random rnd = new Random();
			for (int i = 0; i < 1000; i++)
			{
				final int key = rnd.nextInt();
				final String value = "";
				mapref.put(key, value);
			}
			long stop = System.nanoTime();
			timeref = ((double) (stop - start));
		}
		System.out.println("timeref="+timeref);		
		

		{
			long start = System.nanoTime();
			Random rnd = new Random();
			for (int i = 0; i < 1000; i++)
			{
				final int key = rnd.nextInt();
				final String value = "";
				map.put(key, value);
			}
			long stop = System.nanoTime();
			time = ((double) (stop - start));
		}
		System.out.println("time="+time);
				
		fold = timeref/time;
		System.out.println("put: FastIntegerHashMap is "+fold+" times faster than HashMap<Integer,O> ");

		{
			long start = System.nanoTime();
			Random rnd = new Random();
			for (int i = 0; i < 1000; i++)
			{
				final int key = rnd.nextInt();
				mapref.get(key);
			}
			long stop = System.nanoTime();
			timeref = ((double) (stop - start)) ;
		}
		System.out.println("timeref="+timeref);		
		

		{
			long start = System.nanoTime();
			Random rnd = new Random();
			for (int i = 0; i < 1000; i++)
			{
				final int key = rnd.nextInt();
				map.get(key);
			}
			long stop = System.nanoTime();
			time = ((double) (stop - start));
		}
		System.out.println("time="+time);
				
		fold = timeref/time;
		System.out.println("get: FastIntegerHashMap is "+fold+" times faster than HashMap<Integer,O> ");
	}

	/**/
}
