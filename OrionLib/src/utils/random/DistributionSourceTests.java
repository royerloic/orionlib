package utils.random;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

/**
 */
public class DistributionSourceTests
{

	@Test
	public void testDistributionSource()
	{
		final DistributionSource<String> source = new DistributionSource<String>();

		source.addObject("apple", 0.75);
		source.addObject("orange", 0.25);

		try
		{
			source.prepare(10000, 0.01);
		}
		catch (final Exception e)
		{
			fail("distortion too high");
			e.printStackTrace();
		}
		final Random lRandom = new Random(System.currentTimeMillis());

		final int size = 1000;
		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < size; i++)
		{
			list.add(source.getObject(lRandom));
		}

		int applecout = 0;
		int orangecount = 0;
		for (final String type : list)
		{
			applecout += type == "apple" ? 1 : 0;
			orangecount += type == "orange" ? 1 : 0;
		}

		assertTrue(Math.abs((double) applecout / (double) size - 0.75) < 0.05);
		assertTrue(Math.abs((double) orangecount / (double) size - 0.25) < 0.05);

	}
}
