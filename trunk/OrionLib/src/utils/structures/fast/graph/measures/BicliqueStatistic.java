package utils.structures.fast.graph.measures;

import java.util.Collection;

import utils.math.statistics.HyperGeometricEnrichement;
import utils.structures.fast.graph.FastGraph;

public class BicliqueStatistic
{
	public static <N> double bicliquePValue(final FastGraph<N> pGraph,
																					Collection<N> pFirstSet,
																					Collection<N> pSecondSet,
																					double threshold)
	{
		double lOneSide = bicliqueOneSidedPValue(	pGraph,
																							pFirstSet,
																							pSecondSet,
																							threshold);
		double lOtherSide = bicliqueOneSidedPValue(	pGraph,
																								pSecondSet,
																								pFirstSet,
																								threshold);
		return Math.max(lOneSide, lOtherSide);
	}

	public static <N> double bicliqueOneSidedPValue(final FastGraph<N> pGraph,
																									Collection<N> pFirstSet,
																									Collection<N> pSecondSet,
																									double threshold)
	{
		double[] lSizeArray = new double[pFirstSet.size()];
		int i = 0;
		for (N lN : pFirstSet)
		{
			final double lSize = pGraph.getNodeNeighbours(lN).size();
			lSizeArray[i] = lSize;
			i++;
		}

		final double lNumberOfNodes = pGraph.getNumberOfNodes();
		final double lSecondSetSize = pSecondSet.size();

		final double pvalue = HyperGeometricEnrichement.generalizedHyperG(lNumberOfNodes,
																																			lSizeArray,
																																			lSecondSetSize,
																																			threshold);

		return pvalue;
	}
}
