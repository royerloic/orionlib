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
		public int compare(final Set<N> pNodeSet1, final Set<N> pNodeSet2)
		{
			final int lSize1 = pNodeSet1.size();
			final int lSize2 = pNodeSet2.size();
			return -(lSize1 - lSize2); // when sorting the result is in descending
		}
	}

	private static final int	cMinimalBubleSize		= 2;

	private final NodeSetComparator	cNodeSetComparator	= new NodeSetComparator();

	private final double			mMinimalSimilarity;

	private final int					mMaxIterations;

	private final boolean	mCentralityWeighting;

	public PowerGraphExtractor()
	{
		super();
		this.mMinimalSimilarity = 0;
		this.mMaxIterations = Integer.MAX_VALUE;
		this.mCentralityWeighting = true;
	}

	public PowerGraphExtractor(final double pMinimalSimilarity, final int pMaxIterations, final boolean pCentralityWeighting)
	{
		super();
		this.mMinimalSimilarity = pMinimalSimilarity;
		this.mMaxIterations = pMaxIterations;
		this.mCentralityWeighting = pCentralityWeighting;
	}

	public final PowerGraph<N> extractPowerGraph(final Graph<N, Edge<N>> pGraph)
	{
		return extractPowerGraph(pGraph, 1, this.mMinimalSimilarity, this.mMaxIterations);
	}

	public final PowerGraph<N> extractPowerGraph(final Graph<N, Edge<N>> pGraph, final double pProbabilityThreshold)
	{
		return extractPowerGraph(pGraph, pProbabilityThreshold, this.mMinimalSimilarity, this.mMaxIterations);
	}

	public final PowerGraph<N> extractPowerGraph(	final Graph<N, Edge<N>> pGraph,
																								final double pProbabilityThreshold,
																								final double pMinSimilarity,
																								final int pMaxIterations)
	{
		final PowerGraph<N> lPowerGraph = new PowerGraph<N>();
		final GraphClustering<N> mClustering = new GraphClustering<N>(this.mCentralityWeighting);
		final Set<Set<N>> lNodeSetSet = mClustering.cluster(pGraph, pMinSimilarity, pMaxIterations);

		// System.out.println("Started constructing Power Graph");
		final List<Set<N>> lNodeSetList = new ArrayList<Set<N>>(lNodeSetSet);

		Collections.sort(lNodeSetList, this.cNodeSetComparator);
		// System.out.println(lClusterArray.length);

		mergesimilarsets(lNodeSetList);

		final List<UndirectedEdge<Set<N>>> lPowerEdgeList = new ArrayList<UndirectedEdge<Set<N>>>();

		// System.out.println("Adding Power Edges.");
		if (true)
			for (int i = 0; i < lNodeSetList.size(); i++)
			{
				final Set<N> lFirstPowerNode = lNodeSetList.get(i);

				if ((lFirstPowerNode.size() == 1) || (lFirstPowerNode.size() >= cMinimalBubleSize))
					for (int j = 0; j <= i; j++)
					{
						final Set<N> lSecondPowerNode = lNodeSetList.get(j);

						if ((lSecondPowerNode.size() == 1) || (lSecondPowerNode.size() >= cMinimalBubleSize))
							if (!(lFirstPowerNode.equals(lSecondPowerNode) && (lSecondPowerNode.size() == 2)))
							{
								boolean isPowerEdge;

								if (pProbabilityThreshold == 1)
									isPowerEdge = isPowerEdge(pGraph, lFirstPowerNode, lSecondPowerNode);
								else
									isPowerEdge = isStochasticPowerEdge(pGraph, lFirstPowerNode, lSecondPowerNode,
											pProbabilityThreshold);

								if (isPowerEdge)
								{
									final UndirectedEdge<Set<N>> lPowerEdge = new UndirectedEdge<Set<N>>(lFirstPowerNode,
											lSecondPowerNode);

									lPowerEdgeList.add(lPowerEdge);
								}
							}
					}
			}

		// System.out.println("Adding remaining edges.");
		if (false)
			for (final Edge<N> lEdge : pGraph.getEdgeSet())
			{
				final Set<N> lFirstPowerNode = new HashSet<N>();
				lFirstPowerNode.add(lEdge.getFirstNode());
				final Set<N> lSecondPowerNode = new HashSet<N>();
				lSecondPowerNode.add(lEdge.getSecondNode());

				final UndirectedEdge<Set<N>> lPowerEdge = new UndirectedEdge<Set<N>>(lFirstPowerNode, lSecondPowerNode);
				lPowerEdgeList.add(lPowerEdge);
				// System.out.print(",");
			}

		if (false)
			for (final Set<N> lNodeSet : lNodeSetList)
				lPowerGraph.addCluster(lNodeSet);

		for (final Edge<Set<N>> lPowerEdge : lPowerEdgeList)
			lPowerGraph.addPowerEdgeDelayed(lPowerEdge);
		lPowerGraph.commitDelayedEdges();

		for (final N lNode : pGraph.getNodeSet())
		{
			final Set<N> lPowerNode = new HashSet<N>();
			lPowerNode.add(lNode);
			lPowerGraph.addPowerNode(lPowerNode);
		}

		// System.out.println("Finished constructing Power Graph");

		/***************************************************************************
		 * File lPowerEdgeDump = new
		 * File(lPowerGraph.getNumberOfNodes()+"dump.txt"); try {
		 * PowerEdgeDump.dumpPowerEdges(lPowerGraph, lPowerEdgeDump); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }/
		 **************************************************************************/

		return lPowerGraph;
	}

	private void mergesimilarsets(final List<Set<N>> pNodeSetList)
	{
		int i = 0;
		while (i < pNodeSetList.size())
		{
			final Set<N> lSet = pNodeSetList.get(i);
			final int lSize = lSet.size();
			boolean lThereIsSmaller = false;
			for (final Set<N> lOtherSet : pNodeSetList)
				if ((lOtherSet.size()>2) && (lOtherSet.size() == lSize - 1) && lSet.containsAll(lOtherSet))
					lThereIsSmaller = true;
			if (lThereIsSmaller)
				pNodeSetList.remove(lSet);
			else
				i++;
		}

	}

	private boolean isPowerEdge(final Graph<N, Edge<N>> pGraph, final Set<N> pFirstPowerNode, final Set<N> pSecondPowerNode)
	{
		if (pFirstPowerNode.equals(pSecondPowerNode))
		{
			if (pFirstPowerNode.size() == 1)
			{
				final N lNode = pFirstPowerNode.iterator().next();
				return pGraph.isEdge(lNode, lNode);
			}
			for (final N lNode1 : pFirstPowerNode)
				for (final N lNode2 : pSecondPowerNode)
					if (!lNode1.equals(lNode2))
						if (!pGraph.isEdge(lNode1, lNode2))
							return false;
			return true;
		}
		for (final N lNode : pFirstPowerNode)
			if (pSecondPowerNode.contains(lNode))
				return false;

		for (final N lNode1 : pFirstPowerNode)
			for (final N lNode2 : pSecondPowerNode)
				if (!pGraph.isEdge(lNode1, lNode2))
					return false;
		return true;
	}

	private boolean isStochasticPowerEdge(final Graph<N, Edge<N>> pGraph,
																				final Set<N> pFirstPowerNode,
																				final Set<N> pSecondPowerNode,
																				final double pThresholdProbability)
	{
		double lProbability = 0;
		if (pFirstPowerNode.equals(pSecondPowerNode))
		{
			if (pFirstPowerNode.size() == 1)
			{
				final N lNode = pFirstPowerNode.iterator().next();
				if (pGraph.isEdge(lNode, lNode))
					lProbability++;
			}
			else
				for (final N lNode1 : pFirstPowerNode)
					for (final N lNode2 : pSecondPowerNode)
						if (!lNode1.equals(lNode2))
							if (pGraph.isEdge(lNode1, lNode2))
								lProbability++;
			lProbability /= (pFirstPowerNode.size() * (pFirstPowerNode.size() - 1)) / 2;
		}
		else
		{
			for (final N lNode : pFirstPowerNode)
				if (pSecondPowerNode.contains(lNode))
					return false;

			for (final N lNode1 : pFirstPowerNode)
				for (final N lNode2 : pSecondPowerNode)
					if (pGraph.isEdge(lNode1, lNode2))
						lProbability++;
			lProbability /= (pFirstPowerNode.size() * pSecondPowerNode.size());
		}

		return lProbability >= pThresholdProbability;
	}

}
