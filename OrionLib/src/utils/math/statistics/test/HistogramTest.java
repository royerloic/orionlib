package utils.math.statistics.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import utils.math.statistics.Histogram;

public class HistogramTest
{

	@Test
	public void HistogramTest() throws IOException
	{
		Histogram lHistogram = new Histogram(10);

		lHistogram.enter(1.0);
		lHistogram.enter(2.0);
		lHistogram.enter(3.0);
		lHistogram.enter(4.0);
		lHistogram.enter(5.0);
		lHistogram.enter(6.0);
		lHistogram.enter(7.0);
		lHistogram.enter(8.0);
		lHistogram.enter(9.0);
		lHistogram.enter(10.0);

		double[] lHistogramArray = lHistogram.getStatistic();

		String result = Arrays.toString(lHistogramArray);
		System.out.println(result);

		assertEquals("[0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1]", result);

	}

}
