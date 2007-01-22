package org.royerloic.structures.powergraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.UndirectedEdge;

public class PowerGraph<N>
{
	private static class PowerEdgeComparator<N> implements Comparator<Edge<Set<N>>>
	{
		public int compare(Edge<Set<N>> pPowerEdge1, Edge<Set<N>> pPowerEdge2)
		{
			int lSize1 = pPowerEdge1.getFirstNode().size() * pPowerEdge1.getSecondNode().size();
			int lSize2 = pPowerEdge2.getFirstNode().size() * pPowerEdge2.getSecondNode().size();
			return -(lSize1 - lSize2); // when sorting the result is in descending
		}
	}
	private PowerEdgeComparator<N>	cPowerEdgeComparator	= new PowerEdgeComparator<N>();

	private Set<Set<N>>							mPowerNodeSet;

	private Set<Edge<Set<N>>>				mPowerEdgeSet;

	private Set<N>									mNodeSet;

	private Set<Set<N>>							mClusterSet;

	public PowerGraph()
	{
		super();
		mPowerNodeSet = new HashSet<Set<N>>();
		mPowerEdgeSet = new HashSet<Edge<Set<N>>>();
		mNodeSet = new HashSet<N>();
		mClusterSet = new HashSet<Set<N>>();
	}

	public void addNode(N pNode)
	{
		mNodeSet.add(pNode);
	}

	public void addPowerNode(Set<N> pNodeSet)
	{
		mNodeSet.addAll(pNodeSet);
		mPowerNodeSet.add(pNodeSet);
	}

	public void addCluster(Set<N> pNodeSet)
	{
		mClusterSet.add(pNodeSet);
		mNodeSet.addAll(pNodeSet);
	}

	private List<Edge<Set<N>>>	mDelayedPowerEdgeList	= new ArrayList<Edge<Set<N>>>();

	public void addPowerEdgeDelayed(Edge<Set<N>> pPowerEdge)
	{
		mDelayedPowerEdgeList.add(pPowerEdge);
	}

	public void commitDelayedEdges()
	{
		Collections.<Edge<Set<N>>> sort(mDelayedPowerEdgeList, cPowerEdgeComparator);
		for (Edge<Set<N>> lEdge : mDelayedPowerEdgeList)
			if (!isIntersectingPowerEdgePresent(lEdge))
			{
				addPowerEdge(lEdge);
			}
		mDelayedPowerEdgeList.clear();
	}

	public void addPowerEdge(Edge<Set<N>> pPowerEdge)
	{
		Set<N> lFirstPowerNode = pPowerEdge.getFirstNode();
		Set<N> lSecondPowerNode = pPowerEdge.getSecondNode();

		mNodeSet.addAll(lFirstPowerNode);
		mNodeSet.addAll(lSecondPowerNode);

		addPowerNode(lFirstPowerNode);
		addPowerNode(lSecondPowerNode);

		Edge<Set<N>> lPowerEdge = new UndirectedEdge<Set<N>>(lFirstPowerNode, lSecondPowerNode);
		mPowerEdgeSet.add(lPowerEdge);

	}

	public void removePowerEdge(Edge<Set<N>> pPowerEdge)
	{
		mPowerNodeSet.remove(pPowerEdge.getFirstNode());
		mPowerNodeSet.remove(pPowerEdge.getSecondNode());
		mPowerEdgeSet.remove(pPowerEdge);
		mNodeSet.clear();
		for (Set<N> lNodeSet : mPowerNodeSet)
		{
			mNodeSet.addAll(lNodeSet);
		}
		for (Set<N> lNodeSet : mClusterSet)
		{
			mNodeSet.addAll(lNodeSet);
		}
	}

	private void removeSmallerPowerEdges(Edge<Set<N>> pAddedPowerEdge)
	{
		List<Edge<Set<N>>> lDeleteList = new ArrayList<Edge<Set<N>>>();
		for (Edge<Set<N>> lExistingPowerEdge : mPowerEdgeSet)
		{
			if (strictlyIncludedIn(lExistingPowerEdge, pAddedPowerEdge))
				lDeleteList.add(lExistingPowerEdge);
		}
		for (Edge<Set<N>> lPowerEdge : lDeleteList)
		{
			removePowerEdge(lPowerEdge);
		}
	}

	private boolean isBiggerPowerEdgePresent(Edge<Set<N>> pPowerEdge)
	{
		for (Edge<Set<N>> lPowerEdge : mPowerEdgeSet)
		{
			if (includedIn(pPowerEdge, lPowerEdge))
				return true;
		}
		return false;
	}

	public boolean strictlyIncludedIn(Edge<Set<N>> pPowerEdge1, Edge<Set<N>> pPowerEdge2)
	{
		if (pPowerEdge1.equals(pPowerEdge2))
		{
			return false;
		}
		else
		{
			return includedIn(pPowerEdge1, pPowerEdge2);
		}
	}

