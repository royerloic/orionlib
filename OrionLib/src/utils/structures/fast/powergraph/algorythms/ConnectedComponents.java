package utils.structures.fast.powergraph.algorythms;

import java.util.ArrayList;

import utils.structures.fast.powergraph.FastIntegerPowerGraph;
import utils.structures.fast.set.FastBoundedIntegerSet;

public class ConnectedComponents
{

	public static final ArrayList<FastBoundedIntegerSet> getConnectedComponents(FastIntegerPowerGraph pPowerGraph)
	{
		ArrayList<FastBoundedIntegerSet> lConnectedComponentList = new ArrayList<FastBoundedIntegerSet>();

		FastBoundedIntegerSet allpowernodes = pPowerGraph.getPowerNodeIdSet();
		allpowernodes.remove(0);

		FastBoundedIntegerSet componentpowernodes = null;
		FastBoundedIntegerSet frontierpowernodes = new FastBoundedIntegerSet(pPowerGraph.getNumberOfPowerNodes());
		FastBoundedIntegerSet newfrontierpowernodes = new FastBoundedIntegerSet(pPowerGraph.getNumberOfPowerNodes());

		while (!allpowernodes.isEmpty())
		{
			componentpowernodes = new FastBoundedIntegerSet(pPowerGraph.getNumberOfPowerNodes());
			int lPowerNodeId = allpowernodes.getMin(1);

			frontierpowernodes.clear();
			frontierpowernodes.add(lPowerNodeId);

			while (!frontierpowernodes.isEmpty())
			{
				componentpowernodes.union(frontierpowernodes);
				newfrontierpowernodes.clear();

				for (int lFrontierPowerNodeId : frontierpowernodes)
				{
					newfrontierpowernodes.union(pPowerGraph.getConnectedPowerNodeNeighbors(lFrontierPowerNodeId));
				}

				newfrontierpowernodes.difference(componentpowernodes);

				FastBoundedIntegerSet temp = frontierpowernodes;
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
