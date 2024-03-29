package utils.bioinformatics.fasta;

import java.io.Serializable;
import java.util.HashMap;

public class FastaSequence implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String mFastaName;

	protected String mSequence = "";

	protected HashMap<String, String> mHeaderMap = new HashMap<String, String>();

	public FastaSequence(final String pFastaName)
	{
		super();
		mFastaName = pFastaName;
	}

	protected FastaSequence(final String pFastaName, final String pSequenceString)
	{
		mFastaName = pFastaName;
		mSequence = pSequenceString;
	}

	@SuppressWarnings("unchecked")
	public FastaSequence(final FastaSequence pFastaSequence)
	{
		super();
		mFastaName = new String(pFastaSequence.mFastaName);
		mSequence = new String(pFastaSequence.mSequence);
		mHeaderMap = (HashMap<String, String>) mHeaderMap.clone();
	}

	public void append(final String pString)
	{
		mSequence = mSequence + pString;
	}

	public String getSequenceString()
	{
		return mSequence;
	}

	public char[] getSequenceArray()
	{
		return mSequence.toCharArray();
	}

	public void setSequenceArray(char[] pSequenceArray)
	{
		mSequence = new String(pSequenceArray);
	}

	@Override
	public String toString()
	{
		final StringBuilder lStringBuilder = new StringBuilder();
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

	public void put(final String pKey, final String pValue)
	{
		mHeaderMap.put(pKey, pValue);
	}

	public int length()
	{
		return mSequence.length();
	}

	public FastaSequence subSequence(final int pStart, final int pEnd)
	{
		final FastaSequence lFastaSequence = new FastaSequence(	mFastaName + "\t{SubSequence["
																																+ pStart
																																+ ","
																																+ pEnd
																																+ "[}",
																														mSequence.substring(pStart,
																																								pEnd));
		return lFastaSequence;
	}

	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mSequence == null) ? 0 : mSequence.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
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
		FastaSequence other = (FastaSequence) obj;
		if (mSequence == null)
		{
			if (other.mSequence != null)
			{
				return false;
			}
		}
		else if (!mSequence.equals(other.mSequence))
		{
			return false;
		}
		return true;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		super.clone();
		FastaSequence lFastaSequence = new FastaSequence(this);
		return lFastaSequence;
	}

}
