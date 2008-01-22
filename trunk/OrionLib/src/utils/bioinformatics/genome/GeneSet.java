package utils.bioinformatics.genome;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class GeneSet implements Serializable
{

	protected HashMap<String,Gene> mIdToGeneMap = new HashMap<String,Gene>();


	
	public GeneSet()
	{
		super();
	}

	public void add(Gene pGene)
	{
		mIdToGeneMap.put(pGene.getId(), pGene);		
	}

	@Override
	public String toString()
	{
		StringBuilder lStringBuilder = new StringBuilder();
		for (Gene lGene : mIdToGeneMap.values())
		{
			lStringBuilder.append(lGene);
			lStringBuilder.append("\n");
		}
		return lStringBuilder.toString();
	}

	public int getNumberOfGenes()
	{
		return mIdToGeneMap.size();
	}

	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
							+ ((mIdToGeneMap == null) ? 0 : mIdToGeneMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final GeneSet other = (GeneSet) obj;
		if (mIdToGeneMap == null)
		{
			if (other.mIdToGeneMap != null)
				return false;
		}
		else if (!mIdToGeneMap.equals(other.mIdToGeneMap))
			return false;
		return true;
	}

		
	public Gene getGeneById(String pId)
	{
		return mIdToGeneMap.get(pId);
	}

	public Collection<Gene> getSet()
	{
		return mIdToGeneMap.values();
	}
	
	
	
}
