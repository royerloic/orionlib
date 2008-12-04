package utils.math.statistics.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import utils.math.statistics.HyperGeometricEnrichement;

public class HyperGeometricEnrichementTest
{

	@Test
	public void testHyperG() throws IOException
	{
		double pvalue;

		/*
		 * pvalue = HyperGeometricEnrichement.hyperG(10, 4, 1, 1, 1);
		 * System.out.println(pvalue); assertEquals(0.004597580943530466,pvalue);/
		 */

		pvalue = HyperGeometricEnrichement.hyperG(100000, 100, 100, 1, 1);
		System.out.println(pvalue);
		assertEquals(0.004597580943530466, pvalue);

		pvalue = HyperGeometricEnrichement.hyperG(1000000, 1000, 1000, 1, 1);
		System.out.println(pvalue);
		assertEquals(0.264240872029061, pvalue);

		pvalue = HyperGeometricEnrichement.hyperG(195554976, 729875, 14642, 500, 1);
		System.out.println(pvalue);
		assertEquals(9.636921972856124E-293, pvalue);
		/**
		 * checked with R: phyper(500, 729875, 195554976-729875, 14642,
		 * lower.tail=FALSE, log = FALSE) [1] 1.018223e-293 close enough...
		 */

	}

	@Test
	public void testgeneralizedHyperG() throws IOException
	{
		double pvalue;

		pvalue = HyperGeometricEnrichement.generalizedHyperG(10, new double[]
		{ 2, 3, 4 }, 1, 1);
		System.out.println(pvalue);
		assertEquals(0.2577777777777757, pvalue);

		pvalue = HyperGeometricEnrichement.generalizedHyperG(1000000, new double[]
		{ 1000, 1000, 1000 }, 1, 1);
		System.out.println(pvalue);
		// assertEquals(0.2577777777777757,pvalue);
	}

	@Test
	public void testHyperGeometricEnrichementTest() throws IOException
	{
		final InputStream lInputStream = HyperGeometricEnrichementTest.class.getResourceAsStream("test.tab.txt");
		final OutputStream lOutputStream = new ByteArrayOutputStream();

		HyperGeometricEnrichement.testStream(	lInputStream,
																					0,
																					0,
																					1,
																					1,
																					lOutputStream);

		final String lString = lOutputStream.toString();
		System.out.println(lString);

		assertTrue(lString.contains("81.44516686402928"));

	}

}
