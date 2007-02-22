package org.royerloic.structures.powergraph.algorythms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;
import org.royerloic.structures.graph.HashGraph;
import org.royerloic.structures.graph.UndirectedEdge;

public class GraphClustering<N>
{

	final double computeSetDistance(Set<N> pSet1, Set<N> pSet2)
	{
		Set<N> lSet = new HashSet<N>(pSet1);
		lSet.retainAll(pSet2);
		final double lIntersectionSize = lSet.size();
		double lDistance = ((double) (pSet1.size() + pSet2.size() - 2 * lIntersectionSize)) / lIntersectionSize;
		if (lIntersectionSize == Double.NaN)
			return Double.POSITIVE_INFINITY;
		return lDistance;
	}

	final double computeWeightedSize(Map<N, Double> pWeightedSet)
	{
		double lSize = 0;
		for (Map.Entry<N, Double> lEntry : pWeightedSet.entrySet())
		{
			lSize += lEntry.getValue();
		}
		return lSize;
	}

	final double computeWeightedSetSimilarity(Map<N, Double> pWeightedSet1, Map<N, Double> pWeightedSet2)
	{
		final double lSize1 = computeWeightedSize(pWeightedSet1);
		final double lSize2 = computeWeightedSize(pWeightedSet2);

		Map<N, Double> lWeightedSetIntersection = new HashMap<N, Double>(pWeightedSet1);
		for (Map.Entry<N, Double> lEntry : pWeightedSet1.entrySet())
		{
			if (!pWeightedSet2.containsKey(lEntry.getKey()))
				lWeightedSetIntersection.remove(lEntry.getKey());
		}
		final double lSizeIntersection = computeWeightedSize(lWeightedSetIntersection);

		double lDistance = (lSizeIntersection) / (lSize1 + lSize2 - lSizeIntersection);
		lDistance = lDistance == Double.NaN ? Double.POSITIVE_INFINITY : lDistance;

		return lDistance;
	}

	private class NodeComparator implements Comparator<N>
	{
		Graph	mGraph;

		public NodeComparator(Graph pGraph)
		{
			super();
			mGraph = pGraph;
		}

		public int compare(N pNode1, N pNode2)
		{
			Set<N> lNode1Neighbors = mGraph.getNodeNeighbours(pNode1);
			Set<N> lNode2Neighbors = mGraph.getNodeNeighbours(pNode2);
			int lSize1 = lNode1Neighbors.size();
			int lSize2 = lNode2Neighbors.size();
			return -(lSize1 - lSize2); // when sorting the result is descending order
		}
	}

	private class ClusterComparator implements Comparator<Cluster>
	{
		public int compare(Cluster pCluster1, Cluster pCluster2)
		{
			Set<N> lNode1Neighbors = pCluster1.mNodeSet;
			Set<N> lNode2Neighbors = pCluster2.mNodeSet;
			int lSize1 = lNode1Neighbors.size();
			int lSize2 = lNode2Neighbors.size();
			return +(lSize1 - lSize2);
		}
	}

	public class Cluster
	{
		Set<N>					mNodeSet								= new HashSet<N>();
		Map<N, Double>	mNodeToConnectivityMap	= new HashMap<N, Double>();

		Set<N> getNodesInCluster()
		{
			return mNodeSet;
		}

		public void addNode(Graph pGraph, N pNode)
		{
			mNodeSet.add(pNode);
			Set<N> lNodeNeighbors = pGraph.getNodeNeighbours(pNode);
			for (N lNode : lNodeNeighbors)
			{
				double lConnectivity = pGraph.getNodeNeighbours(lNode).size();
				// System.out.println(lConnectivity);
				mNodeToConnectivityMap.put(lNode, lConnectivity);
			}
			for (N lNode : mNodeSet)
			{
				mNodeToConnectivityMap.remove(lNode);
			}
			// filter(mNodeToConnectivityMap, mThreshold);
		}

