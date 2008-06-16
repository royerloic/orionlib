package utils.structures.fast.powergraph.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import utils.structures.fast.powergraph.FastIntegerPowerGraph;
import utils.structures.fast.powergraph.FastPowerGraph;
import utils.structures.fast.set.FastBoundedIntegerSet;

/**
 */
public class FastIntegerPowerGraphTests
{

	@Test
	public void testAddPowerNode()
	{
		final FastIntegerPowerGraph lFastIntegerPowerGraph = new FastIntegerPowerGraph();

		final FastBoundedIntegerSet ps1 = new FastBoundedIntegerSet(true,
																																0,
																																1,
																																2,
																																3,
																																4,
																																5,
																																6);
		final FastBoundedIntegerSet ps2 = new FastBoundedIntegerSet(true,
																																0,
																																1,
																																2,
																																3,
																																4,
																																5,
																																7);
		assertEquals(1, lFastIntegerPowerGraph.addPowerNode(ps1));
		assertNull(lFastIntegerPowerGraph.addPowerNode(ps2));

		final FastBoundedIntegerSet ps3 = new FastBoundedIntegerSet(true,
																																0,
																																1,
																																2,
																																3,
																																4);
		assertEquals(2, lFastIntegerPowerGraph.addPowerNode(ps3));
		assertEquals(	"[2] as Set",
									lFastIntegerPowerGraph.getPowerNodeChildrenOf(1).toString());
		assertEquals(	"[1] as Set",
									lFastIntegerPowerGraph.getPowerNodeChildrenOf(0).toString());

		final FastBoundedIntegerSet ps4 = new FastBoundedIntegerSet(true,
																																0,
																																1,
																																2,
																																3,
																																4,
																																5);
		assertEquals(3, lFastIntegerPowerGraph.addPowerNode(ps4));
		assertEquals(	"[2] as Set",
									lFastIntegerPowerGraph.getPowerNodeChildrenOf(3).toString());

		final FastBoundedIntegerSet ps5 = new FastBoundedIntegerSet(true,
																																9,
																																10,
																																11,
																																12);
		assertEquals(4, lFastIntegerPowerGraph.addPowerNode(ps5));
		assertEquals(	"[1, 4] as Set",
									lFastIntegerPowerGraph.getPowerNodeChildrenOf(0).toString());

		final FastBoundedIntegerSet ps6 = new FastBoundedIntegerSet(true,
																																0,
																																1,
																																2,
																																3,
																																4,
																																5,
																																6,
																																7,
																																8,
																																9,
																																10,
																																11,
																																12,
																																13);
		assertEquals(5, lFastIntegerPowerGraph.addPowerNode(ps6));
		assertEquals(	"[5] as Set",
									lFastIntegerPowerGraph.getPowerNodeChildrenOf(0).toString());
		assertEquals(	"[1, 4] as Set",
									lFastIntegerPowerGraph.getPowerNodeChildrenOf(5).toString());
	}

	@Test
	public void testAddPowerEdge()
	{
		final FastIntegerPowerGraph pg = new FastIntegerPowerGraph();

		final FastBoundedIntegerSet ps1 = new FastBoundedIntegerSet(true, 0, 1, 2);
		final FastBoundedIntegerSet ps2 = new FastBoundedIntegerSet(true, 3, 4, 5);

		pg.addPowerEdge(ps1, ps2);
		assertEquals(1, pg.getIdForPowerNode(ps1));
		assertEquals(2, pg.getIdForPowerNode(ps2));
		assertTrue(pg.isPowerEdge(ps1, ps2));
		assertTrue(pg.isPowerEdge(1, 2));
	}

	@Test
	public void testRemovePowerEdge()
	{
		final FastIntegerPowerGraph pg = new FastIntegerPowerGraph();

		final FastBoundedIntegerSet ps1 = new FastBoundedIntegerSet(true, 0, 1, 2);
		final FastBoundedIntegerSet ps2 = new FastBoundedIntegerSet(true, 3, 4, 5);

		pg.addPowerEdge(ps1, ps2);
		pg.addPowerEdge(ps1, ps2);
		assertEquals(1, pg.getNumberOfPowerEdges());
		pg.removePowerEdge(ps1, ps2);
		assertEquals(0, pg.getNumberOfPowerEdges());
		pg.addPowerEdge(ps1, ps2);
		pg.removePowerEdge(1, 2);
		assertEquals(0, pg.getNumberOfPowerEdges());
	}

	@Test
	public void testToTabDel()
	{
		final FastIntegerPowerGraph pg = new FastIntegerPowerGraph();

		final FastBoundedIntegerSet ps1 = new FastBoundedIntegerSet(true,
																																0,
																																1,
																																2,
																																3);
		final FastBoundedIntegerSet ps2 = new FastBoundedIntegerSet(true,
																																4,
																																5,
																																6,
																																7);
		final FastBoundedIntegerSet ps3 = new FastBoundedIntegerSet(true, 0, 1);
		final FastBoundedIntegerSet ps4 = new FastBoundedIntegerSet(true, 4, 5);
		final FastBoundedIntegerSet ps5 = new FastBoundedIntegerSet(true, 8, 9, 10);
		final FastBoundedIntegerSet ps6 = new FastBoundedIntegerSet(true,
																																11,
																																12,
																																13);
		pg.addPowerNode(ps1);
		pg.addPowerNode(ps2);
		pg.addPowerNode(ps3);
		pg.addPowerNode(ps4);
		pg.addPowerNode(ps5);
		pg.addPowerNode(ps6);

		pg.addPowerEdge(ps1, ps2);
		pg.addPowerEdge(ps3, ps4);
		pg.addPowerEdge(ps5, ps6);

		final String lString = pg.toTabDel();
		System.out.println(lString);

		assertTrue(lString.contains("NODE	node0"));
		assertTrue(lString.contains("SET	set1"));
		assertTrue(lString.contains("IN	node0	set1"));

		assertTrue(lString.contains("IN	set3	set1"));
		assertTrue(lString.contains("IN	set4	set2"));
		assertTrue(lString.contains("EDGE	set1	set2"));
		assertTrue(lString.contains("EDGE	set3	set4"));
		assertTrue(lString.contains("EDGE	set5	set6"));

		final FastIntegerPowerGraph pgcloned = new FastIntegerPowerGraph();
		try
		{
			pgcloned.readEdgeFile(lString);
			assertEquals(pg, pgcloned);
		}
		catch (final IOException e)
		{
			fail();
		}

	}

	@Test
	public void testGetExclusiveNodeChildren()
	{
		final FastIntegerPowerGraph pg = new FastIntegerPowerGraph();

		final FastBoundedIntegerSet ps1 = new FastBoundedIntegerSet(true,
																																0,
																																1,
																																2,
																																3,
																																4,
																																5,
																																6,
																																7);
		final FastBoundedIntegerSet ps2 = new FastBoundedIntegerSet(true, 1, 2, 3);
		final FastBoundedIntegerSet ps3 = new FastBoundedIntegerSet(true, 8);
		final FastBoundedIntegerSet ps4 = new FastBoundedIntegerSet(true, 9);

		final int lPowerNodeId = pg.addPowerNode(ps1);
		pg.addPowerNode(ps2);
		pg.addPowerNode(ps3);
		pg.addPowerNode(ps4);

		pg.addPowerEdge(ps1, ps3);
		pg.addPowerEdge(ps2, ps4);
		FastBoundedIntegerSet lExclusiveNodeChildren = pg.getExclusiveNodeChildren(lPowerNodeId);
		assertEquals(5,lExclusiveNodeChildren.size());

	}

}
