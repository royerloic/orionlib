package utils.structures.fast.powergraph.algorythms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import utils.structures.fast.powergraph.FastIntegerPowerGraph;
import utils.structures.fast.powergraph.FastPowerGraph;
import utils.structures.fast.set.FastBoundedIntegerSet;

public class ConnectedComponents
{

	public static final <N extends Serializable> ArrayList<FastBoundedIntegerSet> getConnectedComponents(final FastPowerGraph<N> pPowerGraph)
	{
		FastIntegerPowerGraph lFastIntegerPowerGraph = pPowerGraph.getUnderlyingFastIntegerPowerGraph();
		ArrayList<FastBoundedIntegerSet> lConnectedComponents = getConnectedComponents(lFastIntegerPowerGraph);

		return lConnectedComponents;
	}

	public static final ArrayList<FastBoundedIntegerSet> getConnectedComponents(final FastIntegerPowerGraph pPowerGraph)
	{
			
		final ArrayList<FastBoundedIntegerSet> lConnectedComponentList = new ArrayList<FastBoundedIntegerSet>();

		final FastBoundedIntegerSet allpowernodes = pPowerGraph.getPowerNodeIdSet();
		allpowernodes.remove(0);

		FastBoundedIntegerSet componentpowernodes = null;
		FastBoundedIntegerSet frontierpowernodes = new FastBoundedIntegerSet(pPowerGraph.getNumberOfPowerNodes());
		FastBoundedIntegerSet newfrontierpowernodes = new FastBoundedIntegerSet(pPowerGraph.getNumberOfPowerNodes());

		while (!allpowernodes.isEmpty())
		{
			componentpowernodes = new FastBoundedIntegerSet(pPowerGraph.getNumberOfPowerNodes());
			final int lPowerNodeId = allpowernodes.getMin(1);

			frontierpowernodes.clear();
			frontierpowernodes.add(lPowerNodeId);

			while (!frontierpowernodes.isEmpty())
			{
				componentpowernodes.union(frontierpowernodes);
				newfrontierpowernodes.clear();

				for (final int lFrontierPowerNodeId : frontierpowernodes)
				{
					FastBoundedIntegerSet lConnectedPowerNodeNeighbors = pPowerGraph.getConnectedPowerNodeNeighbors(lFrontierPowerNodeId);
					newfrontierpowernodes.union(lConnectedPowerNodeNeighbors);
				}

				newfrontierpowernodes.difference(componentpowernodes);

				final FastBoundedIntegerSet temp = frontierpowernodes;
				frontierpowernodes = newfrontierpowernodes;
				newfrontierpowernodes = temp;
			}
			
			

			componentpowernodes.remove(0);
			lConnectedComponentList.add(componentpowernodes);
			allpowernodes.difference(componentpowernodes);
		}
		return lConnectedComponentList;
	}

}