		public void mergeWith(Cluster pCluster)
		{
			mNodeSet.addAll(pCluster.mNodeSet);
			mNodeToConnectivityMap.putAll(pCluster.mNodeToConnectivityMap);
			for (N lNode : mNodeSet)
			{
				mNodeToConnectivityMap.remove(lNode);
			}
			// filter(mNodeToConnectivityMap, mThreshold);
		}

		private void filter(Map<N, Double> pNodeToConnectivityMap, double pThreshold)
		{
			double lValueMax = Double.NEGATIVE_INFINITY;
			for (Map.Entry<N, Double> lEntry : pNodeToConnectivityMap.entrySet())
				lValueMax = Math.max(lValueMax, lEntry.getValue());
			for (Iterator<Map.Entry<N, Double>> lI = pNodeToConnectivityMap.entrySet().iterator(); lI.hasNext();)
			{
				double lValue = lI.next().getValue();
				if (lValue < pThreshold * lValueMax)
					lI.remove();
			}

		}

		int getClusterSize()
		{
			return mNodeSet.size();
		}

		double similarityToCluster(Cluster pCluster)
		{
			double lDistance = computeWeightedSetSimilarity(this.mNodeToConnectivityMap,
					pCluster.mNodeToConnectivityMap);
			// if (!Double.isInfinite(lDistance)) System.out.println(lDistance);
			return lDistance;
		}

		@Override
		public String toString()
		{
			return mNodeSet.toString();
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (this.hashCode() != ((Cluster) obj).hashCode())
				return false;

			return mNodeSet.equals(((Cluster) obj).mNodeSet);
		}

		@Override
		public int hashCode()
		{
			return mNodeSet.hashCode();
		}

		protected Cluster copy()
		{
			Cluster lCluster = new Cluster();
			lCluster.mNodeSet.addAll(this.mNodeSet);
			lCluster.mNodeToConnectivityMap.putAll(this.mNodeToConnectivityMap);
			return lCluster;
		}

	};

	public GraphClustering()
	{
		super();
	}

	public final Set<Set<N>> clusterOld(Graph<N, Edge<N>> pGraph, double pCutOff)
	{
		List<Cluster> lInitialClusterList = new ArrayList<Cluster>();

		if (pGraph.getNodeSet().size() == 0)
			return Collections.EMPTY_SET;

		for (N lNode : pGraph.getNodeSet())
		{
			Cluster lCluster = new Cluster();
			lCluster.addNode(pGraph, lNode);
			lInitialClusterList.add(lCluster);
		}

		List<Cluster> lClusterList = new ArrayList<Cluster>(lInitialClusterList);
		Comparator<Cluster> lClusterComparator = new ClusterComparator();
		Set<Cluster> lFinalClusterSet = new HashSet<Cluster>();
		for (Cluster lCluster : lClusterList)
		{
			lFinalClusterSet.add(lCluster.copy());
		}

		Map<UndirectedEdge<Cluster>, Double> lDistanceCache = new HashMap<UndirectedEdge<Cluster>, Double>();
		List<Cluster> lMostCompactClusterList = new ArrayList<Cluster>();
		Set<Cluster> lClusterDeleteSet = new HashSet<Cluster>();
		double lMaximalSimilarity = -1;
		while (lClusterList.size() > 1)
		{
			// System.out.println("lClusterList.size()=" + lClusterList.size());
			// System.out.println("lMaximalSimilarity=" + lMaximalSimilarity);
			System.out.print(".");
			lClusterDeleteSet.clear();
			lMostCompactClusterList.clear();
			lMaximalSimilarity = -1;

			for (int i = 0; i < lClusterList.size(); i++)
			{
				for (int j = 0; j < i; j++)
				{
					Cluster lCluster1 = lClusterList.get(i);
					Cluster lCluster2 = lClusterList.get(j);
					Double lSimilarity = lCluster1.similarityToCluster(lCluster2);

					if (lSimilarity > lMaximalSimilarity)
					{
						lMaximalSimilarity = lSimilarity;
						lMostCompactClusterList.clear();
						lClusterDeleteSet.clear();
					}

					if ((lSimilarity == lMaximalSimilarity))
					{
						Cluster lNewCluster = new Cluster();
						lNewCluster.mergeWith(lCluster1);
						lNewCluster.mergeWith(lCluster2);
						boolean lMerged = false;

						Cluster lIntersectingCluster = findIntersectingCluster(lMostCompactClusterList, lNewCluster);
						if ((lIntersectingCluster != null))
						{
							lIntersectingCluster.mergeWith(lNewCluster);
							lMerged = true;
						}

						if (!lMerged)
						{
							lMostCompactClusterList.add(lNewCluster);
						}
						lClusterDeleteSet.add(lCluster1);
						lClusterDeleteSet.add(lCluster2);
					}
				}
			}
			lClusterList.removeAll(lClusterDeleteSet);
			lClusterList.addAll(lMostCompactClusterList);
			for (Cluster lCluster : lMostCompactClusterList)
			{
				lFinalClusterSet.add(lCluster.copy());
			}
			if (lMaximalSimilarity < pCutOff)
				break;
		}

		Set<Set<N>> lSetSet = new HashSet<Set<N>>();
		for (Cluster lCluster : lFinalClusterSet)
		{
			lSetSet.add(lCluster.mNodeSet);
		}

		System.out.println("done.");
		return lSetSet;
	}

