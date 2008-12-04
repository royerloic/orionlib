package utils.structures.fast.powergraph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import utils.structures.fast.graph.FastIntegerDirectedGraph;
import utils.structures.fast.graph.FastIntegerGraph;
import utils.structures.fast.set.FastBoundedIntegerSet;

public class FastIntegerPowerGraph implements Serializable
{
	private static final long serialVersionUID = 1L;

	FastBoundedIntegerSet mNodesSet;
	ArrayList<FastBoundedIntegerSet> mId2PowerNode;
	HashMap<FastBoundedIntegerSet, Integer> mPowerNode2Id;
	FastIntegerDirectedGraph mHierarchyGraph;
	FastIntegerGraph mPowerEgdesGraph;

	public FastIntegerPowerGraph(	final int pNumberOfNodes,
																final int pNumberOfPowerNodes,
																final int pNumberOfPowerEdges)
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

	public Integer addPowerNode(final FastBoundedIntegerSet pPowerNode)
	{
		Integer lPowerNodeId = mPowerNode2Id.get(pPowerNode);
		if (lPowerNodeId == null)
		{
			lPowerNodeId = insertInDirectedGraphRecusive(0, pPowerNode);
		}
		return lPowerNodeId;
	}

	private Integer insertInDirectedGraphRecusive(final int pInsertPowerNodeId,
																								final FastBoundedIntegerSet pPowerNode)
	{
		{
			final FastBoundedIntegerSet childrenids = mHierarchyGraph.getOutgoingNodeNeighbours(pInsertPowerNodeId);
			final FastBoundedIntegerSet childtodisplace = new FastBoundedIntegerSet();
			Integer lPowerNodeId = null;

			for (final int childid : childrenids)
			{
				final FastBoundedIntegerSet child = mId2PowerNode.get(childid);
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

			for (final int child : childtodisplace)
			{
				mHierarchyGraph.removeEdge(pInsertPowerNodeId, child);
				mHierarchyGraph.addEdge(lPowerNodeId, child);
			}

			return lPowerNodeId;
		}
	}

	public FastBoundedIntegerSet getPowerNodeById(final int pPowerNodeId)
	{
		return mId2PowerNode.get(pPowerNodeId);
	}

	public Integer getIdForPowerNode(final FastBoundedIntegerSet pPowerNode)
	{
		return mPowerNode2Id.get(pPowerNode);
	}

	public boolean isPowerNode(final FastBoundedIntegerSet pPowerNode)
	{
		final Integer lId = mPowerNode2Id.get(pPowerNode);
		return mHierarchyGraph.isNode(lId);
	}

	public boolean isPowerNode(final int pPowerNodeId)
	{
		return mHierarchyGraph.isNode(pPowerNodeId);
	}

	public int getPowerNodeSize(int pPowerNodeId)
	{
		return mId2PowerNode.get(pPowerNodeId).size();
	}

	public FastBoundedIntegerSet getPowerNodeIdSet()
	{
		return mPowerEgdesGraph.getNodeSet();
	}

	public int getNumberOfPowerNodes()
	{
		return mPowerEgdesGraph.getNumberOfNodes();
	}

	public int getNumberOfNonSingletonPowerNodes()
	{
		int lNumberOfNonSingletonPowerNodes = 0;
		for (FastBoundedIntegerSet lPowerNode : mId2PowerNode)
			if (lPowerNode.size() > 1)
			{
				lNumberOfNonSingletonPowerNodes++;
			}
		return lNumberOfNonSingletonPowerNodes;
	}

	public int getNumberOfNodes()
	{
		return mNodesSet.size();
	}

	public void addPowerEdge(final int pPowerNodeId1, final int pPowerNodeId2)
	{
		mPowerEgdesGraph.addEdge(pPowerNodeId1, pPowerNodeId2);
	}

	public boolean addPowerEdge(final FastBoundedIntegerSet pPowerNode1,
															final FastBoundedIntegerSet pPowerNode2)
	{
		final Integer lId1 = addPowerNode(pPowerNode1);
		final Integer lId2 = addPowerNode(pPowerNode2);
		if (lId1 != null && lId2 != null)
		{
			return mPowerEgdesGraph.addEdge(lId1, lId2);
		}
		else
		{
			return false;
		}
	}

	public void removePowerEdge(final int pPowerNodeId1, final int pPowerNodeId2)
	{
		mPowerEgdesGraph.removeEdge(pPowerNodeId1, pPowerNodeId2);
	}

	public void removePowerEdge(final FastBoundedIntegerSet pPowerNode1,
															final FastBoundedIntegerSet pPowerNode2)
	{
		final Integer lId1 = addPowerNode(pPowerNode1);
		final Integer lId2 = addPowerNode(pPowerNode2);
		mPowerEgdesGraph.removeEdge(lId1, lId2);
	}

	public boolean isPowerEdge(	final FastBoundedIntegerSet pPowerNode1,
															final FastBoundedIntegerSet pPowerNode2)
	{
		final Integer lId1 = mPowerNode2Id.get(pPowerNode1);
		final Integer lId2 = mPowerNode2Id.get(pPowerNode2);
		if (lId1 == null || lId2 == null)
		{
			return false;
		}
		else
		{
			return mPowerEgdesGraph.isEdge(lId1, lId2);
		}
	}

	public boolean isPowerEdge(final int pPowerNodeId1, final int pPowerNodeId2)
	{
		return mPowerEgdesGraph.isEdge(pPowerNodeId1, pPowerNodeId2);
	}

	public int getNumberOfPowerEdges()
	{
		return mPowerEgdesGraph.getNumberOfEdges();
	}

	public int getNumberOfReflexivePowerEdges()
	{
		return mPowerEgdesGraph.getNumberOfReflexiveEdges();
	}

	public FastBoundedIntegerSet getExclusiveNodeChildren(int pPowerNodeId)
	{
		FastBoundedIntegerSet lNodeSet = mId2PowerNode.get(pPowerNodeId);
		FastBoundedIntegerSet lExclusiveNodeChildren = new FastBoundedIntegerSet(lNodeSet);

		FastBoundedIntegerSet lPowerNodeChildren = mHierarchyGraph.getOutgoingNodeNeighbours(pPowerNodeId);
		for (int lPowerNodeChild : lPowerNodeChildren)
		{
			FastBoundedIntegerSet lChildrenNodeSet = mId2PowerNode.get(lPowerNodeChild);
			lExclusiveNodeChildren.difference(lChildrenNodeSet);
		}

		return lExclusiveNodeChildren;
	}

	public FastBoundedIntegerSet getPowerNodeChildrenOf(final int pPowerNodeId)
	{
		return mHierarchyGraph.getOutgoingNodeNeighbours(pPowerNodeId);
	}

	public FastBoundedIntegerSet getPowerNodeChildrenOf(final FastBoundedIntegerSet pPowerNode)
	{
		return mHierarchyGraph.getOutgoingNodeNeighbours(mPowerNode2Id.get(pPowerNode));
	}

	public FastBoundedIntegerSet getPowerNodeDescendentsOf(final int pPowerNodeId)
	{
		return mHierarchyGraph.getOutgoingTransitiveClosure(pPowerNodeId);
	}

	public FastBoundedIntegerSet getPowerNodeParentOf(final int pPowerNodeId)
	{
		return mHierarchyGraph.getIncommingNodeNeighbours(pPowerNodeId);
	}

	public FastBoundedIntegerSet getPowerNodeParentOf(final FastBoundedIntegerSet pPowerNode)
	{
		return mHierarchyGraph.getIncommingNodeNeighbours(mPowerNode2Id.get(pPowerNode));
	}

	public FastBoundedIntegerSet getPowerNodeAncestorsOf(final int pPowerNodeId)
	{
		return mHierarchyGraph.getIncommingTransitiveClosure(pPowerNodeId);
	}

	public boolean isTopPowerNode(int pPowerNodeId)
	{
		FastBoundedIntegerSet lParentPowerNodeIdSet = mHierarchyGraph.getIncommingNodeNeighbours(pPowerNodeId);
		return lParentPowerNodeIdSet.size() == 1 && lParentPowerNodeIdSet.contains(0);
	}

	private boolean hasPowerEdge(int pPowerNodeId)
	{
		return mPowerEgdesGraph.getNodeNeighbours(pPowerNodeId).size() > 0;
	}

	public FastBoundedIntegerSet getDirectPowerNodeNeighbors(final int pPowerNodeId)
	{
		return mPowerEgdesGraph.getNodeNeighbours(pPowerNodeId);
	}

	public FastBoundedIntegerSet getAllPowerNodeNeighbors(final int pPowerNodeId)
	{
		final FastBoundedIntegerSet lDirectNeighbors = mPowerEgdesGraph.getNodeNeighbours(pPowerNodeId);
		final FastBoundedIntegerSet lDescendents = mHierarchyGraph.getOutgoingTransitiveClosure(pPowerNodeId);
		final FastBoundedIntegerSet lAncestors = mHierarchyGraph.getIncommingTransitiveClosure(pPowerNodeId);

		final FastBoundedIntegerSet lAllPowerNodeNeighbors = new FastBoundedIntegerSet(lDirectNeighbors.size() + lDescendents.size()
																																										+ lAncestors.size());
		lAllPowerNodeNeighbors.union(lDirectNeighbors);

		for (final int lPowerNodeId : lDescendents)
		{
			lAllPowerNodeNeighbors.union(getDirectPowerNodeNeighbors(lPowerNodeId));
		}

		for (final int lPowerNodeId : lAncestors)
		{
			lAllPowerNodeNeighbors.union(getDirectPowerNodeNeighbors(lPowerNodeId));
		}

		return lAllPowerNodeNeighbors;
	}

	public FastBoundedIntegerSet getConnectedPowerNodeNeighbors(final int pPowerNodeId)
	{
		final FastBoundedIntegerSet lDirectNeighbors = mPowerEgdesGraph.getNodeNeighbours(pPowerNodeId);
		final FastBoundedIntegerSet lDescendents = mHierarchyGraph.getOutgoingTransitiveClosure(pPowerNodeId);
		final FastBoundedIntegerSet lAncestors = mHierarchyGraph.getIncommingTransitiveClosure(pPowerNodeId);
		lAncestors.remove(0);

		final FastBoundedIntegerSet lAllPowerNodeNeighbors = new FastBoundedIntegerSet(lDirectNeighbors.size() + lDescendents.size()
																																										+ lAncestors.size());
		lAllPowerNodeNeighbors.union(lDirectNeighbors);

		for (final int lPowerNodeId : lAncestors)
			if (hasPowerEdge(lPowerNodeId))
			{
				lAllPowerNodeNeighbors.add(lPowerNodeId);
				lAllPowerNodeNeighbors.union(getDirectPowerNodeNeighbors(lPowerNodeId));
			}

		if (!lAllPowerNodeNeighbors.isEmpty())
		{
			for (final int lPowerNodeId : lDescendents)
			{
				lAllPowerNodeNeighbors.add(lPowerNodeId);
				lAllPowerNodeNeighbors.union(getDirectPowerNodeNeighbors(lPowerNodeId));
			}
		}

		return lAllPowerNodeNeighbors;
	}

	public ArrayList<int[]> getPowerEdgeList()
	{
		return mPowerEgdesGraph.getIntPairList();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
							+ (mHierarchyGraph == null ? 0 : mHierarchyGraph.hashCode());
		result = prime * result
							+ (mId2PowerNode == null ? 0 : mId2PowerNode.hashCode());
		result = prime * result + (mNodesSet == null ? 0 : mNodesSet.hashCode());
		result = prime * result
							+ (mPowerEgdesGraph == null ? 0 : mPowerEgdesGraph.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final FastIntegerPowerGraph other = (FastIntegerPowerGraph) obj;
		if (mHierarchyGraph == null)
		{
			if (other.mHierarchyGraph != null)
			{
				return false;
			}
		}
		else if (!mHierarchyGraph.equals(other.mHierarchyGraph))
		{
			return false;
		}
		if (mId2PowerNode == null)
		{
			if (other.mId2PowerNode != null)
			{
				return false;
			}
		}
		else if (!mId2PowerNode.equals(other.mId2PowerNode))
		{
			return false;
		}
		if (mNodesSet == null)
		{
			if (other.mNodesSet != null)
			{
				return false;
			}
		}
		else if (!mNodesSet.equals(other.mNodesSet))
		{
			return false;
		}
		if (mPowerEgdesGraph == null)
		{
			if (other.mPowerEgdesGraph != null)
			{
				return false;
			}
		}
		else if (!mPowerEgdesGraph.equals(other.mPowerEgdesGraph))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return toTabDel();
	}

	public String toTabDel()
	{
		final StringBuilder lBuilder = new StringBuilder();
		for (final int node : mNodesSet)
		{
			lBuilder.append("NODE\tnode" + node + "\n");
		}

		for (int i = 1; i < mId2PowerNode.size(); i++)
		{
			lBuilder.append("SET\tset" + i + "\n");
		}

		for (final int lPowerNodeId : getPowerNodeIdSet())
		{
			if (lPowerNodeId != 0)
			{
				for (final int lNode : mId2PowerNode.get(lPowerNodeId))
				{
					lBuilder.append("IN\tnode" + lNode + "\tset" + lPowerNodeId + "\n");
				}
			}
		}

		for (final int lPowerNodeId : getPowerNodeIdSet())
		{
			if (lPowerNodeId != 0)
			{
				for (final int lChildrenId : mHierarchyGraph.getOutgoingNodeNeighbours(lPowerNodeId))
				{
					lBuilder.append("IN\tset" + lChildrenId
													+ "\tset"
													+ lPowerNodeId
													+ "\n");
				}
			}
		}

		for (final int[] lEdge : mPowerEgdesGraph.getIntPairList())
		{
			final String powernode1str = "set" + lEdge[0];
			final String powernode2str = "set" + lEdge[1];

			lBuilder.append("EDGE\t" + powernode1str + "\t" + powernode2str + "\n");
		}

		return lBuilder.toString();
	}

	public void writeEdgeFile(final File pFile) throws IOException
	{
		writeEdgeFile(new FileOutputStream(pFile));
	}

	public void writeEdgeFile(final OutputStream pOutputStream) throws IOException
	{
		final Writer lWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream));
		lWriter.append(toTabDel());
		lWriter.flush();
	}

