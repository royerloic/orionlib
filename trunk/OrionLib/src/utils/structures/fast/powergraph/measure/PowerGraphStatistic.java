package utils.structures.fast.powergraph.measure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import utils.math.statistics.Average;
import utils.math.statistics.MinMax;
import utils.math.statistics.StandardDeviation;
import utils.structures.fast.graph.Edge;
import utils.structures.fast.graph.FastGraph;
import utils.structures.fast.graph.measures.BicliqueStatistic;
import utils.structures.fast.powergraph.FastPowerGraph;
import utils.structures.fast.set.FastBoundedIntegerSet;
import utils.structures.map.DoubleMap;
import utils.structures.map.HashDoubleMap;

public class PowerGraphStatistic
{
	public static <N extends Serializable> HashMap<String, Double> computePowerGraphPValue(	final FastPowerGraph<N> pPowerGraph,
																																													final double threshold)
	{
		HashMap<String, Double> lResultMap = new HashMap<String, Double>();

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

		// product, min, max, average, std
		lResultMap.put("product", pvalue);
		lResultMap.put("min", lMinMax.getStatistic()[0]);
		lResultMap.put("max", lMinMax.getStatistic()[1]);
		lResultMap.put("average", lAverage.getStatistic());
		lResultMap.put("stddev", lStandardDeviation.getStatistic());

		return lResultMap;
	}

	public static <N extends Serializable> HashMap<Edge<N>, Double> computePowerEdgePValues(final FastPowerGraph<N> pPowerGraph,
																																													final double threshold)
	{
		HashMap<Edge<N>, Double> lPowerEdgePValues = new HashMap<Edge<N>, Double>();

		FastGraph<N> lGraph = pPowerGraph.getGraph();

		ArrayList<Edge<N>> lPowerEdgeList = pPowerGraph.getPowerEdgeList();

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
				lPowerEdgePValues.put(lEdge, lPowerNodePValue);
			}
		}

		return lPowerEdgePValues;
	}

	public static <N extends Serializable> HashDoubleMap<N> computePowerNodePValues(final FastPowerGraph<N> pPowerGraph,
																																									final double threshold)
	{
		HashDoubleMap<N> lPowerNodePValues = new HashDoubleMap<N>();

		FastGraph<N> lGraph = pPowerGraph.getGraph();

		// First we transfert pvalues from power edges to power nodes:
		ArrayList<Edge<N>> lPowerEdgeList = pPowerGraph.getPowerEdgeList();
		for (Edge<N> lEdge : lPowerEdgeList)
		{
			N lFirstNode = lEdge.getFirstNode();
			N lSecondNode = lEdge.getSecondNode();
			Set<N> lPowerNodeContents1 = pPowerGraph.getPowerNodeContent(lFirstNode);
			Set<N> lPowerNodeContents2 = pPowerGraph.getPowerNodeContent(lSecondNode);
			
			//System.out.println(lPowerNodeContents1.size());
			//System.out.println(lPowerNodeContents2.size());
			double lPowerEdgePValue = BicliqueStatistic.bicliquePValue(	lGraph,
																																	lPowerNodeContents1,
																																	lPowerNodeContents2,
																																	threshold);

			//System.out.println(lPowerEdgePValue);

			if (!(lPowerNodeContents1.size() == 1 || lPowerNodeContents2.size() == 1))
			{
				lPowerNodePValues.putIfNull(lFirstNode, 1.0);
				lPowerNodePValues.putIfNull(lSecondNode, 1.0);
				lPowerNodePValues.mult(lFirstNode, lPowerEdgePValue);
				lPowerNodePValues.mult(lSecondNode, lPowerEdgePValue);
			}
		}

		//System.out.println(lPowerNodePValues);

		HashDoubleMap<N> lPowerNodePValuesDiffused = new HashDoubleMap<N>(lPowerNodePValues);
		
		// Second we transfert pvalues _ one step up and down_ accross the power node hierarchy:

		FastBoundedIntegerSet lPowerNodeIdSet = pPowerGraph.getPowerNodeIdSet();
		for (int lPowerNodeId : lPowerNodeIdSet)
		{
			N lPowerNode = pPowerGraph.getPowerNodeById(lPowerNodeId);
			if (lPowerNode == null)
				continue;// this is the root.
			double powernodesize = pPowerGraph.getPowerNodeSize(lPowerNodeId);
			if (powernodesize < 2)
				continue;

			//System.out.println(lPowerNode);
			lPowerNodePValuesDiffused.putIfNull(lPowerNode, 1.0);
			double pvalue = lPowerNodePValuesDiffused.get(lPowerNode);

			FastBoundedIntegerSet lPowerNodeChildren = pPowerGraph.getPowerNodeChildrenOf(lPowerNodeId);
			for (int lChildId : lPowerNodeChildren)
			{
				double childsize = pPowerGraph.getPowerNodeSize(lChildId);
				if (childsize < 2)
					continue;
				double sizeratio = (childsize - 1) / (powernodesize - 1);
				double contributedpvalue = Math.pow(pvalue, sizeratio);

				N lChildrenPowerNode = pPowerGraph.getPowerNodeById(lChildId);
				//System.out.println(lChildrenPowerNode);
				lPowerNodePValuesDiffused.putIfNull(lChildrenPowerNode, 1.0);
				lPowerNodePValuesDiffused.mult(lChildrenPowerNode, contributedpvalue);
			}

		}

		return lPowerNodePValuesDiffused;
	}

}
