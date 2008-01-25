package utils.structures.powergraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import utils.structures.Pair;
import utils.structures.graph.Edge;
import utils.structures.graph.Node;
import utils.structures.graph.UndirectedEdge;

public class PowerGraphGenerator
{

	private PowerGraphGenerator()
	{
	}

	public static PowerGraph<Node> generateOptimalPowerGraph(	final Random pRandom,
																														final int pNumberOfMotifs,
																														final int pMinMotifSize,
																														final int pMaxMotifSize,
																														final double pProportionOfLinks)
	{
		final int lNumberOfLinks = (int) (pNumberOfMotifs * pProportionOfLinks);

		final List<Pair<Set<Node>>> lPairList = new ArrayList<Pair<Set<Node>>>();
		for (int i = 0; i < pNumberOfMotifs / 2; i++)
		{
			final Set<Node> lNodeSet1 = new HashSet<Node>();
			final int lMotifSize1 = pMinMotifSize + pRandom.nextInt(pMaxMotifSize);
			for (int j = 0; j < lMotifSize1; j++)
			{
				final Node lNode = newNode();
				lNodeSet1.add(lNode);
			}

			final Set<Node> lNodeSet2 = new HashSet<Node>();
			final int lMotifSize2 = pMinMotifSize + pRandom.nextInt(pMaxMotifSize);
			for (int j = 0; j < lMotifSize2; j++)
			{
				final Node lNode = newNode();
				lNodeSet2.add(lNode);
			}

			final Pair<Set<Node>> lPair = new Pair<Set<Node>>(lNodeSet1, lNodeSet2);
			lPairList.add(lPair);
		}

		for (int i = 0; i < pNumberOfMotifs / 2; i++)
		{
			final Set<Node> lNodeSet = new HashSet<Node>();
			final int lMotifSize = pMinMotifSize + pRandom.nextInt(pMaxMotifSize);
			for (int j = 0; j < lMotifSize; j++)
			{
				final Node lNode = newNode();
				lNodeSet.add(lNode);
			}

			final Pair<Set<Node>> lPair = new Pair<Set<Node>>(lNodeSet, lNodeSet);
			lPairList.add(lPair);
		}

		final PowerGraph<Node> lPowerGraph = new PowerGraph<Node>();

		for (final Pair<Set<Node>> lPair : lPairList)
		{
			final Edge<Set<Node>> lPowerEdge = new UndirectedEdge<Set<Node>>(	lPair.mA,
																																				lPair.mB);
			lPowerGraph.addPowerEdge(lPowerEdge);
		}

		final Set<Node> lUsedNodeSet = new HashSet<Node>();
		for (int k = 0; k < lNumberOfLinks; k++)
		{
			final Pair<Set<Node>> lPair1 = lPairList.get(pRandom.nextInt(lPairList.size()));
			Pair<Set<Node>> lPair2;

			do
				lPair2 = lPairList.get(pRandom.nextInt(lPairList.size()));
			while (lPair2.equals(lPair1));

			Node lNode1 = null;
			if (!(lUsedNodeSet.containsAll(lPair1.mA) && lUsedNodeSet.containsAll(lPair1.mB)))
				do
					lNode1 = chooseNodeInPair(pRandom, lPair1);
				while (lUsedNodeSet.contains(lNode1));

			Node lNode2 = null;
			if (!(lUsedNodeSet.containsAll(lPair2.mA) && lUsedNodeSet.containsAll(lPair2.mB)))
				do
					lNode2 = chooseNodeInPair(pRandom, lPair2);
				while (lUsedNodeSet.contains(lNode2));

			if ((lNode1 != null) & (lNode2 != null))
			{

				final Set<Node> lNode1Set = new HashSet<Node>();
				final Set<Node> lNode2Set = new HashSet<Node>();
				lNode1Set.add(lNode1);
				lNode2Set.add(lNode2);

				final Edge<Set<Node>> lEdge = new UndirectedEdge<Set<Node>>(lNode1Set,
																																		lNode2Set);
				lPowerGraph.addPowerEdge(lEdge);

				lUsedNodeSet.add(lNode1);
				lUsedNodeSet.add(lNode2);
			}

		}

		return lPowerGraph;
	}

