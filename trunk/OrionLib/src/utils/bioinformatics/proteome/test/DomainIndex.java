package utils.bioinformatics.proteome.test;

import java.util.Map;
import java.util.Set;

import utils.bioinformatics.proteome.Domain;
import utils.bioinformatics.proteome.Protein;
import utils.bioinformatics.proteome.Proteome;
import utils.structures.map.HashSetMap;

public class DomainIndex
{

	private final Proteome mProteome;

	private final HashSetMap<String, Protein> mDomainIdToProteinIndex = new HashSetMap<String, Protein>();

	public DomainIndex(Proteome pProteome)
	{
		mProteome = pProteome;

	}

	public void index()
	{
		for (Protein lProtein : mProteome.getProteinSet().getSet())
		{
			HashSetMap<String, Domain> lDomainsMap = lProtein.getDomainMap();
			for (Map.Entry<String, Set<Domain>> lEntry : lDomainsMap.entrySet())
				for (Domain lDomain : lEntry.getValue())
				{
					mDomainIdToProteinIndex.put(lDomain.getInterproId(), lProtein);
				}
		}
	}

	public Set<Protein> getProteinByDomainInterproId(String pDomainId)
	{
		return mDomainIdToProteinIndex.get(pDomainId);
	}

	public Set<String> getDomainInterProIdSet()
	{
		return mDomainIdToProteinIndex.keySet();
	}

}
