// ©2006 Transinsight GmbH - www.transinsight.com - All rights reserved.
package utils.structures.fast;

import static org.junit.Assert.*;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;


/**
 */
public class FastGraphTests
{

	@Test
	public void testAddNode()
	{
		FastGraph lFastGraph = new FastGraph();

		lFastGraph.addNode("me");
		lFastGraph.addNode("you");
		lFastGraph.addNode("them");

		assertSame(3, lFastGraph.getNumberOfNodes());

		assertTrue(lFastGraph.isNode("me"));
		assertTrue(lFastGraph.isNode("you"));
		assertTrue(lFastGraph.isNode("them"));
		assertFalse(lFastGraph.isNode("aloa"));

	}

}
