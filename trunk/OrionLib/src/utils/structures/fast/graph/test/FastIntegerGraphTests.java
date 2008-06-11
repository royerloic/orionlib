package utils.structures.fast.graph.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

import utils.structures.fast.graph.FastIntegerGraph;
import utils.structures.fast.set.FastBoundedIntegerSet;

/**
 */
public class FastIntegerGraphTests
{

	@Test
	public void testAddNode()
	{
		final FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

		assertSame(0, lFastIntegerGraph.addNode());
		assertSame(1, lFastIntegerGraph.addNode());
		assertSame(2, lFastIntegerGraph.addNode());

		assertSame(3, lFastIntegerGraph.getNumberOfNodes());

		lFastIntegerGraph.addNodesUpTo(0);
		lFastIntegerGraph.addNodesUpTo(1);
		lFastIntegerGraph.addNodesUpTo(2);

		assertSame(3, lFastIntegerGraph.getNumberOfNodes());

		lFastIntegerGraph.addNodesUpTo(10);

		assertSame(11, lFastIntegerGraph.getNumberOfNodes());

		lFastIntegerGraph.addNodesUpTo(10);

		assertSame(11, lFastIntegerGraph.getNumberOfNodes());

		assertTrue(lFastIntegerGraph.isNode(0));
		assertTrue(lFastIntegerGraph.isNode(1));
		assertTrue(lFastIntegerGraph.isNode(2));
		assertTrue(lFastIntegerGraph.isNode(10));

	}

	@Test
	public void testAddEdge()
	{
		final FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

		lFastIntegerGraph.addEdge(0, 1);
		lFastIntegerGraph.addEdge(0, 2);
		lFastIntegerGraph.addEdge(0, 3);

		assertSame(4, lFastIntegerGraph.getNumberOfNodes());
		assertSame(3, lFastIntegerGraph.getNumberOfEdges());

		assertTrue(lFastIntegerGraph.isEdge(0, 1));
		assertTrue(lFastIntegerGraph.isEdge(0, 2));
		assertTrue(lFastIntegerGraph.isEdge(0, 3));

		assertFalse(lFastIntegerGraph.isEdge(1, 2));
		assertFalse(lFastIntegerGraph.isEdge(2, 3));
		assertFalse(lFastIntegerGraph.isEdge(3, 1));

	}

	@Test
	public void testRemoveEdge1()
	{
		final FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

		lFastIntegerGraph.addEdge(1, 2);
		lFastIntegerGraph.addEdge(2, 3);
		lFastIntegerGraph.addEdge(3, 1);

		lFastIntegerGraph.removeEdge(1, 2);
		lFastIntegerGraph.removeEdge(2, 3);
		lFastIntegerGraph.removeEdge(3, 1);

		lFastIntegerGraph.addEdge(1, 2);
		lFastIntegerGraph.addEdge(2, 3);
		lFastIntegerGraph.addEdge(3, 1);

		lFastIntegerGraph.addEdge(0, 1);
		lFastIntegerGraph.addEdge(0, 2);
		lFastIntegerGraph.addEdge(0, 3);

		lFastIntegerGraph.removeEdge(1, 2);
		lFastIntegerGraph.removeEdge(2, 3);
		lFastIntegerGraph.removeEdge(3, 1);

		assertSame(4, lFastIntegerGraph.getNumberOfNodes());
		assertSame(3, lFastIntegerGraph.getNumberOfEdges());

		assertTrue(lFastIntegerGraph.isEdge(0, 1));
		assertTrue(lFastIntegerGraph.isEdge(0, 2));
		assertTrue(lFastIntegerGraph.isEdge(0, 3));

		assertFalse(lFastIntegerGraph.isEdge(1, 2));
		assertFalse(lFastIntegerGraph.isEdge(2, 3));
		assertFalse(lFastIntegerGraph.isEdge(3, 1));

	}

	@Test
	public void testRemoveEdgeFromEmptyGraph()
	{
		final FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

		lFastIntegerGraph.removeEdge(1, 2);
		lFastIntegerGraph.removeEdge(2, 3);
		lFastIntegerGraph.removeEdge(3, 1);

		assertSame(0, lFastIntegerGraph.getNumberOfNodes());
		assertSame(0, lFastIntegerGraph.getNumberOfEdges());
	}

	@Test
	public void testGetNodeSet()
	{
		final FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

		lFastIntegerGraph.addEdge(0, 1);
		lFastIntegerGraph.addEdge(0, 2);
		lFastIntegerGraph.addEdge(0, 3);

		lFastIntegerGraph.addNodesUpTo(10);
		lFastIntegerGraph.addNodesUpTo(11);
		lFastIntegerGraph.addNodesUpTo(12);

		assertTrue(lFastIntegerGraph.getNodeSet().equals(	0,
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
																											12));
	}

	@Test
	public void testGetEdges()
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

		lFastIntegerGraph.addNodesUpTo(10);
		lFastIntegerGraph.addNodesUpTo(11);
		lFastIntegerGraph.addNodesUpTo(12);