	public final Set<Set<N>> cluster(Graph<N, Edge<N>> pGraph, double pCutOff, int pMaxIterations)
	{
		Graph<Cluster, Edge<Cluster>> lClusterGraph = new HashGraph<Cluster, Edge<Cluster>>();
		{
			Map<N, Cluster> lNodeToClusterMap = new HashMap<N, Cluster>();
			for (N lNode : pGraph.getNodeSet())
			{
				Cluster lCluster = new Cluster();
				lCluster.addNode(pGraph, lNode);
				lClusterGraph.addNode(lCluster);
				lNodeToClusterMap.put(lNode, lCluster);
			}
			for (Edge<N> lEdge : pGraph.getEdgeSet())
			{
				N lNode1 = lEdge.getFirstNode();
				N lNode2 = lEdge.getSecondNode();
				Cluster lCluster1 = lNodeToClusterMap.get(lNode1);
				Cluster lCluster2 = lNodeToClusterMap.get(lNode2);
				lClusterGraph.addEdge(new UndirectedEdge<Cluster>(lCluster1, lCluster2));
			}
		}

		return clusterInternal(lClusterGraph, pCutOff, pMaxIterations);
	}

	public final Set<Set<N>> clusterInternal(	Graph<Cluster, Edge<Cluster>> pClusterGraph,
																						double pCutOff,
																						int pMaxIterations)
	{

		Set<Cluster> lFinalClusterSet = new HashSet<Cluster>();
		for (Cluster lCluster : pClusterGraph.getNodeSet())
		{
			lFinalClusterSet.add(lCluster.copy());
		}

		Map<UndirectedEdge<Cluster>, Double> lDistanceCache = new HashMap<UndirectedEdge<Cluster>, Double>();

		Set<Cluster> lMaximalSimilarityClusterSet = new HashSet<Cluster>();

		int lIterationCount = 0;
		while (pClusterGraph.getNumberOfNodes() > 1)
		{
			// System.out.println("lClusterGraph.getNumberOfNodes()=" +
			// lClusterGraph.getNumberOfNodes());
			// System.out.println("lMaximalSimilarity=" + lMaximalSimilarity);
			System.out.print(".");
			double lMaximalSimilarity = -1;

			lMaximalSimilarityClusterSet.clear();

			for (Cluster lCluster1 : pClusterGraph.getNodeSet())
			{
				Set<Cluster> lNeighboursSet = pClusterGraph.getNodeNeighbours(lCluster1, 2);

				double lMaximalSimilarityForCluster = -1;
				for (Cluster lCluster2 : lNeighboursSet)
				{
					Double lSimilarity = lCluster1.similarityToCluster(lCluster2);
					lMaximalSimilarityForCluster = Math.max(lMaximalSimilarityForCluster, lSimilarity);
				}

				if (lMaximalSimilarityForCluster > lMaximalSimilarity)
				{
					lMaximalSimilarity = lMaximalSimilarityForCluster;
					lMaximalSimilarityClusterSet.clear();
				}

				if (lMaximalSimilarityForCluster == lMaximalSimilarity)
				{
					lMaximalSimilarityClusterSet.add(lCluster1);
				}
			}

			while (!lMaximalSimilarityClusterSet.isEmpty())
			{
				Cluster lCluster1 = lMaximalSimilarityClusterSet.iterator().next();
				Set<Cluster> lNeighboursSet = pClusterGraph.getNodeNeighbours(lCluster1, 2);
				Set<Cluster> lClusterSet = new HashSet<Cluster>();
				for (Cluster lCluster2 : lNeighboursSet)
				{
					Double lSimilarity = lCluster1.similarityToCluster(lCluster2);
					if ((lSimilarity == lMaximalSimilarity))
					{
						lClusterSet.add(lCluster2);
					}
				}
				lClusterSet.add(lCluster1);

				Set<Cluster> lClusterNeighbours = pClusterGraph.getNodeNeighbours(lClusterSet);

				pClusterGraph.removeAllNodes(lClusterSet);
				Cluster lNewCluster = new Cluster();
				for (Cluster lCluster : lClusterSet)
				{
					lNewCluster.mergeWith(lCluster);
				}
				pClusterGraph.addNode(lNewCluster);

				for (Cluster lNeighbourCluster : lClusterNeighbours)
				{
					pClusterGraph.addEdge(new UndirectedEdge<Cluster>(lNewCluster, lNeighbourCluster));
				}

				lMaximalSimilarityClusterSet.removeAll(lClusterSet);
			}

			for (Cluster lCluster : pClusterGraph.getNodeSet())
			{
				lFinalClusterSet.add(lCluster.copy());
			}
			lIterationCount++;

			if ((lMaximalSimilarity < pCutOff) || lIterationCount >= pMaxIterations)
				break;
		}

		Set<Set<N>> lSetSet = new HashSet<Set<N>>();
		for (Cluster lCluster : lFinalClusterSet)
		{
			lSetSet.add(lCluster.mNodeSet);
		}

		System.out.println("done.");
		return lSetSet;
	}

	private Cluster findClosestCluster(List<Cluster> pClusterList, Cluster pCluster)
	{
		double lMinimalDistance = Double.POSITIVE_INFINITY;
		Cluster lCurrentClosestCluster = null;
		for (Cluster lCluster : pClusterList)
		{
			double lDistance = 0;
			lDistance = lCluster.similarityToCluster(pCluster);

			if (lDistance <= lMinimalDistance)
			{
				lCurrentClosestCluster = lCluster;
				lMinimalDistance = lDistance;
			}
		}

		if ((lCurrentClosestCluster == null) && (pClusterList.size() > 0))
		{
			lCurrentClosestCluster = pClusterList.get(0);
		}

		return lCurrentClosestCluster;
	}

	private Cluster findIntersectingCluster(List<Cluster> pClusterList, Cluster pCluster)
	{
		for (Cluster lCluster : pClusterList)
		{
			boolean isIntersecting = computeSetDistance(lCluster.mNodeSet, pCluster.mNodeSet) != Double.POSITIVE_INFINITY;
			if (isIntersecting)
				return lCluster;
		}

		return null;
	}

}