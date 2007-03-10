package org.royerloic.structures.graph.algorythms;

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

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;

public class StarEnumerator<N>
{
	Graph<N,Edge<N>>														mGraph;

	Set<Graph>											mStarSet;

	SortedMap<Integer, Integer>			mConnectivityStatistics;

	List<StarEnumerator<N>.Star<N>>	mStarList;

	/**
	 * @author loic (<href>royer@biotec.tu-dresden.de</href>) Dec 5, 2005
	 * 
	 */
	public class Star<N> implements Comparable<Star<N>>
	{
		public Graph	mStarGraph;

		public N			mStarNode;

		public Set<N>	mStarNodesSet;

		public int		mConnectivity;

		public int compareTo(Star pO)
		{
			return ((Star) pO).mConnectivity - mConnectivity;
		}

		@Override
		public String toString()
		{
			return "Star(" + mStarNode + ", " + mStarGraph.getNodeSet() + ")";
		}

	}

	/**
	 * @param pGraphInterface
	 */
	public StarEnumerator(Graph<N,Edge<N>> pGraphInterface)
	{
		super();
		mGraph = pGraphInterface;
		mStarSet = new HashSet<Graph>(pGraphInterface.getNumberOfNodes());
		mConnectivityStatistics = new TreeMap<Integer, Integer>();
		mStarList = new ArrayList<Star<N>>(pGraphInterface.getNumberOfNodes());
		doEnumerateStars();
	}

	private void doEnumerateStars()
	{
		Set<N> lNodeSet = mGraph.getNodeSet();
		for (N lNode : lNodeSet)
		{

			Set<N> lNeighboursSet = mGraph.getNodeNeighbours(lNode);
			int lNodeConnectivity = lNeighboursSet.size();

			if (lNodeConnectivity > 1)
			{

				Integer lNumberOfNodesForConnectivity = mConnectivityStatistics.get(lNodeConnectivity);
				if (lNumberOfNodesForConnectivity == null)
				{
					lNumberOfNodesForConnectivity = 0;
				}
				mConnectivityStatistics.put(lNodeConnectivity, lNumberOfNodesForConnectivity + 1);

				Set<N> lSingletonSet = new HashSet<N>(1);
				lSingletonSet.add(lNode);
				Graph<N,Edge<N>> lSubGraph = mGraph.extractSubGraph(lSingletonSet);

				mStarSet.add(lSubGraph);

				Star lStar = new Star<N>();
				lStar.mStarGraph = lSubGraph;
				lStar.mConnectivity = lNodeConnectivity;
				lStar.mStarNodesSet = lNeighboursSet;
				lStar.mStarNode = lNode;
				mStarList.add(lStar);
			}
		}

		Collections.sort(mStarList);
	}

	public void associateGraph(Graph<N,Edge<N>> pGraph)
	{
		mGraph = pGraph;
	}

	public Graph getAssociatedGraph()
	{
		return mGraph;
	}

	public int size()
	{
		return mStarSet.size();
	}

	public boolean isEmpty()
	{
		return mStarSet.isEmpty();
	}

	public boolean contains(Object pO)
	{
		return mStarSet.contains(pO);
	}

	public Iterator<Graph> iterator()
	{
		return mStarSet.iterator();
	}

	public Object[] toArray()
	{
		return mStarSet.toArray();
	}

	public <T> T[] toArray(T[] pA)
	{
		return mStarSet.<T> toArray(pA);
	}

	public boolean add(Graph pO)
	{
		return mStarSet.add(pO);
	}

	public boolean remove(Object pO)
	{
		return mStarSet.remove(pO);
	}

	public boolean containsAll(Collection<?> pC)
	{
		return mStarSet.removeAll(pC);
	}

	public boolean addAll(Collection<? extends Graph> pC)
	{
		return mStarSet.removeAll(pC);
	}

	public boolean retainAll(Collection<?> pC)
	{
		return mStarSet.retainAll(pC);
	}

	public boolean removeAll(Collection<?> pC)
	{
		return mStarSet.removeAll(pC);
	}

	public void clear()
	{
		mStarSet.clear();

	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return mStarSet.toString();
	}

	public Map<Integer, Integer> getConnectivityStatistics()
	{
		return mConnectivityStatistics;
	}

	public double getGamma()
	{
		Map<Integer, Integer> lConnectivity = getConnectivityStatistics();

		Map<Double, Double> lLogConnectivity = new HashMap<Double, Double>();

		for (int lK : lConnectivity.keySet())
		{
			if ((lK != 0) && (lConnectivity.get(lK) != 0))
				lLogConnectivity.put(Math.log(lK), Math.log(lConnectivity.get(lK)));
		}

		double lSigmaX = 0;
		double lSigmaY = 0;
		double lSigmaX2 = 0;
		double lSigmaY2 = 0;
		double lSigmaXY = 0;
		double lN = lLogConnectivity.size();

		for (double lLogK : lLogConnectivity.keySet())
		{
			double lLogNK = lLogConnectivity.get(lLogK);
			lSigmaX += lLogK;
			lSigmaY += lLogNK;
			lSigmaX2 += lLogK * lLogK;
			lSigmaY2 += lLogNK * lLogNK;
			lSigmaXY += lLogK * lLogNK;
		}

		double a = (((lSigmaY) * (lSigmaX2)) - ((lSigmaX) * (lSigmaXY)))
				/ ((lN * (lSigmaX2)) - (Math.pow(lSigmaX, 2)));
		double b = ((lN * lSigmaXY) - (lSigmaX * lSigmaY)) / ((lN * lSigmaX2) - (Math.pow(lSigmaX, 2)));

		final double gamma = -b;
		
		return gamma;
		
//		final double gammaprime =  getNewGamma();
//		if(Math.abs(gammaprime-gamma)>0.01)
//			throw new RuntimeException("Disagrement while calculating gamma!!");
//		
//		return (gamma+gammaprime)/2;
	}
	
	public double getNewGamma()
	{
		Map<Integer, Integer> lConnectivity = getConnectivityStatistics();

		Map<Double, Double> lLogConnectivity = new HashMap<Double, Double>();

		for (int lK : lConnectivity.keySet())
		{
			if ((lK != 0) && (lConnectivity.get(lK) != 0))
				lLogConnectivity.put(Math.log(lK), Math.log(lConnectivity.get(lK)));
		}
		return 0;

	}

}
