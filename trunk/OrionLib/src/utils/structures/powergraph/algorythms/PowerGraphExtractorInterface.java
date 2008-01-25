package utils.structures.powergraph.algorythms;

import utils.structures.graph.Edge;
import utils.structures.graph.Graph;
import utils.structures.powergraph.PowerGraph;

public interface PowerGraphExtractorInterface<N>
{
	public PowerGraph<N> extractPowerGraph(Graph<N, Edge<N>> pGraph);

	public PowerGraph<N> extractPowerGraph(	Graph<N, Edge<N>> pGraph,
																					double pProbabilityThresold);

}
