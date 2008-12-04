package utils.math.statistics.transform.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import utils.math.statistics.transform.NormalizedZTransform;

public class NormalizedZTransformTest
{

	@Test
	public void testNormalizedZTransform() throws IOException
	{
		NormalizedZTransform lNormalizedZTransform = new NormalizedZTransform();

		lNormalizedZTransform.enter(-1);
		lNormalizedZTransform.enter(0);
		lNormalizedZTransform.enter(1);

		double[] lStatistic1 = lNormalizedZTransform.getStatistic();
		assertEquals(0, lStatistic1[0]);
		assertEquals(0.8164965809277259, lStatistic1[1]);

		System.out.println(lNormalizedZTransform.transform(1));
		assertTrue(lNormalizedZTransform.transform(0) == 0);
		assertTrue(lNormalizedZTransform.transform(0.5) == 0.5457949611286318);
		assertTrue(lNormalizedZTransform.transform(lStatistic1[1]) == 0.7615941559557649);
		assertTrue(lNormalizedZTransform.transform(1) == 0.8410482573684664);
		assertTrue(lNormalizedZTransform.transform(2) == 0.9852019349430062);

		lNormalizedZTransform.reset();

		lNormalizedZTransform.enter(0);
		lNormalizedZTransform.enter(1);
		lNormalizedZTransform.enter(2);

		double[] lStatistic2 = lNormalizedZTransform.getStatistic();

		assertTrue(lStatistic1[0] + 1 == lStatistic2[0]);
		assertTrue(lStatistic1[1] == lStatistic2[1]);

	}

}
