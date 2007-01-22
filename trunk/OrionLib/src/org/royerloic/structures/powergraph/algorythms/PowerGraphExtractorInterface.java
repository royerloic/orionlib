package org.royerloic.structures.powergraph.algorythms;

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;
import org.royerloic.structures.powergraph.PowerGraph;

public interface PowerGraphExtractorInterface<N>
{
	public PowerGraph<N> extractPowerGraph(Graph<N, Edge<N>> pGraph);

	public PowerGraph<N> extractPowerGraph(Graph<N, Edge<N>> pGraph, double pProbabilityThresold);

	public PowerGraph<N> extractPowerGraph(	Graph<N, Edge<N>> pGraph,
																					double pProbabilityThresold,
																					int pMaxIterations);
}
