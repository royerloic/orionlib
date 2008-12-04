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
import java.util.regex.Pattern;

import utils.structures.fast.set.FastBoundedIntegerSet;

public class FastIntegerDirectedGraph implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ArrayList<FastBoundedIntegerSet> mOutgoing;
	ArrayList<FastBoundedIntegerSet> mIncomming;

	int mEdgeCount = 0;

	public FastIntegerDirectedGraph()
	{
		super();
		mOutgoing = new ArrayList<FastBoundedIntegerSet>();
		mIncomming = new ArrayList<FastBoundedIntegerSet>();
	}

	public FastIntegerDirectedGraph(final int pNumberOfNodes)
	{
		super();
		mOutgoing = new ArrayList<FastBoundedIntegerSet>(pNumberOfNodes);
		mIncomming = new ArrayList<FastBoundedIntegerSet>(pNumberOfNodes);
		addNodesUpTo(pNumberOfNodes - 1); // 0 is the first node.
	}

	public int addNode()
	{
		mOutgoing.add(new FastBoundedIntegerSet());
		mIncomming.add(new FastBoundedIntegerSet());
		return mOutgoing.size() - 1;
	}

	public void addNodesUpTo(final int pNode)
	{
		if (!isNode(pNode))
		{
			while (addNode() < pNode)
			{
				;
			}
		}
	}

	public boolean isNode(final int pNodeIndex)
	{
		return pNodeIndex < mOutgoing.size();
	}

	public void addEdges(final int pNode1, final FastBoundedIntegerSet pNodeSet)
	{
		for (final int lNode2 : pNodeSet)
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

			final FastBoundedIntegerSet lNeiSet1 = mOutgoing.get(pNode1);
			final FastBoundedIntegerSet lNeiSet2 = mIncomming.get(pNode2);

			lNeiSet1.add(pNode2);
			lNeiSet2.add(pNode1);

			mOutgoing.set(pNode1, lNeiSet1);
			mIncomming.set(pNode2, lNeiSet2);

			mEdgeCount++;
		}
	}

	public void removeEdge(final int pNode1, final int pNode2)
	{
		if (isEdge(pNode1, pNode2))
		{
			final FastBoundedIntegerSet lNeiSet1 = mOutgoing.get(pNode1);
			final FastBoundedIntegerSet lNeiSet2 = mIncomming.get(pNode2);

			lNeiSet1.remove(pNode2);
			lNeiSet2.remove(pNode1);

			mOutgoing.set(pNode1, lNeiSet1);
			mIncomming.set(pNode2, lNeiSet2);

			mEdgeCount--;
		}
	}

	public boolean isEdge(final int pNode1, final int pNode2)
	{
		final int lAfterLastIndex = mOutgoing.size();
		if (pNode1 >= lAfterLastIndex || pNode2 >= lAfterLastIndex)
		{
			return false;
		}

		final FastBoundedIntegerSet lNeiSet1 = mOutgoing.get(pNode1);
		final FastBoundedIntegerSet lNeiSet2 = mIncomming.get(pNode2);
		// not optimized but we could detect bugs better.
		return lNeiSet1.contains(pNode2) && lNeiSet2.contains(pNode1);
	}

	public FastBoundedIntegerSet getNodeSet()
	{
		final FastBoundedIntegerSet lNodeSet = new FastBoundedIntegerSet(mOutgoing.size());
		for (int i = 0; i < mOutgoing.size(); i++)
		{
			lNodeSet.add(i);
		}
		return lNodeSet;
	}

	public ArrayList<int[]> getIntPairList()
	{
		// NOT OPTIMIZED: should use an iterator to avoid allocating data
		final ArrayList<int[]> lEdgeList = new ArrayList<int[]>();
		final boolean[] lVisited = new boolean[mOutgoing.size()];
		for (int node1 = 0; node1 < mOutgoing.size(); node1++)
		{
			final FastBoundedIntegerSet lNei = mOutgoing.get(node1);
			for (final int node2 : lNei)
			{
				if (!lVisited[node2])
				{
					lEdgeList.add(new int[]
					{ node1, node2 });

				}
			}
			lVisited[node1] = true;
		}
		return lEdgeList;
	}

	public ArrayList<Edge<Integer>> getEdgeList()
	{
		// NOT OPTIMIZED: should use an iterator to avoid allocating data
		final ArrayList<Edge<Integer>> lEdgeList = new ArrayList<Edge<Integer>>();
		final boolean[] lVisited = new boolean[mOutgoing.size()];
		for (int node1 = 0; node1 < mOutgoing.size(); node1++)
		{
			final FastBoundedIntegerSet lNei = mOutgoing.get(node1);
			for (final int node2 : lNei)
			{
				if (!lVisited[node2])
				{
					lEdgeList.add(new Edge<Integer>(node1, node2));

				}
			}
			lVisited[node1] = true;
		}
		return lEdgeList;
	}

	public int getNumberOfNodes()
	{
		return mOutgoing.size();
	}

	public int getNumberOfEdges()
	{
		return mEdgeCount;
	}

	public double getAverageDegree()
	{
		return (double) 2 * getNumberOfEdges() / getNumberOfNodes();
	}

	public double getEdgeDensity()
	{
		return (double) getNumberOfEdges() / (double) (getNumberOfNodes() * (getNumberOfNodes() - 1) / 2);
	}

	public FastBoundedIntegerSet getOutgoingNodeNeighbours(final int pNode)
	{
		return mOutgoing.get(pNode);
	}

	/**
	 * Does not includes node itself, even if it is a neighbours of one of its
	 * neighnboors.
	 */
	public FastBoundedIntegerSet getOutgoingNodeNeighbours(	final int pNode,
																													final int pDepth)
	{
		if (pDepth == 0)
		{
			return new FastBoundedIntegerSet();
		}
		else if (pDepth == 1)
		{
			return mOutgoing.get(pNode);
		}
		else if (pDepth > 1)
		{
			FastBoundedIntegerSet lNeiResult = new FastBoundedIntegerSet();
			final FastBoundedIntegerSet lNei = mOutgoing.get(pNode);
			lNeiResult = FastBoundedIntegerSet.union(lNeiResult, lNei);

			for (final int lNode : lNei)
			{
				final FastBoundedIntegerSet lNeiNei = getOutgoingNodeNeighbours(lNode,
																																				pDepth - 1);
				lNeiResult = FastBoundedIntegerSet.union(lNeiResult, lNeiNei);
			}
			lNeiResult.remove(pNode);
			return lNeiResult;
		}
		else
		{
			throw new IndexOutOfBoundsException("Argument pDepth cannot be negative: " + pDepth);
		}
	}

	public FastBoundedIntegerSet getOutgoingTransitiveClosure(final int pNodeId)
	{
		final FastBoundedIntegerSet lClosure = new FastBoundedIntegerSet();
		final FastBoundedIntegerSet lFrontier = new FastBoundedIntegerSet();
		lFrontier.add(pNodeId);

		while (!lFrontier.isEmpty())
		{
			lClosure.union(lFrontier);
			for (final Integer lNodeId : lFrontier)
			{
				final FastBoundedIntegerSet lNei = getOutgoingNodeNeighbours(lNodeId);
				lFrontier.union(lNei);
			}
			lFrontier.difference(lClosure);
		}
		lClosure.remove(pNodeId);
		return lClosure;
	}

	public FastBoundedIntegerSet getIncommingNodeNeighbours(final int pNode)
	{
		return mIncomming.get(pNode);
	}

	/**
	 * Does not includes node itself, even if it is a neighbours of one of its
	 * neighnboors.
	 */
	public FastBoundedIntegerSet getIncommingNodeNeighbours(final int pNode,
																													final int pDepth)
	{
		if (pDepth == 0)
		{
			return new FastBoundedIntegerSet();
		}
		else if (pDepth == 1)
		{
			return mIncomming.get(pNode);
		}
		else if (pDepth > 1)
		{
			FastBoundedIntegerSet lNeiResult = new FastBoundedIntegerSet();
			final FastBoundedIntegerSet lNei = mIncomming.get(pNode);
			lNeiResult = FastBoundedIntegerSet.union(lNeiResult, lNei);

			for (final int lNode : lNei)
			{
				final FastBoundedIntegerSet lNeiNei = getIncommingNodeNeighbours(	lNode,
																																					pDepth - 1);
				lNeiResult = FastBoundedIntegerSet.union(lNeiResult, lNeiNei);
			}
			lNeiResult.remove(pNode);
			return lNeiResult;
		}
		else
		{
			throw new IndexOutOfBoundsException("Argument pDepth cannot be negative: " + pDepth);
		}
	}

	public FastBoundedIntegerSet getIncommingTransitiveClosure(final int pNodeId)
	{
		final FastBoundedIntegerSet lClosure = new FastBoundedIntegerSet();
		final FastBoundedIntegerSet lFrontier = new FastBoundedIntegerSet();
		lFrontier.add(pNodeId);

		while (!lFrontier.isEmpty())
		{
			lClosure.union(lFrontier);
			for (final Integer lNodeId : lFrontier)
			{
				final FastBoundedIntegerSet lNei = getIncommingNodeNeighbours(lNodeId);
				lFrontier.union(lNei);
			}
			lFrontier.difference(lClosure);
		}
		lClosure.remove(pNodeId);
		return lClosure;
	}

	public FastIntegerDirectedGraph extractStrictSubGraph(final FastBoundedIntegerSet pNodeSet)
	{
		final FastIntegerDirectedGraph lNewGraph = new FastIntegerDirectedGraph();

		for (final int i : pNodeSet)
		{
			FastBoundedIntegerSet lNei = mOutgoing.get(i);
			lNei = FastBoundedIntegerSet.intersection(lNei, pNodeSet);
			lNewGraph.addNodesUpTo(i);
			lNewGraph.addEdges(i, lNei);
		}

		return lNewGraph;
	}

	public FastIntegerDirectedGraph extractSubGraph(final int[] pNodeSet)
	{
		final FastIntegerDirectedGraph lNewGraph = new FastIntegerDirectedGraph();

		for (final int i : pNodeSet)
		{
			final FastBoundedIntegerSet lNei = mOutgoing.get(i);
			lNewGraph.addEdges(i, lNei);
		}
		for (final int i : pNodeSet)
		{
			final FastBoundedIntegerSet lNei = mIncomming.get(i);
			lNewGraph.addEdges(i, lNei);
		}

		return lNewGraph;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;

		for (final FastBoundedIntegerSet lNeiSet : mOutgoing)
		{
			result = prime * result + lNeiSet.hashCode();
		}

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
		final FastIntegerDirectedGraph other = (FastIntegerDirectedGraph) obj;

		if (mOutgoing.size() != other.mOutgoing.size())
		{
			return false;
		}
		else
		{
			for (int i = 0; i < mOutgoing.size(); i++)
			{
				if (!mOutgoing.get(i).equals(mOutgoing.get(i)))
				{
					return false;
				}
			}
			return true;
		}
	}

	@Override
	public String toString()
	{
		final StringBuilder lStringBuilder = new StringBuilder();
		int lCurrentNode = 0;
		for (final FastBoundedIntegerSet lNeiSet : mOutgoing)
		{
			lStringBuilder.append(lCurrentNode + " -> " + lNeiSet.toString() + "\n");
			lCurrentNode++;
		}
		return lStringBuilder.toString();
	}

	public void writeEdgeFile(final File pFile) throws IOException
	{
		writeEdgeFile(new FileOutputStream(pFile));
	}

	public void writeEdgeFile(final OutputStream pOutputStream) throws IOException
	{
		final Writer lWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream));

		for (final int lNode : getNodeSet())
		{
			lWriter.append("NODE\t" + lNode + "\n");
		}

		for (final int[] lEdge : getIntPairList())
		{
			lWriter.append("EDGE\t" + lEdge[0] + "\t" + lEdge[1] + "\n");
		}
		lWriter.flush();

	}

	public void readEdgeFile(final File pFile) throws IOException
	{
		readEdgeFile(new FileInputStream(pFile));
	}

	public void readEdgeFile(final InputStream pInputStream) throws IOException
	{
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

}
