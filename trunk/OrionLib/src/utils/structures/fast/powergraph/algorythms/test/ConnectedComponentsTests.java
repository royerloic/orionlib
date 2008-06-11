package utils.structures.fast.powergraph.algorythms.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

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
		final FastIntegerPowerGraph pg = new FastIntegerPowerGraph();

		final FastBoundedIntegerSet ps1 = new FastBoundedIntegerSet(true, 1, 2, 3);
		final FastBoundedIntegerSet ps2 = new FastBoundedIntegerSet(true,
																																1,
																																2,
																																3,
																																4,
																																5,
																																6);
		final FastBoundedIntegerSet ps3 = new FastBoundedIntegerSet(true, 7, 8);
		final FastBoundedIntegerSet ps4 = new FastBoundedIntegerSet(true, 9, 10);
		final FastBoundedIntegerSet ps5 = new FastBoundedIntegerSet(true, 11);
		final FastBoundedIntegerSet ps6 = new FastBoundedIntegerSet(true, 4);

		pg.addPowerNode(ps1);
		pg.addPowerNode(ps2);
		pg.addPowerNode(ps3);
		pg.addPowerNode(ps4);
		pg.addPowerNode(ps5);
		pg.addPowerNode(ps6);

		pg.addPowerEdge(ps1, ps3);
		pg.addPowerEdge(ps2, ps4);
		pg.addPowerEdge(ps5, ps6);

		final ArrayList<FastBoundedIntegerSet> c1 = ConnectedComponents.getConnectedComponents(pg);
		assertEquals("[[1, 2, 3, 4, 5, 6] as Set]", c1.toString());

		final FastBoundedIntegerSet ps7 = new FastBoundedIntegerSet(true, 100);
		final FastBoundedIntegerSet ps8 = new FastBoundedIntegerSet(true, 101);
		pg.addPowerEdge(ps7, ps8);

		final ArrayList<FastBoundedIntegerSet> c2 = ConnectedComponents.getConnectedComponents(pg);
		assertEquals("[[1, 2, 3, 4, 5, 6] as Set, [7, 8] as Set]", c2.toString());

	}
}