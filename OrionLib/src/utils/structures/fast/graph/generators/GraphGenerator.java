package utils.structures.fast.graph.generators;

import java.util.Random;

import utils.random.DistributionSource;
import utils.random.RandomUtils;
import utils.structures.fast.graph.FastIntegerGraph;
import utils.structures.fast.set.FastSparseIntegerSet;

public class GraphGenerator
{

	private GraphGenerator()
	{
	}

	public static FastIntegerGraph generateErdosRenyiGraph(	final Random pRandom,
																													final int pNumberOfNodes,
																													final double pDensity)
	{
		FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

		for (int i = 0; i < pNumberOfNodes; i++)
		{
			lFastIntegerGraph.addNode();
		}

		int[] lNodeArray = lFastIntegerGraph.getNodeSet().getUnderlyingArray();

		for (int node1 = 0; node1 < lNodeArray.length; node1++)
			for (int node2 = 0; node2 < node1; node2++)
			{
				if (pRandom.nextDouble() <= pDensity)
					lFastIntegerGraph.addEdge(node1, node2);
			}

		return lFastIntegerGraph;
	}

	public static FastIntegerGraph generateBarabasiAlbertGraph(	final Random pRandom,
																															final int pNumberOfNodes,
																															final double pTargetDensity)
	{
		FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

		if (pNumberOfNodes > 0)
		{

			lFastIntegerGraph.addNode();

			int m = (int) ((pTargetDensity * (pNumberOfNodes - 1)) * (3d / 4d));

			while (lFastIntegerGraph.getNumberOfNodes() < pNumberOfNodes)
				addNodePreferentialAttachement(pRandom, lFastIntegerGraph, m);

		}

		return lFastIntegerGraph;
	}

	@SuppressWarnings("boxing")
	static void addNodePreferentialAttachement(	final Random pRandom,
																							final FastIntegerGraph pGraph,
																							final double pNewEdges)
	{
		final int newnode = pGraph.addNode();

		FastSparseIntegerSet nodelist = pGraph.getNodeSet();

		double lTotal = 0;
		for (final int node : nodelist.getUnderlyingArray())
			lTotal += pGraph.getNodeNeighbours(node).size();

		if (lTotal == 0 && pNewEdges > 0)
		{
			pGraph.addEdge(newnode, nodelist.getUnderlyingArray()[pRandom.nextInt(nodelist.size())]);
		}
		else
		{
			final DistributionSource<Integer> lDistributionSource = new DistributionSource<Integer>();
			for (final int node : nodelist.getUnderlyingArray())
			{
				double lProbability = ((pGraph.getNodeNeighbours(node).size()) / lTotal);
				lDistributionSource.addObject(node, lProbability);
			}
			try
			{
				lDistributionSource.prepare(Math.max(100000, nodelist.size()), 0.01);
			}
			catch (final Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			long lNumberOfEdges = RandomUtils.doubleToInteger(pRandom, pNewEdges);
			lNumberOfEdges = Math.min(lNumberOfEdges, pGraph.getNumberOfNodes());
			for (int i = 0; i < lNumberOfEdges; i++)
			{
				int node = -1;
				do
				{
					node = lDistributionSource.getObject(pRandom);
				}
				while (nodelist.size() > 0 && !nodelist.contains(node));

				if (node != -1)
				{
					pGraph.addEdge(newnode, node);
					nodelist.del(node);
				}
			}
		}

	}
}
