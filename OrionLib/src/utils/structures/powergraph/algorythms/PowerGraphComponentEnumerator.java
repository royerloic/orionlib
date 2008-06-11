package utils.structures.powergraph.algorythms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.structures.graph.Edge;
import utils.structures.powergraph.PowerGraph;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 * @param <N>
 */
public class PowerGraphComponentEnumerator
{

	private PowerGraphComponentEnumerator()
	{
	}

	/**
	 * @param <N>
	 * @param pPowerGraph
	 * @return
	 */
	public static <N> List<PowerGraph<N>> doEnumerateComponents(final PowerGraph<N> pPowerGraph)
	{
		final List<PowerGraph<N>> lComponentList = new ArrayList<PowerGraph<N>>();

		final Set<Edge<Set<N>>> lPowerEdgeSet = new HashSet<Edge<Set<N>>>();
		lPowerEdgeSet.addAll(pPowerGraph.getPowerEdgeSet());

		int lPowerEdgeCounter = 0;
		while (!lPowerEdgeSet.isEmpty())
		{
			final Edge<Set<N>> lFirstEdge = lPowerEdgeSet.iterator().next();

			final Set<Edge<Set<N>>> lReachedSet = new HashSet<Edge<Set<N>>>();
			final Set<Edge<Set<N>>> lFrontierSet = new HashSet<Edge<Set<N>>>();
			final Set<Edge<Set<N>>> lNewFrontierSet = new HashSet<Edge<Set<N>>>();

			lReachedSet.add(lFirstEdge);
			for (final Edge<Set<N>> lEdge : lPowerEdgeSet)
			{
				if (PowerGraph.isConnected(lFirstEdge, lEdge))
				{
					lFrontierSet.add(lEdge);
				}
			}

			while (!lFrontierSet.isEmpty())
			{
				for (final Edge<Set<N>> lFrontierPowerEdge : lFrontierSet)
				{
					for (final Edge<Set<N>> lEdge : lPowerEdgeSet)
					{
						if (PowerGraph.isConnected(lFrontierPowerEdge, lEdge))
						{
							lNewFrontierSet.add(lEdge);
						}
					}
				}
				lNewFrontierSet.removeAll(lReachedSet);
				lReachedSet.addAll(lFrontierSet);
				lFrontierSet.clear();
				lFrontierSet.addAll(lNewFrontierSet);
			}

			lPowerEdgeSet.removeAll(lReachedSet);
			// System.out.println("lReachedSet size:"+lReachedSet.size());
			lPowerEdgeCounter += lReachedSet.size();

			final PowerGraph<N> lComponent = new PowerGraph<N>();
			for (final Edge<Set<N>> lEdge : lReachedSet)
			{
				lComponent.addPowerEdge(lEdge);
			}
			lComponentList.add(lComponent);
		}

		final Set<N> lSolitaryNodeSet = new HashSet<N>();
		lSolitaryNodeSet.addAll(pPowerGraph.getNodeSet());
		for (final Edge<Set<N>> lEdge : pPowerGraph.getPowerEdgeSet())
		{
			lSolitaryNodeSet.removeAll(lEdge.getFirstNode());
			lSolitaryNodeSet.removeAll(lEdge.getSecondNode());
		}
		for (final N lNode : lSolitaryNodeSet)
		{
			final PowerGraph<N> lComponent = new PowerGraph<N>();
			final Set<N> lPowerNode = new HashSet<N>();
			lPowerNode.add(lNode);
			lComponent.addPowerNode(lPowerNode);
			lComponentList.add(lComponent);
		}/**/

		return lComponentList;
	}
}
