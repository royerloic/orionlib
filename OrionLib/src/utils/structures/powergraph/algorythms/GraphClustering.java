package utils.structures.powergraph.algorythms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import utils.structures.graph.Edge;
import utils.structures.graph.Graph;
import utils.structures.graph.HashGraph;
import utils.structures.graph.UndirectedEdge;

public class GraphClustering<N>
{

	private final boolean mCentralityWeigting;

	final Double computeSetDistance(final Set<N> pSet1, final Set<N> pSet2)
	{
		final Set<N> lSet = new HashSet<N>(pSet1);
		lSet.retainAll(pSet2);
		final double lIntersectionSize = lSet.size();
		final Double lDistance = ((pSet1.size() + pSet2.size() - 2 * lIntersectionSize)) / lIntersectionSize;
		if (lIntersectionSize == Double.NaN)
			return Double.POSITIVE_INFINITY;
		return lDistance;
	}

	final Double computeWeightedSize(final Map<N, Double> pWeightedSet)
	{
		Double lSize = new Double(0);
		for (final Map.Entry<N, Double> lEntry : pWeightedSet.entrySet())
			lSize += this.mCentralityWeigting ? lEntry.getValue() : 1;
		return lSize;
	}

	final Double computeWeightedSetSimilarity(final Map<N, Double> pWeightedSet1,
																						final Map<N, Double> pWeightedSet2)
	{
		final Double lSize1 = computeWeightedSize(pWeightedSet1);
		final Double lSize2 = computeWeightedSize(pWeightedSet2);

		final Map<N, Double> lWeightedSetIntersection = new HashMap<N, Double>(pWeightedSet1);
		for (final Map.Entry<N, Double> lEntry : pWeightedSet1.entrySet())
			if (!pWeightedSet2.containsKey(lEntry.getKey()))
				lWeightedSetIntersection.remove(lEntry.getKey());
		final Double lSizeIntersection = computeWeightedSize(lWeightedSetIntersection);

		Double lDistance = (lSizeIntersection) / (lSize1 + lSize2 - lSizeIntersection);
		lDistance = lDistance == Double.NaN ? Double.POSITIVE_INFINITY : lDistance;

		return lDistance;
	}

	public class Cluster
	{
		Set<N> mNodeSet = new HashSet<N>();
		Map<N, Double> mNodeToConnectivityMap = new HashMap<N, Double>();

		Set<N> getNodesInCluster()
		{
			return this.mNodeSet;
		}

		public void addNode(final Graph pGraph, final N pNode)
		{
			this.mNodeSet.add(pNode);
			final Set<N> lNodeNeighbors = pGraph.getNodeNeighbours(pNode);
			for (final N lNode : lNodeNeighbors)
			{
				final double lConnectivity = pGraph.getNodeNeighbours(lNode).size();
				// System.out.println(lConnectivity);
				this.mNodeToConnectivityMap.put(lNode, lConnectivity);
			}
			/*************************************************************************
			 * for (N lNode : mNodeSet) { mNodeToConnectivityMap.remove(lNode); }/
			 ************************************************************************/
			// filter(mNodeToConnectivityMap, mThreshold);
		}

		public void mergeWith(final Cluster pCluster)
		{
			this.mNodeSet.addAll(pCluster.mNodeSet);
			this.mNodeToConnectivityMap.putAll(pCluster.mNodeToConnectivityMap);
			for (final N lNode : this.mNodeSet)
				this.mNodeToConnectivityMap.remove(lNode);
			// filter(mNodeToConnectivityMap, mThreshold);
		}

		int getClusterSize()
		{
			return this.mNodeSet.size();
		}

		Double similarityToCluster(final Cluster pCluster)
		{
			final Double lDistance = computeWeightedSetSimilarity(this.mNodeToConnectivityMap,
																														pCluster.mNodeToConnectivityMap);
			// if (!Double.isInfinite(lDistance)) System.out.println(lDistance);
			return lDistance;
		}

		@Override
		public String toString()
		{
			return this.mNodeSet.toString();
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
				return true;
			if (this.hashCode() != ((Cluster) obj).hashCode())
				return false;

			return this.mNodeSet.equals(((Cluster) obj).mNodeSet);
		}

		@Override
		public int hashCode()
		{
			return this.mNodeSet.hashCode();
		}

		protected Cluster copy()
		{
			final Cluster lCluster = new Cluster();
			lCluster.mNodeSet.addAll(this.mNodeSet);
			lCluster.mNodeToConnectivityMap.putAll(this.mNodeToConnectivityMap);
			return lCluster;
		}

	};

	public GraphClustering(final boolean pCentralityWeigting)
	{
		super();
		this.mCentralityWeigting = pCentralityWeigting;
	}

