package org.royerloic.structures.graph;

import java.util.ArrayList;
import java.util.Set;

public class GraphUtils
{
	public static void removeSymetricEdgesFromGraph(HashGraph pGraph)
	{
		Set<UndirectedEdge<Node>> lEdgeSet = pGraph.getEdgeSet();

		for (Edge<Node> lEdge : new ArrayList<UndirectedEdge<Node>>(lEdgeSet))
		{
			pGraph.removeEdge(lEdge.createSymetricEdge());
		}
	}

}
