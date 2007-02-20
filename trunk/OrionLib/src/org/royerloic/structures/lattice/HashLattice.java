package org.royerloic.structures.lattice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.royerloic.structures.IntegerHashMap;
import org.royerloic.structures.IntegerMap;
import org.royerloic.structures.IntersectionMonitor;
import org.royerloic.structures.Triple;
import org.royerloic.structures.graph.DirectedEdge;
import org.royerloic.structures.graph.HashGraph;
import org.royerloic.structures.utils.CollectionUtils;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 * @param <N>
 */
public class HashLattice<N> extends HashGraph<N, DirectedEdge<N>> implements Lattice<N>
{

	public Double getDepth(N pN)
	{
		Set<N> lNodeSet = getParents(pN);
		List<Double> lDepthList = new ArrayList<Double>();
		for (N lN : lNodeSet)
		{
			lDepthList.add(getDepth(lN));
		}
		Double lDepth = CollectionUtils.max(lDepthList) + 1;
		return lDepth;
	}

	public Double getHeight(N pN)
	{
		Set<N> lNodeSet = getChildren(pN);
		List<Double> lDepthList = new ArrayList<Double>();
		for (N lN : lNodeSet)
		{
			lDepthList.add(getHeight(lN));
		}
		Double lHeight = CollectionUtils.max(lDepthList) + 1;
		return lHeight;
	}

	public Set<N> getChildren(N pN)
	{
		return getPositiveNodeNeighbours(pN);
	}

	public Set<N> getChildren(Collection<N> pNCollection)
	{
		Set<N> lChildrenSet = new HashSet<N>();
		for (N lN : pNCollection)
		{
			lChildrenSet.addAll(getPositiveNodeNeighbours(lN));
		}
		return lChildrenSet;
	}

	public IntegerMap<N> getChildren(IntegerMap<N> pMap)
	{
		IntegerMap<N> lChildrenMap = new IntegerHashMap<N>();
		for (Entry<N, Integer> lEntry : pMap.entrySet())
		{
			lChildrenMap.addAllWith(getPositiveNodeNeighbours(lEntry.getKey()), lEntry.getValue() + 1);
		}
		return lChildrenMap;
	}

	public IntegerMap<N> getDescendents(N pN)
	{
		return getDescendentsRecursive(pN, 1);
	}

	private IntegerMap<N> getDescendentsRecursive(N pN, int pDepth)
	{
		IntegerMap<N> lNodeToDepthMap = new IntegerHashMap<N>();
		Set<N> lChildrenSet = getChildren(pN);
		if (!lChildrenSet.isEmpty())
		{
			lNodeToDepthMap.addAllWith(lChildrenSet, pDepth);
			for (N lNode : lChildrenSet)
			{
				lNodeToDepthMap.minAll(getDescendentsRecursive(lNode, pDepth + 1));
			}
		}
		return lNodeToDepthMap;
	}

	public Set<N> getParents(N pN)
	{
		return getNegativeNodeNeighbours(pN);
	}

	public Set<N> getParents(Collection<N> pNCollection)
	{
		Set<N> lParentSet = new HashSet<N>();
		for (N lN : pNCollection)
		{
			lParentSet.addAll(getNegativeNodeNeighbours(lN));
		}
		return lParentSet;
	}

	public IntegerMap<N> getParents(IntegerMap<N> pMap)
	{
		IntegerMap<N> lParentMap = new IntegerHashMap<N>();
		for (Entry<N, Integer> lEntry : pMap.entrySet())
		{
			lParentMap.addAllWith(getNegativeNodeNeighbours(lEntry.getKey()), lEntry.getValue() - 1);
		}
		return lParentMap;
	}

	public IntegerMap<N> getAncestors(N pN)
	{
		return getAncestorsRecursive(pN, -1);
	}

	private IntegerMap<N> getAncestorsRecursive(N pN, int pDepth)
	{
		IntegerMap<N> lNodeToDepthMap = new IntegerHashMap<N>();
		Set<N> lParentSet = getParents(pN);
		if (!lParentSet.isEmpty())
		{
			lNodeToDepthMap.addAllWith(lParentSet, pDepth);
			for (N lNode : lParentSet)
			{
				lNodeToDepthMap.maxAll(getAncestorsRecursive(lNode, pDepth - 1));
			}
		}
		return lNodeToDepthMap;
	}

	private Triple<Set<N>, Integer, Integer> getLowerCommonAncestorsInternal(N pN1, N pN2)
	{
		IntersectionMonitor<N> lIntersectionMonitor = new IntersectionMonitor<N>();

		IntegerMap<N> lMap1 = new IntegerHashMap<N>();
		lMap1.addAllWith(Collections.singleton(pN1), 0);
		IntegerMap<N> lMap2 = new IntegerHashMap<N>();
		lMap2.addAllWith(Collections.singleton(pN2), 0);

		while (true)
		{
			lMap1 = lMap1.maxAll(getParents(lMap1));
			lMap2 = lMap2.maxAll(getParents(lMap2));
			if (lMap1.isEmpty() && lMap2.isEmpty())
				break;

			lIntersectionMonitor.addToA(lMap1.keySet());
			if (lIntersectionMonitor.isIntersecting())
				break;

			lIntersectionMonitor.addToB(lMap2.keySet());
			if (lIntersectionMonitor.isIntersecting())
				break;
		}

		final Set<N> lIntersectionSet = lIntersectionMonitor.getIntersection();
		N lNode = lIntersectionSet.iterator().next();

		return new Triple<Set<N>, Integer, Integer>(lIntersectionSet, lMap1.get(lNode), lMap2.get(lNode));
	}

	public Set<N> getLowerCommonAncestors(N pN1, N pN2)
	{
		Triple<Set<N>, Integer, Integer> lTriple = getLowerCommonAncestorsInternal(pN1, pN2);
		return lTriple.mA;
	}

	public double getLowerCommonAncestorDistance(N pN1, N pN2)
	{
		Triple<Set<N>, Integer, Integer> lTriple = getLowerCommonAncestorsInternal(pN1, pN2);
		return lTriple.mB + lTriple.mC;
	}

	public double getSimilarity(N pN1, N pN2)
	{
		Triple<Set<N>, Integer, Integer> lTriple = getLowerCommonAncestorsInternal(pN1, pN2);

		Set<N> lLowerCommonAncestorsSet = lTriple.mA;

		List<Double> lDepthList = new ArrayList<Double>();
		for (N lN : lLowerCommonAncestorsSet)
		{
			lDepthList.add(getDepth(lN));
		}

		final double lDepth = CollectionUtils.average(lDepthList);
		final double l1 = lTriple.mB;
		final double l2 = lTriple.mC;
		final double lSimilarity = (l1 + l2) / (lDepth + 2 * Math.max(l1, l2));

		return lSimilarity;
	}

	public Set<N> getHigherCommonDescendents(N pN1, N pN2)
	{
		return null;
		// TODO: fill this once the getLowerCommonAncestors is proved to work...
	}

}
