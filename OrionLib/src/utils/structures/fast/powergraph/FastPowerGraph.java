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
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import utils.structures.fast.graph.Edge;
import utils.structures.fast.set.FastBoundedIntegerSet;
import utils.structures.map.HashSetMap;

public class FastPowerGraph<N>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1;

	FastIntegerPowerGraph mFastIntegerPowerGraph;

	final HashMap<N, Integer> mNode2NodeIdMap = new HashMap<N, Integer>();
	final ArrayList<N> mNodeId2NodeList = new ArrayList<N>();
	final HashMap<N, Integer> mPowerNodeToPowerNodeIdMap = new HashMap<N, Integer>();
	final ArrayList<N> mPowerNodeIdToPowerNodeList = new ArrayList<N>();

	public FastPowerGraph()
	{
		super();
		mFastIntegerPowerGraph = new FastIntegerPowerGraph();
	}

	public FastPowerGraph(final int pNumberOfNodes,
												final int pNumberOfPowerNodes,
												final int pNumberOfPowerEdges)
	{
		super();
		mFastIntegerPowerGraph = new FastIntegerPowerGraph(	pNumberOfNodes,
																												pNumberOfPowerNodes,
																												pNumberOfPowerEdges);
	}

	public FastIntegerPowerGraph getUnderlyingFastIntegerPowerGraph()
	{
		return mFastIntegerPowerGraph;
	}

	public int addNode(final N pNode)
	{
		final int lIndex = mNodeId2NodeList.size();
		mNodeId2NodeList.add(pNode);
		mNode2NodeIdMap.put(pNode, lIndex);
		return lIndex;
	}

	public Integer getNodeId(final N pNode)
	{
		return mNode2NodeIdMap.get(pNode);
	}

	public List<N> getNodeList()
	{
		return Collections.unmodifiableList(mNodeId2NodeList);
	}

	public int getNumberOfNodes()
	{
		return mNodeId2NodeList.size();
	}

	public int addPowerNode(final N pPowerNode, final FastBoundedIntegerSet pSet)
	{
		if (pSet.isEmpty())
		{
			throw new InvalidParameterException("Cannot be empty");
		}

		final int lPowerNodeId = mFastIntegerPowerGraph.addPowerNode(pSet);

		if (mPowerNodeIdToPowerNodeList.size() == 0)
		{
			mPowerNodeIdToPowerNodeList.add(null);
		}

		mPowerNodeIdToPowerNodeList.add(pPowerNode);

		if (mPowerNodeIdToPowerNodeList.size() - 1 != lPowerNodeId)
		{
			throw new RuntimeException();
		}

		mPowerNodeToPowerNodeIdMap.put(pPowerNode, lPowerNodeId);
		return lPowerNodeId;
	}

	public Integer getPowerNodeId(final N pPowerNode)
	{
		return mPowerNodeToPowerNodeIdMap.get(pPowerNode);
	}

	public Set<N> getPowerNodeList()
	{
		return mPowerNodeToPowerNodeIdMap.keySet();
	}

	public int getNumberOfPowerNodes()
	{
		return mPowerNodeToPowerNodeIdMap.size();
	}
	
	public int getNumberOfNonSingletonPowerNodes()
	{
		return mFastIntegerPowerGraph.getNumberOfNonSingletonPowerNodes()-1;
	}

	public FastBoundedIntegerSet getPowerNodeIdSet()
	{
		return mFastIntegerPowerGraph.getPowerNodeIdSet();
	}

	public Set<N> getPowerNodeContent(final int pPowerNodeId)
	{
		final HashSet<N> lPowerNodeContents = new HashSet<N>(mFastIntegerPowerGraph.getNumberOfPowerNodes());
		for (final int lNodeId : mFastIntegerPowerGraph.getPowerNodeById(pPowerNodeId))
		{
			final N lNode = mNodeId2NodeList.get(lNodeId);
			lPowerNodeContents.add(lNode);
		}
		return lPowerNodeContents;
	}

	public ArrayList<Set<N>> getPowerNodeContentsList()
	{
		final ArrayList<Set<N>> lPowerNodeList = new ArrayList<Set<N>>(mPowerNodeIdToPowerNodeList.size());
		for (int lPowerNodeId = 1; lPowerNodeId < mPowerNodeIdToPowerNodeList.size(); lPowerNodeId++)
		{
			lPowerNodeList.add(getPowerNodeContent(lPowerNodeId));
		}
		return lPowerNodeList;
	}

	private N getPowerNode(final int pPowerNodeId)
	{
		return mPowerNodeIdToPowerNodeList.get(pPowerNodeId);
	}
	
	public int getNumberOfNodeOrPowerNodeChildren(int pPowerNodeId)
	{
		final int lNumberOfPowerNodeChildren = mFastIntegerPowerGraph.getPowerNodeChildrenOf(pPowerNodeId).size();
		final int lNumberOfNodeChildren = mFastIntegerPowerGraph.getExclusiveNodeChildren(pPowerNodeId).size();
		return lNumberOfPowerNodeChildren + lNumberOfNodeChildren;
	}
	

	public void addPowerEdge(final int pFirst, final int pSecond)
	{
		mFastIntegerPowerGraph.addPowerEdge(pFirst, pSecond);
	}

	public void addPowerEdge(final N pFirst, final N pSecond)
	{
		final int lFirst = mPowerNodeToPowerNodeIdMap.get(pFirst);
		final int lSecond = mPowerNodeToPowerNodeIdMap.get(pSecond);
		mFastIntegerPowerGraph.addPowerEdge(lFirst, lSecond);
	}

	public ArrayList<Edge<N>> getPowerEdgeList()
	{
		final ArrayList<Edge<N>> lPowerNodeList = new ArrayList<Edge<N>>(mFastIntegerPowerGraph.getNumberOfPowerEdges());
		for (final int[] lEdgeArray : mFastIntegerPowerGraph.getPowerEdgeList())
		{
			final N first = getPowerNode(lEdgeArray[0]);
			final N second = getPowerNode(lEdgeArray[1]);
			final Edge lEdge = new Edge<N>(first, second);
			lPowerNodeList.add(lEdge);
		}
		return lPowerNodeList;
	}

	public HashSet<Edge<N>> getPowerEdgeSet()
	{
		final HashSet<Edge<N>> lPowerNodeList = new HashSet<Edge<N>>(mFastIntegerPowerGraph.getNumberOfPowerEdges());
		for (final int[] lEdgeArray : mFastIntegerPowerGraph.getPowerEdgeList())
		{
			final N first = getPowerNode(lEdgeArray[0]);
			final N second = getPowerNode(lEdgeArray[1]);
			final Edge lEdge = new Edge<N>(first, second);
			lPowerNodeList.add(lEdge);
		}
		return lPowerNodeList;
	}
	
	public int getNumberOfPowerEdges()
	{
		return mFastIntegerPowerGraph.getNumberOfPowerEdges();
	}

	/** ********************************************************************** */
	@Override
	public String toString()
	{
		return toTabDel();
	}

	public String toTabDel()
	{
		final StringBuilder lBuilder = new StringBuilder();
		for (final int node : mFastIntegerPowerGraph.mNodesSet)
		{
			lBuilder.append("NODE\t" + mNodeId2NodeList.get(node) + "\n");
		}

		for (int i = 1; i < mFastIntegerPowerGraph.mId2PowerNode.size(); i++)
		{
			final Set<N> lPowerNodeContents = getPowerNodeContent(i);
			if (lPowerNodeContents.size() > 1)
			{
				lBuilder.append("SET\t" + mPowerNodeIdToPowerNodeList.get(i) + "\n");
			}
		}

		for (final int lPowerNodeId : mFastIntegerPowerGraph.getPowerNodeIdSet())
		{
			if (lPowerNodeId != 0)
			{
				final FastBoundedIntegerSet lPowerNodeContents = mFastIntegerPowerGraph.mId2PowerNode.get(lPowerNodeId);
				if (lPowerNodeContents.size() > 1)
				{
					for (final int lNode : lPowerNodeContents)
					{
						lBuilder.append("IN\t" + mNodeId2NodeList.get(lNode)
														+ "\t"
														+ mPowerNodeIdToPowerNodeList.get(lPowerNodeId)
														+ "\n");
					}
				}
			}
		}

		for (final int lPowerNodeId : mFastIntegerPowerGraph.getPowerNodeIdSet())
		{
			if (lPowerNodeId != 0)
			{
				for (final int lChildrenId : mFastIntegerPowerGraph.mHierarchyGraph.getOutgoingNodeNeighbours(lPowerNodeId))
				{
					lBuilder.append("IN\t" + mPowerNodeIdToPowerNodeList.get(lChildrenId)
													+ "\t"
													+ mPowerNodeIdToPowerNodeList.get(lPowerNodeId)
													+ "\n");
				}
			}
		}

		for (final int[] lEdge : mFastIntegerPowerGraph.getPowerEdgeList())
		{
			final String powernode1str = mPowerNodeIdToPowerNodeList.get(lEdge[0])
																															.toString();
			final String powernode2str = mPowerNodeIdToPowerNodeList.get(lEdge[1])
																															.toString();

			lBuilder.append("EDGE\t" + powernode1str + "\t" + powernode2str + "\n");
		}

		return lBuilder.toString();
	}

	public void writeBblFile(final File pFile) throws IOException
	{
		writeBblFile(new FileOutputStream(pFile));
	}

	public void writeBblFile(final OutputStream pOutputStream) throws IOException
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
	public static FastPowerGraph<String> readBblFile(final String pString) throws IOException
	{
		return readBblStream(new ByteArrayInputStream(pString.getBytes("UTF-8")));
	}

	/**
	 * This method reads BBL files written by this class. It does not read other
	 * BBL files, since it assumes nodes and sets of the syntax: setx and nodex.
	 * 
	 * @param pFile
	 * @throws IOException
	 */
	public static FastPowerGraph<String> readBblFile(final File pFile) throws IOException
	{
		return readBblStream(new FileInputStream(pFile));
	}

	/**
	 * This method reads BBL files written by this class. It does not read other
	 * BBL files, since it assumes nodes and sets of the syntax: setx and nodex.
	 * 
	 * @param pInputStream
	 * @throws IOException
	 */
	public static FastPowerGraph<String> readBblStream(final InputStream pInputStream) throws IOException
	{
		final FastPowerGraph<String> lFastPowerGraph = new FastPowerGraph<String>();

		final BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(pInputStream));
		final Pattern lPattern = Pattern.compile("\t");

		final HashSetMap<String, Integer> lPowerNodeNameMapSet = new HashSetMap<String, Integer>();
		final ArrayList<String[]> lInList = new ArrayList<String[]>();
		final ArrayList<String[]> lEdgeList = new ArrayList<String[]>();

		String lLine = "";
		while ((lLine = lBufferedReader.readLine()) != null)
		{
			if (!lLine.isEmpty() && !lLine.startsWith("#") && !lLine.startsWith("//"))
			{
				final String[] lArray = lPattern.split(lLine, -1);
				if (lLine.startsWith("NODE\t"))
				{
					lFastPowerGraph.addNode(lArray[1]);
				}
				else if (lLine.startsWith("SET\t"))
				{
					lPowerNodeNameMapSet.put(lArray[1]);
				}
				else if (lLine.startsWith("IN\t"))
				{
					lInList.add(new String[]
					{ lArray[1], lArray[2] });
				}
				else if (lLine.startsWith("EDGE\t"))
				{
					lEdgeList.add(new String[]
					{ lArray[1], lArray[2] });
				}
			}
		}

		// now we resolve the IN dependencies and add power nodes one by one:
		for (final Map.Entry<String, Set<Integer>> lEntry : lPowerNodeNameMapSet.entrySet())
		{
			final String lSetName = lEntry.getKey();
			computeSet(	lFastPowerGraph.mNode2NodeIdMap,
									lPowerNodeNameMapSet,
									lInList,
									lSetName);
		}

		// now we resolve the IN dependencies and add power nodes one by one:
		for (final Map.Entry<String, Set<Integer>> lEntry : lPowerNodeNameMapSet.entrySet())
		{
			final String lSetName = lEntry.getKey();
			final FastBoundedIntegerSet lSet = new FastBoundedIntegerSet(lEntry.getValue());
			lFastPowerGraph.addPowerNode(lSetName, lSet);
		}

		// and now we load the edges into the power graph:
		for (final String[] lEdge : lEdgeList)
		{
			Integer lFirst = lFastPowerGraph.getPowerNodeId(lEdge[0]);
			Integer lSecond = lFastPowerGraph.getPowerNodeId(lEdge[1]);

			if (lFirst == null)
			{
				final FastBoundedIntegerSet lSingletonSet = new FastBoundedIntegerSet();
				final Integer lNodeId = lFastPowerGraph.getNodeId(lEdge[0]);
				lSingletonSet.add(lNodeId);
				lFirst = lFastPowerGraph.addPowerNode(lEdge[0], lSingletonSet);
			}

			if (lSecond == null)
			{
				final FastBoundedIntegerSet lSingletonSet = new FastBoundedIntegerSet();
				final Integer lNodeId = lFastPowerGraph.getNodeId(lEdge[1]);
				lSingletonSet.add(lNodeId);
				lSecond = lFastPowerGraph.addPowerNode(lEdge[1], lSingletonSet);
			}

			lFastPowerGraph.addPowerEdge(lFirst, lSecond);
		}

		return lFastPowerGraph;
	}

	private static void computeSet(	final HashMap<String, Integer> pNodeSet2IdMap,
																	final HashSetMap<String, Integer> pPowerNodeNameMapSet,
																	final ArrayList<String[]> pInMap,
																	final String pSetName)
	{
		if (!pPowerNodeNameMapSet.get(pSetName).isEmpty())
		{
			return;
		}

		final HashSet<Integer> lHashSet = new HashSet<Integer>();

		for (final String[] lIn : pInMap)
		{
			final String lNodeOrSetName1 = lIn[0];
			final String lNodeOrSetName2 = lIn[1];

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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + getPowerNodeList().hashCode();
		result = prime * result + getNodeList().hashCode();
		result = prime * result + getPowerEdgeList().hashCode();
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
		final FastPowerGraph other = (FastPowerGraph) obj;

		if (!this.getPowerNodeList().equals(other.getPowerNodeList()))
		{
			return false;
		}

		if (!this.getNodeList().equals(other.getNodeList()))
		{
			return false;
		}

		if (!this.getPowerEdgeSet().equals(other.getPowerEdgeSet()))
		{
			return false;
		}

		return true;

	}





}
