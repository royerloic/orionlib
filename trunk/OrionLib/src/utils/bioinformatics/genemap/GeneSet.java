package utils.bioinformatics.genemap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import utils.structures.Couple;

public class GeneSet implements Comparable<GeneSet>, Serializable
{

	private static final long serialVersionUID = 1L;
	
	private final HashSet<Element> mSet;
	private final HashMap<Element, Double> mAttribute2PValueMap = new HashMap<Element, Double>();

	public GeneSet(Set<Element> pSet)
	{
		mSet = new HashSet<Element>(pSet);
	}

	public void addAttributeAndPValue(Element pAttribute, double pValue)
	{
		mAttribute2PValueMap.put(pAttribute, pValue);
	}

	public HashSet<Element> getGenes()
	{
		return mSet;
	}

	public ArrayList<Couple<Element, Double>> getAttributesPValuesCouples()
	{
		ArrayList<Couple<Element, Double>> lList = new ArrayList<Couple<Element, Double>>();

		for (Entry<Element, Double> lEntry : mAttribute2PValueMap.entrySet())
		{
			Couple<Element, Double> lCouple = new Couple<Element, Double>(lEntry.getKey(),
																																		lEntry.getValue());
			lList.add(lCouple);
		}
		return lList;
	}

	public Double getBestPValue()
	{
		Double lBestPValue = Double.POSITIVE_INFINITY;
		for (Double lPValue : mAttribute2PValueMap.values())
		{
			lBestPValue = Math.min(lBestPValue, lPValue);
		}
		return lBestPValue;
	}

	@Override
	public String toString()
	{
		return "{ size=" + mSet.size() + mAttribute2PValueMap + "}";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mSet == null) ? 0 : mSet.hashCode());
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
		if (mSet == null)
		{
			if (other.mSet != null)
				return false;
		}
		else if (!mSet.equals(other.mSet))
			return false;
		return true;
	}

	public int compareTo(GeneSet pO)
	{
		return getBestPValue().compareTo(pO.getBestPValue());
	}

}
