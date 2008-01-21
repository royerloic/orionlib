package utils.structures.fast;

import java.util.ArrayList;
import java.util.HashMap;


public class FastGraph
{
	final FastIntegerGraph mFastIntegerGraph = new FastIntegerGraph();

	final HashMap<String, Integer> mNameToNodeMap = new HashMap<String, Integer>();
	final HashMap<Integer, String> mNodeToNameMap = new HashMap<Integer, String>();

	public FastGraph()
	{
		super();
	}

	public void addNode(final String pNodeName)
	{
		if (!mNodeToNameMap.containsKey(pNodeName))
		{
			final int lNodeIndex = mFastIntegerGraph.addNode();
			mNameToNodeMap.put(pNodeName, lNodeIndex);
			mNodeToNameMap.put(lNodeIndex, pNodeName);
		}
	}
	
	public boolean isNode(final String pNodeName)
	{
		Integer lNodeIndex=null;
		return (lNodeIndex=mNameToNodeMap.get(pNodeName))!=null && (mFastIntegerGraph.isNode(lNodeIndex));
	}

	public void addEdge(final String pNodeName1, final String pNodeName2)
	{
		Integer lNodeIndex1 = mNameToNodeMap.get(pNodeName1);
		Integer lNodeIndex2 = mNameToNodeMap.get(pNodeName2);

		if (lNodeIndex1 == null)
		{
			lNodeIndex1 = mFastIntegerGraph.addNode();
		}

		if (lNodeIndex2 == null)
		{
			lNodeIndex2 = mFastIntegerGraph.addNode();
		}

		mFastIntegerGraph.addEdge(lNodeIndex1, lNodeIndex2);
	}

	public void removeEdge(final String pNodeName1, final String pNodeName2)
	{
		final Integer lNodeIndex1 = mNameToNodeMap.get(pNodeName1);
		final Integer lNodeIndex2 = mNameToNodeMap.get(pNodeName2);

		if (lNodeIndex1 == null || lNodeIndex2 == null)
			return;

		mFastIntegerGraph.removeEdge(lNodeIndex1, lNodeIndex2);
	}

	public boolean isEdge(final String pNodeName1, final String pNodeName2)
	{
		final Integer lNodeIndex1 = mNameToNodeMap.get(pNodeName1);
		final Integer lNodeIndex2 = mNameToNodeMap.get(pNodeName2);

		if (lNodeIndex1 == null || lNodeIndex2 == null)
			return false;
		
		return mFastIntegerGraph.isEdge(lNodeIndex1, lNodeIndex2);
	}

	public int getNumberOfNodes()
	{
		return mFastIntegerGraph.getNumberOfNodes();
	}

	public int getNumberOfEdges()
	{
		return mFastIntegerGraph.getNumberOfEdges();
	}

}
