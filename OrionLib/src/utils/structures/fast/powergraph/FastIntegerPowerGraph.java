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
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

import utils.structures.fast.graph.Edge;
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

	public boolean addPowerEdge(FastBoundedIntegerSet pPowerNode1,
															FastBoundedIntegerSet pPowerNode2)
	{
		Integer lId1 = addPowerNode(pPowerNode1);
		Integer lId2 = addPowerNode(pPowerNode2);
		if (lId1 != null && lId2 != null)
		{
			return mPowerEgdesGraph.addEdge(lId1, lId2);
		}
		else
			return false;
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
		return toTabDel();
	}

	public String toTabDel()
	{
		StringBuilder lBuilder = new StringBuilder();
		for (int node : mNodesSet)
		{
			lBuilder.append("NODE\tnode" + node + "\n");
		}

		for (int i = 1; i < mId2PowerNode.size(); i++)
		{
			lBuilder.append("SET\tset" + i + "\n");
		}

		for (int lPowerNodeId : getPowerNodeIdSet())
			if (lPowerNodeId != 0)
			{
				for (int lNode : mId2PowerNode.get(lPowerNodeId))
				{
					lBuilder.append("IN\tnode" + lNode + "\tset" + lPowerNodeId + "\n");
				}
			}

		for (int lPowerNodeId : getPowerNodeIdSet())
			if (lPowerNodeId != 0)
			{
				for (int lChildrenId : mHierarchyGraph.getOutgoingNodeNeighbours(lPowerNodeId))
				{
					lBuilder.append("IN\tset" + lChildrenId
													+ "\tset"
													+ lPowerNodeId
													+ "\n");
				}
			}

		for (int[] lEdge : mPowerEgdesGraph.getIntPairList())
		{
			String powernode1str = "set" + lEdge[0];
			String powernode2str = "set" + lEdge[1];

			lBuilder.append("EDGE\t" + powernode1str + "\t" + powernode2str + "\n");
		}

		return lBuilder.toString();
	}

	public void writeEdgeFile(File pFile) throws IOException
	{
		writeEdgeFile(new FileOutputStream(pFile));
	}

	public void writeEdgeFile(OutputStream pOutputStream) throws IOException
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
	public void readEdgeFile(String pString) throws IOException
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
	public void readEdgeFile(File pFile) throws IOException
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
	public void readEdgeFile(InputStream pInputStream) throws IOException
	{
		BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(pInputStream));
		Pattern lPattern = Pattern.compile("\t");

		ArrayList<FastBoundedIntegerSet> lPowerNodeList = new ArrayList<FastBoundedIntegerSet>();
		lPowerNodeList.add(null); // need to start at 1 since 0 is root.
		ArrayList<int[]> lEgdeList = new ArrayList<int[]>();

		String lLine = null;
		while ((lLine = lBufferedReader.readLine()) != null)
			if (!lLine.isEmpty() && !lLine.startsWith("#") && !lLine.startsWith("//"))
			{
				final String[] lArray = lPattern.split(lLine, -1);
				if (lLine.startsWith("NODE\t"))
				{
					int node = Integer.parseInt(lArray[1].substring(4));
				}
				else if (lLine.startsWith("SET\t"))
				{
					int setid = Integer.parseInt(lArray[1].substring(3));
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

		for (FastBoundedIntegerSet lPowerNode : lPowerNodeList)
			if (lPowerNode != null)
			{
				addPowerNode(lPowerNode);
			}

		for (int[] lEdge : lEgdeList)
		{
			addPowerEdge(lEdge[0], lEdge[1]);
		}

	}
}
