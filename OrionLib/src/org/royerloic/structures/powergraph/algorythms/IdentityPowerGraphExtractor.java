package org.royerloic.structures.powergraph.algorythms;

import java.util.HashSet;
import java.util.Set;

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;
import org.royerloic.structures.graph.UndirectedEdge;
import org.royerloic.structures.powergraph.PowerGraph;

public class IdentityPowerGraphExtractor<N> implements PowerGraphExtractorInterface<N>
{
	public IdentityPowerGraphExtractor()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public final PowerGraph<N> extractPowerGraph(Graph<N, Edge<N>> pGraph)
	{
		PowerGraph lPowerGraph = new PowerGraph();

		for (N lNode : pGraph.getNodeSet())
		{
			Set<N> lPowerNode = new HashSet<N>();
			lPowerNode.add(lNode);
			lPowerGraph.addPowerNode(lPowerNode);
		}

		for (Edge<N> lEdge : pGraph.getEdgeSet())
		{
			Set<N> lFirstPowerNode = new HashSet<N>();
			lFirstPowerNode.add(lEdge.getFirstNode());
			Set<N> lSecondPowerNode = new HashSet<N>();
			lSecondPowerNode.add(lEdge.getSecondNode());

			Edge<Set<N>> lPowerEdge = new UndirectedEdge<Set<N>>(lFirstPowerNode, lSecondPowerNode);
			lPowerGraph.addPowerEdge(lPowerEdge);
		}
		return lPowerGraph;
	}

	public PowerGraph<N> extractPowerGraph(Graph<N, Edge<N>> pGraph, double pProbabilityThresold)
	{
		return extractPowerGraph(pGraph);
	}

	public PowerGraph<N> extractPowerGraph(	Graph<N, Edge<N>> pGraph,
																					double pProbabilityThresold,
																					int pMaxIterations)
	{
		return extractPowerGraph(pGraph);
	}

}
