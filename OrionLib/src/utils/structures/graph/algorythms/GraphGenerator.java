package utils.structures.graph.algorythms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import utils.random.DistributionSource;
import utils.random.RandomUtils;
import utils.structures.Couple;
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

	public static Graph<Node, Edge<Node>> generateDomainInducedGraph(	final Random pRandom,
																																		final int pNumberOfNodes,
																																		final Graph<Node, Edge<Node>> pDomainGraph,
																																		final double pAverageNumberOfDomains)
	{
		final Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();
		final List<Node> lNodeList = new ArrayList<Node>();
		final List<Set<Node>> lDomainList = new ArrayList<Set<Node>>();
		final double pDomainProbability = pAverageNumberOfDomains / pDomainGraph.getNodeSet().size();
		for (int i = 0; i < pNumberOfNodes; i++)
		{
			lNodeList.add(new Node("node" + i));
			final Set<Node> lDomainSet = new HashSet<Node>();
			lDomainList.add(i, lDomainSet);
			for (final Node lDomainNode : pDomainGraph.getNodeSet())
				if (pRandom.nextDouble() < pDomainProbability)
					lDomainSet.add(lDomainNode);
		}

		for (int i = 0; i < lNodeList.size(); i++)
			for (int j = 0; j < i; j++)
			{
				final Node lNode1 = lNodeList.get(i);
				final Node lNode2 = lNodeList.get(j);
				lGraph.addNode(lNode1);
				lGraph.addNode(lNode2);
				final Set<Node> lDomainSet1 = lDomainList.get(i);
				final Set<Node> lDomainSet2 = lDomainList.get(j);
				for (final Node lDomain1 : lDomainSet1)
					for (final Node lDomain2 : lDomainSet2)
						if (pDomainGraph.isEdge(lDomain1, lDomain2))
							lGraph.addEdge(new UndirectedEdge<Node>(lNode1, lNode2));
			}
		return lGraph;
	}

	public static Graph<Node, Edge<Node>> generateRandomErdosGraph(	final Random pRandom,
																																	final int pNumberOfNodes,
																																	final double pEdgeProbability)
	{
		final Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();
		final List<Node> lNodeList = new ArrayList<Node>();
		for (int i = 0; i < pNumberOfNodes; i++)
		{
			final Node lNode = new Node("node" + i);
			lNodeList.add(lNode);
			lGraph.addNode(lNode);
		}
		for (int i = 0; i < lNodeList.size(); i++)
			for (int j = 0; j < i; j++)
			{
				final Node lNode1 = lNodeList.get(i);
				final Node lNode2 = lNodeList.get(j);
				if (pRandom.nextDouble() < pEdgeProbability)
					lGraph.addEdge(new UndirectedEdge<Node>(lNode1, lNode2));
			}
		return lGraph;
	}

	public static Graph<Node, Edge<Node>> addErdosGraphNoise(	final Random pRandom,
																														final Graph<Node, Edge<Node>> pGraph,
																														final double pFPNoiseRate,
																														final double pFNNoiseRate)
	{
		final Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>(pGraph);
		final List<Node> lNodeList = new ArrayList<Node>(pGraph.getNodeSet());

		final int lNumberOfEdges = pGraph.getNumberOfEdges();

		final int lNumberOfEdgesToRemove = (int) (pGraph.getNumberOfEdges() * pFNNoiseRate);
		final int lNumberOfEdgesToAdd = (int) (pGraph.getNumberOfEdges() * pFPNoiseRate);

		final List<Edge<Node>> lEdgeList = new ArrayList<Edge<Node>>(pGraph.getEdgeSet());

		for (int i = 0; i < lNumberOfEdgesToRemove; i++)
		{
			final Edge<Node> lEdgeToRemove = RandomUtils.randomElement(pRandom, lEdgeList);
			lEdgeList.remove(lEdgeToRemove);
			lGraph.removeEdge(lEdgeToRemove.getFirstNode(), lEdgeToRemove.getSecondNode());
			lGraph.removeEdge(lEdgeToRemove.getSecondNode(), lEdgeToRemove.getFirstNode());
		}

		for (int i = 0; i < lNumberOfEdgesToAdd; i++)
		{
			Node lNode1;
			Node lNode2;
			do
			{
				lNode1 = RandomUtils.randomElement(pRandom, lNodeList);
				lNode2 = RandomUtils.randomElement(pRandom, lNodeList);
			}
			while (lGraph.isEdge(lNode1, lNode2));

			final Edge<Node> lEdgeToAdd = new UndirectedEdge<Node>(lNode1, lNode2);
			lGraph.addEdge(lEdgeToAdd);
		}

		if (lGraph.getNumberOfEdges() != pGraph.getNumberOfEdges())
			throw new RuntimeException("Not Average Degree invariant!"
					+ (lGraph.getNumberOfEdges() - pGraph.getNumberOfEdges()));

		return lGraph;
	}

	public static Couple<Graph<Node, Edge<Node>>, Double> addSpokeNoise(final Random pRandom,
																																			final Graph<Node, Edge<Node>> pGraph,
																																			final double pBaitProportion,
																																			final double pReconnectionProbability)
	{
		final Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>(pGraph);
		final List<Node> lNodeList = new ArrayList<Node>(pGraph.getNodeSet());

		int lCounter = 0;
		do
		{
			final List<Node> lBaitList = RandomUtils.randomSample(pRandom, pBaitProportion, lNodeList);

			for (final Node lBait : lBaitList)
			{
				final Set<Node> lNeighboursSet = lGraph.getNodeNeighbours(lBait);
				for (final Node lNeighbour : lNeighboursSet)
				{
					final Set<Node> lNeighboursSet2 = lGraph.getNodeNeighbours(lNeighbour);
					lNeighboursSet2.remove(lBait);
					lNeighboursSet2.removeAll(lNeighboursSet);
					final List<Node> lReconnectionList = RandomUtils.randomSample(pRandom, pReconnectionProbability,
							lNeighboursSet2);
					for (final Node lReconnectedNode : lReconnectionList)
						if (!lGraph.isEdge(lBait, lReconnectedNode))
						{
							lGraph.removeEdge(lReconnectedNode, lNeighbour);
							lGraph.removeEdge(lNeighbour, lReconnectedNode);
							lGraph.addEdge(new UndirectedEdge<Node>(lBait, lReconnectedNode));

							// System.out.println("reconnecting");
						}
				}
			}

			if (lGraph.getNumberOfEdges() != pGraph.getNumberOfEdges())
				throw new RuntimeException("Not Average Degree invariant! :"
						+ (lGraph.getNumberOfEdges() - pGraph.getNumberOfEdges()));

			if (lGraph.equals(pGraph))
				System.out.println("Graph was not changed!");
			// throw new RuntimeException("Graph was not changed!");
			lCounter++;
			if (lCounter > 10)
				break;
		}
		while (lGraph.equals(pGraph));

		final Set<Edge<Node>> lSet = new HashSet<Edge<Node>>();
		lSet.addAll(pGraph.getEdgeSet());
		lSet.retainAll(lGraph.getEdgeSet());

		final double lNoiseLevel = (double) (pGraph.getNumberOfEdges() - lSet.size())
				/ (double) pGraph.getNumberOfEdges();

		return new Couple<Graph<Node, Edge<Node>>, Double>(lGraph, lNoiseLevel);
	}

	public static Graph<Node, Edge<Node>> randomSampling(	final Random pRandom,
																												final Graph<Node, Edge<Node>> pGraph,
																												final double pSamplingRate)
	{
		final Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();
		final List<Node> lOriginalNodeList = new ArrayList<Node>(pGraph.getNodeSet());
		final List<Node> lNewNodeList = new ArrayList<Node>();

		for (final Node lNode : lOriginalNodeList)
			if (pRandom.nextDouble() < pSamplingRate)
				lNewNodeList.add(lNode);

		for (final Node lNode : lNewNodeList)
			lGraph.addNode(lNode);

		for (int i = 0; i < lNewNodeList.size(); i++)
			for (int j = 0; j < i; j++)
			{
				final Node lNode1 = lNewNodeList.get(i);
				final Node lNode2 = lNewNodeList.get(j);

				if (pGraph.isEdge(lNode1, lNode2))
					lGraph.addEdge(new UndirectedEdge<Node>(lNode1, lNode2));
			}
		return lGraph;
	}

	public static Graph<Node, Edge<Node>> generateScaleFreePreferentialAttachement(	final Random pRandom,
																																									final Integer pNumberOfNodes,
																																									final Double pAverageDegree,
																																									final Double pExponent)
	{
		final Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();
		int lCounter = 0;
		if (pNumberOfNodes > 0)
		{
			final Node lFirstNode = new Node("node" + (lCounter++));
			lGraph.addNode(lFirstNode);

			while (lGraph.getNumberOfNodes() < pNumberOfNodes)
				addNodePreferentialAttachement(pRandom, lGraph, lCounter++, pAverageDegree / 2, pExponent);

		}
		return lGraph;
	}

	@SuppressWarnings("boxing")
	static void addNodePreferentialAttachement(	final Random pRandom,
																							final Graph<Node, Edge<Node>> pGraph,
																							final Integer pCounter,
																							final Double pNumberOfNewEdges,
																							final Double pExponent)
	{
		final Node lNewNode = new Node("node" + pCounter);

		final List<Node> lNodeList = new ArrayList<Node>(pGraph.getNodeSet());

		double lTotal = 0;
		for (final Node lNode : lNodeList)
			lTotal += pGraph.getNodeNeighbours(lNode).size();

		if (lTotal == 0)
		{
			final Edge<Node> lEdge = new UndirectedEdge<Node>(lNewNode, lNodeList.get(pRandom.nextInt(lNodeList.size())));
			pGraph.addEdge(lEdge);
		}
		else
		{
			final DistributionSource<Node> lDistributionSource = new DistributionSource<Node>();
			for (final Node lNode : lNodeList)
			{
				double lProbability = ((pGraph.getNodeNeighbours(lNode).size()) / lTotal);
				lProbability = Math.pow(lProbability, pExponent);
				lDistributionSource.addObject(lNode, lProbability);
			}
			try
			{
				lDistributionSource.prepare(Math.max(100000, lNodeList.size()), 0.01);
			}
			catch (final Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			long lNumberOfEdges = RandomUtils.longGaussian(pRandom, pNumberOfNewEdges, 0.5);
			lNumberOfEdges = (long) Math.min(lNumberOfEdges, 2 * pNumberOfNewEdges);
			for (int i = 0; i < lNumberOfEdges; i++)
			{
				final Node lNode = lDistributionSource.getObject(pRandom);
				final Edge<Node> lEdge = new UndirectedEdge<Node>(lNewNode, lNode);
				pGraph.addEdge(lEdge);
			}
		}

	}

	public static Graph<Node, Edge<Node>> generateScaleFreeTree(final Random pRandom, final int pNumberOfNodes)
	{
		final Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();
		int lCounter = 0;
		if (pNumberOfNodes > 0)
		{
			final Node lFirstNode = new Node("node" + (lCounter++));
			lGraph.addNode(lFirstNode);
			Node lCurrentNode = lFirstNode;

			while (lGraph.getNumberOfNodes() < pNumberOfNodes)
			{
				for (int i = 0; i < pRandom.nextInt(pNumberOfNodes); i++)
					lCurrentNode = oneStep(pRandom, lGraph, lCurrentNode);
				addNodeAndConnect(lGraph, lCurrentNode, lCounter++);
			}

		}
		return lGraph;
	}

	static Node oneStep(final Random pRandom, final Graph<Node, Edge<Node>> pGraph, final Node pNode)
	{
		final Set<Node> lNodeSet = pGraph.getNodeNeighbours(pNode);
		if (lNodeSet.isEmpty())
			return pNode;
		else
		{
			final Node[] lNodeArray = lNodeSet.toArray(new Node[lNodeSet.size()]);
			return lNodeArray[pRandom.nextInt(lNodeArray.length)];
		}
	}

	static void addNodeAndConnect(final Graph<Node, Edge<Node>> pGraph, final Node pNode, final int pCounter)
	{
		final Node lNewNode = new Node("node" + pCounter);
		final Edge<Node> lEdge = new UndirectedEdge(pNode, lNewNode);
		pGraph.addEdge(lEdge);
	}

	public static Graph<Node, Edge<Node>> generateScaleFreeDuplicationDivergenceRoyer(final Random pRandom,
																																										final int pNumberOfNodes,
																																										final double pParameter)
	{
		final Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();

		final int lCounter = 0;
		if (pNumberOfNodes > 0)
		{
			final Node lFirstNode = new Node("first node");
			lGraph.addNode(lFirstNode);
			Node lCurrentNode = lFirstNode;

			while (lGraph.getNumberOfNodes() < pNumberOfNodes)
			{
				for (int i = 0; i < pRandom.nextInt(pNumberOfNodes); i++)
					lCurrentNode = oneStep(pRandom, lGraph, lCurrentNode);

				final Set<Node> lNodeSet = lGraph.getNodeNeighbours(lCurrentNode);

				if (lNodeSet.isEmpty())
					addNodeAndConnect(lGraph, lCurrentNode, lCounter);
				else
				{
					final Node lNewNode = new Node("node" + lCounter);
					double lTotal = 0;
					for (final Node lNode : lNodeSet)
						lTotal += lGraph.getNodeNeighbours(lNode).size();

					for (final Node lNode : lNodeSet)
					{
						double lProbability = ((lGraph.getNodeNeighbours(lNode).size()) / lTotal);
						lProbability = Math.pow(lProbability, pParameter);
						if (pRandom.nextDouble() < lProbability)
						{
							final Edge<Node> lEdge = new UndirectedEdge<Node>(lNewNode, lNode);
							lGraph.addEdge(lEdge);
						}
					}
				}
				System.out.print(".");
			}

		}
		return lGraph;
	}

	public static Graph<Node, Edge<Node>> generateScaleFreeDuplicationDivergence(	final Random pRandom,
																																								final int pNumberOfNodes,
																																								final double pCopyProbability)
	{
		final Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();

		int lCounter = 0;
		if (pNumberOfNodes > 0)
		{
			final Node lFirstNode = new Node("first node");
			final Node lSecondNode = new Node("second node");
			lGraph.addNode(lFirstNode);
			lGraph.addNode(lSecondNode);
			lGraph.addEdge(new UndirectedEdge<Node>(lFirstNode, lSecondNode));
			Node lCurrentNode = lFirstNode;

			while (lGraph.getNumberOfNodes() < pNumberOfNodes)
			{
				final List<Node> lNodeList = new ArrayList<Node>(lGraph.getNodeSet());
				lCurrentNode = lNodeList.get(pRandom.nextInt(lNodeList.size()));

				final Set<Node> lNodeSet = lGraph.getNodeNeighbours(lCurrentNode);

				if (!lNodeSet.isEmpty())
				{
					final Node lNewNode = new Node("node" + lCounter);
					lGraph.addNode(lNewNode);
					lCounter++;
					for (final Node lNode : lNodeSet)
						if (pRandom.nextDouble() < pCopyProbability)
						{
							final Edge<Node> lEdge = new UndirectedEdge<Node>(lNewNode, lNode);
							lGraph.addEdge(lEdge);
						}
				}
				System.out.print(".");
			}

		}
		return lGraph;
	}

	public static Graph<Node, Edge<Node>> generateRandomGeometric2d(final Random pRandom,
																																final int pNumberOfNodes,
																																final double pDegree)
	{
		final double lRadius = Math.sqrt(pDegree / (Math.PI * pNumberOfNodes));
		final Random lRandom = new Random(System.currentTimeMillis());
		
		final Graph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();
		final Map<Integer,Node> lIndexToNodeMap = new HashMap<Integer,Node>();
		final double[][] lVectorArray = new double[pNumberOfNodes][];
		for (int i=0; i<pNumberOfNodes; i++)
		{
			lVectorArray[i] = randomVector(lRandom,2);
			final Node lNode = new Node("node " + i);
			lGraph.addNode(lNode);
			lIndexToNodeMap.put(i, lNode);
		}
		
		
		
		for(int i=0; i<pNumberOfNodes; i++)
			for(int j=0; j<i; j++)
			{
				final double[] lVector1 =  lVectorArray[i];
				final double[] lVector2 =  lVectorArray[j];
				final double lDistance = euclideanDistance(lVector1, lVector2);
				if (lDistance<=lRadius)
				{
					final Node lNode1 = lIndexToNodeMap.get(i);
					final Node lNode2 = lIndexToNodeMap.get(j);
					final Edge<Node> lEdge = new UndirectedEdge<Node>(lNode1, lNode2);
					lGraph.addEdge(lEdge);
				}
			}
				
		return lGraph;
	}

	private static double[] randomVector(final Random pRandom, final int pDimension)
	{
		final double[] lVector = new double[pDimension];
		for(int i=0;i<pDimension;i++)
			lVector[i]=pRandom.nextDouble();			
		return lVector;
	}
	
	private static double euclideanDistance(final double[] pVector1, final double[] pVector2)
	{
		final int lLength = Math.max(pVector1.length,pVector2.length);
		double lSum=0;
		for(int i=0; i<lLength; i++)
		{
			final double lFirst = (i<pVector1.length)?pVector1[i]:0;
			final double lSecond = (i<pVector2.length)?pVector2[i]:0;
			lSum+= (lFirst-lSecond)*(lFirst-lSecond);
		}
		final double lDistance = Math.sqrt(lSum);
		return lDistance;		
	}



















}
