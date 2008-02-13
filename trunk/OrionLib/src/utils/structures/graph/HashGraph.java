package utils.structures.graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import utils.structures.map.HashSetMap;
import utils.structures.map.SetMap;

public class HashGraph<N, E extends Edge<N>>	implements
																							Graph<N, E>,
																							Serializable
{
	private Set<N> mNodeSet;
	private Set<E> mEdgeSet;
	private SetMap<N, N> mNodeToFrontNeighboursSetMap;
	private SetMap<N, N> mNodeToBackNeighboursSetMap;
	private SetMap<N, E> mNodeToEdgeSetMap;

	public HashGraph()
	{
		this.mNodeSet = new HashSet<N>();
		this.mEdgeSet = new HashSet<E>();
		this.mNodeToFrontNeighboursSetMap = new HashSetMap<N, N>();
		this.mNodeToBackNeighboursSetMap = new HashSetMap<N, N>();
		this.mNodeToEdgeSetMap = new HashSetMap<N, E>();
	}

	public HashGraph(final Graph<N, E> pGraph)
	{
		this();
		addGraph(pGraph);
	}

	public void addNode(final N pNode)
	{
		this.mNodeSet.add(pNode);
		this.mNodeToFrontNeighboursSetMap.put(pNode);
		this.mNodeToBackNeighboursSetMap.put(pNode);
		this.mNodeToEdgeSetMap.put(pNode);
	}

	public void addEdge(final E pEdge)
	{
		final N lFirstNode = pEdge.getFirstNode();
		final N lSecondNode = pEdge.getSecondNode();

		addNode(lFirstNode);
		addNode(lSecondNode);
		this.mEdgeSet.add(pEdge);
		this.mNodeToFrontNeighboursSetMap.put(lFirstNode, lSecondNode);
		this.mNodeToBackNeighboursSetMap.put(lSecondNode, lFirstNode);
		this.mNodeToEdgeSetMap.put(lFirstNode, pEdge);
		this.mNodeToEdgeSetMap.put(lSecondNode, pEdge);
	}

	public void addGraph(final Graph<N, E> pGraph)
	{
		for (final N lNode : pGraph.getNodeSet())
			addNode(lNode);
		for (final E lEdge : pGraph.getEdgeSet())
			addEdge(lEdge);
	}

	public void removeNode(final N pNode)
	{
		if (this.mNodeSet.remove(pNode))
		{
			final Set<N> lFrontNeighboursNodeSet = this.mNodeToFrontNeighboursSetMap.get(pNode);
			if (lFrontNeighboursNodeSet != null)
				for (final N lNode : lFrontNeighboursNodeSet)
					if (!lNode.equals(pNode))
					{
						final Set<N> lNodeSet = this.mNodeToBackNeighboursSetMap.get(lNode);
						if (lNodeSet != null)
							lNodeSet.remove(pNode);
					}
			this.mNodeToFrontNeighboursSetMap.remove(pNode);

			final Set<N> lBackNeighboursNodeSet = this.mNodeToBackNeighboursSetMap.get(pNode);
			if (lBackNeighboursNodeSet != null)
				for (final N lNode : lBackNeighboursNodeSet)
					if (!lNode.equals(pNode))
					{
						final Set<N> lNodeSet = this.mNodeToFrontNeighboursSetMap.get(lNode);
						if (lNodeSet != null)
							lNodeSet.remove(pNode);
					}
			this.mNodeToBackNeighboursSetMap.remove(pNode);

			Set<E> lDeletedEdgeSet = this.mNodeToEdgeSetMap.get(pNode);
			lDeletedEdgeSet = lDeletedEdgeSet == null	? new HashSet<E>()
																								: lDeletedEdgeSet;

			this.mEdgeSet.removeAll(lDeletedEdgeSet);
			this.mNodeToEdgeSetMap.clear(pNode);
		}
	}

	public void removeAllNodes(final Set<N> pNodeSet)
	{
		for (final N lNode : pNodeSet)
			removeNode(lNode);
	}

	public void removeEdge(final N pFirstNode, final N pSecondNode)
	{
		final Set<E> lCandidateEdgeList = this.mNodeToEdgeSetMap.get(pFirstNode);
		for (final E lEdge : lCandidateEdgeList)
			if (lEdge.getFirstNode().equals(pFirstNode) && lEdge.getSecondNode()
																													.equals(pSecondNode))
			{
				this.mEdgeSet.remove(lEdge);
				final N lFirstNode = lEdge.getFirstNode();
				final N lSecondNode = lEdge.getSecondNode();

				this.mNodeToFrontNeighboursSetMap.get(lFirstNode).remove(lSecondNode);
				this.mNodeToBackNeighboursSetMap.get(lSecondNode).remove(lFirstNode);

				this.mNodeToEdgeSetMap.get(lFirstNode).remove(lEdge);
				this.mNodeToEdgeSetMap.get(lSecondNode).remove(lEdge);
				break;
			}

	}

	public int getNumberOfNodes()
	{
		return this.mNodeSet.size();
	}

	public int getNumberOfEdges()
	{
		return this.mEdgeSet.size();
	}

	public Set<N> getNodeSet()
	{
		return Collections.unmodifiableSet(this.mNodeSet);
	}

	public Set<E> getEdgeSet()
	{
		return Collections.<E> unmodifiableSet(this.mEdgeSet);
	}

	public Set<N> getNodeNeighbours(final N pNode)
	{
		final Set<N> lSet = new HashSet<N>();
		final Set<N> lSetFront = this.mNodeToFrontNeighboursSetMap.get(pNode);
		final Set<N> lSetBack = this.mNodeToBackNeighboursSetMap.get(pNode);
		if (lSetFront != null)
			lSet.addAll(lSetFront);
		if (lSetBack != null)
			lSet.addAll(lSetBack);
		return lSet;
	}

	public Set<N> getPositiveNodeNeighbours(final N pNode)
	{
		return Collections.<N> unmodifiableSet(this.mNodeToFrontNeighboursSetMap.get(pNode));
	}

	public Set<N> getNegativeNodeNeighbours(final N pNode)
	{
		Set<N> lSet = this.mNodeToBackNeighboursSetMap.get(pNode);
		lSet = lSet == null ? Collections.<N> emptySet() : lSet;
		return Collections.<N> unmodifiableSet(lSet);
	}

	public Set<N> getNodeNeighbours(final Collection<N> pNodeCollection)
	{
		final Set<N> lNodeSet = new HashSet<N>();
		for (final N lNode : pNodeCollection)
		{
			final Set<N> lNewNodeSet = getNodeNeighbours(lNode);
			lNodeSet.addAll(lNewNodeSet);
		}
		lNodeSet.removeAll(pNodeCollection);
		return lNodeSet;
	}

	public Set<N> getNodeNeighbours(final N pNode, final int pDepth)
	{
		if (pDepth < 1)
			return Collections.<N> emptySet();
		if (pDepth == 1)
			return getNodeNeighbours(pNode);

		final Set<N> lNodeSet = new HashSet<N>();
		for (final N lNode : getNodeNeighbours(pNode))
			lNodeSet.addAll(getNodeNeighbours(lNode, pDepth - 1));
		lNodeSet.addAll(getNodeNeighbours(pNode));
		lNodeSet.remove(pNode);
		return lNodeSet;
	}

	public boolean isEdge(final N pNode1, final N pNode2)
	{
		return getNodeNeighbours(pNode1).contains(pNode2);
	}

	public Set<E> getNeighbouringEdges(final N pNode)
	{
		return Collections.unmodifiableSet(this.mNodeToEdgeSetMap.get(pNode));
	}

	public Graph<N, E> extractStrictSubGraph(final Set<N> pNodeSet)
	{
		final HashGraph<N, E> lGraph = new HashGraph<N, E>();

		for (final N lNode : pNodeSet)
		{
			lGraph.addNode(lNode);
			final Set<E> lEdgeSet = getNeighbouringEdges(lNode);
			for (final E lEdge : lEdgeSet)
			{
				final N lFirstNode = lEdge.getFirstNode();
				final N lSecondNode = lEdge.getSecondNode();
				if (pNodeSet.contains(lFirstNode) && pNodeSet.contains(lSecondNode))
				{
					lGraph.addNode(lFirstNode);
					lGraph.addNode(lSecondNode);
					lGraph.addEdge(lEdge);
				}
			}
		}
		return lGraph;
	}

	public Graph extractSubGraph(final Set<N> pNodeSet)
	{
		final HashGraph<N, E> lGraph = new HashGraph<N, E>();

		for (final N lNode : pNodeSet)
		{
			lGraph.addNode(lNode);
			final Set<E> lEdgeSet = getNeighbouringEdges(lNode);
			for (final E lEdge : lEdgeSet)
			{
				final N lFirstNode = lEdge.getFirstNode();
				final N lSecondNode = lEdge.getSecondNode();
				if (pNodeSet.contains(lFirstNode) || pNodeSet.contains(lSecondNode))
				{
					lGraph.addNode(lFirstNode);
					lGraph.addNode(lSecondNode);
					lGraph.addEdge(lEdge);
				}
			}
		}
		return lGraph;
	}

	@Override
	public boolean equals(final Object pObj)
	{
		if (pObj instanceof HashGraph)
		{
			final HashGraph<N, E> lGraph = (HashGraph<N, E>) pObj;
			return (lGraph.getEdgeSet().equals(getEdgeSet())) && (lGraph.getNodeSet().equals(getNodeSet()));
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int lHashCode = this.mEdgeSet.hashCode();
		lHashCode += this.mNodeSet.hashCode();

		return lHashCode;
	}

	@Override
	public String toString()
	{
		return "NodeSet= " + this.mNodeSet + " EdgeSet= " + this.mEdgeSet;
	}

	public Double getAverageDegree()
	{
		return ((double) 2 * getNumberOfEdges()) / getNumberOfNodes();
	}

}
