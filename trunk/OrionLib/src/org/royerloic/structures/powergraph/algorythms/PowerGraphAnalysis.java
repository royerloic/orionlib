package org.royerloic.structures.powergraph.algorythms;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.UndirectedEdge;
import org.royerloic.structures.powergraph.PowerGraph;

public class PowerGraphAnalysis<N>
{
	double																mNodeCount				= 0;
	double																mEdgeCount				= 0;
	double																mPowerNodeCount		= 0;
	double																mPowerEdgeCount		= 0;

	Map<UndirectedEdge<Integer>, Double>	lMotifToCountMap	= new HashMap<UndirectedEdge<Integer>, Double>();

	public PowerGraphAnalysis()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public void addPowerGraph(PowerGraph<N> pPowerGraph)
	{
		mNodeCount += pPowerGraph.getNodeSet().size();
		mPowerNodeCount += pPowerGraph.getNumberOfPowerNodes();

		for (Edge<Set<N>> lEdge : pPowerGraph.getPowerEdgeSet())
		{
			Set<N> lFirstNode = lEdge.getFirstNode();
			Set<N> lSecondNode = lEdge.getSecondNode();
			mEdgeCount += lFirstNode.size() * lSecondNode.size();

			UndirectedEdge<Integer> lMotif = new UndirectedEdge<Integer>(lFirstNode.size(), lSecondNode.size());
			Double lCount = lMotifToCountMap.get(lMotif);
			if (lCount == null)
			{
				lCount = 0.0;
			}
			lCount++;
			lMotifToCountMap.put(lMotif, lCount);
		}

		mPowerEdgeCount += pPowerGraph.getNumberOfPowerEdges();
	}

	public Double getNodeOverhead()
	{
		return (mPowerNodeCount) / (mNodeCount);
	}

	public Double getEdgeReduction()
	{
		return 1 - (mPowerEdgeCount) / (mEdgeCount);
	}

	public Map<UndirectedEdge<Integer>, Double> getMotifToCountMap()
	{
		return Collections.unmodifiableMap(lMotifToCountMap);
	}

	public Object getSymbolReduction()
	{
		return 1 - ((mPowerEdgeCount + (mPowerNodeCount - mNodeCount)) / (mEdgeCount));
	}

}
