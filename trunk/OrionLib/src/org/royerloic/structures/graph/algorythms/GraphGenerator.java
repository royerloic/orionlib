package org.royerloic.structures.graph.algorythms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.royerloic.random.DistributionSource;
import org.royerloic.random.RandomUtils;
import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;
import org.royerloic.structures.graph.HashGraph;
import org.royerloic.structures.graph.Node;
import org.royerloic.structures.graph.UndirectedEdge;

public class GraphGenerator
{

	private GraphGenerator()
	{
	}

	public static Graph<Node, Edge<Node>> generateDomainInducedGraph(	Random pRandom,
																																		int pNumberOfNodes,
																																		Graph<Node, Edge<Node>> pDomainGraph,
																																		double pAverageNumberOfDomains)
	{
		Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();
		List<Node> lNodeList = new ArrayList<Node>();
		List<Set<Node>> lDomainList = new ArrayList<Set<Node>>();
		double pDomainProbability = pAverageNumberOfDomains / pDomainGraph.getNodeSet().size();
		for (int i = 0; i < pNumberOfNodes; i++)
		{
			lNodeList.add(new Node("node" + i));
			Set<Node> lDomainSet = new HashSet<Node>();
			lDomainList.add(i, lDomainSet);
			for (Node lDomainNode : pDomainGraph.getNodeSet())
				if (pRandom.nextDouble() < pDomainProbability)
					lDomainSet.add(lDomainNode);
		}

		for (int i = 0; i < lNodeList.size(); i++)
			for (int j = 0; j < i; j++)
			{
				Node lNode1 = lNodeList.get(i);
				Node lNode2 = lNodeList.get(j);
				lGraph.addNode(lNode1);
				lGraph.addNode(lNode2);
				Set<Node> lDomainSet1 = lDomainList.get(i);
				Set<Node> lDomainSet2 = lDomainList.get(j);
				for (Node lDomain1 : lDomainSet1)
					for (Node lDomain2 : lDomainSet2)
						if (pDomainGraph.isEdge(lDomain1, lDomain2))
							lGraph.addEdge(new UndirectedEdge<Node>(lNode1, lNode2));
			}
		return lGraph;
	}

	public static Graph<Node, Edge<Node>> generateRandomErdosGraph(	Random pRandom,
																																	int pNumberOfNodes,
																																	double pEdgeProbability)
	{
		Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();
		List<Node> lNodeList = new ArrayList<Node>();
		for (int i = 0; i < pNumberOfNodes; i++)
		{
			Node lNode = new Node("node" + i);
			lNodeList.add(lNode);
			lGraph.addNode(lNode);
		}
		for (int i = 0; i < lNodeList.size(); i++)
			for (int j = 0; j < i; j++)
			{
				Node lNode1 = lNodeList.get(i);
				Node lNode2 = lNodeList.get(j);
				if (pRandom.nextDouble() < pEdgeProbability)
					lGraph.addEdge(new UndirectedEdge<Node>(lNode1, lNode2));
			}
		return lGraph;
	}

	public static Graph<Node, Edge<Node>> addErdosGraphNoise(	Random pRandom,
																														Graph<Node, Edge<Node>> pGraph,
																														double pFPNoiseRate,
																														double pFNNoiseRate)
	{
		Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>(pGraph);
		List<Node> lNodeList = new ArrayList<Node>(pGraph.getNodeSet());

		final int lNumberOfEdges = pGraph.getNumberOfEdges();

		final int lNumberOfEdgesToRemove = (int) (pGraph.getNumberOfEdges() * pFNNoiseRate);
		final int lNumberOfEdgesToAdd = (int) (pGraph.getNumberOfEdges() * pFPNoiseRate);

		List<Edge<Node>> lEdgeList = new ArrayList<Edge<Node>>(pGraph.getEdgeSet());

		for (int i = 0; i < lNumberOfEdgesToRemove; i++)
		{
			Edge<Node> lEdgeToRemove = RandomUtils.randomElement(pRandom, lEdgeList);
			lGraph.removeEdge(lEdgeToRemove.getFirstNode(),lEdgeToRemove.getSecondNode());
		}

		for (int i = 0; i < lNumberOfEdgesToRemove; i++)
		{
			Edge<Node> lEdgeToRemove = RandomUtils.randomElement(pRandom, lEdgeList);
			lGraph.removeEdge(lEdgeToRemove.getFirstNode(),lEdgeToRemove.getSecondNode());
			lGraph.removeEdge(lEdgeToRemove.getSecondNode(),lEdgeToRemove.getFirstNode());
		}

		for (int i = 0; i < lNumberOfEdgesToAdd; i++)
		{
			Node lNode1 = RandomUtils.randomElement(pRandom, lNodeList);
			Node lNode2 = RandomUtils.randomElement(pRandom, lNodeList);
			Edge<Node> lEdgeToAdd = new UndirectedEdge<Node>(lNode1, lNode2);
			lGraph.addEdge(lEdgeToAdd);
		}

		return lGraph;
	}

