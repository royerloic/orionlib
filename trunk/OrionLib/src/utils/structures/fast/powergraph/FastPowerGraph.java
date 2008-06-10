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
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import utils.structures.fast.graph.Edge;
import utils.structures.fast.graph.FastGraph;
import utils.structures.fast.graph.FastIntegerGraph;
import utils.structures.fast.set.FastBoundedIntegerSet;
import utils.structures.fast.set.FastSparseIntegerSet;
import utils.structures.graph.Node;
import utils.structures.map.HashSetMap;
import utils.structures.map.SetMap;

public class FastPowerGraph<N>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1;

	FastIntegerPowerGraph mFastIntegerPowerGraph;

	final HashMap<N, Integer> mNameToNodeMap = new HashMap<N, Integer>();
	final ArrayList<N> mNodeToNameMap = new ArrayList<N>();
	final HashMap<N, Integer> mNameToPowerNodeIdMap = new HashMap<N, Integer>();
	final HashMap<Integer, N> mPowerNodeIdToNameMap = new HashMap<Integer, N>();

	public FastPowerGraph()
	{
		super();
		mFastIntegerPowerGraph = new FastIntegerPowerGraph();
	}

	public FastPowerGraph(int pNumberOfNodes,
												int pNumberOfPowerNodes,
												int pNumberOfPowerEdges)
	{
		super();
		mFastIntegerPowerGraph = new FastIntegerPowerGraph(	pNumberOfNodes,
																												pNumberOfPowerNodes,
																												pNumberOfPowerEdges);
	}

	protected FastIntegerPowerGraph getUnderlyingFastIntegerPowerGraph()
	{
		return mFastIntegerPowerGraph;
	}

	@Override
	public String toString()
	{
		return toTabDel();
	}

	public String toTabDel()
	{
		StringBuilder lBuilder = new StringBuilder();
		for (int node : mFastIntegerPowerGraph.mNodesSet)
		{
			lBuilder.append("NODE\t" + mNodeToNameMap.get(node) + "\n");
		}

		for (int i = 1; i < mFastIntegerPowerGraph.mId2PowerNode.size(); i++)
		{
			lBuilder.append("SET\t" + mPowerNodeIdToNameMap.get(i) + "\n");
		}

		for (int lPowerNodeId : mFastIntegerPowerGraph.getPowerNodeIdSet())
			if (lPowerNodeId != 0)
			{
				for (int lNode : mFastIntegerPowerGraph.mId2PowerNode.get(lPowerNodeId))
				{
					lBuilder.append("IN\tnode" + mNodeToNameMap.get(lNode)
													+ "\t"
													+ mPowerNodeIdToNameMap.get(lPowerNodeId)
													+ "\n");
				}
			}

		for (int lPowerNodeId : mFastIntegerPowerGraph.getPowerNodeIdSet())
			if (lPowerNodeId != 0)
			{
				for (int lChildrenId : mFastIntegerPowerGraph.mHierarchyGraph.getOutgoingNodeNeighbours(lPowerNodeId))
				{
					lBuilder.append("IN\t" + mPowerNodeIdToNameMap.get(lChildrenId)
													+ "\t"
													+ mPowerNodeIdToNameMap.get(lPowerNodeId)
													+ "\n");
				}
			}

		for (int[] lEdge : mFastIntegerPowerGraph.mPowerEgdesGraph.getIntPairList())
		{
			String powernode1str = mPowerNodeIdToNameMap.get(lEdge[0]).toString();
			String powernode2str = mPowerNodeIdToNameMap.get(lEdge[1]).toString();

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
	public static FastPowerGraph<String> readEdgeFile(String pString) throws IOException
	{
		return readEdgeFile(new ByteArrayInputStream(pString.getBytes("UTF-8")));
	}

	/**
	 * This method reads BBL files written by this class. It does not read other
	 * BBL files, since it assumes nodes and sets of the syntax: setx and nodex.
	 * 
	 * @param pFile
	 * @throws IOException
	 */
	public static FastPowerGraph<String> readEdgeFile(File pFile) throws IOException
	{
		return readEdgeFile(new FileInputStream(pFile));
	}

	/**
	 * This method reads BBL files written by this class. It does not read other
	 * BBL files, since it assumes nodes and sets of the syntax: setx and nodex.
	 * 
	 * @param pInputStream
	 * @throws IOException
	 */
	public static FastPowerGraph<String> readEdgeFile(InputStream pInputStream) throws IOException
	{
		FastPowerGraph<String> lFastPowerGraph = new FastPowerGraph<String>();

		BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(pInputStream));
		Pattern lPattern = Pattern.compile("\t");

		final LinkedHashMap<String, Integer> lNodeSet2IdMap = new LinkedHashMap<String, Integer>();
		final HashSetMap<String, Integer> lPowerNodeNameMapSet = new HashSetMap<String, Integer>();
		final LinkedHashMap<String, String> lInMap = new LinkedHashMap<String, String>();
		final LinkedHashMap<String, String> lEdgeSet = new LinkedHashMap<String, String>();

		int nodecounter = 0;
		String lLine = "";
		while ((lLine = lBufferedReader.readLine()) != null)
			if (!lLine.isEmpty() && !lLine.startsWith("#") && !lLine.startsWith("//"))
			{
				final String[] lArray = lPattern.split(lLine, -1);
				if (lLine.startsWith("NODE\t"))
				{
					lNodeSet2IdMap.put(lArray[1], nodecounter);
					nodecounter++;
				}
				else if (lLine.startsWith("SET\t"))
				{
					lPowerNodeNameMapSet.put(lArray[1]);
				}
				else if (lLine.startsWith("IN\t"))
				{
					lInMap.put(lArray[1], lArray[2]);
				}
				else if (lLine.startsWith("EDGE\t"))
				{
					lEdgeSet.put(lArray[1], lArray[2]);
				}
			}

		// now we resolve the IN dependencies and add power nodes one by one:
		for (final Map.Entry<String, Set<Integer>> lEntry : lPowerNodeNameMapSet.entrySet())
		{
			final String lSetName = lEntry.getKey();
			computeSet(lNodeSet2IdMap, lPowerNodeNameMapSet, lInMap, lSetName);

			FastBoundedIntegerSet lSet = new FastBoundedIntegerSet(lEntry.getValue());

			lFastPowerGraph.mFastIntegerPowerGraph.addPowerNode(lSet);
		}

		return lFastPowerGraph;
	}

	private static void computeSet(	final LinkedHashMap<String, Integer> pNodeSet2IdMap,
																	final HashSetMap<String, Integer> pPowerNodeNameMapSet,
																	final LinkedHashMap<String, String> pInMap,
																	final String pSetName)
	{
		if (!pPowerNodeNameMapSet.get(pSetName).isEmpty())
		{
			return;
		}

		HashSet<Integer> lHashSet = new HashSet<Integer>();

		for (final Map.Entry<String, String> lEntry : pInMap.entrySet())
		{
			final String lNodeOrSetName1 = lEntry.getKey();
			final String lNodeOrSetName2 = lEntry.getValue();

			if (lNodeOrSetName2.equals(pSetName))
			{
				// First we check if the first name refers to a set or node
				if (pPowerNodeNameMapSet.get(lNodeOrSetName1) == null)
				{
					lHashSet.add(pNodeSet2IdMap.get(lNodeOrSetName1));
				}
				else
				{
					computeSet(	pNodeSet2IdMap,
											pPowerNodeNameMapSet,
											pInMap,
											lNodeOrSetName1);
					lHashSet.addAll(pPowerNodeNameMapSet.get(lNodeOrSetName1));
				}
			}
		}

		pPowerNodeNameMapSet.putAll(pSetName, lHashSet);
	}

}
