package utils.structures.graph.io.psimi;

import java.util.List;

import utils.bioinformatics.interpro.InterproToGo;
import utils.structures.graph.Edge;
import utils.structures.graph.HashGraph;

public class PsiMiGraph extends HashGraph<PsiMiNode, Edge<PsiMiNode>>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void addGoIdsFromDomains()
	{
		for (final PsiMiNode lNode : getNodeSet())
		{
			final List<Integer> lInterproDomainIdList = lNode.getInterproIdList();
			for (final Integer lInterproDomainId : lInterproDomainIdList)
			{
				final List<Integer> lGoIdList = InterproToGo.getGoIdsForInterproId(lInterproDomainId);
				for (final Integer lNewGoId : lGoIdList)
				{
					lNode.addGoId(lNewGoId);
					/***********************************************************************
					 * if (!getGoIdSet(lNode).contains(lNewGoId))
					 * System.out.println("added goid: "+lNewGoId);/
					 **********************************************************************/
				}
			}
		}

	}

}
