package utils.bioinformatics.fasta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import utils.bioinformatics.genome.ParseException;
import utils.io.LineReader;

public class FastaSet implements Serializable, Iterable<FastaSequence>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Pattern lSplitCommaPattern = Pattern.compile("\\, ");
	private static final Pattern lSplitSpacePattern = Pattern.compile("\\ ");
	private static final Pattern lPipeSpacePattern = Pattern.compile("\\|");
	private static final Pattern lTwoPointsSpacePattern = Pattern.compile("\\:");

	protected HashMap<String, FastaSequence> mFastaSequencesMap = new HashMap<String, FastaSequence>();

	public FastaSet()
	{
		super();
	}

	public FastaSet(final InputStream pInputStream) throws IOException
	{
		super();
		addSequencesFromStream(pInputStream);
	}

	public FastaSet(final File pFile) throws IOException
	{
		super();
		addSequencesFromStream(new FileInputStream(pFile));
	}

	public FastaSequence newSequence(final String pCurrentFastaSequenceName)
	{
		final FastaSequence lFastaSequence = new FastaSequence(pCurrentFastaSequenceName);
		mFastaSequencesMap.put(pCurrentFastaSequenceName, lFastaSequence);
		return lFastaSequence;
	}

	private FastaSequence newSgdSequence(	final String pSgdSystematicName,
																				final String pName,
																				final String pID,
																				final String pHeader)
	{
		final FastaSequence lFastaSequence = new FastaSequence(pSgdSystematicName);
		lFastaSequence.put("SystematicName", pSgdSystematicName);
		lFastaSequence.put("Name", pName);
		lFastaSequence.put("Id", pID);
		lFastaSequence.put("Header", pHeader);
		mFastaSequencesMap.put(pSgdSystematicName, lFastaSequence);
		return lFastaSequence;
	}

	private FastaSequence newSwissProtSequence(	final String pSwissProtId,
																							final String pCurrentFastaSequenceHeader)
	{
		final FastaSequence lFastaSequence = new FastaSequence(pSwissProtId);
		lFastaSequence.put("Id", pSwissProtId);
		lFastaSequence.put("Header", pCurrentFastaSequenceHeader);
		mFastaSequencesMap.put(pSwissProtId, lFastaSequence);
		return lFastaSequence;
	}

	public void addSequencesFromFile(final File pFile) throws IOException
	{
		addSequencesFromStream(new FileInputStream(pFile));
	}

	public void addSequencesFromStream(final InputStream pInputStream) throws IOException
	{

		String lCurrentFastaSequenceHeader;
		FastaSequence lCurrentFastaSequence = null;

		for (String lLine : LineReader.getLines(pInputStream))
		{
			lLine = lLine.trim();
			if (lLine.startsWith("#") || lLine.length() == 0)
			{
				// ignore comments
			}
			else if (lLine.startsWith(">"))
			{
				lCurrentFastaSequenceHeader = lLine.substring(1);
				if (lLine.contains("SGDID"))
				{
					// We know that this is a SGD header...

					final String[] lHeaderArray = lSplitCommaPattern.split(	lCurrentFastaSequenceHeader,
																																	-1);
					final String[] lNamesArray = lSplitSpacePattern.split(lHeaderArray[0],
																																-1);

					final String lSystematicName = lNamesArray[0];
					final String lName = lNamesArray[1];
					final String lId = lNamesArray[2];

					lCurrentFastaSequence = newSgdSequence(	lSystematicName,
																									lName,
																									lId,
																									lCurrentFastaSequenceHeader);
				}
				else if (lLine.contains("SWISS-PROT"))
				{
					final String[] lHeaderArray = lPipeSpacePattern.split(lCurrentFastaSequenceHeader,
																																-1);
					final String lSwissProtId = lHeaderArray[0];

					lCurrentFastaSequence = newSwissProtSequence(	lSwissProtId,
																												lCurrentFastaSequenceHeader);
				}
				else
				{
					final String[] lHeaderArray = lPipeSpacePattern.split(lCurrentFastaSequenceHeader,
																																-1);
					lCurrentFastaSequence = newSequence(lCurrentFastaSequenceHeader);
				}
				

			}
			else
			{
				if (lCurrentFastaSequence != null)
				{
					lCurrentFastaSequence.append(lLine.trim());
				}
				else
				{
					throw new ParseException("Fasta sequence without name");
				}
			}
		}

	}

	public Set<String> getIdSet()
	{
		return mFastaSequencesMap.keySet();
	}

	public Collection<FastaSequence> getFastaSequences()
	{
		return mFastaSequencesMap.values();
	}

	@Override
	public String toString()
	{
		final StringBuilder lStringBuilder = new StringBuilder();
		for (final FastaSequence lFastaSequence : mFastaSequencesMap.values())
		{
			lStringBuilder.append(lFastaSequence);
			lStringBuilder.append("\n");
		}
		return lStringBuilder.toString();
	}

	public FastaSequence getSequenceByName(final String pName)
	{
		return mFastaSequencesMap.get(pName);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
							+ (mFastaSequencesMap == null ? 0 : mFastaSequencesMap.hashCode());
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
		final FastaSet other = (FastaSet) obj;
		if (mFastaSequencesMap == null)
		{
			if (other.mFastaSequencesMap != null)
			{
				return false;
			}
		}
		else if (!mFastaSequencesMap.equals(other.mFastaSequencesMap))
		{
			return false;
		}
		return true;
	}

	public Iterator<FastaSequence> iterator()
	{
		return mFastaSequencesMap.values().iterator();
	}

	public int size()
	{
		return mFastaSequencesMap.size();
	}

	public void toFile(File tempFastaFile)
	{
		// TODO Auto-generated method stub
		
	}

}