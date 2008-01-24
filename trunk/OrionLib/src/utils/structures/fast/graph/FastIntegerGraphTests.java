// ©2006 Transinsight GmbH - www.transinsight.com - All rights reserved.
package utils.structures.fast.graph;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import utils.structures.fast.set.FastIntegerSet;


/**
 */
public class FastIntegerGraphTests
{

	@Test
	public void testAddNode()
	{
		FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

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
		FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

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
		FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

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
		FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

		lFastIntegerGraph.removeEdge(1, 2);
		lFastIntegerGraph.removeEdge(2, 3);
		lFastIntegerGraph.removeEdge(3, 1);

		assertSame(0, lFastIntegerGraph.getNumberOfNodes());
		assertSame(0, lFastIntegerGraph.getNumberOfEdges());
	}

	@Test
	public void testGetNodeSet()
	{
		FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

		lFastIntegerGraph.addEdge(0, 1);
		lFastIntegerGraph.addEdge(0, 2);
		lFastIntegerGraph.addEdge(0, 3);

		lFastIntegerGraph.addNodesUpTo(10);
		lFastIntegerGraph.addNodesUpTo(11);
		lFastIntegerGraph.addNodesUpTo(12);

		assertTrue(FastIntegerSet.equals(	new int[]
																			{ 0,
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
																				12 },
																			lFastIntegerGraph.getNodeSet()));
	}
	
	@Test
	public void testGetEdges()
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

		lFastIntegerGraph.addNodesUpTo(10);
		lFastIntegerGraph.addNodesUpTo(11);
		lFastIntegerGraph.addNodesUpTo(12);

		assertTrue(lFastIntegerGraph.getEdgeList().size()==12);
		assertTrue(lFastIntegerGraph.getIntPairList().size()==12);
		

	}
	

	@Test
	public void testGetNodeNeighbours()
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

		lFastIntegerGraph.addNodesUpTo(10);
		lFastIntegerGraph.addNodesUpTo(11);
		lFastIntegerGraph.addNodesUpTo(12);

		assertTrue(FastIntegerSet.equals(new int[]
		{ 1, 2, 3 }, lFastIntegerGraph.getNodeNeighbours(0)));
		assertTrue(FastIntegerSet.equals(	new int[] {},
																			lFastIntegerGraph.getNodeNeighbours(0, 0)));
		assertTrue(FastIntegerSet.equals(new int[]
		{ 1, 2, 3 }, lFastIntegerGraph.getNodeNeighbours(0, 1)));
		assertTrue(FastIntegerSet.equals(	new int[]
																			{ 1,
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
																				33 },
																			lFastIntegerGraph.getNodeNeighbours(0, 2)));
	}

	@Test
	public void testExtractStrictSubGraph()
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

		lFastIntegerGraph.addNodesUpTo(10);
		lFastIntegerGraph.addNodesUpTo(11);
		lFastIntegerGraph.addNodesUpTo(12);

		// System.out.println(lFastIntegerGraph.toString());

		FastIntegerGraph lSubGraph = lFastIntegerGraph.extractStrictSubGraph(new int[]
		{ 1, 2, 3 });

		// System.out.println(lSubGraph.toString());

		assertSame(4, lSubGraph.getNumberOfNodes());
		assertSame(0, lSubGraph.getNumberOfEdges());
		assertFalse(lFastIntegerGraph.isEdge(1, 2));
		assertFalse(lFastIntegerGraph.isEdge(2, 3));
		assertFalse(lFastIntegerGraph.isEdge(3, 1));

		lSubGraph = lFastIntegerGraph.extractStrictSubGraph(new int[]
		{ 0, 1, 2, 3 });

		assertSame(4, lSubGraph.getNumberOfNodes());
		assertSame(3, lSubGraph.getNumberOfEdges());
		assertTrue(lFastIntegerGraph.isEdge(0, 1));
		assertTrue(lFastIntegerGraph.isEdge(0, 2));
		assertTrue(lFastIntegerGraph.isEdge(0, 3));

		lSubGraph = lFastIntegerGraph.extractStrictSubGraph(new int[]
		{ 0, 11, 12, 13 });

		assertSame(14, lSubGraph.getNumberOfNodes());
		assertSame(0, lSubGraph.getNumberOfEdges());
	}
	
	@Test
	public void testExtractSubGraph()
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

			File lEdgeFile = File.createTempFile("temp", "temp");
			System.out.println(lEdgeFile);
			if (lEdgeFile.exists())
				lEdgeFile.delete();

			FileOutputStream lFileOutPutStream = new FileOutputStream(lEdgeFile);
			lFastIntegerGraph.writeEdgeFile(lFileOutPutStream);
			lFileOutPutStream.close();

			FastIntegerGraph lFastIntegerGraphReadFromFile = new FastIntegerGraph();
			FileInputStream lFileInputStream = new FileInputStream(lEdgeFile);
			lFastIntegerGraphReadFromFile.readEdgeFile(lFileInputStream);

			assertTrue(lFastIntegerGraph.equals(lFastIntegerGraphReadFromFile));
		}
		catch (RuntimeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
