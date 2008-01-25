package utils.structures.graph.algorythms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import utils.structures.graph.Edge;
import utils.structures.graph.Graph;

public class StarEnumerator<N>
{
	Graph<N, Edge<N>> mGraph;

	Set<Graph> mStarSet;

	SortedMap<Integer, Integer> mConnectivityStatistics;

	List<StarEnumerator<N>.Star<N>> mStarList;

	/**
	 * @author loic (<href>royer@biotec.tu-dresden.de</href>) Dec 5, 2005
	 * 
	 */
	public class Star<N> implements Comparable<Star<N>>
	{
		public Graph mStarGraph;

		public N mStarNode;

		public Set<N> mStarNodesSet;

		public int mConnectivity;

		public int compareTo(final Star pO)
		{
			return (pO).mConnectivity - this.mConnectivity;
		}

		@Override
		public String toString()
		{
			return "Star(" + this.mStarNode
							+ ", "
							+ this.mStarGraph.getNodeSet()
							+ ")";
		}

	}

	/**
	 * @param pGraphInterface
	 */
	public StarEnumerator(final Graph<N, Edge<N>> pGraphInterface)
	{
		super();
		this.mGraph = pGraphInterface;
		this.mStarSet = new HashSet<Graph>(pGraphInterface.getNumberOfNodes());
		this.mConnectivityStatistics = new TreeMap<Integer, Integer>();
		this.mStarList = new ArrayList<Star<N>>(pGraphInterface.getNumberOfNodes());
		doEnumerateStars();
	}

	private void doEnumerateStars()
	{
		final Set<N> lNodeSet = this.mGraph.getNodeSet();
		for (final N lNode : lNodeSet)
		{

			final Set<N> lNeighboursSet = this.mGraph.getNodeNeighbours(lNode);
			final int lNodeConnectivity = lNeighboursSet.size();

			if (lNodeConnectivity > 1)
			{

				Integer lNumberOfNodesForConnectivity = this.mConnectivityStatistics.get(lNodeConnectivity);
				if (lNumberOfNodesForConnectivity == null)
					lNumberOfNodesForConnectivity = 0;
				this.mConnectivityStatistics.put(	lNodeConnectivity,
																					lNumberOfNodesForConnectivity + 1);

				final Set<N> lSingletonSet = new HashSet<N>(1);
				lSingletonSet.add(lNode);
				final Graph<N, Edge<N>> lSubGraph = this.mGraph.extractSubGraph(lSingletonSet);

				this.mStarSet.add(lSubGraph);

				final Star lStar = new Star<N>();
				lStar.mStarGraph = lSubGraph;
				lStar.mConnectivity = lNodeConnectivity;
				lStar.mStarNodesSet = lNeighboursSet;
				lStar.mStarNode = lNode;
				this.mStarList.add(lStar);
			}
		}

		Collections.sort(this.mStarList);
	}

	public void associateGraph(final Graph<N, Edge<N>> pGraph)
	{
		this.mGraph = pGraph;
	}

	public Graph getAssociatedGraph()
	{
		return this.mGraph;
	}

	public int size()
	{
		return this.mStarSet.size();
	}

	public boolean isEmpty()
	{
		return this.mStarSet.isEmpty();
	}

	public boolean contains(final Object pO)
	{
		return this.mStarSet.contains(pO);
	}

	public Iterator<Graph> iterator()
	{
		return this.mStarSet.iterator();
	}

	public Object[] toArray()
	{
		return this.mStarSet.toArray();
	}

	public <T> T[] toArray(final T[] pA)
	{
		return this.mStarSet.<T> toArray(pA);
	}

	public boolean add(final Graph pO)
	{
		return this.mStarSet.add(pO);
	}

	public boolean remove(final Object pO)
	{
		return this.mStarSet.remove(pO);
	}

	public boolean containsAll(final Collection<?> pC)
	{
		return this.mStarSet.removeAll(pC);
	}

	public boolean addAll(final Collection<? extends Graph> pC)
	{
		return this.mStarSet.removeAll(pC);
	}

	public boolean retainAll(final Collection<?> pC)
	{
		return this.mStarSet.retainAll(pC);
	}

	public boolean removeAll(final Collection<?> pC)
	{
		return this.mStarSet.removeAll(pC);
	}

	public void clear()
	{
		this.mStarSet.clear();

	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return this.mStarSet.toString();
	}

	public Map<Integer, Integer> getConnectivityStatistics()
	{
		return this.mConnectivityStatistics;
	}

	public double getGamma()
	{
		final Map<Integer, Integer> lConnectivity = getConnectivityStatistics();

		final Map<Double, Double> lLogConnectivity = new HashMap<Double, Double>();

		for (final int lK : lConnectivity.keySet())
			if ((lK != 0) && (lConnectivity.get(lK) != 0))
				lLogConnectivity.put(Math.log(lK), Math.log(lConnectivity.get(lK)));

		double lSigmaX = 0;
		double lSigmaY = 0;
		double lSigmaX2 = 0;
		double lSigmaY2 = 0;
		double lSigmaXY = 0;
		final double lN = lLogConnectivity.size();

		for (final double lLogK : lLogConnectivity.keySet())
		{
			final double lLogNK = lLogConnectivity.get(lLogK);
			lSigmaX += lLogK;
			lSigmaY += lLogNK;
			lSigmaX2 += lLogK * lLogK;
			lSigmaY2 += lLogNK * lLogNK;
			lSigmaXY += lLogK * lLogNK;
		}

		final double a = (((lSigmaY) * (lSigmaX2)) - ((lSigmaX) * (lSigmaXY))) / ((lN * (lSigmaX2)) - (Math.pow(lSigmaX,
																																																						2)));
		double b = ((lN * lSigmaXY) - (lSigmaX * lSigmaY)) / ((lN * lSigmaX2) - (Math.pow(lSigmaX,
																																											2)));

		final double gamma = -b;

		return gamma;

		// final double gammaprime = getNewGamma();
		// if(Math.abs(gammaprime-gamma)>0.01)
		// throw new RuntimeException("Disagrement while calculating gamma!!");
		//		
		// return (gamma+gammaprime)/2;
	}

	public double getNewGamma()
	{
		final Map<Integer, Integer> lConnectivity = getConnectivityStatistics();

		final Map<Double, Double> lLogConnectivity = new HashMap<Double, Double>();

		for (final int lK : lConnectivity.keySet())
			if ((lK != 0) && (lConnectivity.get(lK) != 0))
				lLogConnectivity.put(Math.log(lK), Math.log(lConnectivity.get(lK)));
		return 0;

	}

}
