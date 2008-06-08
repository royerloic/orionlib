package utils.structures.fast.powergraph;

import java.util.ArrayList;
import java.util.HashMap;

import utils.structures.fast.graph.FastIntegerDirectedGraph;
import utils.structures.fast.graph.FastIntegerGraph;
import utils.structures.fast.set.FastBoundedIntegerSet;

public class FastIntegerPowerGraph
{
	private static final long serialVersionUID = 1L;

	FastBoundedIntegerSet mNodesSet;
	ArrayList<FastBoundedIntegerSet> mId2PowerNode;
	HashMap<FastBoundedIntegerSet, Integer> mPowerNode2Id;
	FastIntegerDirectedGraph mHierarchyGraph;
	FastIntegerGraph mPowerEgdesGraph;

	public FastIntegerPowerGraph(	int pNumberOfNodes,
																int pNumberOfPowerNodes,
																int pNumberOfPowerEdges)
	{
		super();
		mNodesSet = new FastBoundedIntegerSet(pNumberOfNodes);
		mId2PowerNode = new ArrayList<FastBoundedIntegerSet>(pNumberOfPowerNodes);
		mPowerNode2Id = new HashMap<FastBoundedIntegerSet, Integer>(pNumberOfPowerNodes);
		mPowerEgdesGraph = new FastIntegerGraph(pNumberOfPowerNodes);
		mHierarchyGraph = new FastIntegerDirectedGraph(pNumberOfPowerNodes);

		mPowerNode2Id.put(mNodesSet, 0);
		mId2PowerNode.add(mNodesSet);
		mHierarchyGraph.addNode();
	}

	public FastIntegerPowerGraph()
	{
		this(0, 0, 0);
	}

	public Integer addPowerNode(FastBoundedIntegerSet pPowerNode)
	{
		Integer lPowerNodeId = mPowerNode2Id.get(pPowerNode);
		if (lPowerNodeId == null)
		{
			lPowerNodeId = insertInDirectedGraphRecusive(0, pPowerNode);
		}
		return lPowerNodeId;
	}

	private Integer insertInDirectedGraphRecusive(int pInsertPowerNodeId,
																								FastBoundedIntegerSet pPowerNode)
	{
		{
			FastBoundedIntegerSet childrenids = mHierarchyGraph.getOutgoingNodeNeighbours(pInsertPowerNodeId);
			FastBoundedIntegerSet childtodisplace = new FastBoundedIntegerSet();
			Integer lPowerNodeId = null;

			for (int childid : childrenids)
			{
				FastBoundedIntegerSet child = mId2PowerNode.get(childid);
				final int relationship = FastBoundedIntegerSet.relationship(child,
																																		pPowerNode);

				if (relationship == 2) // strictly intersecting
				{
					return null; // cannot insert
				}
				else if (relationship == 3) // equal
				{
					return childid;
				}
				else if (relationship == 1) // child contains pPowerNode
				{
					return insertInDirectedGraphRecusive(childid, pPowerNode);
				}
				else if (relationship == -1) // pPowerNode contains child
				{
					childtodisplace.add(childid);
				}
			}

			mNodesSet.union(pPowerNode);
			lPowerNodeId = mPowerNode2Id.size();
			mId2PowerNode.add(pPowerNode);
			mPowerNode2Id.put(pPowerNode, lPowerNodeId);
			mPowerEgdesGraph.addNodesUpTo(lPowerNodeId);
			mHierarchyGraph.addEdge(pInsertPowerNodeId, lPowerNodeId);

			for (int child : childtodisplace)
			{
				mHierarchyGraph.removeEdge(pInsertPowerNodeId, child);
				mHierarchyGraph.addEdge(lPowerNodeId, child);
			}

			return lPowerNodeId;
		}
	}

	public FastBoundedIntegerSet getPowerNodeById(int pPowerNodeId)
	{
		return mId2PowerNode.get(pPowerNodeId);
	}

	public Integer getIdForPowerNode(FastBoundedIntegerSet pPowerNode)
	{
		return mPowerNode2Id.get(pPowerNode);
	}

	public boolean isPowerNode(FastBoundedIntegerSet pPowerNode)
	{
		Integer lId = mPowerNode2Id.get(pPowerNode);
		return mHierarchyGraph.isNode(lId);
	}

