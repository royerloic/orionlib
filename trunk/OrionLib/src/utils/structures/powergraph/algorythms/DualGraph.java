package utils.structures.powergraph.algorythms;

import java.util.Set;

import utils.structures.graph.Edge;
import utils.structures.graph.Graph;
import utils.structures.graph.HashGraph;
import utils.structures.graph.UndirectedEdge;
import utils.structures.powergraph.PowerGraph;

public class DualGraph
{

	public static <N> Graph<Edge<Set<N>>, Edge<Edge<Set<N>>>> computeDualGraph(final PowerGraph<N> pPowerGraph)
	{
		final Graph<Edge<Set<N>>, Edge<Edge<Set<N>>>> lDualGraph = new HashGraph<Edge<Set<N>>, Edge<Edge<Set<N>>>>();
		for (final Edge<Set<N>> lPowerEdge1 : pPowerGraph.getPowerEdgeSet())
			for (final Edge<Set<N>> lPowerEdge2 : pPowerGraph.getPowerEdgeSet())
				if (lPowerEdge1 != lPowerEdge2)
					if (PowerGraph.isAdjacent(lPowerEdge1, lPowerEdge2))
						lDualGraph.addEdge(new UndirectedEdge<Edge<Set<N>>>(lPowerEdge1,
																																lPowerEdge2));
		for (final Edge<Set<N>> lPowerEdge : pPowerGraph.getPowerEdgeSet())
			lDualGraph.addNode(lPowerEdge);

		return lDualGraph;
	}

}
