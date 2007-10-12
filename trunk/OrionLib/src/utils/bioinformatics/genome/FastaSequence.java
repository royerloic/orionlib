package utils.bioinformatics.genome;

import java.io.Serializable;
import java.util.HashMap;

public class FastaSequence implements Serializable
{

	protected String mFastaName;

	protected String mSequence = "";

	protected HashMap<String, String> mHeaderMap = new HashMap<String, String>();

	public FastaSequence(String pFastaName)
	{
		super();
		mFastaName = pFastaName;
	}

	public void append(String pString)
	{
		mSequence = mSequence + pString;
	}

	public String getSequenceString()
	{
		return mSequence;
	}

	@Override
	public String toString()
	{
		StringBuilder lStringBuilder = new StringBuilder();
		lStringBuilder.append(">" + mFastaName + "\n");
		int lColumn = 0;
		for (int i = 0; i < mSequence.length(); i++)
		{
			lColumn++;
			final char lChar = mSequence.charAt(i);
			lStringBuilder.append(lChar);
			if (lColumn == 80)
			{
				lStringBuilder.append("\n");
				lColumn = 0;
			}
		}
		lStringBuilder.append("\n");
		return lStringBuilder.toString();
	}

	public String getFastaName()
	{
		return mFastaName;
	}

	public void put(String pKey, String pValue)
	{
		mHeaderMap.put(pKey, pValue);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
							+ ((mFastaName == null) ? 0 : mFastaName.hashCode());
		result = prime * result + ((mSequence == null) ? 0 : mSequence.hashCode());
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
		final FastaSequence other = (FastaSequence) obj;
		if (mFastaName == null)
		{
			if (other.mFastaName != null)
				return false;
		}
		else if (!mFastaName.equals(other.mFastaName))
			return false;
		if (mSequence == null)
		{
			if (other.mSequence != null)
				return false;
		}
		else if (!mSequence.equals(other.mSequence))
			return false;
		return true;
	}

}
