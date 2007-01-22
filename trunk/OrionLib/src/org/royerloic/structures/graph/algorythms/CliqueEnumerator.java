package org.royerloic.structures.graph.algorythms;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;
import org.royerloic.structures.graph.HashGraph;

public class CliqueEnumerator<N>
{
	Graph<N, Edge<N>>				mGraphInterface;

	Set<Graph<N, Edge<N>>>	mCliqueSet;

	public CliqueEnumerator(Graph<N, Edge<N>> pGraphInterface)
	{
		super();
		mGraphInterface = pGraphInterface;
		mCliqueSet = new HashSet<Graph<N, Edge<N>>>();
		doEnumerateCliques();
	}

	@SuppressWarnings("unchecked")
	private void doEnumerateCliques()
	{
		Set<N> lInitialReachableSet = new HashSet<N>(mGraphInterface.getNumberOfNodes());
		Set<N> lInitialPotentialSet = mGraphInterface.getNodeSet();
		Set<N> lInitialExcludedSet = new HashSet<N>(mGraphInterface.getNumberOfNodes());

		BronKerboschRecursive(lInitialReachableSet, lInitialPotentialSet, lInitialExcludedSet);
	}

	@SuppressWarnings("unchecked")
	private void BronKerboschRecursive(Set<N> pReachable, Set<N> pPotential, Set<N> pExcluded)
	{
		if (pPotential.isEmpty() && pExcluded.isEmpty())
		{
			mCliqueSet.add(mGraphInterface.extractStrictSubGraph(pReachable));
		}
		else
		{
			for (N lNode : pPotential)
			{
				Set<N> ReachableNew = new HashSet<N>(pPotential.size());
				Set<N> PotentialNew = new HashSet<N>(pPotential.size());
				Set<N> ExcludedNew = new HashSet<N>(pPotential.size());
				Set lNodeNeighbours = mGraphInterface.getNodeNeighbours(lNode);

				ReachableNew.addAll(pReachable);
				ReachableNew.add(lNode);

				PotentialNew.addAll(pPotential);
				PotentialNew.retainAll(lNodeNeighbours);

				ExcludedNew.addAll(pExcluded);
				ExcludedNew.retainAll(lNodeNeighbours);

				BronKerboschRecursive(ReachableNew, PotentialNew, ExcludedNew);

			}
		}

	}

	public void associateGraph(HashGraph pGraph)
	{
		mGraphInterface = pGraph;
	}

	public Graph<N, Edge<N>> getAssociatedGraph()
	{
		return mGraphInterface;
	}

	public int size()
	{
		return mCliqueSet.size();
	}

	public boolean isEmpty()
	{
		return mCliqueSet.isEmpty();
	}

	public boolean contains(Object pO)
	{
		return mCliqueSet.contains(pO);
	}

	public Iterator<Graph<N, Edge<N>>> iterator()
	{
		return mCliqueSet.iterator();
	}

	public Object[] toArray()
	{
		return mCliqueSet.toArray();
	}

	public <T> T[] toArray(T[] pA)
	{
		return mCliqueSet.<T> toArray(pA);
	}

	public boolean add(HashGraph pO)
	{
		return mCliqueSet.add(pO);
	}

	public boolean remove(Object pO)
	{
		return mCliqueSet.remove(pO);
	}

	public boolean containsAll(Collection<?> pC)
	{
		return mCliqueSet.removeAll(pC);
	}

	public boolean addAll(Collection<? extends HashGraph> pC)
	{
		return mCliqueSet.removeAll(pC);
	}

	public boolean retainAll(Collection<?> pC)
	{
		return mCliqueSet.retainAll(pC);
	}

	public boolean removeAll(Collection<?> pC)
	{
		return mCliqueSet.removeAll(pC);
	}

	public void clear()
	{
		mCliqueSet.clear();

	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return mCliqueSet.toString();
	}

}
