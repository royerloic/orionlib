package utils.structures.fast.powergraph;

import java.util.ArrayList;
import java.util.HashMap;

import utils.structures.fast.graph.FastIntegerDirectedGraph;
import utils.structures.fast.graph.FastIntegerGraph;
import utils.structures.fast.set.FastBoundedIntegerSet;

public class FastIntegerPowerGraph<N>
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

		addPowerNode(mNodesSet);
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
		mHierarchyGraph.addEdge(pInsertPowerNodeId, lPowerNodeId);

		for (int child : childtodisplace)
		{
			mHierarchyGraph.removeEdge(pInsertPowerNodeId, child);
			mHierarchyGraph.addEdge(lPowerNodeId, child);
		}

		return lPowerNodeId;
	}
	
	public FastBoundedIntegerSet getPowerNodeById(int pPowerNodeId)
	{
		return mId2PowerNode.get(pPowerNodeId);
	}

	public Integer getIdForPowerNode(FastBoundedIntegerSet pPowerNode)
	{
		return mPowerNode2Id.get(pPowerNode);
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

	public FastBoundedIntegerSet getPowerNodesChildren(int pPowerNodeId)
	{
		return mHierarchyGraph.getOutgoingNodeNeighbours(pPowerNodeId);
	}

}