		assertTrue(lFastIntegerGraph.getEdgeList().size() == 12);
		assertTrue(lFastIntegerGraph.getIntPairList().size() == 12);

	}

	@Test
	public void testGetNodeNeighbours()
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

		lFastIntegerGraph.addNodesUpTo(10);
		lFastIntegerGraph.addNodesUpTo(11);
		lFastIntegerGraph.addNodesUpTo(12);

		assertTrue(lFastIntegerGraph.getNodeNeighbours(0).equals(1, 2, 3));
		assertTrue(lFastIntegerGraph.getNodeNeighbours(0, 0).isEmpty());
		assertTrue(lFastIntegerGraph.getNodeNeighbours(0, 1).equals(1, 2, 3));
		assertTrue(lFastIntegerGraph.getNodeNeighbours(0, 2).equals(1,
																																2,
																																3,
																																11,
																																12,
																																13,
																																21,
																																22,
																																23,
																																31,
																																32,
																																33));
	}

	@Test
	public void testExtractStrictSubGraph()
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

		lFastIntegerGraph.addNodesUpTo(10);
		lFastIntegerGraph.addNodesUpTo(11);
		lFastIntegerGraph.addNodesUpTo(12);

		// System.out.println(lFastIntegerGraph.toString());

		FastIntegerGraph lSubGraph = lFastIntegerGraph.extractStrictSubGraph(new FastBoundedIntegerSet(	true,
																																																		1,
																																																		2,
																																																		3));

		// System.out.println(lSubGraph.toString());

		assertSame(4, lSubGraph.getNumberOfNodes());
		assertSame(0, lSubGraph.getNumberOfEdges());
		assertFalse(lFastIntegerGraph.isEdge(1, 2));
		assertFalse(lFastIntegerGraph.isEdge(2, 3));
		assertFalse(lFastIntegerGraph.isEdge(3, 1));

		lSubGraph = lFastIntegerGraph.extractStrictSubGraph(new FastBoundedIntegerSet(true,
																																									0,
																																									1,
																																									2,
																																									3));

		assertSame(4, lSubGraph.getNumberOfNodes());
		assertSame(3, lSubGraph.getNumberOfEdges());
		assertTrue(lFastIntegerGraph.isEdge(0, 1));
		assertTrue(lFastIntegerGraph.isEdge(0, 2));
		assertTrue(lFastIntegerGraph.isEdge(0, 3));

		lSubGraph = lFastIntegerGraph.extractStrictSubGraph(new FastBoundedIntegerSet(true,
																																									0,
																																									11,
																																									12,
																																									13));

		assertSame(14, lSubGraph.getNumberOfNodes());
		assertSame(0, lSubGraph.getNumberOfEdges());
	}

	@Test
	public void testExtractSubGraph()
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

		lFastIntegerGraph.addNodesUpTo(10);
		lFastIntegerGraph.addNodesUpTo(11);
		lFastIntegerGraph.addNodesUpTo(12);

		// System.out.println(lFastIntegerGraph.toString());

		FastIntegerGraph lSubGraph = lFastIntegerGraph.extractSubGraph(new int[]
		{ 1, 2, 3 });

		// System.out.println(lSubGraph.toString());

		assertSame(34, lSubGraph.getNumberOfNodes());
		assertSame(12, lSubGraph.getNumberOfEdges());
		assertTrue(lFastIntegerGraph.isEdge(0, 1));
		assertTrue(lFastIntegerGraph.isEdge(1, 11));

		lSubGraph = lFastIntegerGraph.extractSubGraph(new int[]
		{ 0 });

		assertSame(4, lSubGraph.getNumberOfNodes());
		assertSame(3, lSubGraph.getNumberOfEdges());
		assertTrue(lFastIntegerGraph.isEdge(0, 1));
		assertTrue(lFastIntegerGraph.isEdge(0, 2));
		assertTrue(lFastIntegerGraph.isEdge(0, 3));

		lSubGraph = lFastIntegerGraph.extractSubGraph(new int[]
		{ 0, 11, 12, 13 });

		assertSame(14, lSubGraph.getNumberOfNodes());
		assertSame(6, lSubGraph.getNumberOfEdges());
	}

	@Test
	public void testReadWriteEdgeFile()
	{

		try
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

			final File lEdgeFile = File.createTempFile("temp", "temp");
			System.out.println(lEdgeFile);
			if (lEdgeFile.exists())
			{
				lEdgeFile.delete();
			}

			final FileOutputStream lFileOutPutStream = new FileOutputStream(lEdgeFile);
			lFastIntegerGraph.writeEdgeFile(lFileOutPutStream);
			lFileOutPutStream.close();

			final FastIntegerGraph lFastIntegerGraphReadFromFile = new FastIntegerGraph();
			final FileInputStream lFileInputStream = new FileInputStream(lEdgeFile);
			lFastIntegerGraphReadFromFile.readEdgeFile(lFileInputStream);

			assertTrue(lFastIntegerGraph.equals(lFastIntegerGraphReadFromFile));
		}
		catch (final RuntimeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testAverageDegreeAndEdgeDensity()
	{
		final FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

		lFastIntegerGraph.addEdge(0, 1);
		lFastIntegerGraph.addEdge(0, 2);
		lFastIntegerGraph.addEdge(0, 3);

		lFastIntegerGraph.addEdge(1, 4);
		lFastIntegerGraph.addEdge(1, 5);
		lFastIntegerGraph.addEdge(1, 6);

		lFastIntegerGraph.addEdge(2, 7);
		lFastIntegerGraph.addEdge(2, 8);
		lFastIntegerGraph.addEdge(2, 9);

		lFastIntegerGraph.addEdge(3, 10);
		lFastIntegerGraph.addEdge(3, 11);
		lFastIntegerGraph.addEdge(3, 12);

		assertEquals(lFastIntegerGraph.getAverageDegree(), (24d / 13d));
		assertEquals(lFastIntegerGraph.getEdgeDensity(), (2d / 13d));

	}
}
