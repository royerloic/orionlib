package utils.structures.fast.graph.algorythms;

import java.util.ArrayList;
import java.util.HashSet;

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
			for (int y = 1; y <= Math.min(x,pMaxB); y++)
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

	public static HashSet<FastBoundedIntegerSet> findBicliques(	final FastGraph pFastGraph,
																															final int pA,
																															final int pB)
	{
		FastIntegerGraph lUnderlyingFastIntegerGraph = pFastGraph.getUnderlyingFastIntegerGraph();
		return findBicliques(lUnderlyingFastIntegerGraph, pA, pB);
	}

	public static HashSet<FastBoundedIntegerSet> findBicliques(	final FastIntegerGraph pFastIntegerGraph,
																															final int pA,
																															final int pB)
	{
		HashSet<FastBoundedIntegerSet> lBicliqueSet = new HashSet<FastBoundedIntegerSet>();

		if (pA > pB)
			return findBicliques(pFastIntegerGraph, pB, pA);

		if (pA == 1)
			return findStars(pFastIntegerGraph, pB);

		HashSet<FastBoundedIntegerSet> lBicliquesFound = findBicliques(	pFastIntegerGraph,
																																		pA - 1,
																																		pB);

		for (FastBoundedIntegerSet lBiclique : lBicliquesFound)
		{
			for (int lNodeId : pFastIntegerGraph.getNodeSet())
				if (!lBiclique.contains(lNodeId))
				{
					FastBoundedIntegerSet lNodeNeighbours = pFastIntegerGraph.getNodeNeighbours(lNodeId);
					if (lNodeNeighbours.size() >= pB)
					{
						FastBoundedIntegerSet lBiggerBiclique = new FastBoundedIntegerSet(lBiclique);
						lBiggerBiclique.add(lNodeId);

						if (neighbourIntersection(pFastIntegerGraph, lBiggerBiclique) >= pB)
						{
							lBicliqueSet.add(lBiggerBiclique);
						}
					}
				}
		}

		return lBicliqueSet;
	}

	private static int neighbourIntersection(	FastIntegerGraph pFastIntegerGraph,
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

		return lIntersection.size();
	}

	public static HashSet<FastBoundedIntegerSet> findStars(	final FastIntegerGraph pFastIntegerGraph,
																													final int pB)
	{
		HashSet<FastBoundedIntegerSet> lStarList = new HashSet<FastBoundedIntegerSet>();

		for (int lNodeId : pFastIntegerGraph.getNodeSet())
		{
			FastBoundedIntegerSet lStar = new FastBoundedIntegerSet();
			lStar.add(lNodeId);

			FastBoundedIntegerSet lNodeNeighbours = pFastIntegerGraph.getNodeNeighbours(lNodeId);

			if (lNodeNeighbours.size() >= pB)
			{
				lStarList.add(lStar);
			}
		}

		return lStarList;
	}
}
