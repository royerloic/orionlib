package utils.math.statistics.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.junit.Test;

import utils.math.statistics.Histogram;
import utils.math.statistics.HyperGeometricEnrichement;
import utils.math.statistics.transform.NormalizedZTransform;

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
