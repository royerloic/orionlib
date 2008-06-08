package utils.structures.fast.powergraph.algorythms.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import utils.structures.fast.graph.FastIntegerGraph;
import utils.structures.fast.powergraph.FastIntegerPowerGraph;
import utils.structures.fast.powergraph.algorythms.ConnectedComponents;
import utils.structures.fast.set.FastBoundedIntegerSet;

/**
 */
public class ConnectedComponentsTests
{

	@Test
	public void testConnectedComponents()
	{
		FastIntegerPowerGraph pg = new FastIntegerPowerGraph();

		FastBoundedIntegerSet ps1 = new FastBoundedIntegerSet(true, 1, 2, 3);
		FastBoundedIntegerSet ps2 = new FastBoundedIntegerSet(true,
																													1,
																													2,
																													3,
																													4,
																													5,
																													6);
		FastBoundedIntegerSet ps3 = new FastBoundedIntegerSet(true, 7, 8);
		FastBoundedIntegerSet ps4 = new FastBoundedIntegerSet(true, 9, 10);
		FastBoundedIntegerSet ps5 = new FastBoundedIntegerSet(true, 11);
		FastBoundedIntegerSet ps6 = new FastBoundedIntegerSet(true, 4);

		pg.addPowerNode(ps1);
		pg.addPowerNode(ps2);
		pg.addPowerNode(ps3);
		pg.addPowerNode(ps4);
		pg.addPowerNode(ps5);
		pg.addPowerNode(ps6);

		pg.addPowerEdge(ps1, ps3);
		pg.addPowerEdge(ps2, ps4);
		pg.addPowerEdge(ps5, ps6);

		ArrayList<FastBoundedIntegerSet> c1 = ConnectedComponents.getConnectedComponents(pg);
		assertEquals("[[1, 2, 3, 4, 5, 6] as Set]", c1.toString());
		
		FastBoundedIntegerSet ps7 = new FastBoundedIntegerSet(true, 100);
		FastBoundedIntegerSet ps8 = new FastBoundedIntegerSet(true, 101);
		pg.addPowerEdge(ps7, ps8);
		
		ArrayList<FastBoundedIntegerSet> c2 = ConnectedComponents.getConnectedComponents(pg);
		assertEquals("[[1, 2, 3, 4, 5, 6] as Set, [7, 8] as Set]", c2.toString());
		
		
		
		

	}
}