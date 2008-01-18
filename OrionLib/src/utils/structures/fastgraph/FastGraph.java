package utils.structures.fastgraph;

import java.util.ArrayList;
import java.util.HashMap;

import utils.structures.fast.FastIntegerSet;

public class FastGraph
{
	ArrayList<int[]> mSparseMatrix = new ArrayList<int[]>();

	HashMap<String, Integer> mNodeToNameMap = new HashMap<String, Integer>();

	int mAfterLastNodeIndex = 0;

	int edgeCount = 0;

	public FastGraph()
	{
		super();
	}

	public int addNode(final String pNodeName)
	{
		if (mNodeToNameMap.containsKey(pNodeName))
		{
			return mNodeToNameMap.get(pNodeName);
		}
		else
		{
			mNodeToNameMap.put(pNodeName, mAfterLastNodeIndex);
			mSparseMatrix.add(new int[0]);
			final int lNodeIndex = mAfterLastNodeIndex;
			mAfterLastNodeIndex++;

			return lNodeIndex;
		}
	}
	

	public void addEdge(final String pNodeName1, final String pNodeName2)
	{
		if (!isEdge(pNodeName1, pNodeName2))
		{
			final int lNodeIndex1 = addNode(pNodeName1);
			final int lNodeIndex2 = addNode(pNodeName2);

			int[] lNeiSet1 = mSparseMatrix.get(lNodeIndex1);
			int[] lNeiSet2 = mSparseMatrix.get(lNodeIndex2);

			lNeiSet1 = FastIntegerSet.add(lNeiSet1, lNodeIndex2);
			lNeiSet2 = FastIntegerSet.add(lNeiSet2, lNodeIndex1);

			mSparseMatrix.set(lNodeIndex1, lNeiSet1);
			mSparseMatrix.set(lNodeIndex2, lNeiSet2);

			edgeCount++;
		}
	}

	public void removeEdge(final String pNodeName1, final String pNodeName2)
	{
		if (isEdge(pNodeName1, pNodeName2))
		{
			final int lNodeIndex1 = addNode(pNodeName1);
			final int lNodeIndex2 = addNode(pNodeName2);

			int[] lNeiSet1 = mSparseMatrix.get(lNodeIndex1);
			int[] lNeiSet2 = mSparseMatrix.get(lNodeIndex2);

			lNeiSet1 = FastIntegerSet.del(lNeiSet1, lNodeIndex2);
			lNeiSet2 = FastIntegerSet.del(lNeiSet2, lNodeIndex1);

			mSparseMatrix.set(lNodeIndex1, lNeiSet1);
			mSparseMatrix.set(lNodeIndex2, lNeiSet2);

			edgeCount--;
		}
	}
	
	public boolean isEdge(final String pNodeName1, final String pNodeName2)
	{
		final int lNodeIndex1 = addNode(pNodeName1);
		final int lNodeIndex2 = addNode(pNodeName2);

		int[] lNeiSet1 = mSparseMatrix.get(lNodeIndex1);
		return FastIntegerSet.contains(lNeiSet1, lNodeIndex2);
	}
	
	

	public int getNumberOfNodes()
	{
		return mNodeToNameMap.size();
	}

	public int getNumberOfEdges()
	{
		return edgeCount;
	}



}