	public boolean includedIn(Edge<Set<N>> pPowerEdge1, Edge<Set<N>> pPowerEdge2)
	{
		Set<N> lF1 = pPowerEdge1.getFirstNode();
		Set<N> lS1 = pPowerEdge1.getSecondNode();
		Set<N> lF2 = pPowerEdge2.getFirstNode();
		Set<N> lS2 = pPowerEdge2.getSecondNode();
		boolean isFirstIncludedA = lF2.containsAll(lF1);
		boolean isSecondIncludedA = lS2.containsAll(lS1);

		if (isFirstIncludedA && isSecondIncludedA)
			return true;

		boolean isFirstIncludedB = lF2.containsAll(lS1);
		boolean isSecondIncludedB = lS2.containsAll(lF1);
		if (isFirstIncludedB && isSecondIncludedB)
			return (true);

		return false;
	}

	private Edge<Set<N>> isInterlockingPowerEdgePresent(Edge<Set<N>> pPowerEdge)
	{
		for (Edge<Set<N>> lPowerEdge : mPowerEdgeSet)
		{
			if (strictlyInterlockingWith(pPowerEdge, lPowerEdge))
				return lPowerEdge;
		}
		return null;
	}

	private boolean strictlyInterlockingWith(Edge<Set<N>> pPowerEdge1, Edge<Set<N>> pPowerEdge2)
	{
		Set<N> lF1 = pPowerEdge1.getFirstNode();
		Set<N> lS1 = pPowerEdge1.getSecondNode();
		Set<N> lF2 = pPowerEdge2.getFirstNode();
		Set<N> lS2 = pPowerEdge2.getSecondNode();

		boolean isA1 = lF1.containsAll(lF2);
		boolean isA2 = lS2.containsAll(lS1);
		boolean isStrictA = (lF1.size() > lF2.size()) && (lS2.size() > lS1.size());
		if (isA1 && isA2 && isStrictA)
			return true;

		boolean isB1 = lF1.containsAll(lS2);
		boolean isB2 = lF2.containsAll(lS1);
		boolean isStrictB = (lF1.size() > lS2.size()) && (lF2.size() > lS1.size());
		if (isB1 && isB2 && isStrictB)
			return true;

		boolean isC1 = lS1.containsAll(lF2);
		boolean isC2 = lS2.containsAll(lF1);
		boolean isStrictC = (lS1.size() > lF2.size()) && (lS2.size() > lF1.size());
		if (isC1 && isC2 && isStrictC)
			return true;

		boolean isD1 = lS1.containsAll(lS2);
		boolean isD2 = lF2.containsAll(lF1);
		boolean isStrictD = (lS1.size() > lS2.size()) && (lF2.size() > lF1.size());
		if (isD1 && isD2 && isStrictD)
			return true;

		return false;
	}

	private Edge<Set<N>> cutInterlockingPowerEdge(Edge<Set<N>> pPowerEdge1, Edge<Set<N>> pPowerEdge2)
	{
		final Set<N> lF1 = pPowerEdge1.getFirstNode();
		final Set<N> lS1 = pPowerEdge1.getSecondNode();
		final Set<N> lF2 = pPowerEdge2.getFirstNode();
		final Set<N> lS2 = pPowerEdge2.getSecondNode();

		final boolean isA1 = lF1.containsAll(lF2);
		final boolean isA2 = lS2.containsAll(lS1);
		final boolean isStrictA = (lF1.size() > lF2.size()) && (lS2.size() > lS1.size());
		if (isA1 && isA2 && isStrictA)
		{
			final Set<N> lS2MS1 = new HashSet<N>(lS2);
			lS2MS1.removeAll(lS1);
			return new UndirectedEdge<Set<N>>(lF2, lS2MS1);
		}
		;

		final boolean isB1 = lF1.containsAll(lS2);
		final boolean isB2 = lF2.containsAll(lS1);
		final boolean isStrictB = (lF1.size() > lS2.size()) && (lF2.size() > lS1.size());
		if (isB1 && isB2 && isStrictB)
		{
			final Set<N> lF2MS1 = new HashSet<N>(lF2);
			lF2MS1.removeAll(lS1);
			return new UndirectedEdge<Set<N>>(lS2, lF2MS1);
		}
		;

		final boolean isC1 = lS1.containsAll(lF2);
		final boolean isC2 = lS2.containsAll(lF1);
		final boolean isStrictC = (lS1.size() > lF2.size()) && (lS2.size() > lF1.size());
		if (isC1 && isC2 && isStrictC)
		{
			final Set<N> lS2MF1 = new HashSet<N>(lS2);
			lS2MF1.removeAll(lF1);
			return new UndirectedEdge<Set<N>>(lF2, lS2MF1);
		}
		;

		final boolean isD1 = lS1.containsAll(lS2);
		final boolean isD2 = lF2.containsAll(lF1);
		final boolean isStrictD = (lS1.size() > lS2.size()) && (lF2.size() > lF1.size());
		if (isD1 && isD2 && isStrictD)
		{
			final Set<N> lF2MF1 = new HashSet<N>(lF2);
			lF2MF1.removeAll(lF1);
			return new UndirectedEdge<Set<N>>(lS2, lF2MF1);
		}
		;

		return pPowerEdge2;
	}