	public final Set<Set<N>> cluster(	final Graph<N, Edge<N>> pGraph,
																		final double pCutOff,
																		final int pMaxIterations)
	{
		final Graph<Cluster, Edge<Cluster>> lClusterGraph = new HashGraph<Cluster, Edge<Cluster>>();
		{
			final Map<N, Cluster> lNodeToClusterMap = new HashMap<N, Cluster>();
			for (final N lNode : pGraph.getNodeSet())
			{
				final Cluster lCluster = new Cluster();
				lCluster.addNode(pGraph, lNode);
				lClusterGraph.addNode(lCluster);
				lNodeToClusterMap.put(lNode, lCluster);
			}
			for (final Edge<N> lEdge : pGraph.getEdgeSet())
			{
				final N lNode1 = lEdge.getFirstNode();
				final N lNode2 = lEdge.getSecondNode();
				final Cluster lCluster1 = lNodeToClusterMap.get(lNode1);
				final Cluster lCluster2 = lNodeToClusterMap.get(lNode2);
				lClusterGraph.addEdge(new UndirectedEdge<Cluster>(lCluster1, lCluster2));
			}
		}

		return clusterInternal(lClusterGraph, pCutOff, pMaxIterations);
	}

	public final Set<Set<N>> clusterInternal(	final Graph<Cluster, Edge<Cluster>> pClusterGraph,
																						final double pMinSimilarity,
																						final int pMaxIterations)
	{

		final Set<Cluster> lFinalClusterSet = new HashSet<Cluster>();
		for (final Cluster lCluster : pClusterGraph.getNodeSet())
			lFinalClusterSet.add(lCluster.copy());

		final Map<UndirectedEdge<Cluster>, Double> lDistanceCache = new HashMap<UndirectedEdge<Cluster>, Double>();

		final Set<Cluster> lMaximalSimilarityClusterSet = new HashSet<Cluster>();

		int lIterationCount = 0;
		while (pClusterGraph.getNumberOfNodes() > 1)
		{

			// System.out.print(".");
			double lMaximalSimilarity = -1;

			lMaximalSimilarityClusterSet.clear();

			for (final Cluster lCluster1 : pClusterGraph.getNodeSet())
			{
				final Set<Cluster> lNeighboursSet = pClusterGraph.getNodeNeighbours(lCluster1,
																																						2);

				double lMaximalSimilarityForCluster = -1;
				for (final Cluster lCluster2 : lNeighboursSet)
				{
					final Double lSimilarity = lCluster1.similarityToCluster(lCluster2);
					// System.out.println("s( "+lCluster1+" , "+lCluster2+" ) \n =
					// "+lSimilarity+"\n");
					lMaximalSimilarityForCluster = Math.max(lMaximalSimilarityForCluster,
																									lSimilarity);
				}

				if (lMaximalSimilarityForCluster > lMaximalSimilarity)
				{
					lMaximalSimilarity = lMaximalSimilarityForCluster;
					lMaximalSimilarityClusterSet.clear();
				}

				if (lMaximalSimilarityForCluster == lMaximalSimilarity)
					lMaximalSimilarityClusterSet.add(lCluster1);
			}

			System.out.println("lMaximalSimilarityClusterSet.size()=" + lMaximalSimilarityClusterSet.size());
			System.out.println("lMaximalSimilarity=" + lMaximalSimilarity);

			while (!lMaximalSimilarityClusterSet.isEmpty())
			{
				final Cluster lCluster1 = lMaximalSimilarityClusterSet.iterator()
																															.next();
				final Set<Cluster> lNeighboursSet = pClusterGraph.getNodeNeighbours(lCluster1,
																																						2);
				final Set<Cluster> lClusterSet = new HashSet<Cluster>();
				for (final Cluster lCluster2 : lNeighboursSet)
				{
					final Double lSimilarity = lCluster1.similarityToCluster(lCluster2);
					if ((lSimilarity == lMaximalSimilarity))
						lClusterSet.add(lCluster2);
				}
				lClusterSet.add(lCluster1);

				final Set<Cluster> lClusterNeighbours = pClusterGraph.getNodeNeighbours(lClusterSet);

				pClusterGraph.removeAllNodes(lClusterSet);
				final Cluster lNewCluster = new Cluster();
				for (final Cluster lCluster : lClusterSet)
					lNewCluster.mergeWith(lCluster);
				pClusterGraph.addNode(lNewCluster);

				for (final Cluster lNeighbourCluster : lClusterNeighbours)
					pClusterGraph.addEdge(new UndirectedEdge<Cluster>(lNewCluster,
																														lNeighbourCluster));

				lMaximalSimilarityClusterSet.removeAll(lClusterSet);
			}

			for (final Cluster lCluster : pClusterGraph.getNodeSet())
				lFinalClusterSet.add(lCluster.copy());
			lIterationCount++;

			if ((lMaximalSimilarity < pMinSimilarity) || (lIterationCount >= pMaxIterations))
				break;
		}

		final Set<Set<N>> lSetSet = new HashSet<Set<N>>();
		for (final Cluster lCluster : lFinalClusterSet)
			lSetSet.add(lCluster.mNodeSet);

		System.out.println("done.");
		return lSetSet;
	}

}