	public static PowerGraph<Node> generateHierarchicalOptimalPowerGraph(	final Random pRandom,
																																				final int pNumberOfMotifs,
																																				final int pMinMotifSize,
																																				final int pMaxMotifSize,
																																				final double pProportionOfLinks,
																																				final int pDepth)
	{

		if (pDepth == 0)
			return generateOptimalPowerGraph(	pRandom,
																				pNumberOfMotifs,
																				pMinMotifSize,
																				pMaxMotifSize,
																				pProportionOfLinks);

		final PowerGraph<Node> lPowerGraph = new PowerGraph<Node>();

		final int lNumberOfLinks = (int) (pNumberOfMotifs * pProportionOfLinks);

		final List<Pair<Set<Node>>> lPairList = new ArrayList<Pair<Set<Node>>>();
		for (int i = 0; i < pNumberOfMotifs / 2; i++)
		{

			final PowerGraph<Node> lPowerGraph1 = generateHierarchicalOptimalPowerGraph(pRandom,
																																									pNumberOfMotifs / 4,
																																									pMinMotifSize,
																																									pMaxMotifSize,
																																									pProportionOfLinks,
																																									pDepth - 1);
			lPowerGraph.addPowerGraph(lPowerGraph1);
			final Set<Node> lNodeSet1 = lPowerGraph1.getNodeSet();

			final PowerGraph<Node> lPowerGraph2 = generateHierarchicalOptimalPowerGraph(pRandom,
																																									pNumberOfMotifs / 4,
																																									pMinMotifSize,
																																									pMaxMotifSize,
																																									pProportionOfLinks,
																																									pDepth - 1);
			lPowerGraph.addPowerGraph(lPowerGraph2);
			final Set<Node> lNodeSet2 = lPowerGraph2.getNodeSet();

			final Pair<Set<Node>> lPair = new Pair<Set<Node>>(lNodeSet1, lNodeSet2);
			lPairList.add(lPair);
		}

		for (int i = 0; i < pNumberOfMotifs / 2; i++)
		{
			final Set<Node> lNodeSet = new HashSet<Node>();
			final int lMotifSize = pMinMotifSize + pRandom.nextInt(pMaxMotifSize);
			for (int j = 0; j < lMotifSize; j++)
			{
				final Node lNode = newNode();
				lNodeSet.add(lNode);
			}

			final Pair<Set<Node>> lPair = new Pair<Set<Node>>(lNodeSet, lNodeSet);
			lPairList.add(lPair);
		}

		for (final Pair<Set<Node>> lPair : lPairList)
		{
			final Edge<Set<Node>> lPowerEdge = new UndirectedEdge<Set<Node>>(	lPair.mA,
																																				lPair.mB);
			lPowerGraph.addPowerEdge(lPowerEdge);
		}

		final Set<Node> lUsedNodeSet = new HashSet<Node>();
		for (int k = 0; k < lNumberOfLinks; k++)
		{
			final Pair<Set<Node>> lPair1 = lPairList.get(pRandom.nextInt(lPairList.size()));
			Pair<Set<Node>> lPair2;

			do
				lPair2 = lPairList.get(pRandom.nextInt(lPairList.size()));
			while (lPair2.equals(lPair1));

			Node lNode1 = null;
			if (!(lUsedNodeSet.containsAll(lPair1.mA) && lUsedNodeSet.containsAll(lPair1.mB)))
				do
					lNode1 = chooseNodeInPair(pRandom, lPair1);
				while (lUsedNodeSet.contains(lNode1));

			Node lNode2 = null;
			if (!(lUsedNodeSet.containsAll(lPair2.mA) && lUsedNodeSet.containsAll(lPair2.mB)))
				do
					lNode2 = chooseNodeInPair(pRandom, lPair2);
				while (lUsedNodeSet.contains(lNode2));

			if ((lNode1 != null) & (lNode2 != null))
			{

				final Set<Node> lNode1Set = new HashSet<Node>();
				final Set<Node> lNode2Set = new HashSet<Node>();
				lNode1Set.add(lNode1);
				lNode2Set.add(lNode2);

				final Edge<Set<Node>> lEdge = new UndirectedEdge<Set<Node>>(lNode1Set,
																																		lNode2Set);
				lPowerGraph.addPowerEdge(lEdge);

				lUsedNodeSet.add(lNode1);
				lUsedNodeSet.add(lNode2);
			}

		}

		return lPowerGraph;
	}

	private static final Node chooseNodeInPair(	final Random pRandom,
																							final Pair<Set<Node>> pPair)
	{
		Set<Node> lSet;
		if (pRandom.nextBoolean())
			lSet = pPair.mA;
		else
			lSet = pPair.mB;

		final List<Node> lList = new ArrayList<Node>(lSet);

		final Node lNode = lList.get(pRandom.nextInt(lList.size()));
		return lNode;
	}

	private static int mNodeCounter = 0;

	private static final Node newNode()
	{
		mNodeCounter++;
		return new Node("Node" + mNodeCounter);
	}

}
