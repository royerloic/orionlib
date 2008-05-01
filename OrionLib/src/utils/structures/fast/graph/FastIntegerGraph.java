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
import java.util.Arrays;
import java.util.regex.Pattern;

import utils.structures.fast.set.FastIntegerSet;

public class FastIntegerGraph implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ArrayList<FastIntegerSet> mSparseMatrix;

	int mAfterLastNodeIndex = 0;
	int mEdgeCount = 0;

	public FastIntegerGraph()
	{
		super();
		mSparseMatrix = new ArrayList<FastIntegerSet>();
	}

	public FastIntegerGraph(final int pNumberOfNodes)
	{
		super();
		mSparseMatrix = new ArrayList<FastIntegerSet>(pNumberOfNodes);
		addNodesUpTo(pNumberOfNodes - 1); // 0 is the first node.
	}

	public int addNode()
	{
		mSparseMatrix.add(new FastIntegerSet());
		final int lNodeIndex = mAfterLastNodeIndex;
		mAfterLastNodeIndex++;
		return lNodeIndex;
	}

	public void addNodesUpTo(final int pNode)
	{
		if (!isNode(pNode))
		{
			while (addNode() < pNode)
				;
		}
	}

	public boolean isNode(final int pNodeIndex)
	{
		return pNodeIndex < mSparseMatrix.size();
	}

	public void addEdges(final int pNode1, final FastIntegerSet pNodeSet)
	{
		for (int lNode2 : pNodeSet.getUnderlyingArray())
		{
			addEdge(pNode1, lNode2);
		}
	}

	public void addEdge(final int pNode1, final int pNode2)
	{
		if (!isEdge(pNode1, pNode2))
		{
			addNodesUpTo(pNode1);
			addNodesUpTo(pNode2);

			FastIntegerSet lNeiSet1 = mSparseMatrix.get(pNode1);
			FastIntegerSet lNeiSet2 = mSparseMatrix.get(pNode2);

			lNeiSet1.add(pNode2);
			lNeiSet2.add(pNode1);

			mSparseMatrix.set(pNode1, lNeiSet1);
			mSparseMatrix.set(pNode2, lNeiSet2);

			mEdgeCount++;
		}
	}

	public void removeEdge(final int pNode1, final int pNode2)
	{
		if (isEdge(pNode1, pNode2))
		{
			FastIntegerSet lNeiSet1 = mSparseMatrix.get(pNode1);
			FastIntegerSet lNeiSet2 = mSparseMatrix.get(pNode2);

			lNeiSet1.del(pNode2);
			lNeiSet2.del(pNode1);

			mSparseMatrix.set(pNode1, lNeiSet1);
			mSparseMatrix.set(pNode2, lNeiSet2);

			mEdgeCount--;
		}
	}

	public boolean isEdge(final int pNode1, final int pNode2)
	{
		if (pNode1 >= mAfterLastNodeIndex || pNode2 >= mAfterLastNodeIndex)
			return false;

		FastIntegerSet lNeiSet1 = mSparseMatrix.get(pNode1);
		FastIntegerSet lNeiSet2 = mSparseMatrix.get(pNode2);
		// not optimized but we could detect bugs better.
		return lNeiSet1.contains(pNode2) && lNeiSet2.contains(pNode1);
	}

	public FastIntegerSet getNodeSet()
	{
		final FastIntegerSet lNodeSet = new FastIntegerSet();
		lNodeSet.ensureCapacity(mSparseMatrix.size());
		for (int i = 0; i < mSparseMatrix.size(); i++)
			lNodeSet.add(i);
		return lNodeSet;
	}

	public ArrayList<int[]> getIntPairList()
	{
		// NOT OPTIMIZED: should use an iterator to avoid allocating data
		ArrayList<int[]> lEdgeList = new ArrayList<int[]>();
		boolean[] lVisited = new boolean[mSparseMatrix.size()];
		for (int node1 = 0; node1 < mSparseMatrix.size(); node1++)
		{
			FastIntegerSet lNei = mSparseMatrix.get(node1);
			for (int node2 : lNei.getUnderlyingArray())
				if (!lVisited[node2])
				{
					lEdgeList.add(new int[]
					{ node1, node2 });

				}
			lVisited[node1] = true;
		}
		return lEdgeList;
	}

	public ArrayList<Edge<Integer>> getEdgeList()
	{
		// NOT OPTIMIZED: should use an iterator to avoid allocating data
		ArrayList<Edge<Integer>> lEdgeList = new ArrayList<Edge<Integer>>();
		boolean[] lVisited = new boolean[mSparseMatrix.size()];
		for (int node1 = 0; node1 < mSparseMatrix.size(); node1++)
		{
			FastIntegerSet lNei = mSparseMatrix.get(node1);
			for (int node2 : lNei.getUnderlyingArray())
				if (!lVisited[node2])
				{
					lEdgeList.add(new Edge<Integer>(node1, node2));

				}
			lVisited[node1] = true;
		}
		return lEdgeList;
	}

	public int getNumberOfNodes()
	{
		return mSparseMatrix.size();
	}

	public int getNumberOfEdges()
	{
		return mEdgeCount;
	}

	public double getAverageDegree()
	{
		return ((double) 2 * getNumberOfEdges()) / (getNumberOfNodes());
	}

	public double getEdgeDensity()
	{
		return ((double) getNumberOfEdges()) / ((double) (getNumberOfNodes() * (getNumberOfNodes() - 1) / 2));
	}

	public FastIntegerSet getNodeNeighbours(final int pNode)
	{
		return mSparseMatrix.get(pNode);
	}

	/**
	 * Does not includes node itself, even if it is a neighbours of one of its
	 * neighnboors.
	 */
	public FastIntegerSet getNodeNeighbours(final int pNode, final int pDepth)
	{
		if (pDepth == 0)
		{
			return new FastIntegerSet();
		}
		else if (pDepth == 1)
		{
			return mSparseMatrix.get(pNode);
		}
		else if (pDepth > 1)
		{
			FastIntegerSet lNeiResult = new FastIntegerSet();
			FastIntegerSet lNei = mSparseMatrix.get(pNode);
			lNeiResult = FastIntegerSet.union(lNeiResult, lNei);

			for (int lNode : lNei.getUnderlyingArray())
			{
				FastIntegerSet lNeiNei = getNodeNeighbours(lNode, pDepth - 1);
				lNeiResult = FastIntegerSet.union(lNeiResult, lNeiNei);
			}
			lNeiResult.del(pNode);
			return lNeiResult;
		}
		else
		{
			throw new IndexOutOfBoundsException("Argument pDepth cannot be negative: " + pDepth);
		}
	}

	public FastIntegerGraph extractStrictSubGraph(FastIntegerSet pNodeSet)
	{
		FastIntegerGraph lNewGraph = new FastIntegerGraph();

		for (int i : pNodeSet.getUnderlyingArray())
		{
			FastIntegerSet lNei = mSparseMatrix.get(i);
			lNei = FastIntegerSet.intersection(lNei, pNodeSet);
			lNewGraph.addNodesUpTo(i);
			lNewGraph.addEdges(i, lNei);
		}

		return lNewGraph;
	}

	public FastIntegerGraph extractSubGraph(int[] pNodeSet)
	{
		FastIntegerGraph lNewGraph = new FastIntegerGraph();

		for (int i : pNodeSet)
		{
			FastIntegerSet lNei = mSparseMatrix.get(i);
			lNewGraph.addEdges(i, lNei);
		}

		return lNewGraph;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;

		for (FastIntegerSet lNeiSet : mSparseMatrix)
		{
			result = prime * result + lNeiSet.hashCode();
		}

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
		final FastIntegerGraph other = (FastIntegerGraph) obj;
		if (mSparseMatrix == null)
		{
			if (other.mSparseMatrix != null)
				return false;
		}
		else if (mSparseMatrix.size() != other.mSparseMatrix.size())
			return false;
		else
		{
			for (int i = 0; i < mSparseMatrix.size(); i++)
			{
				if (!mSparseMatrix.get(i).equals(mSparseMatrix.get(i)))
					return false;
			}
			return true;
		}

		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder lStringBuilder = new StringBuilder();
		int lCurrentNode = 0;
		for (FastIntegerSet lNeiSet : mSparseMatrix)
		{
			lStringBuilder.append(lCurrentNode + " - "
														+ lNeiSet.toString()
														+ "\n");
			lCurrentNode++;
		}
		return lStringBuilder.toString();
	}

	public void writeEdgeFile(File pFile) throws IOException
	{
		writeEdgeFile(new FileOutputStream(pFile));
	}

	public void writeEdgeFile(OutputStream pOutputStream) throws IOException
	{
		final Writer lWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream));

		for (int lNode : getNodeSet().getUnderlyingArray())
		{
			lWriter.append("NODE\t" + lNode + "\n");
		}

		for (int[] lEdge : getIntPairList())
		{
			lWriter.append("EDGE\t" + lEdge[0] + "\t" + lEdge[1] + "\n");
		}
		lWriter.flush();

	}

	public void readEdgeFile(File pFile) throws IOException
	{
		readEdgeFile(new FileInputStream(pFile));
	}

	public void readEdgeFile(InputStream pInputStream) throws IOException
	{
		BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(pInputStream));
		Pattern lPattern = Pattern.compile("\t");

		int nodeindex1 = 1;
		int nodeindex2 = 2;
		int confindex = -1;

		double confmin = Double.NEGATIVE_INFINITY;
		double confmax = Double.POSITIVE_INFINITY;

		String lLine = null;
		while ((lLine = lBufferedReader.readLine()) != null)
			if (!lLine.isEmpty() && !lLine.startsWith("#") && !lLine.startsWith("//"))
			{
				final String[] lArray = lPattern.split(lLine, -1);
				if (lLine.startsWith("EDGEFORMAT\t"))
				{
					nodeindex1 = Integer.parseInt(lArray[1]);
					nodeindex2 = Integer.parseInt(lArray[2]);
					if (lArray.length >= 4 && !lArray[3].isEmpty())
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
				else if (lLine.startsWith("NODE\t"))
				{
					final String lNodeString = lArray[1];
					final int node = Integer.parseInt(lNodeString);
					addNodesUpTo(node);
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
						final int node1 = Integer.parseInt(lFirstNodeString);
						final int node2 = Integer.parseInt(lSecondNodeString);
						this.addEdge(node1, node2);
					}
				}

			}

	}

}
