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

	public CliqueEnumerator(final Graph<N, Edge<N>> pGraphInterface)
	{
		super();
		this.mGraphInterface = pGraphInterface;
		this.mCliqueSet = new HashSet<Graph<N, Edge<N>>>();
		doEnumerateCliques();
	}

	@SuppressWarnings("unchecked")
	private void doEnumerateCliques()
	{
		final Set<N> lInitialReachableSet = new HashSet<N>(this.mGraphInterface.getNumberOfNodes());
		final Set<N> lInitialPotentialSet = this.mGraphInterface.getNodeSet();
		final Set<N> lInitialExcludedSet = new HashSet<N>(this.mGraphInterface.getNumberOfNodes());

		BronKerboschRecursive(lInitialReachableSet, lInitialPotentialSet, lInitialExcludedSet);
	}

	@SuppressWarnings("unchecked")
	private void BronKerboschRecursive(final Set<N> pReachable, final Set<N> pPotential, final Set<N> pExcluded)
	{
		if (pPotential.isEmpty() && pExcluded.isEmpty())
			this.mCliqueSet.add(this.mGraphInterface.extractStrictSubGraph(pReachable));
		else
			for (final N lNode : pPotential)
			{
				final Set<N> ReachableNew = new HashSet<N>(pPotential.size());
				final Set<N> PotentialNew = new HashSet<N>(pPotential.size());
				final Set<N> ExcludedNew = new HashSet<N>(pPotential.size());
				final Set lNodeNeighbours = this.mGraphInterface.getNodeNeighbours(lNode);

				ReachableNew.addAll(pReachable);
				ReachableNew.add(lNode);

				PotentialNew.addAll(pPotential);
				PotentialNew.retainAll(lNodeNeighbours);

				ExcludedNew.addAll(pExcluded);
				ExcludedNew.retainAll(lNodeNeighbours);

				BronKerboschRecursive(ReachableNew, PotentialNew, ExcludedNew);

			}

	}

	public void associateGraph(final HashGraph pGraph)
	{
		this.mGraphInterface = pGraph;
	}

	public Graph<N, Edge<N>> getAssociatedGraph()
	{
		return this.mGraphInterface;
	}

	public int size()
	{
		return this.mCliqueSet.size();
	}

	public boolean isEmpty()
	{
		return this.mCliqueSet.isEmpty();
	}

	public boolean contains(final Object pO)
	{
		return this.mCliqueSet.contains(pO);
	}

	public Iterator<Graph<N, Edge<N>>> iterator()
	{
		return this.mCliqueSet.iterator();
	}

	public Object[] toArray()
	{
		return this.mCliqueSet.toArray();
	}

	public <T> T[] toArray(final T[] pA)
	{
		return this.mCliqueSet.<T> toArray(pA);
	}

	public boolean add(final HashGraph pO)
	{
		return this.mCliqueSet.add(pO);
	}

	public boolean remove(final Object pO)
	{
		return this.mCliqueSet.remove(pO);
	}

	public boolean containsAll(final Collection<?> pC)
	{
		return this.mCliqueSet.removeAll(pC);
	}

	public boolean addAll(final Collection<? extends HashGraph> pC)
	{
		return this.mCliqueSet.removeAll(pC);
	}

	public boolean retainAll(final Collection<?> pC)
	{
		return this.mCliqueSet.retainAll(pC);
	}

	public boolean removeAll(final Collection<?> pC)
	{
		return this.mCliqueSet.removeAll(pC);
	}

	public void clear()
	{
		this.mCliqueSet.clear();

	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return this.mCliqueSet.toString();
	}

}
