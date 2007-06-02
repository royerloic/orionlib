package utils.structures.graph.algorythms;

import utils.structures.graph.Edge;
import utils.structures.graph.Graph;

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