	public static Graph<Node, Edge<Node>> addSpokeNoise(Random pRandom,
																											Graph<Node, Edge<Node>> pGraph,
																											double pBaitProportion,
																											double pReconnectionProbability)
	{
		Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>(pGraph);
		List<Node> lNodeList = new ArrayList<Node>(pGraph.getNodeSet());
		List<Node> lBaitList = RandomUtils.randomSample(pRandom, pBaitProportion, lNodeList);

		for (Node lBait : lBaitList)
		{
			Set<Node> lNeighboursSet = lGraph.getNodeNeighbours(lBait);
			for (Node lNeighbour : lNeighboursSet)
			{
				Set<Node> lNeighboursSet2 = lGraph.getNodeNeighbours(lNeighbour);
				lNeighboursSet2.remove(lBait);
				List<Node> lReconnectionList = RandomUtils.randomSample(pRandom, pReconnectionProbability, lNeighboursSet2);
				for (Node lReconnectedNode : lReconnectionList)
				{
					lGraph.removeEdge(lReconnectedNode, lNeighbour);
					lGraph.removeEdge(lNeighbour,lReconnectedNode);
					lGraph.addEdge(new UndirectedEdge<Node>(lBait, lReconnectedNode));
				}
			}
		}
		
		return lGraph;
	}

	public static Graph<Node, Edge<Node>> randomSampling(	Random pRandom,
																												Graph<Node, Edge<Node>> pGraph,
																												double pSamplingRate)
	{
		Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();
		List<Node> lOriginalNodeList = new ArrayList<Node>(pGraph.getNodeSet());
		List<Node> lNewNodeList = new ArrayList<Node>();

		for (Node lNode : lOriginalNodeList)
			if (pRandom.nextDouble() < pSamplingRate)
				lNewNodeList.add(lNode);

		for (Node lNode : lNewNodeList)
		{
			lGraph.addNode(lNode);
		}

		for (int i = 0; i < lNewNodeList.size(); i++)
			for (int j = 0; j < i; j++)
			{
				Node lNode1 = lNewNodeList.get(i);
				Node lNode2 = lNewNodeList.get(j);

				if (pGraph.isEdge(lNode1, lNode2))
				{
					lGraph.addEdge(new UndirectedEdge<Node>(lNode1, lNode2));
				}
			}
		return lGraph;
	}

	public static Graph<Node, Edge<Node>> generateScaleFreePreferentialAttachement(	Random pRandom,
																																									Integer pNumberOfNodes,
																																									Double pAverageDegree,
																																									Double pExponent)
	{
		Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();
		int lCounter = 0;
		if (pNumberOfNodes > 0)
		{
			Node lFirstNode = new Node("node" + (lCounter++));
			lGraph.addNode(lFirstNode);

			while (lGraph.getNumberOfNodes() < pNumberOfNodes)
			{
				addNodePreferentialAttachement(pRandom, lGraph, lCounter++, pAverageDegree/2, pExponent);
			}

		}
		return lGraph;
	}