	/**
	 * This method reads BBL files written by this class. It does not read other
	 * BBL files, since it assumes nodes and sets of the syntax: setx and nodex.
	 * 
	 * @param pFile
	 * @throws IOException
	 */
	public void readEdgeFile(final String pString) throws IOException
	{
		readEdgeFile(new ByteArrayInputStream(pString.getBytes("UTF-8")));
	}

	/**
	 * This method reads BBL files written by this class. It does not read other
	 * BBL files, since it assumes nodes and sets of the syntax: setx and nodex.
	 * 
	 * @param pFile
	 * @throws IOException
	 */
	public void readEdgeFile(final File pFile) throws IOException
	{
		readEdgeFile(new FileInputStream(pFile));
	}

	/**
	 * This method reads BBL files written by this class. It does not read other
	 * BBL files, since it assumes nodes and sets of the syntax: setx and nodex.
	 * 
	 * @param pInputStream
	 * @throws IOException
	 */
	public void readEdgeFile(final InputStream pInputStream) throws IOException
	{
		final BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(pInputStream));
		final Pattern lPattern = Pattern.compile("\t");

		final ArrayList<FastBoundedIntegerSet> lPowerNodeList = new ArrayList<FastBoundedIntegerSet>();
		lPowerNodeList.add(null); // need to start at 1 since 0 is root.
		final ArrayList<int[]> lEgdeList = new ArrayList<int[]>();