	public boolean isIntersectingPowerEdgePresent(Edge<Set<N>> pPowerEdge)
	{
		for (Edge<Set<N>> lPowerEdge : mPowerEdgeSet)
		{
			if (isIntersecting(pPowerEdge, lPowerEdge))
				return true;
		}
		return false;
	}

	public static <N> boolean isIntersecting(Edge<Set<N>> pPowerEdge1, Edge<Set<N>> pPowerEdge2)
	{
		final Set<N> lF1 = pPowerEdge1.getFirstNode();
		final Set<N> lS1 = pPowerEdge1.getSecondNode();
		final Set<N> lF2 = pPowerEdge2.getFirstNode();
		final Set<N> lS2 = pPowerEdge2.getSecondNode();

		return (setIntersects(lF1, lF2) && setIntersects(lS1, lS2))
				|| (setIntersects(lF1, lS2) && setIntersects(lS1, lF2));

	}

	public static <N> boolean isConnected(Edge<Set<N>> pPowerEdge1, Edge<Set<N>> pPowerEdge2)
	{
		final Set<N> lF1 = pPowerEdge1.getFirstNode();
		final Set<N> lS1 = pPowerEdge1.getSecondNode();
		final Set<N> lF2 = pPowerEdge2.getFirstNode();
		final Set<N> lS2 = pPowerEdge2.getSecondNode();

		return setIntersects(lF1, lF2) || setIntersects(lS1, lS2) || setIntersects(lF1, lS2)
				|| setIntersects(lS1, lF2);

	}

	public static <N> boolean isAdjacent(Edge<Set<N>> pPowerEdge1, Edge<Set<N>> pPowerEdge2)
	{
		final Set<N> lF1 = pPowerEdge1.getFirstNode();
		final Set<N> lS1 = pPowerEdge1.getSecondNode();
		final Set<N> lF2 = pPowerEdge2.getFirstNode();
		final Set<N> lS2 = pPowerEdge2.getSecondNode();

		boolean lA = setIntersects(lS1, lF2) && !setIntersects(lS1, lS2) && !setIntersects(lF1, lF2);
		boolean lB = setIntersects(lF1, lF2) && !setIntersects(lF1, lS2) && !setIntersects(lS1, lF2);
		boolean lC = setIntersects(lS1, lS2) && !setIntersects(lS1, lF2) && !setIntersects(lF1, lS2);
		boolean lD = setIntersects(lF1, lS2) && !setIntersects(lF1, lF2) && !setIntersects(lS1, lS2);

		return lA || lB || lC || lD;
	}

	private static final <N> boolean setIntersects(Set<N> pSet1, Set<N> pSet2)
	{
		return !Collections.disjoint(pSet1, pSet2);
	}

	void addPowerGraph(PowerGraph<N> pPowerGraph)
	{
		for (Edge<Set<N>> lPowerEdge : pPowerGraph.mPowerEdgeSet)
		{
			addPowerEdge(lPowerEdge);
		}
	}

	public Integer getNumberOfNodes()
	{
		return mNodeSet.size();
	}

	public Integer getNumberOfPowerNodes()
	{
		return mPowerNodeSet.size();
	}

	public Integer getNumberOfPowerEdges()
	{
		return mPowerEdgeSet.size();
	}

	public Set<N> getNodeSet()
	{
		return Collections.unmodifiableSet(mNodeSet);
	}

	public Set<Set<N>> getPowerNodeSet()
	{
		return Collections.unmodifiableSet(mPowerNodeSet);
	}

	public Set<Edge<Set<N>>> getPowerEdgeSet()
	{
		return Collections.unmodifiableSet(mPowerEdgeSet);
	}

	@Override
	public boolean equals(Object pObj)
	{
		if (pObj instanceof PowerGraph)
		{
			PowerGraph lGraph = (PowerGraph) pObj;

			return (lGraph.getPowerEdgeSet().equals(getPowerEdgeSet()))
					&& (lGraph.getPowerNodeSet().equals(getPowerNodeSet()));
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int lHashCode = mPowerEdgeSet.hashCode();
		return lHashCode;
	}

	@Override
	public String toString()
	{
		return "PowerNodeSet= " + mPowerNodeSet + " PowerEdgeSet= " + mPowerEdgeSet;
	}

	public Set<Set<N>> getClusterSet()
	{
		return mClusterSet;
	}

	public int getNumberOfEdges()
	{
		int lNumberOfEdges = 0;

		for (Edge<Set<N>> lEdge : mPowerEdgeSet)
			if (lEdge.getFirstNode().equals(lEdge.getSecondNode()))
			{
				lNumberOfEdges += (lEdge.getFirstNode().size() * (lEdge.getFirstNode().size() - 1)) / 2;
			}
			else
			{
				lNumberOfEdges += lEdge.getFirstNode().size() * lEdge.getSecondNode().size();
			}

		return lNumberOfEdges;
	}

	public double getEdgeReduction()
	{
		return 1 - ((double) (getNumberOfPowerEdges()) / (double) getNumberOfEdges());
	}

}
