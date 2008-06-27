package utils.math.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import utils.math.statistics.Histogram;
import utils.math.statistics.HyperGeometricEnrichement;
import utils.math.statistics.transform.NormalizedZTransform;

public class HypotBenchmarkTest
{
	static final int n = 100000;

	double[] array1 = new double[n];
	double[] array2 = new double[n];

	@Test
	public void HypotBenchmarkTest() throws IOException
	{
		Random rnd = new Random();
		for (int i = 0; i < n; i++)
		{
			double angle = 2*Math.PI*rnd.nextDouble();
			array1[i] = Math.cos(angle);
			array2[i] = Math.sin(angle);
		}

		fasthypot(1, 0);
		fasthypot(0.707, 0.707);
		
		double nanohypot;
		{
			long start = System.nanoTime();

			for (int j = 0; j < n; j++)
				for (int i = 0; i < n; i++)
				{
					sqrt(squaresum(array1[i], array2[i]));
					dummy(array1[i], array2[i]);
					dummyfinal(array1[i], array2[i]);
					sum(array1[i], array2[i]);
					
									
					double val1 = hypot(array1[i], array2[i]);
					double val2 = fasthypot(array1[i], array2[i]);
					double val3 = Math.hypot(array1[i], array2[i]);		
					
					System.out.println("x="+array1[i]);
					System.out.println("y="+array2[i]);
					//System.out.println(val1);
					System.out.println(val2);
					
				}

			long stop = System.nanoTime();

			double elapsed = (double) (stop - start);
			nanohypot = elapsed / n;
		}
		System.out.println("hypot: " + nanohypot + " nanoseconds per call");

	}

	private static final double squaresum(double x, double y)
	{
		return x*x+y*y;
	}
	
	private static final double sqrt(double x)
	{
		return Math.sqrt(x);
	}
	
	private static final double dummyfinal(double x, double y)
	{
		return x;
	}

	private static double dummy(double x, double y)
	{
		return x;
	}

	private static final double sum(double x, double y)
	{
		return x+y;
	}
	
	private static final double hypot(double dx, double dy)
	{
		return Math.sqrt(dx * dx + dy * dy);
	}

	private static final double fasthypot(double x, double y)
	{
		final double ax = x > 0 ? x : -x;
		final double ay = y > 0 ? y : -y;
		final double maxaxay = ax > ay ? ax : ay;
		final double axpay = 0.70710 * (ax + ay);
		final double minaxpaymaxaxay = axpay > maxaxay ? axpay : maxaxay;
		return minaxpaymaxaxay;
	}

}
