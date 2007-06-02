package utils.structures.powergraph.io;

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
import utils.structures.HashSetMap;
import utils.structures.SetMap;
import utils.structures.graph.Edge;
import utils.structures.graph.Node;
import utils.structures.graph.UndirectedEdge;
import utils.structures.powergraph.PowerGraph;

public class PowerGraphIO
{

	public PowerGraphIO()
	{
		super();
	}

	public static <N> void savePowerGraph(final PowerGraph<N> pPowerGraph, final File pFile) throws FileNotFoundException,
			IOException
	{
		final List<List<String>> lBubbleFormat = new ArrayList<List<String>>();

		for (final N lNode : pPowerGraph.getNodeSet())
		{
			final List<String> lNodeEntry = new ArrayList<String>();
			lNodeEntry.add("NODE");
			lNodeEntry.add(lNode.toString());
			lBubbleFormat.add(lNodeEntry);
		}

		final Map<Set<N>, String> lPowerNodeToNameMap = new HashMap<Set<N>, String>();
		int lPowerNodeCounter = 0;
		for (final Set<N> lPowerNode : pPowerGraph.getPowerNodeSet())
			if (lPowerNode.size() > 1)
			{
				final List<String> lSetEntry = new ArrayList<String>();
				lSetEntry.add("SET");
				final String lPowerNodeName = "PowerNode" + lPowerNodeCounter;
				lPowerNodeToNameMap.put(lPowerNode, lPowerNodeName);
				lSetEntry.add(lPowerNodeName);
				lBubbleFormat.add(lSetEntry);
				lPowerNodeCounter++;
			}
			else
				lPowerNodeToNameMap.put(lPowerNode, lPowerNode.iterator().next().toString());

		for (final Set<N> lPowerNode : pPowerGraph.getPowerNodeSet())
			if (lPowerNode.size() > 1)
				for (final N lNode : lPowerNode)
				{
					final List<String> lNodeEntry = new ArrayList<String>();
					lNodeEntry.add("IN");
					lNodeEntry.add(lNode.toString());
					lNodeEntry.add(lPowerNodeToNameMap.get(lPowerNode));
					lBubbleFormat.add(lNodeEntry);
				}

		for (final Edge<Set<N>> lPowerEdge : pPowerGraph.getPowerEdgeSet())
		{
			final List<String> lEdgeEntry = new ArrayList<String>();
			lEdgeEntry.add("EDGE");
			final Set<N> lFirstPowerNode = lPowerEdge.getFirstNode();
			final Set<N> lSecondPowerNode = lPowerEdge.getSecondNode();
			lEdgeEntry.add(lPowerNodeToNameMap.get(lFirstPowerNode));
			lEdgeEntry.add(lPowerNodeToNameMap.get(lSecondPowerNode));
			lBubbleFormat.add(lEdgeEntry);
		}

		MatrixFile.writeMatrixToFile(lBubbleFormat, pFile);
	}

	public static PowerGraph<Node> loadPowerGraph(final File pFile) throws FileNotFoundException, IOException
	{
		final PowerGraph<Node> lPowerGraph = new PowerGraph<Node>();
		final List<List<String>> lMatrix = MatrixFile.readMatrixFromFile(pFile, false);

		final Map<String, Node> lNodeNameToNodeMap = new HashMap<String, Node>();
		final SetMap<String, Node> lPowerNodeNameToSetMap = new HashSetMap<String, Node>();

		for (final List<String> lLine : lMatrix)
			if (lLine.get(0).equalsIgnoreCase("NODE"))
			{
				final String lNodeName = lLine.get(1);
				final Node lNode = new Node(lNodeName);
				lNodeNameToNodeMap.put(lNodeName, lNode);
				lPowerGraph.addNode(lNode);
			}

		for (final List<String> lLine : lMatrix)
			if (lLine.get(0).equalsIgnoreCase("SET"))
				lPowerNodeNameToSetMap.put(lLine.get(1));

		for (final List<String> lLine : lMatrix)
			if (lLine.get(0).equalsIgnoreCase("IN"))
			{
				final String lNodeOrSetName1 = lLine.get(1);
				final String lNodeOrSetName2 = lLine.get(2);

				// First we check if the first name refers to a set or node
				if (lPowerNodeNameToSetMap.get(lNodeOrSetName1) == null)
				{
					final Node lNode = new Node(lNodeOrSetName1);
					lPowerNodeNameToSetMap.put(lNodeOrSetName2, lNode);
				}
				else
					lPowerNodeNameToSetMap.get(lNodeOrSetName2).addAll(lPowerNodeNameToSetMap.get(lNodeOrSetName1));
			}

		for (final Map.Entry<String, Set<Node>> lEntry : lPowerNodeNameToSetMap.entrySet())
			lPowerGraph.addCluster(lEntry.getValue());

		for (final List<String> lLine : lMatrix)
			if (lLine.get(0).equalsIgnoreCase("EDGE"))
			{
				final String lNodeOrSetName1 = lLine.get(1);
				final String lNodeOrSetName2 = lLine.get(2);
				Set<Node> lPowerNode1 = lPowerNodeNameToSetMap.get(lNodeOrSetName1);
				Set<Node> lPowerNode2 = lPowerNodeNameToSetMap.get(lNodeOrSetName2);

				if (lPowerNode1 == null)
				{
					lPowerNode1 = new HashSet<Node>();
					final Node lNode = lNodeNameToNodeMap.get(lNodeOrSetName1);
					lPowerNode1.add(lNode);
				}

				if (lPowerNode2 == null)
				{
					lPowerNode2 = new HashSet<Node>();
					final Node lNode = lNodeNameToNodeMap.get(lNodeOrSetName2);
					lPowerNode2.add(lNode);
				}

				final Edge<Set<Node>> lPowerEdge = new UndirectedEdge<Set<Node>>(lPowerNode1, lPowerNode2);
				lPowerGraph.addPowerEdge(lPowerEdge);
			}

		return lPowerGraph;
	}
}
