// ©2006 Transinsight GmbH - www.transinsight.com - All rights reserved.
package utils.structures.fast;

import static org.junit.Assert.*;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import utils.structures.fast.FastIntegerSet;

/**
 */
public class FastIntegerGraphNoisesTests
{

	@Test
	public void testRewireOnce()
	{
		FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

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
		
		Random lRandom = new Random();
		assertTrue(FastIntegerGraphNoises.rewireOnce(lRandom, lFastIntegerGraph,10));
		
		int times = 10000;
		
		int lNumberOfRewirings = FastIntegerGraphNoises.rewire(lRandom, lFastIntegerGraph,10,times);
		
		assertTrue(lNumberOfRewirings==times);
		
		System.out.println(lFastIntegerGraph);
	}
	
}
