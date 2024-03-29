package utils.bioinformatics.genome;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import utils.bioinformatics.fasta.FastaSequence;
import utils.bioinformatics.ontology.OboTerm;

public class Gene implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

	public Gene(final String pId,
							final String pName,
							final String pNote,
							final String pOrfClassification,
							final int pStart,
							final int pEnd,
							final String pStrand,
							final int pPhase)
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

	public void addAllOboTerms(final Set<String> pOboTermStringSet) throws Exception
	{
		for (final String lOboTermString : pOboTermStringSet)
		{
			final OboTerm lOboTerm = new OboTerm(lOboTermString);
			mOBOTermSet.add(lOboTerm);
		}
	}

	public String getId()
	{
		return mId;
	}

	public void setId(final String pId)
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

	public void setStart(final int pStart)
	{
		mStart = pStart;
	}

	public int getEnd()
	{
		return mEnd;
	}

	public void setEnd(final int pEnd)
	{
		mEnd = pEnd;
	}

	public String getStrand()
	{
		return mStrand;
	}

	public void setStrand(final String pStrand)
	{
		mStrand = pStrand;
	}

	public int getPhase()
	{
		return mPhase;
	}

	public void setPhase(final int pPhase)
	{
		mPhase = pPhase;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + mEnd;
		result = prime * result + (mId == null ? 0 : mId.hashCode());
		result = prime * result + mPhase;
		result = prime * result + mStart;
		result = prime * result + (mStrand == null ? 0 : mStrand.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final Gene other = (Gene) obj;
		if (mEnd != other.mEnd)
		{
			return false;
		}
		if (mId == null)
		{
			if (other.mId != null)
			{
				return false;
			}
		}
		else if (!mId.equals(other.mId))
		{
			return false;
		}
		if (mPhase != other.mPhase)
		{
			return false;
		}
		if (mStart != other.mStart)
		{
			return false;
		}
		if (mStrand == null)
		{
			if (other.mStrand != null)
			{
				return false;
			}
		}
		else if (!mStrand.equals(other.mStrand))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		/*
		 * protected String mSeqId; protected String mSource; protected String
		 * mType; protected int mStart; protected int mEnd; protected double mScore;
		 * protected String mStrand; protected int mPhase; protected
		 * HashSetMap<String,String> mAttributes = new HashSetMap<String, String>();
		 */
		final StringBuilder lStringBuilder = new StringBuilder();
		// lStringBuilder.append("# SequenceId \t Source \t Type \t Start \t End \t
		// Score \t Strand \t Phase \t Attributes \n");

		if (mCorrespondingFastaSequence != null)
		{
			lStringBuilder.append("# This gene is linked to sequence: " + mCorrespondingFastaSequence.getFastaName()
														+ "\n");
		}

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

	public void setCorrespondingFastaSequence(final FastaSequence pCorrespondingFastaSequence)
	{
		mCorrespondingFastaSequence = pCorrespondingFastaSequence;
	}

}