		String lLine = null;
		while ((lLine = lBufferedReader.readLine()) != null)
		{
			if (!(lLine.length() == 0) && !lLine.startsWith("#")
					&& !lLine.startsWith("//"))
			{
				final String[] lArray = lPattern.split(lLine, -1);
				if (lLine.startsWith("NODE\t"))
				{
					final int node = Integer.parseInt(lArray[1].substring(4));
				}
				else if (lLine.startsWith("SET\t"))
				{
					final int setid = Integer.parseInt(lArray[1].substring(3));
					lPowerNodeList.add(new FastBoundedIntegerSet());
				}
				else if (lLine.startsWith("IN\t"))
				{
					final String first = lArray[1];
					if (first.startsWith("node"))
					{
						final int nodeid = Integer.parseInt(first.substring(4));
						final String second = lArray[2];
						final int setid = Integer.parseInt(second.substring(3));
						lPowerNodeList.get(setid).add(nodeid);
					}
				}
				else if (lLine.startsWith("EDGE\t"))
				{
					final int node1 = Integer.parseInt(lArray[1].substring(3));
					final int node2 = Integer.parseInt(lArray[2].substring(3));
					lEgdeList.add(new int[]
					{ node1, node2 });
				}
			}
		}

		for (final FastBoundedIntegerSet lPowerNode : lPowerNodeList)
		{
			if (lPowerNode != null)
			{
				addPowerNode(lPowerNode);
			}
		}

		for (final int[] lEdge : lEgdeList)
		{
			addPowerEdge(lEdge[0], lEdge[1]);
		}

	}

}
