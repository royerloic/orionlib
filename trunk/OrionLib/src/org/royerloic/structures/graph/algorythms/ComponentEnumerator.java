package org.royerloic.structures.graph.algorythms;

import java.util.HashSet;
import java.util.Set;

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;

public class ComponentEnumerator<N>
{
	Graph<N, Edge<N>>				mGraph;

	Set<Graph<N, Edge<N>>>	mComponentSet;

	public ComponentEnumerator(final Graph pGraphInterface)
	{
		super();
		this.mGraph = pGraphInterface;
		this.mComponentSet = new HashSet<Graph<N, Edge<N>>>();
		doEnumerateComponents();
	}

	private void doEnumerateComponents()
	{
		final Set<N> lNodeSet = new HashSet<N>();
		lNodeSet.addAll(this.mGraph.getNodeSet());

		int lNodeCounter = 0;
		while (!lNodeSet.isEmpty())
		{
			final N lFirstNode = lNodeSet.iterator().next();

			final Set<N> lReachedSet = new HashSet<N>();
			final Set<N> lFrontierSet = new HashSet<N>();
			final Set<N> lNewFrontierSet = new HashSet<N>();

			lReachedSet.add(lFirstNode);
			lFrontierSet.addAll(this.mGraph.getNodeNeighbours(lFirstNode));

			while (!lFrontierSet.isEmpty())
			{
				for (final N lFrontierNode : lFrontierSet)
					lNewFrontierSet.addAll(this.mGraph.getNodeNeighbours(lFrontierNode));
				lNewFrontierSet.removeAll(lReachedSet);
				lReachedSet.addAll(lFrontierSet);
				lFrontierSet.clear();
				lFrontierSet.addAll(lNewFrontierSet);
			}

			lNodeSet.removeAll(lReachedSet);
			// System.out.println("lReachedSet size:"+lReachedSet.size());
			lNodeCounter += lReachedSet.size();

			final Graph<N, Edge<N>> lComponent = this.mGraph.extractStrictSubGraph(lReachedSet);
			this.mComponentSet.add(lComponent);
		}
		// System.out.println("lNodeCounter="+lNodeCounter);
	}

	public Set<Graph<N, Edge<N>>> getComponentsSet()
	{
		return this.mComponentSet;
	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return this.mComponentSet.toString();
	}

}