	@SuppressWarnings("boxing")
	static void addNodePreferentialAttachement(	Random pRandom,
																							Graph<Node, Edge<Node>> pGraph,
																							Integer pCounter,
																							Double pNumberOfNewEdges,
																							Double pExponent)
	{
		Node lNewNode = new Node("node" + pCounter);

		List<Node> lNodeList = new ArrayList<Node>(pGraph.getNodeSet());

		double lTotal = 0;
		for (Node lNode : lNodeList)
			lTotal += pGraph.getNodeNeighbours(lNode).size();

		if (lTotal == 0)
		{
			Edge<Node> lEdge = new UndirectedEdge<Node>(lNewNode, lNodeList.get(pRandom.nextInt(lNodeList.size())));
			pGraph.addEdge(lEdge);
		}
		else
		{
			DistributionSource<Node> lDistributionSource = new DistributionSource<Node>();
			for (Node lNode : lNodeList)
			{
				double lProbability = (((double) pGraph.getNodeNeighbours(lNode).size()) / lTotal);
				lProbability = Math.pow(lProbability, pExponent);
				lDistributionSource.addObject(lNode, lProbability);
			}
			try
			{
				lDistributionSource.prepare(Math.max(100000, lNodeList.size()), 0.01);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			long lNumberOfEdges =  RandomUtils.longGaussian(pRandom,pNumberOfNewEdges,0.5);
			lNumberOfEdges = (long) Math.min(lNumberOfEdges,2*pNumberOfNewEdges);
			for (int i = 0; i < lNumberOfEdges; i++)
			{
				Node lNode = lDistributionSource.getObject(pRandom);
				Edge<Node> lEdge = new UndirectedEdge<Node>(lNewNode, lNode);
				pGraph.addEdge(lEdge);
			}
		}

	}

	public static Graph<Node, Edge<Node>> generateScaleFreeTree(Random pRandom, int pNumberOfNodes)
	{
		Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();
		int lCounter = 0;
		if (pNumberOfNodes > 0)
		{
			Node lFirstNode = new Node("node" + (lCounter++));
			lGraph.addNode(lFirstNode);
			Node lCurrentNode = lFirstNode;

			while (lGraph.getNumberOfNodes() < pNumberOfNodes)
			{
				for (int i = 0; i < pRandom.nextInt(pNumberOfNodes); i++)
				{
					lCurrentNode = oneStep(pRandom, lGraph, lCurrentNode);
				}
				addNodeAndConnect(lGraph, lCurrentNode, lCounter++);
			}

		}
		return lGraph;
	}

	static Node oneStep(Random pRandom, Graph<Node, Edge<Node>> pGraph, Node pNode)
	{
		Set<Node> lNodeSet = pGraph.getNodeNeighbours(pNode);
		if (lNodeSet.isEmpty())
		{
			return pNode;
		}
		else
		{
			Node[] lNodeArray = lNodeSet.toArray(new Node[lNodeSet.size()]);
			return lNodeArray[pRandom.nextInt(lNodeArray.length)];
		}
	}

	static void addNodeAndConnect(Graph<Node, Edge<Node>> pGraph, Node pNode, int pCounter)
	{
		Node lNewNode = new Node("node" + pCounter);
		Edge<Node> lEdge = new UndirectedEdge(pNode, lNewNode);
		pGraph.addEdge(lEdge);
	}

	public static Graph<Node, Edge<Node>> generateScaleFreeDuplicationDivergenceRoyer(Random pRandom,
																																										int pNumberOfNodes,
																																										double pParameter)
	{
		Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();

		int lCounter = 0;
		if (pNumberOfNodes > 0)
		{
			Node lFirstNode = new Node("first node");
			lGraph.addNode(lFirstNode);
			Node lCurrentNode = lFirstNode;

			while (lGraph.getNumberOfNodes() < pNumberOfNodes)
			{
				for (int i = 0; i < pRandom.nextInt(pNumberOfNodes); i++)
				{
					lCurrentNode = oneStep(pRandom, lGraph, lCurrentNode);
				}

				Set<Node> lNodeSet = lGraph.getNodeNeighbours(lCurrentNode);

				if (lNodeSet.isEmpty())
				{
					addNodeAndConnect(lGraph, lCurrentNode, lCounter);
				}
				else
				{
					Node lNewNode = new Node("node" + lCounter);
					double lTotal = 0;
					for (Node lNode : lNodeSet)
						lTotal += lGraph.getNodeNeighbours(lNode).size();

					for (Node lNode : lNodeSet)
					{
						double lProbability = (((double) lGraph.getNodeNeighbours(lNode).size()) / lTotal);
						lProbability = Math.pow(lProbability, pParameter);
						if (pRandom.nextDouble() < lProbability)
						{
							Edge<Node> lEdge = new UndirectedEdge<Node>(lNewNode, lNode);
							lGraph.addEdge(lEdge);
						}
					}
				}
				System.out.print(".");
			}

		}
		return lGraph;
	}

	public static Graph<Node, Edge<Node>> generateScaleFreeDuplicationDivergence(	Random pRandom,
																																								int pNumberOfNodes,
																																								double pCopyProbability)
	{
		Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();

		int lCounter = 0;
		if (pNumberOfNodes > 0)
		{
			Node lFirstNode = new Node("first node");
			Node lSecondNode = new Node("second node");
			lGraph.addNode(lFirstNode);
			lGraph.addNode(lSecondNode);
			lGraph.addEdge(new UndirectedEdge<Node>(lFirstNode, lSecondNode));
			Node lCurrentNode = lFirstNode;

			while (lGraph.getNumberOfNodes() < pNumberOfNodes)
			{
				List<Node> lNodeList = new ArrayList<Node>(lGraph.getNodeSet());
				lCurrentNode = lNodeList.get(pRandom.nextInt(lNodeList.size()));

				Set<Node> lNodeSet = lGraph.getNodeNeighbours(lCurrentNode);

				if (!lNodeSet.isEmpty())
				{
					Node lNewNode = new Node("node" + lCounter);
					lGraph.addNode(lNewNode);
					lCounter++;
					for (Node lNode : lNodeSet)
					{
						if (pRandom.nextDouble() < pCopyProbability)
						{
							Edge<Node> lEdge = new UndirectedEdge<Node>(lNewNode, lNode);
							lGraph.addEdge(lEdge);
						}
					}
				}
				System.out.print(".");
			}

		}
		return lGraph;
	}

}
