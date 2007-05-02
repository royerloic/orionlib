package org.royerloic.structures.graph.algorythms;

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;

public class EdgeDensity
{
	public static <N> double computeEdgeDensity(final Graph<N, Edge<N>> pGraph)
	{
		final double lNumberOfEdges = pGraph.getEdgeSet().size();
		final double lNumberOfNodes = pGraph.getNodeSet().size();

		final double lEdgeDensity = lNumberOfEdges / ((lNumberOfNodes) * (lNumberOfNodes - 1) / 2);
		return lEdgeDensity;
	}

}
