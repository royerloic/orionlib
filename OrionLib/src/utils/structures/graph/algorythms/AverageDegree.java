package utils.structures.graph.algorythms;

import utils.structures.graph.Edge;
import utils.structures.graph.Graph;

public class AverageDegree
{
	public static <N> double computeAverageDegree(final Graph<N, Edge<N>> pGraph)
	{
		final double lAverageDegree = ((double)(2 * pGraph.getNumberOfEdges())) / pGraph.getNumberOfNodes();
		return lAverageDegree;
	}

}
