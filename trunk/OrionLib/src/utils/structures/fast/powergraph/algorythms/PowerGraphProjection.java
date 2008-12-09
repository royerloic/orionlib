package utils.structures.fast.powergraph.algorythms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import utils.structures.fast.graph.Edge;
import utils.structures.fast.graph.FastGraph;
import utils.structures.fast.powergraph.FastPowerGraph;

public class PowerGraphProjection
{
	public static <N extends Serializable> FastGraph<N> project(final FastPowerGraph<N> pPowerGraph)
	{

		FastGraph<N> lGraph = new FastGraph<N>();

		List<N> lNodeList = pPowerGraph.getNodeList();

		for (N lN : lNodeList)
		{
			lGraph.addNode(lN);
		}

		ArrayList<Edge<N>> lPowerEdgeList = pPowerGraph.getPowerEdgeList();

		for (Edge<N> lEdge : lPowerEdgeList)
		{
			N lFirstNode = lEdge.getFirstNode();
			N lSecondNode = lEdge.getSecondNode();

			Set<N> lPowerNodeContent1 = pPowerGraph.getPowerNodeContent(lFirstNode);
			Set<N> lPowerNodeContent2 = pPowerGraph.getPowerNodeContent(lSecondNode);

			for (N lN1 : lPowerNodeContent1)
				for (N lN2 : lPowerNodeContent2)
					if (!lN1.equals(lN2))
					{
						lGraph.addEdge(lN1, lN2);
					}

		}

		return lGraph;
	}
}
