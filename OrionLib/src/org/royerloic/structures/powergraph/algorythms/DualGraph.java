package org.royerloic.structures.powergraph.algorythms;

import java.util.Set;

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;
import org.royerloic.structures.graph.HashGraph;
import org.royerloic.structures.graph.UndirectedEdge;
import org.royerloic.structures.powergraph.PowerGraph;

public class DualGraph
{

	public static <N> Graph<Edge<Set<N>>, Edge<Edge<Set<N>>>> computeDualGraph(PowerGraph<N> pPowerGraph)
	{
		Graph<Edge<Set<N>>, Edge<Edge<Set<N>>>> lDualGraph = new HashGraph<Edge<Set<N>>, Edge<Edge<Set<N>>>>();
		for (Edge<Set<N>> lPowerEdge1 : pPowerGraph.getPowerEdgeSet())
			for (Edge<Set<N>> lPowerEdge2 : pPowerGraph.getPowerEdgeSet())
				if (lPowerEdge1 != lPowerEdge2)
				{
					if (pPowerGraph.isAdjacent(lPowerEdge1, lPowerEdge2))
						lDualGraph.addEdge(new UndirectedEdge<Edge<Set<N>>>(lPowerEdge1, lPowerEdge2));
				}
		for (Edge<Set<N>> lPowerEdge : pPowerGraph.getPowerEdgeSet())
		{
			lDualGraph.addNode(lPowerEdge);
		}

		return lDualGraph;
	}

}
