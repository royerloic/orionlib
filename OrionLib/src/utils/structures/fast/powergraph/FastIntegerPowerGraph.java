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
		this(10, 10, 10);
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
	
	public int getNumberOfPowerNodes()
	{
		return mPowerEgdesGraph.getNumberOfNodes();
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





}
