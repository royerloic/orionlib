package org.royerloic.structures.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.royerloic.structures.HashSetMap;
import org.royerloic.structures.SetMap;

public class HashGraph<N, E extends Edge<N>> implements Graph<N, E>
{
	private Set<N>				mNodeSet;
	private Set<E>				mEdgeSet;
	private SetMap<N, N>	mNodeToFrontNeighboursSetMap;
	private SetMap<N, N>	mNodeToBackNeighboursSetMap;
	private SetMap<N, E>	mNodeToEdgeSetMap;

	public HashGraph()
	{
		mNodeSet = new HashSet<N>();
		mEdgeSet = new HashSet<E>();
		mNodeToFrontNeighboursSetMap = new HashSetMap<N, N>();
		mNodeToBackNeighboursSetMap = new HashSetMap<N, N>();
		mNodeToEdgeSetMap = new HashSetMap<N, E>();
	}

	public HashGraph(Graph<N, E> pGraph)
	{
		this();
		addGraph(pGraph);
	}

	public void addNode(N pNode)
	{
		mNodeSet.add(pNode);
		mNodeToFrontNeighboursSetMap.put(pNode);
		mNodeToBackNeighboursSetMap.put(pNode);
		mNodeToEdgeSetMap.put(pNode);
	}

	public void addEdge(E pEdge)
	{
		N lFirstNode = pEdge.getFirstNode();
		N lSecondNode = pEdge.getSecondNode();

		addNode(lFirstNode);
		addNode(lSecondNode);
		mEdgeSet.add(pEdge);
		mNodeToFrontNeighboursSetMap.put(lFirstNode, lSecondNode);
		mNodeToBackNeighboursSetMap.put(lSecondNode, lFirstNode);
		mNodeToEdgeSetMap.put(lFirstNode, pEdge);
		mNodeToEdgeSetMap.put(lSecondNode, pEdge);
	}

	public void addGraph(Graph<N, E> pGraph)
	{
		for (N lNode : pGraph.getNodeSet())
		{
			addNode(lNode);
		}
		for (E lEdge : pGraph.getEdgeSet())
		{
			addEdge(lEdge);
		}
	}

	public void removeNode(N pNode)
	{
		if (mNodeSet.remove(pNode))
		{
			Set<N> lFrontNeighboursNodeSet = mNodeToFrontNeighboursSetMap.get(pNode);
			if (lFrontNeighboursNodeSet != null)
				for (N lNode : lFrontNeighboursNodeSet)
					if (!lNode.equals(pNode))
					{
						Set<N> lNodeSet = mNodeToBackNeighboursSetMap.get(lNode);
						if (lNodeSet != null)
							lNodeSet.remove(pNode);
					}
			mNodeToFrontNeighboursSetMap.remove(pNode);

			Set<N> lBackNeighboursNodeSet = mNodeToBackNeighboursSetMap.get(pNode);
			if (lBackNeighboursNodeSet != null)
				for (N lNode : lBackNeighboursNodeSet)
					if (!lNode.equals(pNode))
					{
						Set<N> lNodeSet = mNodeToFrontNeighboursSetMap.get(lNode);
						if (lNodeSet != null)
							lNodeSet.remove(pNode);
					}
			mNodeToBackNeighboursSetMap.remove(pNode);

			Set<E> lDeletedEdgeSet = mNodeToEdgeSetMap.get(pNode);
			lDeletedEdgeSet = lDeletedEdgeSet == null ? new HashSet<E>() : lDeletedEdgeSet;

			mEdgeSet.removeAll(lDeletedEdgeSet);
			mNodeToEdgeSetMap.clear(pNode);
		}
	}

	public void removeAllNodes(Set<N> pNodeSet)
	{
		for (N lNode : pNodeSet)
		{
			removeNode(lNode);
		}
	}

	public void removeEdge(N pFirstNode, N pSecondNode)
	{
		Set<E> lCandidateEdgeList = mNodeToEdgeSetMap.get(pFirstNode);
		for (E lEdge : lCandidateEdgeList)
			if (lEdge.getFirstNode().equals(pFirstNode) && lEdge.getSecondNode().equals(pSecondNode))
			{
				mEdgeSet.remove(lEdge);
				N lFirstNode = lEdge.getFirstNode();
				N lSecondNode = lEdge.getSecondNode();

				mNodeToFrontNeighboursSetMap.get(lFirstNode).remove(lSecondNode);
				mNodeToBackNeighboursSetMap.get(lSecondNode).remove(lFirstNode);

				mNodeToEdgeSetMap.get(lFirstNode).remove(lEdge);
				mNodeToEdgeSetMap.get(lSecondNode).remove(lEdge);
				break;
			}

	}

