package org.royerloic.structures.powergraph.io;

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
import org.royerloic.structures.HashSetMap;
import org.royerloic.structures.SetMap;
import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Node;
import org.royerloic.structures.graph.UndirectedEdge;
import org.royerloic.structures.powergraph.PowerGraph;

public class PowerGraphIO
{

	public PowerGraphIO()
	{
		super();
	}

	public static <N> void savePowerGraph(PowerGraph<N> pPowerGraph, File pFile) throws FileNotFoundException,
			IOException
	{
		List<List<String>> lBubbleFormat = new ArrayList<List<String>>();

		for (N lNode : pPowerGraph.getNodeSet())
		{
			List<String> lNodeEntry = new ArrayList<String>();
			lNodeEntry.add("NODE");
			lNodeEntry.add(lNode.toString());
			lBubbleFormat.add(lNodeEntry);
		}

		Map<Set<N>, String> lPowerNodeToNameMap = new HashMap<Set<N>, String>();
		int lPowerNodeCounter = 0;
		for (Set<N> lPowerNode : pPowerGraph.getPowerNodeSet())
			if (lPowerNode.size() > 1)
			{
				List<String> lSetEntry = new ArrayList<String>();
				lSetEntry.add("SET");
				String lPowerNodeName = "PowerNode" + lPowerNodeCounter;
				lPowerNodeToNameMap.put(lPowerNode, lPowerNodeName);
				lSetEntry.add(lPowerNodeName);
				lBubbleFormat.add(lSetEntry);
				lPowerNodeCounter++;
			}
			else
			{
				lPowerNodeToNameMap.put(lPowerNode, lPowerNode.iterator().next().toString());
			}

		for (Set<N> lPowerNode : pPowerGraph.getPowerNodeSet())
			if (lPowerNode.size() > 1)
				for (N lNode : lPowerNode)
				{
					List<String> lNodeEntry = new ArrayList<String>();
					lNodeEntry.add("IN");
					lNodeEntry.add(lNode.toString());
					lNodeEntry.add(lPowerNodeToNameMap.get(lPowerNode));
					lBubbleFormat.add(lNodeEntry);
				}

		for (Edge<Set<N>> lPowerEdge : pPowerGraph.getPowerEdgeSet())
		{
			List<String> lEdgeEntry = new ArrayList<String>();
			lEdgeEntry.add("EDGE");
			Set<N> lFirstPowerNode = lPowerEdge.getFirstNode();
			Set<N> lSecondPowerNode = lPowerEdge.getSecondNode();
			lEdgeEntry.add(lPowerNodeToNameMap.get(lFirstPowerNode));
			lEdgeEntry.add(lPowerNodeToNameMap.get(lSecondPowerNode));
			lBubbleFormat.add(lEdgeEntry);
		}

		MatrixFile.writeMatrixToFile(lBubbleFormat, pFile);
	}

	public static PowerGraph<Node> loadPowerGraph(File pFile) throws FileNotFoundException, IOException
	{
		PowerGraph<Node> lPowerGraph = new PowerGraph<Node>();
		List<List<String>> lMatrix = MatrixFile.readMatrixFromFile(pFile, false);

		Map<String, Node> lNodeNameToNodeMap = new HashMap<String, Node>();
		SetMap<String, Node> lPowerNodeNameToSetMap = new HashSetMap<String, Node>();

		for (List<String> lLine : lMatrix)
		{
			if (lLine.get(0).equalsIgnoreCase("NODE"))
			{
				String lNodeName = lLine.get(1);
				Node lNode = new Node(lNodeName);
				lNodeNameToNodeMap.put(lNodeName, lNode);
				lPowerGraph.addNode(lNode);
			}
		}

		for (List<String> lLine : lMatrix)
		{
			if (lLine.get(0).equalsIgnoreCase("SET"))
			{
				lPowerNodeNameToSetMap.put(lLine.get(1));
			}
		}

		for (List<String> lLine : lMatrix)
		{
			if (lLine.get(0).equalsIgnoreCase("IN"))
			{
				String lNodeOrSetName1 = lLine.get(1);
				String lNodeOrSetName2 = lLine.get(2);

				// First we check if the first name refers to a set or node
				if (lPowerNodeNameToSetMap.get(lNodeOrSetName1) == null)
				{
					Node lNode = new Node(lNodeOrSetName1);
					lPowerNodeNameToSetMap.put(lNodeOrSetName2, lNode);
				}
				else
				{
					lPowerNodeNameToSetMap.get(lNodeOrSetName2).addAll(lPowerNodeNameToSetMap.get(lNodeOrSetName1));
				}
			}
		}

		for (Map.Entry<String, Set<Node>> lEntry : lPowerNodeNameToSetMap.entrySet())
		{
			lPowerGraph.addCluster(lEntry.getValue());
		}

		for (List<String> lLine : lMatrix)
		{
			if (lLine.get(0).equalsIgnoreCase("EDGE"))
			{
				String lNodeOrSetName1 = lLine.get(1);
				String lNodeOrSetName2 = lLine.get(2);
				Set<Node> lPowerNode1 = lPowerNodeNameToSetMap.get(lNodeOrSetName1);
				Set<Node> lPowerNode2 = lPowerNodeNameToSetMap.get(lNodeOrSetName2);

				if (lPowerNode1 == null)
				{
					lPowerNode1 = new HashSet<Node>();
					Node lNode = lNodeNameToNodeMap.get(lNodeOrSetName1);
					lPowerNode1.add(lNode);
				}

				if (lPowerNode2 == null)
				{
					lPowerNode2 = new HashSet<Node>();
					Node lNode = lNodeNameToNodeMap.get(lNodeOrSetName2);
					lPowerNode2.add(lNode);
				}

				Edge<Set<Node>> lPowerEdge = new UndirectedEdge<Set<Node>>(lPowerNode1, lPowerNode2);
				lPowerGraph.addPowerEdge(lPowerEdge);
			}
		}

		return lPowerGraph;
	}
}
