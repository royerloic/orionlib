package utils.structures.fast.graph.test;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import utils.structures.fast.graph.FastIntegerGraph;
import utils.structures.fast.graph.FastIntegerGraphNoises;

/**
 */
public class FastIntegerGraphNoisesTests
{

	@Test
	public void testRewireOnce()
	{
		final FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

		lFastIntegerGraph.addEdge(0, 1);
		lFastIntegerGraph.addEdge(0, 2);
		lFastIntegerGraph.addEdge(0, 3);

		lFastIntegerGraph.addEdge(1, 11);
		lFastIntegerGraph.addEdge(1, 12);
		lFastIntegerGraph.addEdge(1, 13);

		lFastIntegerGraph.addEdge(2, 21);
		lFastIntegerGraph.addEdge(2, 22);
		lFastIntegerGraph.addEdge(2, 23);

		lFastIntegerGraph.addEdge(3, 31);
		lFastIntegerGraph.addEdge(3, 32);
		lFastIntegerGraph.addEdge(3, 33);

		final Random lRandom = new Random(System.currentTimeMillis());
		// assertTrue(FastIntegerGraphNoises.rewireOnce(lRandom,
		// lFastIntegerGraph,10));

		final int lNumberOfEdges = lFastIntegerGraph.getNumberOfEdges();

		final int times = 100;
		final int lSucc = FastIntegerGraphNoises.rewire(lRandom,
																										lFastIntegerGraph,
																										times);

		assertTrue(lSucc > times * 0.90);

		assertTrue(lNumberOfEdges == lFastIntegerGraph.getNumberOfEdges());

	}

	// @Test
	/*****************************************************************************
	 * public void testRewireOnceOld() { FastIntegerGraph lFastIntegerGraph = new
	 * FastIntegerGraph();
	 * 
	 * lFastIntegerGraph.addEdge(0, 1); lFastIntegerGraph.addEdge(0, 2);
	 * lFastIntegerGraph.addEdge(0, 3);
	 * 
	 * lFastIntegerGraph.addEdge(1, 11); lFastIntegerGraph.addEdge(1, 12);
	 * lFastIntegerGraph.addEdge(1, 13);
	 * 
	 * lFastIntegerGraph.addEdge(2, 21); lFastIntegerGraph.addEdge(2, 22);
	 * lFastIntegerGraph.addEdge(2, 23);
	 * 
	 * lFastIntegerGraph.addEdge(3, 31); lFastIntegerGraph.addEdge(3, 32);
	 * lFastIntegerGraph.addEdge(3, 33);
	 * 
	 * Random lRandom = new Random(System.currentTimeMillis());
	 * //assertTrue(FastIntegerGraphNoises.rewireOnce(lRandom,
	 * lFastIntegerGraph,10));
	 * 
	 * 
	 * 
	 * int lNumberOfEdges = lFastIntegerGraph.getNumberOfEdges(); int times = 100;
	 * 
	 * int lNumberOfRewirings = FastIntegerGraphNoises.rewire(lRandom,
	 * lFastIntegerGraph,1);
	 * 
	 * assertTrue(lNumberOfRewirings>0.95*((double)times));
	 * assertTrue(lNumberOfEdges==lFastIntegerGraph.getNumberOfEdges());
	 * 
	 * System.out.println(lFastIntegerGraph); }/
	 ****************************************************************************/

}
