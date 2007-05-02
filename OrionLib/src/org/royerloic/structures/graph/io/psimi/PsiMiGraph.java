package org.royerloic.structures.graph.io.psimi;

import java.util.List;

import org.royerloic.bioinformatics.interpro.InterproToGo;
import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.HashGraph;

public class PsiMiGraph extends HashGraph<PsiMiNode, Edge<PsiMiNode>>
{

	public void addGoIdsFromDomains()
	{
		for (final PsiMiNode lNode : getNodeSet())
		{
			final List<Integer> lInterproDomainIdList = lNode.getInterproIdList();
			for (final Integer lInterproDomainId : lInterproDomainIdList)
			{
				final List<Integer> lGoIdList = InterproToGo.getGoIdsForInterproId(lInterproDomainId);
				for (final Integer lNewGoId : lGoIdList)
					lNode.addGoId(lNewGoId);
					/*********************************************************************
					 * if (!getGoIdSet(lNode).contains(lNewGoId))
					 * System.out.println("added goid: "+lNewGoId);/
					 ********************************************************************/
			}
		}

	}

}
