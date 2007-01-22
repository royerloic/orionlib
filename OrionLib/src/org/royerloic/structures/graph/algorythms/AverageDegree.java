package org.royerloic.structures.graph.algorythms;

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;

public class AverageDegree
{
	public static <N> double computeAverageDegree(Graph<N, Edge<N>> pGraph)
	{
		double lSum = 0;
		for (N lNode : pGraph.getNodeSet())
		{
			lSum += computeAverageDegree(pGraph, lNode);
		}
		final double lClusteringCoefficient = lSum / pGraph.getNodeSet().size();
		return lClusteringCoefficient;
	}

	public static <N> double computeAverageDegree(Graph<N, Edge<N>> pGraph, N pNode)
	{
		final double lDegree = pGraph.getNodeNeighbours(pNode).size();
		return lDegree;
	}

}
