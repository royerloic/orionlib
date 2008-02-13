package utils.structures.lattice;

import java.util.Set;

import utils.structures.map.IntegerMap;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 * @param <N>
 */
public interface Lattice<N>
{

	Double getDepth(N pN);

	Double getHeight(N pN);

	Set<N> getChildren(N pN);

	IntegerMap<N> getDescendents(N pN);

	Set<N> getParents(N pN);

	IntegerMap<N> getAncestors(N pN);

	Set<N> getLowerCommonAncestors(N pN1, N pN2);

	Set<N> getHigherCommonDescendents(N pN1, N pN2);

	double getLowerCommonAncestorDistance(N pN1, N pN2);

	double getSimilarity(N pN1, N pN2);

}
