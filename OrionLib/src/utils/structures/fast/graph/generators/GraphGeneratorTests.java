package utils.structures.fast.graph.generators;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import utils.structures.fast.graph.FastIntegerGraph;

/**
 */
public class GraphGeneratorTests
{
	Random mRandom = new Random(System.currentTimeMillis());

	@Test
	public void testErdosRenyi()
	{
		assertTrue(GraphGenerator	.generateErdosRenyiGraph(mRandom, 100, 0)
															.getNumberOfNodes() == 100);
		assertTrue(GraphGenerator	.generateErdosRenyiGraph(mRandom, 100, 0.5)
															.getNumberOfNodes() == 100);
		assertTrue(GraphGenerator	.generateErdosRenyiGraph(mRandom, 100, 1)
															.getNumberOfNodes() == 100);

		assertTrue(GraphGenerator	.generateErdosRenyiGraph(mRandom, 100, 0)
															.getNumberOfEdges() == 0);
		assertTrue(GraphGenerator	.generateErdosRenyiGraph(mRandom, 100, 0.5)
															.getNumberOfEdges() > 0);
		assertTrue(GraphGenerator	.generateErdosRenyiGraph(mRandom, 100, 0.5)
															.getNumberOfEdges() < (100 * 99) / 2);
		assertTrue(GraphGenerator	.generateErdosRenyiGraph(mRandom, 100, 1)
															.getNumberOfEdges() == (100 * 99) / 2);

	}

	@Test
	public void testErdosRenyiDensityFidelity()
	{
		for (int i = 0; i < 10; i++)
		{
			double lTargetDensity = mRandom.nextDouble();
			FastIntegerGraph graph = GraphGenerator.generateErdosRenyiGraph(mRandom,
																																			1000,
																																			lTargetDensity);
			double lEffectiveDensity = graph.getEdgeDensity();
			double lError = Math.abs((lTargetDensity - lEffectiveDensity) / lTargetDensity);
			System.out.println(lError);
			assertTrue(lError < 0.05);
		}
	}

	@Test
	public void testBarabasiAlbert()
	{
		assertTrue(GraphGenerator	.generateBarabasiAlbertGraph(mRandom, 100, 0)
															.getNumberOfNodes() == 100);
		assertTrue(GraphGenerator	.generateBarabasiAlbertGraph(mRandom, 100, 0.5)
															.getNumberOfNodes() == 100);
		assertTrue(GraphGenerator	.generateBarabasiAlbertGraph(mRandom, 100, 1)
															.getNumberOfNodes() == 100);

		assertTrue(GraphGenerator	.generateBarabasiAlbertGraph(mRandom, 100, 0)
															.getNumberOfEdges() == 0);
		assertTrue(GraphGenerator	.generateBarabasiAlbertGraph(mRandom, 100, 0.5)
															.getNumberOfEdges() > 0);
		assertTrue(GraphGenerator	.generateBarabasiAlbertGraph(mRandom, 100, 0.5)
															.getNumberOfEdges() < (100 * 99) / 2);

	}

	@Test
	public void testBarabasiAlbertDensityFidelity()
	{
		for (double i = 0.; i <= 1; i += 0.05)
		{
			double lTargetDensity = i;// mRandom.nextDouble();
			FastIntegerGraph graph = GraphGenerator.generateBarabasiAlbertGraph(mRandom,
																																					100,
																																					lTargetDensity);
			double lEffectiveDensity = graph.getEdgeDensity();
			double lError = Math.abs((lTargetDensity - lEffectiveDensity) / lTargetDensity);
			System.out.println("lTargetDensity=" + lTargetDensity);
			System.out.println("lEffectiveDensity=" + lEffectiveDensity);
			System.out.println(lError);
			// assertTrue(lError<0.95);
		}
	}

}