	public boolean isPowerNode(int pPowerNodeId)
	{
		return mHierarchyGraph.isNode(pPowerNodeId);
	}

	public FastBoundedIntegerSet getPowerNodeIdSet()
	{
		return mPowerEgdesGraph.getNodeSet();
	}

	public int getNumberOfPowerNodes()
	{
		return mPowerEgdesGraph.getNumberOfNodes();
	}

	public int getNumberOfNodes()
	{
		return mNodesSet.size();
	}

	public void addPowerEdge(int pPowerNodeId1, int pPowerNodeId2)
	{
		mPowerEgdesGraph.addEdge(pPowerNodeId1, pPowerNodeId2);
	}

	public void addPowerEdge(	FastBoundedIntegerSet pPowerNode1,
														FastBoundedIntegerSet pPowerNode2)
	{
		Integer lId1 = addPowerNode(pPowerNode1);
		Integer lId2 = addPowerNode(pPowerNode2);
		mPowerEgdesGraph.addEdge(lId1, lId2);
	}

	public void removePowerEdge(int pPowerNodeId1, int pPowerNodeId2)
	{
		mPowerEgdesGraph.removeEdge(pPowerNodeId1, pPowerNodeId2);
	}

	public void removePowerEdge(FastBoundedIntegerSet pPowerNode1,
															FastBoundedIntegerSet pPowerNode2)
	{
		Integer lId1 = addPowerNode(pPowerNode1);
		Integer lId2 = addPowerNode(pPowerNode2);
		mPowerEgdesGraph.removeEdge(lId1, lId2);
	}

	public boolean isPowerEdge(	FastBoundedIntegerSet pPowerNode1,
															FastBoundedIntegerSet pPowerNode2)
	{
		Integer lId1 = mPowerNode2Id.get(pPowerNode1);
		Integer lId2 = mPowerNode2Id.get(pPowerNode2);
		if (lId1 == null || lId2 == null)
		{
			return false;
		}
		else
		{
			return mPowerEgdesGraph.isEdge(lId1, lId2);
		}
	}

	public boolean isPowerEdge(int pPowerNodeId1, int pPowerNodeId2)
	{
		return mPowerEgdesGraph.isEdge(pPowerNodeId1, pPowerNodeId2);
	}

	public int getNumberOfPowerEdges()
	{
		return mPowerEgdesGraph.getNumberOfEdges();
	}

	public FastBoundedIntegerSet getPowerNodeChildrenOf(int pPowerNodeId)
	{
		return mHierarchyGraph.getOutgoingNodeNeighbours(pPowerNodeId);
	}

	public FastBoundedIntegerSet getPowerNodeChildrenOf(FastBoundedIntegerSet pPowerNode)
	{
		return mHierarchyGraph.getOutgoingNodeNeighbours(mPowerNode2Id.get(pPowerNode));
	}

	public FastBoundedIntegerSet getPowerNodeDescendentsOf(int pPowerNodeId)
	{
		return mHierarchyGraph.getOutgoingTransitiveClosure(pPowerNodeId);
	}

	public FastBoundedIntegerSet getPowerNodeParentsOf(int pPowerNodeId)
	{
		return mHierarchyGraph.getIncommingNodeNeighbours(pPowerNodeId);
	}

	public FastBoundedIntegerSet getPowerNodeParentOf(FastBoundedIntegerSet pPowerNode)
	{
		return mHierarchyGraph.getIncommingNodeNeighbours(mPowerNode2Id.get(pPowerNode));
	}

	public FastBoundedIntegerSet getPowerNodeAncestorsOf(int pPowerNodeId)
	{
		return mHierarchyGraph.getIncommingTransitiveClosure(pPowerNodeId);
	}

	public FastBoundedIntegerSet getDirectPowerNodeNeighbors(int pPowerNodeId)
	{
		return mPowerEgdesGraph.getNodeNeighbours(pPowerNodeId);
	}

