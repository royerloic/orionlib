package utils.structures.fast.powergraph.measure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

import utils.math.statistics.Average;
import utils.math.statistics.MinMax;
import utils.math.statistics.StandardDeviation;
import utils.structures.fast.graph.Edge;
import utils.structures.fast.graph.FastGraph;
import utils.structures.fast.graph.measures.BicliqueStatistic;
import utils.structures.fast.powergraph.FastPowerGraph;

public class PowerGraphStatistic
{
	public static <N extends Serializable> double[] powerGraphPValue(	final FastPowerGraph<N> pPowerGraph,
																																		final double threshold)
	{
		double[] lResultArray = new double[]
		{ 0, 0, 0, 0, 0 }; // product, min, max, average, std

		StandardDeviation lStandardDeviation = new StandardDeviation();
		Average lAverage = new Average();
		MinMax lMinMax = new MinMax();

		FastGraph<N> lGraph = pPowerGraph.getGraph();

		ArrayList<Edge<N>> lPowerEdgeList = pPowerGraph.getPowerEdgeList();

		double pvalue = 1;
		for (Edge<N> lEdge : lPowerEdgeList)
		{
			N lFirstNode = lEdge.getFirstNode();
			N lSecondNode = lEdge.getSecondNode();
			Set<N> lPowerNodeContents1 = pPowerGraph.getPowerNodeContent(lFirstNode);
			Set<N> lPowerNodeContents2 = pPowerGraph.getPowerNodeContent(lSecondNode);

			double lPowerNodePValue = BicliqueStatistic.bicliquePValue(	lGraph,
																																	lPowerNodeContents1,
																																	lPowerNodeContents2,
																																	threshold);

			if (!(lPowerNodeContents1.size() == 1 || lPowerNodeContents2.size() == 1))
			{
				/*
				 * System.out.println("(" + lPowerNodeContents1.size() + "," +
				 * lPowerNodeContents2.size() + ") " + lEdge + " -> " +
				 * lPowerNodePValue);/
				 */
				pvalue *= lPowerNodePValue;
				lStandardDeviation.enter(lPowerNodePValue);
				lAverage.enter(lPowerNodePValue);
				lMinMax.enter(lPowerNodePValue);
			}
		}

		lResultArray[0] = pvalue;
		lResultArray[1] = lMinMax.getStatistic()[0];
		lResultArray[2] = lMinMax.getStatistic()[1];
		lResultArray[3] = lAverage.getStatistic();
		lResultArray[4] = lStandardDeviation.getStatistic();

		return lResultArray;
	}

}
