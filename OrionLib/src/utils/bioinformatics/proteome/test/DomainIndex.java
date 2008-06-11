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

	public DomainIndex(final Proteome pProteome)
	{
		mProteome = pProteome;

	}

	public void index()
	{
		for (final Protein lProtein : mProteome.getProteinSet().getSet())
		{
			final HashSetMap<String, Domain> lDomainsMap = lProtein.getDomainMap();
			for (final Map.Entry<String, Set<Domain>> lEntry : lDomainsMap.entrySet())
			{
				for (final Domain lDomain : lEntry.getValue())
				{
					mDomainIdToProteinIndex.put(lDomain.getInterproId(), lProtein);
				}
			}
		}
	}

	public Set<Protein> getProteinByDomainInterproId(final String pDomainId)
	{
		return mDomainIdToProteinIndex.get(pDomainId);
	}

	public Set<String> getDomainInterProIdSet()
	{
		return mDomainIdToProteinIndex.keySet();
	}

}