	public FastBoundedIntegerSet getAllPowerNodeNeighbors(int pPowerNodeId)
	{
		FastBoundedIntegerSet lDirectNeighbors = mPowerEgdesGraph.getNodeNeighbours(pPowerNodeId);
		FastBoundedIntegerSet lDescendents = mHierarchyGraph.getOutgoingTransitiveClosure(pPowerNodeId);
		FastBoundedIntegerSet lAncestors = mHierarchyGraph.getIncommingTransitiveClosure(pPowerNodeId);

		FastBoundedIntegerSet lAllPowerNodeNeighbors = new FastBoundedIntegerSet(lDirectNeighbors.size() + lDescendents.size()
																																							+ lAncestors.size());
		lAllPowerNodeNeighbors.union(lDirectNeighbors);

		for (int lPowerNodeId : lDescendents)
		{
			lAllPowerNodeNeighbors.union(getDirectPowerNodeNeighbors(lPowerNodeId));
		}

		for (int lPowerNodeId : lAncestors)
		{
			lAllPowerNodeNeighbors.union(getDirectPowerNodeNeighbors(lPowerNodeId));
		}

		return lAllPowerNodeNeighbors;
	}

	public FastBoundedIntegerSet getConnectedPowerNodeNeighbors(int pPowerNodeId)
	{
		FastBoundedIntegerSet lDirectNeighbors = mPowerEgdesGraph.getNodeNeighbours(pPowerNodeId);
		FastBoundedIntegerSet lDescendents = mHierarchyGraph.getOutgoingTransitiveClosure(pPowerNodeId);
		FastBoundedIntegerSet lAncestors = mHierarchyGraph.getIncommingTransitiveClosure(pPowerNodeId);
		lAncestors.remove(0);

		FastBoundedIntegerSet lAllPowerNodeNeighbors = new FastBoundedIntegerSet(lDirectNeighbors.size() + lDescendents.size()
																																							+ lAncestors.size());
		lAllPowerNodeNeighbors.union(lDirectNeighbors);

		for (int lPowerNodeId : lAncestors)
		{
			lAllPowerNodeNeighbors.union(getDirectPowerNodeNeighbors(lPowerNodeId));
		}

		if (!lAllPowerNodeNeighbors.isEmpty())
			for (int lPowerNodeId : lDescendents)
			{
				lAllPowerNodeNeighbors.union(getDirectPowerNodeNeighbors(lPowerNodeId));
			}

		return lAllPowerNodeNeighbors;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
							+ ((mHierarchyGraph == null) ? 0 : mHierarchyGraph.hashCode());
		result = prime * result
							+ ((mId2PowerNode == null) ? 0 : mId2PowerNode.hashCode());
		result = prime * result + ((mNodesSet == null) ? 0 : mNodesSet.hashCode());
		result = prime * result
							+ ((mPowerEgdesGraph == null) ? 0 : mPowerEgdesGraph.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FastIntegerPowerGraph other = (FastIntegerPowerGraph) obj;
		if (mHierarchyGraph == null)
		{
			if (other.mHierarchyGraph != null)
				return false;
		}
		else if (!mHierarchyGraph.equals(other.mHierarchyGraph))
			return false;
		if (mId2PowerNode == null)
		{
			if (other.mId2PowerNode != null)
				return false;
		}
		else if (!mId2PowerNode.equals(other.mId2PowerNode))
			return false;
		if (mNodesSet == null)
		{
			if (other.mNodesSet != null)
				return false;
		}
		else if (!mNodesSet.equals(other.mNodesSet))
			return false;
		if (mPowerEgdesGraph == null)
		{
			if (other.mPowerEgdesGraph != null)
				return false;
		}
		else if (!mPowerEgdesGraph.equals(other.mPowerEgdesGraph))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder lBuilder = new StringBuilder();
		for (int node : mNodesSet)
		{
			lBuilder.append("NODE\t" + node + "\n");
		}
		
		for (int i = 1; i < mId2PowerNode.size(); i++)
		{
			lBuilder.append("SET\t" + i + "\n");
		}

		for (int lPowerNodeId : getPowerNodeIdSet())
			if (lPowerNodeId != 0)
			{
				for (int lChildrenId : mHierarchyGraph.getOutgoingNodeNeighbours(lPowerNodeId))
				{
					lBuilder.append("IN\t" + lChildrenId + "\t" + lPowerNodeId + "\n");
				}
			}

		for (int[] lEdge : mPowerEgdesGraph.getIntPairList())
		{
			lBuilder.append("EDGE\t" + lEdge[0] + "\t" + lEdge[1] + "\n");
		}

		return lBuilder.toString();
	}
}
