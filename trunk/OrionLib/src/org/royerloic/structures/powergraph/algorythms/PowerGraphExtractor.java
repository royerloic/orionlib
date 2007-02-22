package org.royerloic.structures.powergraph.algorythms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;
import org.royerloic.structures.graph.UndirectedEdge;
import org.royerloic.structures.powergraph.PowerGraph;

public class PowerGraphExtractor<N> implements PowerGraphExtractorInterface<N>
{

	private class NodeSetComparator implements Comparator<Set<N>>
	{
		public int compare(Set<N> pNodeSet1, Set<N> pNodeSet2)
		{
			int lSize1 = pNodeSet1.size();
			int lSize2 = pNodeSet2.size();
			return -(lSize1 - lSize2); // when sorting the result is in descending
		}
	}

	private static final int	cMinimalBubleSize		= 2;

	private NodeSetComparator	cNodeSetComparator	= new NodeSetComparator();

	public PowerGraphExtractor()
	{
		super();
	}

	public final PowerGraph<N> extractPowerGraph(Graph<N, Edge<N>> pGraph)
	{
		return extractPowerGraph(pGraph, 1);
	}

	public final PowerGraph<N> extractPowerGraph(Graph<N, Edge<N>> pGraph, double pProbabilityThreshold)
	{
		return extractPowerGraph(pGraph, pProbabilityThreshold, Integer.MAX_VALUE);
	}

	public final PowerGraph<N> extractPowerGraph(	Graph<N, Edge<N>> pGraph,
																								double pProbabilityThreshold,
																								int pMaxIterations)
	{
		PowerGraph<N> lPowerGraph = new PowerGraph<N>();
		GraphClustering<N> mClustering = new GraphClustering<N>();
		Set<Set<N>> lNodeSetSet = mClustering.cluster(pGraph, 0, pMaxIterations);

		System.out.println("Started constructing Power Graph");
		List<Set<N>> lNodeSetList = new ArrayList<Set<N>>(lNodeSetSet);

		Collections.sort(lNodeSetList, cNodeSetComparator);
		// System.out.println(lClusterArray.length);

		List<UndirectedEdge<Set<N>>> lPowerEdgeList = new ArrayList<UndirectedEdge<Set<N>>>();

		System.out.println("Adding Power Edges.");
		if (true) // Power Edges
		{
			for (int i = 0; i < lNodeSetList.size(); i++)
			{
				Set<N> lFirstPowerNode = lNodeSetList.get(i);

				if (lFirstPowerNode.size() == 1 || lFirstPowerNode.size() >= cMinimalBubleSize)
					for (int j = 0; j <= i; j++)
					{
						Set<N> lSecondPowerNode = lNodeSetList.get(j);

						if (lSecondPowerNode.size() == 1 || lSecondPowerNode.size() >= cMinimalBubleSize)
							if (!(lFirstPowerNode.equals(lSecondPowerNode) && lSecondPowerNode.size() == 2))
							{
								boolean isPowerEdge;

								if (pProbabilityThreshold == 1)
								{
									isPowerEdge = isPowerEdge(pGraph, lFirstPowerNode, lSecondPowerNode);
								}
								else
								{
									isPowerEdge = isStochasticPowerEdge(pGraph, lFirstPowerNode, lSecondPowerNode,
											pProbabilityThreshold);
								}

								if (isPowerEdge)
								{
									UndirectedEdge<Set<N>> lPowerEdge = new UndirectedEdge<Set<N>>(lFirstPowerNode,
											lSecondPowerNode);

									lPowerEdgeList.add(lPowerEdge);
								}
							}
					}
			}
		}

		System.out.println("Adding remaining edges.");
		if (false) // Original Graph
		{
			for (Edge<N> lEdge : pGraph.getEdgeSet())
			{
				Set<N> lFirstPowerNode = new HashSet<N>();
				lFirstPowerNode.add(lEdge.getFirstNode());
				Set<N> lSecondPowerNode = new HashSet<N>();
				lSecondPowerNode.add(lEdge.getSecondNode());

				UndirectedEdge<Set<N>> lPowerEdge = new UndirectedEdge<Set<N>>(lFirstPowerNode, lSecondPowerNode);
				lPowerEdgeList.add(lPowerEdge);
				System.out.print(",");
			}
			System.out.println("done,");
		}

		if (false) // Clusters
		{
			for (Set<N> lNodeSet : lNodeSetList)
			{
				lPowerGraph.addCluster(lNodeSet);
			}
		}

		for (Edge<Set<N>> lPowerEdge : lPowerEdgeList)
		{
			lPowerGraph.addPowerEdgeDelayed(lPowerEdge);
		}
		lPowerGraph.commitDelayedEdges();

		for (N lNode : pGraph.getNodeSet())
		{
			Set<N> lPowerNode = new HashSet<N>();
			lPowerNode.add(lNode);
			lPowerGraph.addPowerNode(lPowerNode);
		}

		System.out.println("Finished constructing Power Graph");

		/***************************************************************************
		 * File lPowerEdgeDump = new
		 * File(lPowerGraph.getNumberOfNodes()+"dump.txt"); try {
		 * PowerEdgeDump.dumpPowerEdges(lPowerGraph, lPowerEdgeDump); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }/
		 **************************************************************************/

		return lPowerGraph;
	}

	private boolean isPowerEdge(Graph<N, Edge<N>> pGraph, Set<N> pFirstPowerNode, Set<N> pSecondPowerNode)
	{
		if (pFirstPowerNode.equals(pSecondPowerNode))
		{
			if (pFirstPowerNode.size() == 1)
			{
				N lNode = pFirstPowerNode.iterator().next();
				return pGraph.isEdge(lNode, lNode);
			}
			for (N lNode1 : pFirstPowerNode)
				for (N lNode2 : pSecondPowerNode)
					if (!lNode1.equals(lNode2))
						if (!pGraph.isEdge(lNode1, lNode2))
							return false;
			return true;
		}
		for (N lNode : pFirstPowerNode)
		{
			if (pSecondPowerNode.contains(lNode))
				return false;
		}

		for (N lNode1 : pFirstPowerNode)
			for (N lNode2 : pSecondPowerNode)
				if (!pGraph.isEdge(lNode1, lNode2))
					return false;
		return true;
	}

	private boolean isStochasticPowerEdge(Graph<N, Edge<N>> pGraph,
																				Set<N> pFirstPowerNode,
																				Set<N> pSecondPowerNode,
																				double pThresholdProbability)
	{
		double lProbability = 0;
		if (pFirstPowerNode.equals(pSecondPowerNode))
		{
			if (pFirstPowerNode.size() == 1)
			{
				N lNode = pFirstPowerNode.iterator().next();
				if (pGraph.isEdge(lNode, lNode))
					lProbability++;
			}
			else
			{
				for (N lNode1 : pFirstPowerNode)
					for (N lNode2 : pSecondPowerNode)
						if (!lNode1.equals(lNode2))
							if (pGraph.isEdge(lNode1, lNode2))
								lProbability++;
			}
			lProbability /= (pFirstPowerNode.size() * (pFirstPowerNode.size() - 1)) / 2;
		}
		else
		{
			for (N lNode : pFirstPowerNode)
			{
				if (pSecondPowerNode.contains(lNode))
					return false;
			}

			for (N lNode1 : pFirstPowerNode)
				for (N lNode2 : pSecondPowerNode)
					if (pGraph.isEdge(lNode1, lNode2))
						lProbability++;
			lProbability /= (pFirstPowerNode.size() * pSecondPowerNode.size());
		}

		return lProbability >= pThresholdProbability;
	}

}
