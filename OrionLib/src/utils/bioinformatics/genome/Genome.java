package utils.bioinformatics.genome;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

import utils.io.LineReader;
import utils.structures.map.HashSetMap;

public class Genome implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Pattern lSplitTabPattern = Pattern.compile("\t");
	private static final Pattern lSplitSemicolonPattern = Pattern.compile("\\;");
	private static final Pattern lSplitCommaPattern = Pattern.compile("\\,");

	private final GeneSet mGeneSet = new GeneSet();
	private final FastaSet mFastaSet = new FastaSet();

	public Genome(final InputStream pInputStream) throws IOException
	{
		super();

		final StringBuilder lComments = new StringBuilder();
		for (String lLine : LineReader.getLines(pInputStream))
		{
			lLine = lLine.trim();
			if (lLine.startsWith("###"))
			{
				break;
			}
			else if (lLine.startsWith("#"))
			{
				lComments.append(lLine);
				continue;
			}

			final String[] lTokenArray = lSplitTabPattern.split(lLine, -1);
			final String lType = lTokenArray[3 - 1];

			if (lType.contains("gene"))
			{

				final int lStart = Integer.parseInt(lTokenArray[4 - 1]) - 1; // gff is
				// "1-based"
				// convert to
				// 0-based
				final int lEnd = Integer.parseInt(lTokenArray[5 - 1]) - 1; // gff is
				// "1-based"
				// convert to
				// 0-based
				double lScore = Double.NaN;
				if (!lTokenArray[6 - 1].trim().equals("."))
				{
					lScore = Double.parseDouble(lTokenArray[6 - 1]);
				}

				final String lStrand = lTokenArray[7 - 1];

				int lPhase = Integer.MAX_VALUE;
				if (!lTokenArray[8 - 1].trim().equals("."))
				{
					lPhase = Integer.parseInt(lTokenArray[8 - 1]);
				}

				final String lAttributes = lTokenArray[9 - 1];
				final String[] lAttributesArray = lSplitSemicolonPattern.split(	lAttributes,
																																				-1);
				final HashMap<String, String> lAttributesMap = new HashMap<String, String>();
				final HashSetMap<String, String> lTermMap = new HashSetMap<String, String>();
				for (final String lAttribute : lAttributesArray)
				{
					final int lEqualPosition = lAttribute.indexOf('=');
					final String lKey = lAttribute.substring(0, lEqualPosition);
					final String lValues = lAttribute.substring(lEqualPosition + 1,
																											lAttribute.length());
					final String lDecodedValue = URLDecoder.decode(lValues, "UTF-8");
					final String[] lValuesArray = lSplitCommaPattern.split(	lDecodedValue,
																																	-1);
					if (lValuesArray.length == 1)
					{
						lAttributesMap.put(lKey, lValues);
					}
					else
					{
						for (final String lValue : lValuesArray)
						{
							lTermMap.put(lKey, lValue);
						}
					}
				}

				final String lId = lAttributesMap.get("ID");
				if (lId != null)
				{
					final String lName = lAttributesMap.get("Name");
					final String lNote = lAttributesMap.get("Note");
					final String lOrfClasssification = lAttributesMap.get("orf_classification");
					final Gene lGene = new Gene(lId,
																			lName,
																			lNote,
																			lOrfClasssification,
																			lStart,
																			lEnd,
																			lStrand,
																			lPhase);

					final Set<String> lOboTermStringSet = lTermMap.get("Ontology_term");
					if (lOboTermStringSet != null)
					{
						try
						{
							lGene.addAllOboTerms(lOboTermStringSet);
							mGeneSet.add(lGene);
						}
						catch (final Exception e)
						{
							e.printStackTrace();
						}
					}

				}
			}

		}

	}

	public Genome(final File pFile) throws FileNotFoundException, IOException
	{
		this(new FileInputStream(pFile));
	}

	public void relateSequencesToGenes()
	{
		for (final Gene lGene : mGeneSet.getSet())
		{
			final String lId = lGene.mId;
			final FastaSequence lFastaSequence = mFastaSet.getSequenceByName(lId);
			if (lFastaSequence != null)
			{
				lGene.setCorrespondingFastaSequence(lFastaSequence);
			}
		}
	}

	public GeneSet getGeneSet()
	{
		return mGeneSet;
	}

	@Override
	public String toString()
	{
		final StringBuilder lStringBuilder = new StringBuilder();
		lStringBuilder.append(mGeneSet);
		lStringBuilder.append("\n");
		lStringBuilder.append(mFastaSet);
		return lStringBuilder.toString();
	}

	public void addSequencesFromFile(final File pFile) throws IOException
	{
		mFastaSet.addSequencesFromFile(pFile);
		relateSequencesToGenes();
	}

	public void addSequencesFromStream(final InputStream pInputStream) throws IOException
	{
		mFastaSet.addSequencesFromStream(pInputStream);
		relateSequencesToGenes();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (mFastaSet == null ? 0 : mFastaSet.hashCode());
		result = prime * result + (mGeneSet == null ? 0 : mGeneSet.hashCode());
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
		final Genome other = (Genome) obj;
		if (mFastaSet == null)
		{
			if (other.mFastaSet != null)
			{
				return false;
			}
		}
		else if (!mFastaSet.equals(other.mFastaSet))
		{
			return false;
		}
		if (mGeneSet == null)
		{
			if (other.mGeneSet != null)
			{
				return false;
			}
		}
		else if (!mGeneSet.equals(other.mGeneSet))
		{
			return false;
		}
		return true;
	}

}
