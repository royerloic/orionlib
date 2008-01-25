package utils.random;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

/**
 */
public class RandomUtilsTests
{
	Random mRandom = new Random(System.currentTimeMillis());

	static ArrayList<String> list = new ArrayList<String>();
	static
	{
		list.add("apple");
		list.add("orange");
		list.add("grape");
		list.add("banana");
	}

	@Test
	public void testRandomSample()
	{
		assertTrue((RandomUtils.randomSample(mRandom, 0, list).isEmpty()));
		assertTrue((RandomUtils.randomSample(mRandom, 1, list).size() == 4));
	}

	@Test
	public void randomElement()
	{
		assertTrue(list.contains(RandomUtils.randomElement(mRandom, list)));
	}

	@Test
	public void doubleToInteger()
	{
		double target = mRandom.nextFloat() * 10;
		double expectedvalue = 0;
		int nb = 1000;
		for (int i = 0; i < nb; i++)
			expectedvalue += RandomUtils.doubleToInteger(mRandom, target);
		expectedvalue /= nb;

		assertTrue(Math.abs(expectedvalue - target) < Math.sqrt(1 / ((double) nb)));
	}

}
