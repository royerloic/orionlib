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
		public int compare(final Edge<Set<N>> pPowerEdge1, final Edge<Set<N>> pPowerEdge2)
		{
			final int lSize1 = pPowerEdge1.getFirstNode().size() * pPowerEdge1.getSecondNode().size();
			final int lSize2 = pPowerEdge2.getFirstNode().size() * pPowerEdge2.getSecondNode().size();
			return -(lSize1 - lSize2); // when sorting the result is in descending
		}
	}
	private final PowerEdgeComparator<N>	cPowerEdgeComparator	= new PowerEdgeComparator<N>();

	private Set<Set<N>>							mPowerNodeSet;

	private Set<Edge<Set<N>>>				mPowerEdgeSet;

	private Set<N>									mNodeSet;

	private Set<Set<N>>							mClusterSet;

	public PowerGraph()
	{
		super();
		this.mPowerNodeSet = new HashSet<Set<N>>();
		this.mPowerEdgeSet = new HashSet<Edge<Set<N>>>();
		this.mNodeSet = new HashSet<N>();
		this.mClusterSet = new HashSet<Set<N>>();
	}

	public void addNode(final N pNode)
	{
		this.mNodeSet.add(pNode);
	}

	public void addPowerNode(final Set<N> pNodeSet)
	{
		this.mNodeSet.addAll(pNodeSet);
		this.mPowerNodeSet.add(pNodeSet);
	}

	public void addCluster(final Set<N> pNodeSet)
	{
		this.mClusterSet.add(pNodeSet);
		this.mNodeSet.addAll(pNodeSet);
	}

	private final List<Edge<Set<N>>>	mDelayedPowerEdgeList	= new ArrayList<Edge<Set<N>>>();

	public void addPowerEdgeDelayed(final Edge<Set<N>> pPowerEdge)
	{
		this.mDelayedPowerEdgeList.add(pPowerEdge);
	}

	public void commitDelayedEdges()
	{
		Collections.<Edge<Set<N>>> sort(this.mDelayedPowerEdgeList, this.cPowerEdgeComparator);
		for (final Edge<Set<N>> lEdge : this.mDelayedPowerEdgeList)
			if (!isIntersectingPowerEdgePresent(lEdge))
				addPowerEdge(lEdge);
		this.mDelayedPowerEdgeList.clear();
	}

	public void addPowerEdge(final Edge<Set<N>> pPowerEdge)
	{
		final Set<N> lFirstPowerNode = pPowerEdge.getFirstNode();
		final Set<N> lSecondPowerNode = pPowerEdge.getSecondNode();

		this.mNodeSet.addAll(lFirstPowerNode);
		this.mNodeSet.addAll(lSecondPowerNode);

		addPowerNode(lFirstPowerNode);
		addPowerNode(lSecondPowerNode);

		final Edge<Set<N>> lPowerEdge = new UndirectedEdge<Set<N>>(lFirstPowerNode, lSecondPowerNode);
		this.mPowerEdgeSet.add(lPowerEdge);

	}

	public void removePowerEdge(final Edge<Set<N>> pPowerEdge)
	{
		this.mPowerNodeSet.remove(pPowerEdge.getFirstNode());
		this.mPowerNodeSet.remove(pPowerEdge.getSecondNode());
		this.mPowerEdgeSet.remove(pPowerEdge);
		this.mNodeSet.clear();
		for (final Set<N> lNodeSet : this.mPowerNodeSet)
			this.mNodeSet.addAll(lNodeSet);
		for (final Set<N> lNodeSet : this.mClusterSet)
			this.mNodeSet.addAll(lNodeSet);
	}

	public boolean strictlyIncludedIn(final Edge<Set<N>> pPowerEdge1, final Edge<Set<N>> pPowerEdge2)
	{
		if (pPowerEdge1.equals(pPowerEdge2))
			return false;
		else
			return includedIn(pPowerEdge1, pPowerEdge2);
	}

	public boolean includedIn(final Edge<Set<N>> pPowerEdge1, final Edge<Set<N>> pPowerEdge2)
	{
		final Set<N> lF1 = pPowerEdge1.getFirstNode();
		final Set<N> lS1 = pPowerEdge1.getSecondNode();
		final Set<N> lF2 = pPowerEdge2.getFirstNode();
		final Set<N> lS2 = pPowerEdge2.getSecondNode();
		final boolean isFirstIncludedA = lF2.containsAll(lF1);
		final boolean isSecondIncludedA = lS2.containsAll(lS1);

		if (isFirstIncludedA && isSecondIncludedA)
			return true;

		final boolean isFirstIncludedB = lF2.containsAll(lS1);
		final boolean isSecondIncludedB = lS2.containsAll(lF1);
		if (isFirstIncludedB && isSecondIncludedB)
			return (true);

		return false;
	}

	public boolean isIntersectingPowerEdgePresent(final Edge<Set<N>> pPowerEdge)
	{
		for (final Edge<Set<N>> lPowerEdge : this.mPowerEdgeSet)
			if (isIntersecting(pPowerEdge, lPowerEdge))
				return true;
		return false;
	}

	public static <N> boolean isIntersecting(final Edge<Set<N>> pPowerEdge1, final Edge<Set<N>> pPowerEdge2)
	{
		final Set<N> lF1 = pPowerEdge1.getFirstNode();
		final Set<N> lS1 = pPowerEdge1.getSecondNode();
		final Set<N> lF2 = pPowerEdge2.getFirstNode();
		final Set<N> lS2 = pPowerEdge2.getSecondNode();

		return (setIntersects(lF1, lF2) && setIntersects(lS1, lS2))
				|| (setIntersects(lF1, lS2) && setIntersects(lS1, lF2));

	}

	public static <N> boolean isConnected(final Edge<Set<N>> pPowerEdge1, final Edge<Set<N>> pPowerEdge2)
	{
		final Set<N> lF1 = pPowerEdge1.getFirstNode();
		final Set<N> lS1 = pPowerEdge1.getSecondNode();
		final Set<N> lF2 = pPowerEdge2.getFirstNode();
		final Set<N> lS2 = pPowerEdge2.getSecondNode();

		return setIntersects(lF1, lF2) || setIntersects(lS1, lS2) || setIntersects(lF1, lS2)
				|| setIntersects(lS1, lF2);

	}

	public static <N> boolean isAdjacent(final Edge<Set<N>> pPowerEdge1, final Edge<Set<N>> pPowerEdge2)
	{
		final Set<N> lF1 = pPowerEdge1.getFirstNode();
		final Set<N> lS1 = pPowerEdge1.getSecondNode();
		final Set<N> lF2 = pPowerEdge2.getFirstNode();
		final Set<N> lS2 = pPowerEdge2.getSecondNode();

		final boolean lA = setIntersects(lS1, lF2) && !setIntersects(lS1, lS2) && !setIntersects(lF1, lF2);
		final boolean lB = setIntersects(lF1, lF2) && !setIntersects(lF1, lS2) && !setIntersects(lS1, lF2);
		final boolean lC = setIntersects(lS1, lS2) && !setIntersects(lS1, lF2) && !setIntersects(lF1, lS2);
		final boolean lD = setIntersects(lF1, lS2) && !setIntersects(lF1, lF2) && !setIntersects(lS1, lS2);

		return lA || lB || lC || lD;
	}

	private static final <N> boolean setIntersects(final Set<N> pSet1, final Set<N> pSet2)
	{
		return !Collections.disjoint(pSet1, pSet2);
	}

	void addPowerGraph(final PowerGraph<N> pPowerGraph)
	{
		for (final Edge<Set<N>> lPowerEdge : pPowerGraph.mPowerEdgeSet)
			addPowerEdge(lPowerEdge);
	}

	public Integer getNumberOfNodes()
	{
		return this.mNodeSet.size();
	}

	public Integer getNumberOfPowerNodes()
	{
		return this.mPowerNodeSet.size();
	}

	public Integer getNumberOfPowerEdges()
	{
		return this.mPowerEdgeSet.size();
	}

	public Set<N> getNodeSet()
	{
		return Collections.unmodifiableSet(this.mNodeSet);
	}

	public Set<Set<N>> getPowerNodeSet()
	{
		return Collections.unmodifiableSet(this.mPowerNodeSet);
	}

	public Set<Edge<Set<N>>> getPowerEdgeSet()
	{
		return Collections.unmodifiableSet(this.mPowerEdgeSet);
	}

	@Override
	public boolean equals(final Object pObj)
	{
		if (pObj instanceof PowerGraph)
		{
			final PowerGraph lGraph = (PowerGraph) pObj;

			return (lGraph.getPowerEdgeSet().equals(getPowerEdgeSet()))
					&& (lGraph.getPowerNodeSet().equals(getPowerNodeSet()));
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		final int lHashCode = this.mPowerEdgeSet.hashCode();
		return lHashCode;
	}

	@Override
	public String toString()
	{
		return "PowerNodeSet= " + this.mPowerNodeSet + " PowerEdgeSet= " + this.mPowerEdgeSet;
	}

	public Set<Set<N>> getClusterSet()
	{
		return this.mClusterSet;
	}

	public int getNumberOfEdges()
	{
		int lNumberOfEdges = 0;

		for (final Edge<Set<N>> lEdge : this.mPowerEdgeSet)
			if (lEdge.getFirstNode().equals(lEdge.getSecondNode()))
			{
				if (lEdge.getFirstNode().size() == 1)
					lNumberOfEdges += 1;
				else
					lNumberOfEdges += ((lEdge.getFirstNode().size() * (lEdge.getFirstNode().size() - 1))) / 2;
			}
			else
				lNumberOfEdges += lEdge.getFirstNode().size() * lEdge.getSecondNode().size();

		return lNumberOfEdges;
	}
	
	public double getNumberOfEdgesForEdgeReduction()
	{
		int lNumberOfEdges = 0;

		for (final Edge<Set<N>> lEdge : this.mPowerEdgeSet)
			if (lEdge.getFirstNode().equals(lEdge.getSecondNode()))
			{
				if (lEdge.getFirstNode().size() == 1)
					lNumberOfEdges += 0;
				else
					lNumberOfEdges += (lEdge.getFirstNode().size() * (lEdge.getFirstNode().size() - 1))/2;
			}
			else
				lNumberOfEdges += lEdge.getFirstNode().size() * lEdge.getSecondNode().size();

		return lNumberOfEdges;
	}

	public int getMaximalPowerEdgeSize()
	{
		int lMaxNumberOfEdges = 0;
		for (final Edge<Set<N>> lEdge : this.mPowerEdgeSet)
			if (lEdge.getFirstNode().equals(lEdge.getSecondNode()))
				lMaxNumberOfEdges = Math.max(lMaxNumberOfEdges, (lEdge.getFirstNode().size() * (lEdge.getFirstNode()
						.size() - 1)) / 2);
			else
				lMaxNumberOfEdges = Math.max(lMaxNumberOfEdges, lEdge.getFirstNode().size()
						* lEdge.getSecondNode().size());

		return lMaxNumberOfEdges;
	}

	public Double getEdgeReduction()
	{
		return 1 - ((double) (getNumberOfPowerEdges()) / (double) getNumberOfEdges());
	}

	public List<Set<N>> getAllPowerNodeContaining(final N pNode)
	{
		final List<Set<N>> lList = new ArrayList<Set<N>>();
		for (final Set<N> lPowerNode : this.mPowerNodeSet)
			if (lPowerNode.contains(pNode))
				lList.add(lPowerNode);

		return lList;
	}

	public boolean isPowerEdge(final Set<N> pPowerNode1, final Set<N> pPowerNode2)
	{
		final Edge<Set<N>> lPowerEdge = new UndirectedEdge<Set<N>>(pPowerNode1, pPowerNode2);
		final boolean isPowerEdge = this.mPowerEdgeSet.contains(lPowerEdge);
		return isPowerEdge;
	}

	public Double getAverageDegree()
	{
		return ((double)2*getNumberOfEdges())/getNumberOfNodes();
	}
	
	public Double getScaleFreeIndex()
	{
		return Math.log(getEdgeReduction())+(2.0/3.0)*Math.log(getAverageDegree());
	}

}