	public int getNumberOfNodes()
	{
		return mNodeSet.size();
	}

	public int getNumberOfEdges()
	{
		return mEdgeSet.size();
	}

	public Set<N> getNodeSet()
	{
		return Collections.unmodifiableSet(mNodeSet);
	}

	public Set<E> getEdgeSet()
	{
		return Collections.<E> unmodifiableSet(mEdgeSet);
	}

	public Set<N> getNodeNeighbours(N pNode)
	{
		Set<N> lSet = new HashSet<N>();
		Set<N> lSetFront = mNodeToFrontNeighboursSetMap.get(pNode);
		Set<N> lSetBack = mNodeToBackNeighboursSetMap.get(pNode);
		if (lSetFront != null)
			lSet.addAll(lSetFront);
		if (lSetBack != null)
			lSet.addAll(lSetBack);
		return lSet;
	}

	public Set<N> getPositiveNodeNeighbours(N pNode)
	{
		return Collections.<N> unmodifiableSet(mNodeToFrontNeighboursSetMap.get(pNode));
	}

	public Set<N> getNegativeNodeNeighbours(N pNode)
	{
		Set<N> lSet = mNodeToBackNeighboursSetMap.get(pNode);
		lSet = lSet == null ? Collections.<N> emptySet() : lSet;
		return Collections.<N> unmodifiableSet(lSet);
	}

	public Set<N> getNodeNeighbours(Collection<N> pNodeCollection)
	{
		Set<N> lNodeSet = new HashSet<N>();
		for (N lNode : pNodeCollection)
		{
			Set<N> lNewNodeSet = getNodeNeighbours(lNode);
			lNodeSet.addAll(lNewNodeSet);
		}
		lNodeSet.removeAll(pNodeCollection);
		return lNodeSet;
	}

	public Set<N> getNodeNeighbours(N pNode, int pDepth)
	{
		if (pDepth < 1)
		{
			return Collections.<N> emptySet();
		}
		if (pDepth == 1)
		{
			return getNodeNeighbours(pNode);
		}

		Set<N> lNodeSet = new HashSet<N>();
		for (N lNode : getNodeNeighbours(pNode))
		{
			lNodeSet.addAll(getNodeNeighbours(lNode, pDepth - 1));
		}
		lNodeSet.addAll(getNodeNeighbours(pNode));
		lNodeSet.remove(pNode);
		return lNodeSet;
	}

	public boolean isEdge(N pNode1, N pNode2)
	{
		return getNodeNeighbours(pNode1).contains(pNode2);
	}

	public Set<E> getNeighbouringEdges(N pNode)
	{
		return Collections.unmodifiableSet(mNodeToEdgeSetMap.get(pNode));
	}

	public Graph<N, E> extractStrictSubGraph(Set<N> pNodeSet)
	{
		HashGraph<N, E> lGraph = new HashGraph<N, E>();

		for (N lNode : pNodeSet)
		{
			lGraph.addNode(lNode);
			Set<E> lEdgeSet = getNeighbouringEdges(lNode);
			for (E lEdge : lEdgeSet)
			{
				N lFirstNode = lEdge.getFirstNode();
				N lSecondNode = lEdge.getSecondNode();
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

	public Graph extractSubGraph(Set<N> pNodeSet)
	{
		HashGraph<N, E> lGraph = new HashGraph<N, E>();

		for (N lNode : pNodeSet)
		{
			lGraph.addNode(lNode);
			Set<E> lEdgeSet = getNeighbouringEdges(lNode);
			for (E lEdge : lEdgeSet)
			{
				N lFirstNode = lEdge.getFirstNode();
				N lSecondNode = lEdge.getSecondNode();
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
	public boolean equals(Object pObj)
	{
		if (pObj instanceof HashGraph)
		{
			HashGraph<N, E> lGraph = (HashGraph<N, E>) pObj;
			return (lGraph.getEdgeSet().equals(getEdgeSet())) && (lGraph.getNodeSet().equals(getNodeSet()));
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int lHashCode = mEdgeSet.hashCode();
		lHashCode += mNodeSet.hashCode();

		return lHashCode;
	}

	@Override
	public String toString()
	{
		return "NodeSet= " + mNodeSet + " EdgeSet= " + mEdgeSet;
	}

	public Double getAverageDegree()
	{
		return ((double)2*getNumberOfEdges())/getNumberOfNodes();
	}

}
