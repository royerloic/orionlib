package org.royerloic.structures.graph.algorythms;

import java.util.HashSet;
import java.util.Set;

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;

public class ComponentEnumerator<N>
{
	Graph<N, Edge<N>>				mGraph;

	Set<Graph<N, Edge<N>>>	mComponentSet;

	public ComponentEnumerator(Graph pGraphInterface)
	{
		super();
		mGraph = pGraphInterface;
		mComponentSet = new HashSet<Graph<N, Edge<N>>>();
		doEnumerateComponents();
	}

	private void doEnumerateComponents()
	{
		Set<N> lNodeSet = new HashSet<N>();
		lNodeSet.addAll(mGraph.getNodeSet());

		int lNodeCounter = 0;
		while (!lNodeSet.isEmpty())
		{
			N lFirstNode = lNodeSet.iterator().next();

			Set<N> lReachedSet = new HashSet<N>();
			Set<N> lFrontierSet = new HashSet<N>();
			Set<N> lNewFrontierSet = new HashSet<N>();

			lReachedSet.add(lFirstNode);
			lFrontierSet.addAll(mGraph.getNodeNeighbours(lFirstNode));

			while (!lFrontierSet.isEmpty())
			{
				for (N lFrontierNode : lFrontierSet)
				{
					lNewFrontierSet.addAll(mGraph.getNodeNeighbours(lFrontierNode));
				}
				lNewFrontierSet.removeAll(lReachedSet);
				lReachedSet.addAll(lFrontierSet);
				lFrontierSet.clear();
				lFrontierSet.addAll(lNewFrontierSet);
			}

			lNodeSet.removeAll(lReachedSet);
			// System.out.println("lReachedSet size:"+lReachedSet.size());
			lNodeCounter += lReachedSet.size();

			Graph<N, Edge<N>> lComponent = mGraph.extractStrictSubGraph(lReachedSet);
			mComponentSet.add(lComponent);
		}
		// System.out.println("lNodeCounter="+lNodeCounter);
	}

	public Set<Graph<N, Edge<N>>> getComponentsSet()
	{
		return mComponentSet;
	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return mComponentSet.toString();
	}

}
