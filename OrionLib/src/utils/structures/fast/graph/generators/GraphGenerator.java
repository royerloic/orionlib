package utils.structures.fast.graph.generators;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import utils.structures.fast.graph.FastIntegerGraph;
import utils.structures.graph.Edge;
import utils.structures.graph.Graph;
import utils.structures.graph.HashGraph;
import utils.structures.graph.Node;
import utils.structures.graph.UndirectedEdge;

public class GraphGenerator
{

	private GraphGenerator()
	{
	}

	public static FastIntegerGraph generateRandomErdosGraph(final Random pRandom,
																													final int pNumberOfNodes,
																													final double pEdgeProbability)
	{
		FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

		for (int i = 0; i < pNumberOfNodes; i++)
		{
			lFastIntegerGraph.addNode();
		}

		int[] lNodeArray = lFastIntegerGraph.getNodeSet();

		for (int node1 = 0; node1 < lNodeArray.length; node1++)
			for (int node2 = 0; node2 < node1; node2++)
			{
				if (pRandom.nextDouble() < pEdgeProbability)
					lFastIntegerGraph.addEdge(node1, node2);
			}

		return lFastIntegerGraph;
	}

	public static Graph<Node, Edge<Node>> generateRandomGeometric2d(final Random pRandom,
																																	final int pNumberOfNodes,
																																	final double pDegree)
	{
		final double lRadius = Math.sqrt(pDegree / (Math.PI * pNumberOfNodes));
		final Random lRandom = new Random(System.currentTimeMillis());

		final Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();
		final Map<Integer, Node> lIndexToNodeMap = new HashMap<Integer, Node>();
		final double[][] lVectorArray = new double[pNumberOfNodes][];
		for (int i = 0; i < pNumberOfNodes; i++)
		{
			lVectorArray[i] = randomVector(lRandom, 2);
			final Node lNode = new Node("node " + i);
			lGraph.addNode(lNode);
			lIndexToNodeMap.put(i, lNode);
		}

		for (int i = 0; i < pNumberOfNodes; i++)
			for (int j = 0; j < i; j++)
			{
				final double[] lVector1 = lVectorArray[i];
				final double[] lVector2 = lVectorArray[j];
				final double lDistance = euclideanDistance(lVector1, lVector2);
				if (lDistance <= lRadius)
				{
					final Node lNode1 = lIndexToNodeMap.get(i);
					final Node lNode2 = lIndexToNodeMap.get(j);
					final Edge<Node> lEdge = new UndirectedEdge<Node>(lNode1, lNode2);
					lGraph.addEdge(lEdge);
				}
			}

		return lGraph;
	}

	private static double[] randomVector(	final Random pRandom,
																				final int pDimension)
	{
		final double[] lVector = new double[pDimension];
		for (int i = 0; i < pDimension; i++)
			lVector[i] = pRandom.nextDouble();
		return lVector;
	}

	private static double euclideanDistance(final double[] pVector1,
																					final double[] pVector2)
	{
		final int lLength = Math.max(pVector1.length, pVector2.length);
		double lSum = 0;
		for (int i = 0; i < lLength; i++)
		{
			final double lFirst = (i < pVector1.length) ? pVector1[i] : 0;
			final double lSecond = (i < pVector2.length) ? pVector2[i] : 0;
			lSum += (lFirst - lSecond) * (lFirst - lSecond);
		}
		final double lDistance = Math.sqrt(lSum);
		return lDistance;
	}

}
