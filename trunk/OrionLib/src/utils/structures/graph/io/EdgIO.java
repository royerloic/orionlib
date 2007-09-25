package utils.structures.graph.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.io.MatrixFile;
import utils.structures.Matrix;
import utils.structures.graph.Edge;
import utils.structures.graph.Graph;
import utils.structures.graph.HashGraph;
import utils.structures.graph.Node;
import utils.structures.graph.UndirectedEdge;
import utils.structures.graph.io.psimi.PsiMiGraph;
import utils.structures.graph.io.psimi.PsiMiNode;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class EdgIO
{

	/**
	 * @param pFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Graph<Node, Edge<Node>> load(final File pFile) throws FileNotFoundException,
																															IOException
	{
		final HashGraph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();

		final Map<String, Node> lStringIdToNodeMap = new HashMap<String, Node>();

		final Matrix<String> lMatrix = MatrixFile.readMatrixFromFile(	pFile,
																																	false,
																																	"\\s+");

		int lFirstNodeIndex = 1;
		int lSecondNodeIndex = 2;
		int lConfidenceValueIndex = 0;
		double lConfidenceThreshold = Double.NEGATIVE_INFINITY;

		for (final List<String> lStringList : lMatrix)
		{
			final String lLineType = lStringList.get(0);
			if (lLineType.equalsIgnoreCase("EDGEFORMAT"))
			{
				final String lFirstNodeIndexString = lStringList.get(1);
				final String lSecondNodeIndexString = lStringList.get(2);
				lFirstNodeIndex = Integer.parseInt(lFirstNodeIndexString);
				lSecondNodeIndex = Integer.parseInt(lSecondNodeIndexString);
				if (lStringList.size() >= 4)
				{
					final String lConfidenceValueIndexString = lStringList.get(3).trim();
					if (lConfidenceValueIndexString.matches("[0-9]+"))
						lConfidenceValueIndex = Integer.parseInt(lConfidenceValueIndexString);
				}
				break;
			}
		}

		for (final List<String> lStringList : lMatrix)
		{
			final String lLineType = lStringList.get(0);
			if (lLineType.equalsIgnoreCase("CONFIDENCEVALUETHRESHOLD"))
			{
				final String lThresholdString = lStringList.get(1);
				lConfidenceThreshold = Double.parseDouble(lThresholdString);
				break;
			}
		}

		boolean isNodeFilterDefined = false;
		final Set<Node> lFilteredNodesSet = new HashSet<Node>();
		for (final List<String> lStringList : lMatrix)
		{
			final String lLineType = lStringList.get(0);
			if (lLineType.equalsIgnoreCase("NODESELECT"))
			{
				isNodeFilterDefined = true;
				final String lName = lStringList.get(1);
				final Node lNode = new Node(lName);
				lFilteredNodesSet.add(lNode);
			}
		}

		for (final List<String> lStringList : lMatrix)
		{
			final String lLineType = lStringList.get(0);
			if (lLineType.equalsIgnoreCase("NODE"))
			{
				final String lName = lStringList.get(1);
				final Node lNode = new Node(lName);
				if (lFilteredNodesSet.contains(lNode) || !isNodeFilterDefined)
				{
					lGraph.addNode(lNode);
					lStringIdToNodeMap.put(lName, lNode);
				}
			}
		}

		for (final List<String> lStringList : lMatrix)
		{
			final String lLineType = lStringList.get(0);
			if (lLineType.equalsIgnoreCase("EDGE"))
			{
				final String lNodeName1 = lStringList.get(lFirstNodeIndex);
				final String lNodeName2 = lStringList.get(lSecondNodeIndex);
				

				Node lFirstNode = lStringIdToNodeMap.get(lNodeName1);
				Node lSecondNode = lStringIdToNodeMap.get(lNodeName2);

				if (lFirstNode == null)
				{
					lFirstNode = new Node(lNodeName1);
					lStringIdToNodeMap.put(lNodeName1, lFirstNode);
				}

				if (lSecondNode == null)
				{
					lSecondNode = new Node(lNodeName2);
					lStringIdToNodeMap.put(lNodeName2, lSecondNode);
				}

				if ((lFilteredNodesSet.contains(lFirstNode) && lFilteredNodesSet.contains(lSecondNode)) || !isNodeFilterDefined)
				{
					double lConfidenceValue = 1;
					if (lConfidenceValueIndex != 0)
					{
						final String lConfidenceValueString = lStringList.get(lConfidenceValueIndex);
						lConfidenceValue = Double.parseDouble(lConfidenceValueString);
					}

					if (lConfidenceValue >= lConfidenceThreshold)
					{
						final Edge<Node> lEdge = new UndirectedEdge<Node>(lFirstNode,
																															lSecondNode);
						lEdge.setConfidence(lConfidenceValue);
						
						lGraph.addEdge(lEdge);

						/*for (final String lString : lStringList)
							System.out.print(lString + "\t");
						System.out.print("\n");/**/
					}
				}
			}
		}

		for (final List<String> lStringList : lMatrix)
		{
			final String lLineType = lStringList.get(0);
			if (lLineType.equalsIgnoreCase("STAR"))
			{
				final String lNodeName1 = lStringList.get(1);
				if (!lNodeName1.isEmpty())
				{
					final List<String> lNodesInStarList = lStringList.subList(2,
																																		lStringList.size());
					for (final String lString : lNodesInStarList)
						if (!lString.isEmpty())
						{
							final String lNodeName2 = lString;
							Node lFirstNode = lStringIdToNodeMap.get(lNodeName1);
							if (lFirstNode == null)
							{
								lFirstNode = new Node(lNodeName1);
								lStringIdToNodeMap.put(lNodeName1, lFirstNode);
							}

							if ((lFilteredNodesSet.contains(lNodeName1) && lFilteredNodesSet.contains(lNodeName2)) || !isNodeFilterDefined)
							{
								Node lSecondNode = lStringIdToNodeMap.get(lNodeName2);
								if (lSecondNode == null)
								{
									lSecondNode = new Node(lNodeName2);
									lStringIdToNodeMap.put(lNodeName2, lSecondNode);
								}

								final Edge<Node> lEdge = new UndirectedEdge<Node>(lFirstNode,
																																	lSecondNode);
								lGraph.addEdge(lEdge);
							}
						}
				}

			}
		}

		for (final List<String> lStringList : lMatrix)
		{
			final String lLineType = lStringList.get(0);
			if (lLineType.equalsIgnoreCase("NODEFILTER"))
			{
				isNodeFilterDefined = true;
				final String lNodeName = lStringList.get(1);
				final String lDepthString = lStringList.get(2);
				final Integer lDepth = Integer.parseInt(lDepthString);
				final Node lNode = new Node(lNodeName);
				lFilteredNodesSet.add(lNode);
				lFilteredNodesSet.addAll(lGraph.getNodeNeighbours(lNode, lDepth));
			}
		}

		if (isNodeFilterDefined)
			for (final Node lNode : new ArrayList<Node>(lGraph.getNodeSet()))
				if (!lFilteredNodesSet.contains(lNode))
					lGraph.removeNode(lNode);

		return lGraph;
	}

	/**
	 * @param <N>
	 * @param pGraph
	 * @param pFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static <N> void save(final Graph<N, Edge<N>> pGraph, final File pFile)	throws FileNotFoundException,
																																								IOException
	{
		final List<List<String>> lStringListList = new ArrayList<List<String>>();
		for (final N lNode : pGraph.getNodeSet())
		{
			final List<String> lEdgeList = new ArrayList<String>();
			lEdgeList.add("NODE");
			lEdgeList.add(lNode.toString());
			lStringListList.add(lEdgeList);
		}
		for (final Edge<N> lEdge : pGraph.getEdgeSet())
		{
			final List<String> lEdgeList = new ArrayList<String>();
			lEdgeList.add("EDGE");
			lEdgeList.add(lEdge.getFirstNode().toString());
			lEdgeList.add(lEdge.getSecondNode().toString());
			lStringListList.add(lEdgeList);
		}
		MatrixFile.writeMatrixToFile(lStringListList, pFile);
	}

	/**
	 * @param <N>
	 * @param pGraph
	 * @param pFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void save(final PsiMiGraph pGraph, final File pFile) throws FileNotFoundException,
																																		IOException
	{
		final List<List<String>> lStringListList = new ArrayList<List<String>>();
		for (final PsiMiNode lNode : pGraph.getNodeSet())
		{
			final List<String> lNodeList = new ArrayList<String>();
			lNodeList.add("NODE");
			lNodeList.add(lNode.toString());
			lStringListList.add(lNodeList);

			final List<String> lGoList = new ArrayList<String>();
			lGoList.add("ATTRIBUTE");
			lGoList.add(lNode.toString());
			lGoList.add("GO");
			for (final Integer lInteger : lNode.getGoIdList())
				lGoList.add(lInteger.toString());
			lStringListList.add(lGoList);

			final List<String> lDomainList = new ArrayList<String>();
			lDomainList.add("ATTRIBUTE");
			lDomainList.add(lNode.toString());
			lDomainList.add("DOMAIN");
			for (final Integer lInteger : lNode.getInterproIdList())
				lDomainList.add(lInteger.toString());
			lStringListList.add(lDomainList);

		}
		for (final Edge<PsiMiNode> lEdge : pGraph.getEdgeSet())
		{
			final List<String> lEdgeList = new ArrayList<String>();
			lEdgeList.add("EDGE");
			lEdgeList.add(lEdge.getFirstNode().toString());
			lEdgeList.add(lEdge.getSecondNode().toString());
			lStringListList.add(lEdgeList);
		}
		MatrixFile.writeMatrixToFile(lStringListList, pFile);
	}

}
