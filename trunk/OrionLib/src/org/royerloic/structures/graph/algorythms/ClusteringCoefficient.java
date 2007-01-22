package org.royerloic.structures.graph.algorythms;

import java.util.ArrayList;
import java.util.List;

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;

public class ClusteringCoefficient<N>
{
	public static <N> double computeClusteringCoefficient(Graph<N, Edge<N>> pGraph)
	{
		double lSum = 0;
		for (N lNode : pGraph.getNodeSet())
		{
			lSum += computeClusteringCoefficient(pGraph, lNode);
		}
		final double lClusteringCoefficient = lSum / pGraph.getNodeSet().size();
		return lClusteringCoefficient;
	}

	public static <N> double computeClusteringCoefficient(Graph<N, Edge<N>> pGraph, N pNode)
	{
		int lEdgeCount = 0;
		List<N> lNodeNeighboursList = new ArrayList<N>(pGraph.getNodeNeighbours(pNode));
		final double lDegree = lNodeNeighboursList.size();
		if (lDegree == 0)
			return 0;
		for (int i = 0; i < lDegree; i++)
			for (int j = 0; j < i; j++)
			{
				N lNode1 = lNodeNeighboursList.get(i);
				N lNode2 = lNodeNeighboursList.get(j);

				if (pGraph.isEdge(lNode1, lNode2))
					lEdgeCount++;
			}

		final double lClusteringCoefficient = lEdgeCount / ((lDegree) * (lDegree + 1) / 2);
		return lClusteringCoefficient;
	}

}
