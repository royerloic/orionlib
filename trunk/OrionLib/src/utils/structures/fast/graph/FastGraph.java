package utils.structures.fast.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import utils.structures.fast.set.FastBoundedIntegerSet;
import utils.structures.fast.set.FastSparseIntegerSet;

public class FastGraph<N> implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	FastIntegerGraph mFastIntegerGraph = new FastIntegerGraph();

	final HashMap<N, Integer> mNameToNodeMap = new HashMap<N, Integer>();
	final ArrayList<N> mNodeToNameList = new ArrayList<N>();

	public FastGraph()
	{
		super();
	}

	public FastIntegerGraph getUnderlyingFastIntegerGraph()
	{
		return mFastIntegerGraph;
	}

	/**
	 * Sets the underlying FastIntegerGraph WARNING: you need to know what you are
	 * doing, the integer graph must be compatible with the mappings contained in
	 * the FastGraph...
	 * 
	 * @param pFastIntegerGraph
	 */
	public void setUnderlyingFastIntegerGraph(final FastIntegerGraph pFastIntegerGraph)
	{
		mFastIntegerGraph = pFastIntegerGraph;
	}

	public ArrayList<N> getNodesForIntegers(final int... set)
	{
		final ArrayList<N> list = new ArrayList<N>(set.length);
		for (final Integer lInteger : set)
		{
			list.add(mNodeToNameList.get(lInteger));
		}
		return list;
	}

	public ArrayList<N> getNodesForIntegers(final FastSparseIntegerSet set)
	{
		final ArrayList<N> list = new ArrayList<N>(set.size());
		for (final Integer lInteger : set)
		{
			list.add(mNodeToNameList.get(lInteger));
		}
		return list;
	}

	public void addNode(final N pNodeName)
	{
		if (!mNameToNodeMap.containsKey(pNodeName))
		{
			final int lNodeIndex = mFastIntegerGraph.addNode();
			mNameToNodeMap.put(pNodeName, lNodeIndex);
			mNodeToNameList.add(lNodeIndex, pNodeName);
		}
	}

	public boolean isNode(final N pNodeName)
	{
		Integer lNodeIndex = null;
		return (lNodeIndex = mNameToNodeMap.get(pNodeName)) != null && mFastIntegerGraph.isNode(lNodeIndex);
	}

	public void addEdge(final N pNodeName1, final N pNodeName2)
	{
		Integer lNodeIndex1 = mNameToNodeMap.get(pNodeName1);
		Integer lNodeIndex2 = mNameToNodeMap.get(pNodeName2);

		if (lNodeIndex1 == null)
		{
			lNodeIndex1 = mFastIntegerGraph.addNode();
			mNameToNodeMap.put(pNodeName1, lNodeIndex1);
			mNodeToNameList.add(lNodeIndex1, pNodeName1);
		}

		if (lNodeIndex2 == null)
		{
			lNodeIndex2 = mFastIntegerGraph.addNode();
			mNameToNodeMap.put(pNodeName2, lNodeIndex2);
			mNodeToNameList.add(lNodeIndex2, pNodeName2);
		}

		mFastIntegerGraph.addEdge(lNodeIndex1, lNodeIndex2);
	}

	public void removeEdge(final N pNodeName1, final N pNodeName2)
	{
		final Integer lNodeIndex1 = mNameToNodeMap.get(pNodeName1);
		final Integer lNodeIndex2 = mNameToNodeMap.get(pNodeName2);

		if (lNodeIndex1 == null || lNodeIndex2 == null)
		{
			return;
		}

		mFastIntegerGraph.removeEdge(lNodeIndex1, lNodeIndex2);
	}

	public boolean isEdge(final N pNodeName1, final N pNodeName2)
	{
		final Integer lNodeIndex1 = mNameToNodeMap.get(pNodeName1);
		final Integer lNodeIndex2 = mNameToNodeMap.get(pNodeName2);

		if (lNodeIndex1 == null || lNodeIndex2 == null)
		{
			return false;
		}

		return mFastIntegerGraph.isEdge(lNodeIndex1, lNodeIndex2);
	}

	public int getNumberOfNodes()
	{
		return mFastIntegerGraph.getNumberOfNodes();
	}

	public Set<N> getNodeSet()
	{
		return mNameToNodeMap.keySet();
	}

	public int getNumberOfEdges()
	{
		return mFastIntegerGraph.getNumberOfEdges();
	}

	private HashSet<Edge<N>> getEdgeSet()
	{
		final HashSet<Edge<N>> lEdgeSet = new HashSet<Edge<N>>(getNumberOfEdges());

		for (final int[] lEdgeInts : mFastIntegerGraph.getIntPairList())
		{
			final N lNodeName1 = mNodeToNameList.get(lEdgeInts[0]);
			final N lNodeName2 = mNodeToNameList.get(lEdgeInts[1]);

			final Edge<N> lEdge = new Edge<N>(lNodeName1, lNodeName2);
			lEdgeSet.add(lEdge);
		}

		return lEdgeSet;
	}

	public ArrayList<N> getNodeNeighbours(final N pNode)
	{
		int lNodeId = mNameToNodeMap.get(pNode);
		FastBoundedIntegerSet lNeighboursIntegerIds = getUnderlyingFastIntegerGraph()	.getNodeNeighbours(lNodeId);
		return getNodesForIntegers(lNeighboursIntegerIds.toIntArray());
	}

	/**
	 * Does not includes node itself, even if it is a neighbours of one of its
	 * neighnboors.
	 */
	public ArrayList<N> getNodeNeighbours(final int pNode, final int pDepth)
	{
		int lNodeId = mNameToNodeMap.get(pNode);
		FastBoundedIntegerSet lNeighboursIntegerIds = getUnderlyingFastIntegerGraph()	.getNodeNeighbours(	lNodeId,
																																																			pDepth);
		return getNodesForIntegers(lNeighboursIntegerIds.toIntArray());
	}

	public void writeEdgeFile(final File pFile) throws IOException
	{
		writeEdgeFile(new FileOutputStream(pFile));
	}

	public void writeEdgeFile(final OutputStream pOutputStream) throws IOException
	{
		final Writer lWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream));

		for (final N lNodeName : this.getNodeSet())
		{
			lWriter.append("NODE\t" + lNodeName + "\n");
		}

		for (final Edge<N> lEdge : this.getEdgeSet())
		{
			lWriter.append("EDGE\t" + lEdge.getFirstNode()
											+ "\t"
											+ lEdge.getSecondNode()
											+ "\n");
		}
		lWriter.flush();
	}

	public static FastGraph<String> readEdgeFile(final File pFile) throws IOException
	{
		return readEdgeFile(new FileInputStream(pFile));
	}

	public static FastGraph<String> readEdgeFile(final InputStream pInputStream) throws IOException
	{
		final FastGraph<String> lFastGraph = new FastGraph<String>();
		final BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(pInputStream));
		final Pattern lPattern = Pattern.compile("\t");

		int nodeindex1 = 1;
		int nodeindex2 = 2;
		int confindex = -1;

		double confmin = Double.NEGATIVE_INFINITY;
		double confmax = Double.POSITIVE_INFINITY;

		String lLine = null;
		while ((lLine = lBufferedReader.readLine()) != null)
		{
			if (!(lLine.length() == 0) && !lLine.startsWith("#")
					&& !lLine.startsWith("//"))
			{
				final String[] lArray = lPattern.split(lLine, -1);
				if (lLine.startsWith("EDGEFORMAT\t"))
				{
					nodeindex1 = Integer.parseInt(lArray[1]);
					nodeindex2 = Integer.parseInt(lArray[2]);
					if (lArray.length >= 4 && !(lArray[3].length() == 0))
					{
						confindex = Integer.parseInt(lArray[3]);
					}
				}
				else if (lLine.startsWith("CONFIDENCEVALUETHRESHOLD\t") || lLine.startsWith("CONFIDENCEVALUEMIN\t"))
				{
					confmin = Double.parseDouble(lArray[1]);
				}
				else if (lLine.startsWith("CONFIDENCEVALUEMAX\t"))
				{
					confmax = Double.parseDouble(lArray[1]);
				}
				if (lLine.startsWith("NODE\t"))
				{
					final String lNodeString = lArray[1];
					lFastGraph.addNode(lNodeString);
				}
				else if (lLine.startsWith("EDGE\t"))
				{
					final String lFirstNodeString = lArray[nodeindex1];
					final String lSecondNodeString = lArray[nodeindex2];

					double confvalue = 1;
					if (confindex > 1)
					{
						confvalue = Double.parseDouble(lArray[confindex]);
					}

					if (confvalue >= confmin && confvalue <= confmax)
					{
						lFastGraph.addEdge(lFirstNodeString, lSecondNodeString);
					}
				}
			}
		}
		return lFastGraph;

	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
							+ (mFastIntegerGraph == null ? 0 : getNodeSet().hashCode());
		result = prime * result
							+ (mNameToNodeMap == null ? 0 : getEdgeSet().hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
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
		final FastGraph other = (FastGraph) obj;

		if (!mNameToNodeMap.keySet().equals(other.mNameToNodeMap.keySet()))
		{
			return false;
		}
		else if (!getEdgeSet().equals(other.getEdgeSet()))
		{
			return false;
		}

		return true;
	}

	@Override
	public String toString()
	{
		final StringBuilder lStringBuilder = new StringBuilder();
		for (final Edge<N> lEdge : getEdgeSet())
		{
			lStringBuilder.append(lEdge.toString());
			lStringBuilder.append("\n");
		}
		;
		return lStringBuilder.toString();
	}

}
