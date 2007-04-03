package org.royerloic.structures.graph.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.royerloic.io.MatrixFile;
import org.royerloic.structures.Matrix;
import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;
import org.royerloic.structures.graph.HashGraph;
import org.royerloic.structures.graph.Node;
import org.royerloic.structures.graph.UndirectedEdge;
import org.royerloic.structures.graph.io.psimi.PsiMiGraph;
import org.royerloic.structures.graph.io.psimi.PsiMiNode;

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
	public static Graph<Node, Edge<Node>> load(File pFile) throws FileNotFoundException, IOException
	{
		HashGraph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();

		Map<String, Node> lStringIdToNodeMap = new HashMap<String, Node>();

		Matrix<String> lMatrix = MatrixFile.readMatrixFromFile(pFile, false, "\\s+");

		int lFirstNodeIndex = 1;
		int lSecondNodeIndex = 2;
		int lConfidenceValueIndex = 0;
		double lConfidenceThreshold = Double.NEGATIVE_INFINITY;

		for (List<String> lStringList : lMatrix)
		{
			String lLineType = lStringList.get(0);
			if (lLineType.equalsIgnoreCase("EDGEFORMAT"))
			{
				String lFirstNodeIndexString = lStringList.get(1);
				String lSecondNodeIndexString = lStringList.get(2);
				lFirstNodeIndex = Integer.parseInt(lFirstNodeIndexString);
				lSecondNodeIndex = Integer.parseInt(lSecondNodeIndexString);
				if (lStringList.size() >= 4)
				{
					String lConfidenceValueIndexString = lStringList.get(3).trim();
					if (lConfidenceValueIndexString.matches("[0-9]+"))
					{
						lConfidenceValueIndex = Integer.parseInt(lConfidenceValueIndexString);
					}
				}
				break;
			}
		}

		for (List<String> lStringList : lMatrix)
		{
			String lLineType = lStringList.get(0);
			if (lLineType.equalsIgnoreCase("CONFIDENCEVALUETHRESHOLD"))
			{
				String lThresholdString = lStringList.get(1);
				lConfidenceThreshold = Double.parseDouble(lThresholdString);
				break;
			}
		}

		boolean isNodeFilterDefined = false;
		Set<Node> lFilteredNodesSet = new HashSet<Node>();
		for (List<String> lStringList : lMatrix)
		{
			String lLineType = lStringList.get(0);
			if (lLineType.equalsIgnoreCase("NODEFILTER"))
			{
				isNodeFilterDefined = true;
				String lName = lStringList.get(1);
				Node lNode = new Node(lName);
				lFilteredNodesSet.add(lNode);
			}
		}

		for (List<String> lStringList : lMatrix)
		{
			String lLineType = lStringList.get(0);
			if (lLineType.equalsIgnoreCase("NODE"))
			{
				String lName = lStringList.get(1);
				Node lNode = new Node(lName);
				if (lFilteredNodesSet.contains(lNode) || !isNodeFilterDefined)
				{
					lGraph.addNode(lNode);
					lStringIdToNodeMap.put(lName, lNode);
				}
			}
		}

		for (List<String> lStringList : lMatrix)
		{
			String lLineType = lStringList.get(0);
			if (lLineType.equalsIgnoreCase("EDGE"))
			{
				String lNodeName1 = lStringList.get(lFirstNodeIndex);
				String lNodeName2 = lStringList.get(lSecondNodeIndex);

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

				if ((lFilteredNodesSet.contains(lFirstNode) && lFilteredNodesSet.contains(lSecondNode))
						|| !isNodeFilterDefined)
				{
					double lConfidenceValue = 1;
					if (lConfidenceValueIndex != 0)
					{
						String lConfidenceValueString = lStringList.get(lConfidenceValueIndex);
						lConfidenceValue = Double.parseDouble(lConfidenceValueString);
					}

					if (lConfidenceValue >= lConfidenceThreshold)
					{

						Edge<Node> lEdge = new UndirectedEdge<Node>(lFirstNode, lSecondNode);
						lGraph.addEdge(lEdge);
					}
				}
			}
		}

		for (List<String> lStringList : lMatrix)
		{
			String lLineType = lStringList.get(0);
			if (lLineType.equalsIgnoreCase("STAR"))
			{
				String lNodeName1 = lStringList.get(1);
				List<String> lNodesInStarList = lStringList.subList(2, lStringList.size());

				for (String lString : lNodesInStarList)
				{
					String lNodeName2 = lString;
					Node lFirstNode = lStringIdToNodeMap.get(lNodeName1);
					if (lFirstNode == null)
					{
						lFirstNode = new Node(lNodeName1);
						lStringIdToNodeMap.put(lNodeName1, lFirstNode);
					}

					if ((lFilteredNodesSet.contains(lNodeName1) && lFilteredNodesSet.contains(lNodeName2))
							|| !isNodeFilterDefined)
					{
						Node lSecondNode = lStringIdToNodeMap.get(lNodeName2);
						if (lSecondNode == null)
						{
							lSecondNode = new Node(lNodeName2);
							lStringIdToNodeMap.put(lNodeName2, lSecondNode);
						}

						Edge<Node> lEdge = new UndirectedEdge<Node>(lFirstNode, lSecondNode);
						lGraph.addEdge(lEdge);
					}
				}

			}
		}

		for (List<String> lStringList : lMatrix)
		{
			String lLineType = lStringList.get(0);
			if (lLineType.equalsIgnoreCase("SELECT"))
			{
				isNodeFilterDefined = true;
				String lNodeName = lStringList.get(1);
				String lDepthString = lStringList.get(2);
				Integer lDepth = Integer.parseInt(lDepthString);
				Node lNode = new Node(lNodeName);
				lFilteredNodesSet.add(lNode);
				lFilteredNodesSet.addAll(lGraph.getNodeNeighbours(lNode, lDepth));
			}
		}

		if (isNodeFilterDefined)
			for (Node lNode : new ArrayList<Node>(lGraph.getNodeSet()))
				if (!lFilteredNodesSet.contains(lNode))
				{
					lGraph.removeNode(lNode);
				}

		return lGraph;
	}

	/**
	 * @param <N>
	 * @param pGraph
	 * @param pFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static <N> void save(Graph<N, Edge<N>> pGraph, File pFile) throws FileNotFoundException, IOException
	{
		List<List<String>> lStringListList = new ArrayList<List<String>>();
		for (N lNode : pGraph.getNodeSet())
		{
			List<String> lEdgeList = new ArrayList<String>();
			lEdgeList.add("NODE");
			lEdgeList.add(lNode.toString());
			lStringListList.add(lEdgeList);
		}
		for (Edge<N> lEdge : pGraph.getEdgeSet())
		{
			List<String> lEdgeList = new ArrayList<String>();
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
	public static void save(PsiMiGraph pGraph, File pFile) throws FileNotFoundException, IOException
	{
		List<List<String>> lStringListList = new ArrayList<List<String>>();
		for (PsiMiNode lNode : pGraph.getNodeSet())
		{
			List<String> lNodeList = new ArrayList<String>();
			lNodeList.add("NODE");
			lNodeList.add(lNode.toString());
			lStringListList.add(lNodeList);

			List<String> lGoList = new ArrayList<String>();
			lGoList.add("ATTRIBUTE");
			lGoList.add(lNode.toString());
			lGoList.add("GO");
			for (Integer lInteger : lNode.getGoIdList())
			{
				lGoList.add(lInteger.toString());
			}
			lStringListList.add(lGoList);

			List<String> lDomainList = new ArrayList<String>();
			lDomainList.add("ATTRIBUTE");
			lDomainList.add(lNode.toString());
			lDomainList.add("DOMAIN");
			for (Integer lInteger : lNode.getInterproIdList())
			{
				lDomainList.add(lInteger.toString());
			}
			lStringListList.add(lDomainList);

		}
		for (Edge<PsiMiNode> lEdge : pGraph.getEdgeSet())
		{
			List<String> lEdgeList = new ArrayList<String>();
			lEdgeList.add("EDGE");
			lEdgeList.add(lEdge.getFirstNode().toString());
			lEdgeList.add(lEdge.getSecondNode().toString());
			lStringListList.add(lEdgeList);
		}
		MatrixFile.writeMatrixToFile(lStringListList, pFile);
	}

}
