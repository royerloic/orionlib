package utils.bioinformatics.proteome;

import utils.structures.graph.UndirectedEdge;

public class Interaction<T> extends UndirectedEdge<Protein>
{

	public Interaction(Protein pFirstNode, Protein pSecondNode)
	{
		super(pFirstNode, pSecondNode);
	}

}
