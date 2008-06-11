package utils.bioinformatics.proteome;

import java.io.Serializable;
import java.util.HashSet;

import utils.bioinformatics.genome.FastaSequence;
import utils.bioinformatics.ontology.OboTerm;

public class Domain implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected final String mId;
	protected final String mInterProId;
	protected final String mDescription;
	protected final String mSource;
	protected final int mStart; // inclusive
	protected final int mEnd; // exclusive
	protected final double mEValue;

	protected final HashSet<OboTerm> mOBOTermSet = new HashSet<OboTerm>();
	private FastaSequence mCorrespondingFastaSequence;

	public Domain(final String pId,
								final String pInterProId,
								final String pDescription,
								final String pSource,
								final int pStart,
								final int pEnd,
								final double pEValue)
	{
		super();
		mId = pId;
		mInterProId = pInterProId;
		mSource = pSource;
		mStart = pStart;
		mEnd = pEnd;
		mEValue = pEValue;
		mDescription = pDescription;
	}

	public String getId()
	{
		return mId;
	}

	public String getSource()
	{
		return mSource;
	}

	public String getInterproId()
	{
		return mInterProId;
	}

	public double getEValue()
	{
		return mEValue;
	}

	public FastaSequence getCorrespondingFastaSequence()
	{
		return mCorrespondingFastaSequence;
	}

	public void setCorrespondingFastaSequence(final FastaSequence pCorrespondingFastaSequence)
	{
		mCorrespondingFastaSequence = pCorrespondingFastaSequence;
	}

	@Override
	public String toString()
	{
		final StringBuilder lStringBuilder = new StringBuilder();
		lStringBuilder.append(mId + "\t");
		lStringBuilder.append(mSource + "\t");
		lStringBuilder.append(mInterProId + "\t");
		lStringBuilder.append(mDescription + "\t");
		lStringBuilder.append(mStart + "\t");
		lStringBuilder.append(mEnd + "\t");
		lStringBuilder.append(mEValue + "\t");
		if (mCorrespondingFastaSequence != null)
		{
			lStringBuilder.append("[s=" + mCorrespondingFastaSequence.getFastaName()
														+ "]");
		}

		return lStringBuilder.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + mEnd;
		result = prime * result
							+ (mInterProId == null ? 0 : mInterProId.hashCode());
		result = prime * result + mStart;
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
		final Domain other = (Domain) obj;
		if (mEnd != other.mEnd)
		{
			return false;
		}
		if (mInterProId == null)
		{
			if (other.mInterProId != null)
			{
				return false;
			}
		}
		else if (!mInterProId.equals(other.mInterProId))
		{
			return false;
		}
		if (mStart != other.mStart)
		{
			return false;
		}
		return true;
	}

}
