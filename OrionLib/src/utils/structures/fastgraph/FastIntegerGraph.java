package utils.structures.fastgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import utils.structures.fast.FastIntegerSet;

public class FastIntegerGraph
{
	ArrayList<int[]> mSparseMatrix;

	int mAfterLastNodeIndex = 0;
	int mEdgeCount = 0;

	public FastIntegerGraph()
	{
		super();
		mSparseMatrix = new ArrayList<int[]>();
	}

	public FastIntegerGraph(final int pNumberOfNodes)
	{
		super();
		mSparseMatrix = new ArrayList<int[]>(pNumberOfNodes);
		addNodesUpTo(pNumberOfNodes - 1); // 0 is the first node.
	}

	public int addNode()
	{
		mSparseMatrix.add(new int[0]);
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

	public void addEdges(final int pNode1, final int[] pNodeSet)
	{
		for (int lNode2 : pNodeSet)
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

			int[] lNeiSet1 = mSparseMatrix.get(pNode1);
			int[] lNeiSet2 = mSparseMatrix.get(pNode2);

			lNeiSet1 = FastIntegerSet.add(lNeiSet1, pNode2);
			lNeiSet2 = FastIntegerSet.add(lNeiSet2, pNode1);

			mSparseMatrix.set(pNode1, lNeiSet1);
			mSparseMatrix.set(pNode2, lNeiSet2);

			mEdgeCount++;
		}
	}

	public void removeEdge(final int pNode1, final int pNode2)
	{
		if (isEdge(pNode1, pNode2))
		{

			int[] lNeiSet1 = mSparseMatrix.get(pNode1);
			int[] lNeiSet2 = mSparseMatrix.get(pNode2);

			lNeiSet1 = FastIntegerSet.del(lNeiSet1, pNode2);
			lNeiSet2 = FastIntegerSet.del(lNeiSet2, pNode1);

			mSparseMatrix.set(pNode1, lNeiSet1);
			mSparseMatrix.set(pNode2, lNeiSet2);

			mEdgeCount--;
		}
	}

	public boolean isEdge(final int pNode1, final int pNode2)
	{
		if (pNode1 >= mAfterLastNodeIndex || pNode2 >= mAfterLastNodeIndex)
			return false;

		int[] lNeiSet1 = mSparseMatrix.get(pNode1);
		int[] lNeiSet2 = mSparseMatrix.get(pNode2);
		// not optimized but we could detect bugs better.
		return FastIntegerSet.contains(lNeiSet1, pNode2) && FastIntegerSet.contains(lNeiSet2,
																																								pNode1);
	}

	public int[] getNodeSet()
	{
		final int[] lNodeSet = new int[mSparseMatrix.size()];
		for (int i = 0; i < mSparseMatrix.size(); i++)
			lNodeSet[i] = i;
		return lNodeSet;
	}

	public int getNumberOfNodes()
	{
		return mSparseMatrix.size();
	}

	public int getNumberOfEdges()
	{
		return mEdgeCount;
	}

	public int[] getNodeNeighbours(final int pNode)
	{
		return mSparseMatrix.get(pNode);
	}

	/**
	 * Does not includes node itself, even if it is a neighbours of one of its
	 * neighnboors.
	 */
	public int[] getNodeNeighbours(final int pNode, final int pDepth)
	{
		if (pDepth == 0)
		{
			return new int[0];
		}
		else if (pDepth == 1)
		{
			return mSparseMatrix.get(pNode);
		}
		else if (pDepth > 1)
		{
			int[] lNeiResult = new int[0];
			int[] lNei = mSparseMatrix.get(pNode);
			lNeiResult = FastIntegerSet.union(lNeiResult, lNei);

			for (int lNode : lNei)
			{
				int[] lNeiNei = getNodeNeighbours(lNode, pDepth - 1);
				lNeiResult = FastIntegerSet.union(lNeiResult, lNeiNei);
			}
			lNeiResult = FastIntegerSet.del(lNeiResult, pNode);
			return lNeiResult;
		}
		else
		{
			throw new IndexOutOfBoundsException("Argument pDepth cannot be negative: " + pDepth);
		}
	}

	public FastIntegerGraph extractStrictSubGraph(int[] pNodeSet)
	{
		FastIntegerGraph lNewGraph = new FastIntegerGraph();

		for (int i : pNodeSet)
		{
			int[] lNei = mSparseMatrix.get(i);
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
			int[] lNei = mSparseMatrix.get(i);
			lNewGraph.addEdges(i, lNei);
		}

		return lNewGraph;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;

		for (int[] lNeiSet : mSparseMatrix)
		{
			result = prime * result + Arrays.hashCode(lNeiSet);
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
		else if (!mSparseMatrix.equals(other.mSparseMatrix))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder lStringBuilder = new StringBuilder();
		int lCurrentNode = 0;
		for (int[] lNeiSet : mSparseMatrix)
		{
			lStringBuilder.append(lCurrentNode + " - "
														+ Arrays.toString(lNeiSet)
														+ "\n");
			lCurrentNode++;
		}
		return lStringBuilder.toString();
	}

}
