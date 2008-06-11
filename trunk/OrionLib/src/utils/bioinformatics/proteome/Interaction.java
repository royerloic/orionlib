package utils.bioinformatics.proteome;

import utils.structures.graph.UndirectedEdge;

public class Interaction<T> extends UndirectedEdge<Protein>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Interaction(final Protein pFirstNode, final Protein pSecondNode)
	{
		super(pFirstNode, pSecondNode);
	}

}
