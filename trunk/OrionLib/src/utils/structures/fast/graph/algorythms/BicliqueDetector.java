package utils.structures.fast.graph.algorythms;

import java.util.ArrayList;
import java.util.HashSet;

import utils.structures.Pair;
import utils.structures.fast.graph.FastGraph;
import utils.structures.fast.graph.FastIntegerGraph;
import utils.structures.fast.set.FastBoundedIntegerSet;

public class BicliqueDetector
{

	public static int[][] countAllBicliques(final FastGraph pFastGraph,
																					final int pMaxA,
																					final int pMaxB)
	{
		FastIntegerGraph lUnderlyingFastIntegerGraph = pFastGraph.getUnderlyingFastIntegerGraph();
		return countAllBicliques(lUnderlyingFastIntegerGraph, pMaxA, pMaxB);
	}

	public static int[][] countAllBicliques(final FastIntegerGraph pFastIntegerGraph,
																					final int pMaxA,
																					final int pMaxB)
	{
		int[][] spectrum = new int[pMaxA + 1][pMaxB + 1];

		for (int x = 1; x <= pMaxA; x++)
			for (int y = 1; y <= Math.min(x, pMaxB); y++)
			{
				int count = countBicliques(pFastIntegerGraph, x, y);
				spectrum[x][y] = count;
				spectrum[y][x] = count;
			}
		return spectrum;
	}

	public static boolean detectBicliques(final FastGraph pFastGraph,
																				final int pA,
																				final int pB)
	{
		FastIntegerGraph lUnderlyingFastIntegerGraph = pFastGraph.getUnderlyingFastIntegerGraph();
		return detectBicliques(lUnderlyingFastIntegerGraph, pA, pB);
	}

	public static boolean detectBicliques(final FastIntegerGraph pFastIntegerGraph,
																				final int pA,
																				final int pB)
	{
		return findBicliques(pFastIntegerGraph, pA, pB).size() > 0;
	}

	public static int countBicliques(	final FastGraph pFastGraph,
																		final int pA,
																		final int pB)
	{
		FastIntegerGraph lUnderlyingFastIntegerGraph = pFastGraph.getUnderlyingFastIntegerGraph();
		return countBicliques(lUnderlyingFastIntegerGraph, pA, pB);
	}

	public static int countBicliques(	final FastIntegerGraph pFastIntegerGraph,
																		final int pA,
																		final int pB)
	{
		return findBicliques(pFastIntegerGraph, pA, pB).size();
	}

	public static <N> HashSet<Pair<ArrayList<N>>> findBicliques(final FastGraph<N> pFastGraph,
																															final int pA,
																															final int pB)
	{
		FastIntegerGraph lUnderlyingFastIntegerGraph = pFastGraph.getUnderlyingFastIntegerGraph();

		HashSet<Pair<FastBoundedIntegerSet>> lIntegerBicliquesFound = findBicliques(lUnderlyingFastIntegerGraph,
																																								pA,
																																								pB);

		HashSet<Pair<ArrayList<N>>> lBicliquesFound = new HashSet<Pair<ArrayList<N>>>(lIntegerBicliquesFound.size());
		for (Pair<FastBoundedIntegerSet> lPair : lIntegerBicliquesFound)
		{

			ArrayList<N> lConvertedNodeIdsA = pFastGraph.getNodesForIntegers(lPair.mA);
			ArrayList<N> lConvertedNodeIdsB = pFastGraph.getNodesForIntegers(lPair.mB);

			Pair<ArrayList<N>> lNewPair = new Pair<ArrayList<N>>(	lConvertedNodeIdsA,
																														lConvertedNodeIdsB);
			lBicliquesFound.add(lNewPair);
		}

		return lBicliquesFound;
	}

	public static HashSet<Pair<FastBoundedIntegerSet>> findBicliques(	final FastIntegerGraph pFastIntegerGraph,
																																		final int pA,
																																		final int pB)
	{
		HashSet<Pair<FastBoundedIntegerSet>> lBicliqueSet = new HashSet<Pair<FastBoundedIntegerSet>>();

		if (pA > pB)
			return findBicliques(pFastIntegerGraph, pB, pA);/**/

		if (pA == 1)
			return findStars(pFastIntegerGraph, pB);

		HashSet<Pair<FastBoundedIntegerSet>> lBicliquesFound = findBicliques(	pFastIntegerGraph,
																																					pA - 1,
																																					pB);

		for (Pair<FastBoundedIntegerSet> lBiclique : lBicliquesFound)
		{

			for (int lNodeId : pFastIntegerGraph.getNodeSet())
				if (!lBiclique.mA.contains(lNodeId))
				{
					FastBoundedIntegerSet lNodeNeighbours = pFastIntegerGraph.getNodeNeighbours(lNodeId);
					if (lNodeNeighbours.size() >= pB)
					{
						FastBoundedIntegerSet lBiggerBicliqueASet = new FastBoundedIntegerSet(lBiclique.mA);
						lBiggerBicliqueASet.add(lNodeId);

						FastBoundedIntegerSet lBiggerBicliqueBSet = neighbourIntersection(pFastIntegerGraph,
																																							lBiggerBicliqueASet);

						if (lBiggerBicliqueBSet.size() >= pB)
						{
							Pair<FastBoundedIntegerSet> lNewBiclique = new Pair<FastBoundedIntegerSet>(	lBiggerBicliqueASet,
																																													lBiggerBicliqueBSet);
							lBicliqueSet.add(lNewBiclique);

						}
					}
				}
		}

		return lBicliqueSet;
	}

	private static FastBoundedIntegerSet neighbourIntersection(	FastIntegerGraph pFastIntegerGraph,
																															FastBoundedIntegerSet pNodeIdSet)
	{
		FastBoundedIntegerSet lIntersection = null;

		for (int lNodeId : pNodeIdSet)
		{
			FastBoundedIntegerSet lNodeNeighbours = pFastIntegerGraph.getNodeNeighbours(lNodeId);
			// lNodeNeighbours.add(lNodeId); needed to find cliques...

			if (lIntersection == null)
			{
				lIntersection = new FastBoundedIntegerSet();
				lIntersection.union(lNodeNeighbours);
			}
			else
				lIntersection.intersection(lNodeNeighbours);
		}

		return lIntersection;
	}

	public static HashSet<Pair<FastBoundedIntegerSet>> findStars(	final FastIntegerGraph pFastIntegerGraph,
																																final int pB)
	{
		HashSet<Pair<FastBoundedIntegerSet>> lStarList = new HashSet<Pair<FastBoundedIntegerSet>>();

		for (int lNodeId : pFastIntegerGraph.getNodeSet())
		{
			FastBoundedIntegerSet lStarASet = new FastBoundedIntegerSet();
			lStarASet.add(lNodeId);

			FastBoundedIntegerSet lNodeNeighbours = pFastIntegerGraph.getNodeNeighbours(lNodeId);

			Pair<FastBoundedIntegerSet> lStar = new Pair<FastBoundedIntegerSet>(lStarASet,
																																					lNodeNeighbours);

			if (lNodeNeighbours.size() >= pB)
			{
				lStarList.add(lStar);
			}
		}

		return lStarList;
	}
}
