package utils.structures.powergraph.algorythms;

import java.util.HashSet;
import java.util.Set;

import utils.structures.graph.Edge;
import utils.structures.graph.Graph;
import utils.structures.graph.UndirectedEdge;
import utils.structures.powergraph.PowerGraph;

public class IdentityPowerGraphExtractor<N> implements
																						PowerGraphExtractorInterface<N>
{
	public IdentityPowerGraphExtractor()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public final PowerGraph<N> extractPowerGraph(final Graph<N, Edge<N>> pGraph)
	{
		final PowerGraph lPowerGraph = new PowerGraph();

		for (final N lNode : pGraph.getNodeSet())
		{
			final Set<N> lPowerNode = new HashSet<N>();
			lPowerNode.add(lNode);
			lPowerGraph.addPowerNode(lPowerNode);
		}

		for (final Edge<N> lEdge : pGraph.getEdgeSet())
		{
			final Set<N> lFirstPowerNode = new HashSet<N>();
			lFirstPowerNode.add(lEdge.getFirstNode());
			final Set<N> lSecondPowerNode = new HashSet<N>();
			lSecondPowerNode.add(lEdge.getSecondNode());

			final Edge<Set<N>> lPowerEdge = new UndirectedEdge<Set<N>>(	lFirstPowerNode,
																																	lSecondPowerNode);
			lPowerGraph.addPowerEdge(lPowerEdge);
		}
		return lPowerGraph;
	}

	public PowerGraph<N> extractPowerGraph(	final Graph<N, Edge<N>> pGraph,
																					final double pProbabilityThresold)
	{
		return extractPowerGraph(pGraph);
	}

	public PowerGraph<N> extractPowerGraph(	final Graph<N, Edge<N>> pGraph,
																					final double pProbabilityThresold,
																					final int pMaxIterations)
	{
		return extractPowerGraph(pGraph);
	}

}
