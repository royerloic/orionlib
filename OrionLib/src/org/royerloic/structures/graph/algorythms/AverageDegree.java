package org.royerloic.structures.graph.algorythms;

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;

public class AverageDegree
{
	public static <N> double computeAverageDegree(final Graph<N, Edge<N>> pGraph)
	{
		final double lAverageDegree = ((double)(2 * pGraph.getNumberOfEdges())) / pGraph.getNumberOfNodes();
		return lAverageDegree;
	}

}
