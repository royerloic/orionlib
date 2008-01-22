package utils.bioinformatics.proteome;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import utils.bioinformatics.genome.FastaSequence;
import utils.bioinformatics.genome.Gene;
import utils.bioinformatics.ontology.OboTerm;
import utils.structures.HashSetMap;

public class Protein implements Serializable
{
	protected String mId;
	protected final String mName;

	protected final HashSetMap<String, Domain> mInterProIdToDomainsSetMap = new HashSetMap<String, Domain>();

	protected HashSet<OboTerm> mOBOTermSet = new HashSet<OboTerm>();

	protected FastaSequence mCorrespondingFastaSequence = null;
	protected Gene mCorrespondingGene = null;

	public Protein(String pId)
	{
		super();
		mId = pId;
		mName = pId;
	}

	public Protein(String pId, String pName)
	{
		super();
		mId = pId;
		mName = pName;
	}

	public Protein(Gene pGene)
	{
		super();
		mCorrespondingGene = pGene;
		mId = pGene.getId();
		mName = pGene.getName();
	}

	public void addAllOboTerms(Set<String> pOboTermStringSet) throws Exception
	{
		for (String lOboTermString : pOboTermStringSet)
		{
			OboTerm lOboTerm = new OboTerm(lOboTermString);
			mOBOTermSet.add(lOboTerm);
		}
	}

	public void addDomain(Domain pDomain)
	{
		mInterProIdToDomainsSetMap.put(pDomain.getInterproId(), pDomain);

		final int lStart = Math.max(0, pDomain.mStart);
		final int lEnd = Math.min(pDomain.mEnd,
															mCorrespondingFastaSequence.length());

		if (lStart < lEnd)
		{
			final FastaSequence lFastaSequence = mCorrespondingFastaSequence.subSequence(	lStart,
																																										lEnd);
			pDomain.setCorrespondingFastaSequence(lFastaSequence);
		}
	}

	public Set<Domain> getDomainsByInterProId(String pInterProId)
	{
		return mInterProIdToDomainsSetMap.get(pInterProId);
	}

	public HashSetMap<String, Domain> getDomainMap()
	{
		return mInterProIdToDomainsSetMap;
	}

	public String getId()
	{
		return mId;
	}

	public void setId(String pId)
	{
		mId = pId;
	}

	public int getNumberOfDistinctDomains()
	{
		return mInterProIdToDomainsSetMap.size();
	}

	public FastaSequence getCorrespondingFastaSequence()
	{
		return mCorrespondingFastaSequence;
	}

	public void setCorrespondingFastaSequence(FastaSequence pCorrespondingFastaSequence)
	{
		mCorrespondingFastaSequence = pCorrespondingFastaSequence;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mId == null) ? 0 : mId.hashCode());
		result = prime * result + ((mName == null) ? 0 : mName.hashCode());
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
		final Protein other = (Protein) obj;
		if (mId == null)
		{
			if (other.mId != null)
				return false;
		}
		else if (!mId.equals(other.mId))
			return false;
		if (mName == null)
		{
			if (other.mName != null)
				return false;
		}
		else if (!mName.equals(other.mName))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		/*
		 * protected String mSeqId; protected String mSource; protected String
		 * mType; protected int mStart; protected int mEnd; protected double mScore;
		 * protected String mStrand; protected int mPhase; protected HashSetMap<String,String>
		 * mAttributes = new HashSetMap<String, String>();
		 */
		StringBuilder lStringBuilder = new StringBuilder();
		// lStringBuilder.append("# SequenceId \t Source \t Type \t Start \t End \t
		// Score \t Strand \t Phase \t Attributes \n");

		lStringBuilder.append(mId + "\t");
		lStringBuilder.append(mName + "\t");

		if (mCorrespondingFastaSequence != null)
			lStringBuilder.append("[s=" + mCorrespondingFastaSequence.getFastaName()
														+ "]");

		return lStringBuilder.toString();
	}

}
