package utils.structures.fast.graph.algorythms.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import utils.structures.fast.graph.FastGraph;
import utils.structures.fast.graph.algorythms.BicliqueDetector;
import utils.utils.Arrays;

/**
 */
public class BicliqueDetectorTest
{

	@Test
	public void testCountBicliques() throws IOException
	{

		final InputStream lInputStream = BicliqueDetectorTest.class.getResourceAsStream("test.edg");

		FastGraph<String> lGraph;

		lGraph = FastGraph.readEdgeFile(lInputStream);
		assertEquals(11,BicliqueDetector.countBicliques(lGraph, 1, 1));
		assertEquals(11,BicliqueDetector.countBicliques(lGraph, 2, 2));
		assertEquals(2,BicliqueDetector.countBicliques(lGraph, 1, 6));
		assertEquals(4,BicliqueDetector.countBicliques(lGraph, 1, 3));
	}
	
	@Test
	public void testDetectBicliques() throws IOException
	{

		final InputStream lInputStream = BicliqueDetectorTest.class.getResourceAsStream("test.edg");

		FastGraph<String> lGraph;

		lGraph = FastGraph.readEdgeFile(lInputStream);
		assertTrue(BicliqueDetector.detectBicliques(lGraph, 2, 2));
		assertTrue(BicliqueDetector.detectBicliques(lGraph, 2, 3));
		assertTrue(BicliqueDetector.detectBicliques(lGraph, 2, 4));
		assertTrue(BicliqueDetector.detectBicliques(lGraph, 2, 5));
		assertFalse(BicliqueDetector.detectBicliques(lGraph, 2, 6));

	}
		
	
	@Test
	public void testCountAllBicliques() throws IOException
	{

		final InputStream lInputStream = BicliqueDetectorTest.class.getResourceAsStream("test.edg");

		FastGraph<String> lGraph;

		lGraph = FastGraph.readEdgeFile(lInputStream);
		int[][] lSpectrum = BicliqueDetector.countAllBicliques(lGraph, 8, 8);
		//System.out.println(Arrays.deepToString(lCountAllBicliques));
		for (int[] lIs : lSpectrum)
		{
			for (int lI : lIs)
			{
				System.out.print(lI+"\t");
			}
			System.out.print("\n");
		}
	}

}
