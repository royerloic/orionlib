package utils.bioinformatics.genome;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import utils.bioinformatics.ontology.OboTerm;

public class Gene implements Serializable
{
	protected String mId;
	protected final String mName;
	protected final String mNote;
	protected final String mOrfClassification;
	protected int mStart;
	protected int mEnd;
	protected String mStrand;
	protected int mPhase;
	
	protected HashSet<OboTerm> mOBOTermSet = new HashSet<OboTerm>();

	protected FastaSequence mCorrespondingFastaSequence = null;

	public Gene(String pId,
							String pName,
							String pNote,
							String pOrfClassification,
							int pStart,
							int pEnd,
							String pStrand,
							int pPhase)
	{
		super();
		mId = pId;
		mName = pName;
		mNote = pNote;
		mOrfClassification = pOrfClassification;
		mStart = pStart;
		mEnd = pEnd;
		mStrand = pStrand;
		mPhase = pPhase;
	}
	



	public void addAllOboTerms(Set<String> pOboTermStringSet) throws Exception
	{
		for (String lOboTermString : pOboTermStringSet)
		{
			OboTerm lOboTerm = new OboTerm(lOboTermString);
			mOBOTermSet.add(lOboTerm);
		}		
	}

	

	public String getId()
	{
		return mId;
	}

	public void setId(String pId)
	{
		mId = pId;
	}
	
	public String getName()
	{
		return mName;
	}

	public int getStart()
	{
		return mStart;
	}

	public void setStart(int pStart)
	{
		mStart = pStart;
	}

	public int getEnd()
	{
		return mEnd;
	}

	public void setEnd(int pEnd)
	{
		mEnd = pEnd;
	}

	public String getStrand()
	{
		return mStrand;
	}

	public void setStrand(String pStrand)
	{
		mStrand = pStrand;
	}

	public int getPhase()
	{
		return mPhase;
	}

	public void setPhase(int pPhase)
	{
		mPhase = pPhase;
	}



	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + mEnd;
		result = prime * result + ((mId == null) ? 0 : mId.hashCode());
		result = prime * result + mPhase;
		result = prime * result + mStart;
		result = prime * result + ((mStrand == null) ? 0 : mStrand.hashCode());
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
		final Gene other = (Gene) obj;
		if (mEnd != other.mEnd)
			return false;
		if (mId == null)
		{
			if (other.mId != null)
				return false;
		}
		else if (!mId.equals(other.mId))
			return false;
		if (mPhase != other.mPhase)
			return false;
		if (mStart != other.mStart)
			return false;
		if (mStrand == null)
		{
			if (other.mStrand != null)
				return false;
		}
		else if (!mStrand.equals(other.mStrand))
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

		if (mCorrespondingFastaSequence != null)
			lStringBuilder.append("# This gene is linked to sequence: " + mCorrespondingFastaSequence.getFastaName()
														+ "\n");

		lStringBuilder.append(mId + "\t");
		lStringBuilder.append(mName + "\t");
		lStringBuilder.append(mOrfClassification + "\t");

		lStringBuilder.append(mStart + "\t");
		lStringBuilder.append(mEnd + "\t");

		lStringBuilder.append(mStrand + "\t");
		lStringBuilder.append(mPhase + "\t");
				
		lStringBuilder.append(mNote + "\t");
		
		

		return lStringBuilder.toString();
	}

	public FastaSequence getCorrespondingFastaSequence()
	{
		return mCorrespondingFastaSequence;
	}

	public void setCorrespondingFastaSequence(FastaSequence pCorrespondingFastaSequence)
	{
		mCorrespondingFastaSequence = pCorrespondingFastaSequence;
	}




	



}
