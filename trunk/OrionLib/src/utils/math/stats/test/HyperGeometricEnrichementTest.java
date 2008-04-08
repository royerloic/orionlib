package utils.math.stats.test;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import utils.math.stats.HyperGeometricEnrichement;

public class HyperGeometricEnrichementTest
{

	@Test
	public void testHyperGeometricEnrichementTest() throws IOException
	{
		InputStream lInputStream = HyperGeometricEnrichementTest.class.getResourceAsStream("Test.tab.txt");
		OutputStream lOutputStream = new ByteArrayOutputStream();

		HyperGeometricEnrichement.testStream(	lInputStream,
																					0,
																					0,
																					1,
																					1,
																					lOutputStream);

		String lString = lOutputStream.toString();
		System.out.println(lString);

		assertTrue(lString.contains("81.44516686402928"));

		
	}

}
