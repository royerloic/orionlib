package utils.bioinformatics.fasta;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.Writer;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import utils.bioinformatics.RegexPatterns;
import utils.bioinformatics.genome.ParseException;
import utils.io.LineReader;
import utils.io.LineWriter;
import utils.string.StringUtils;

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

	protected LinkedHashMap<String, FastaSequence> mFastaSequencesMap = new LinkedHashMap<String, FastaSequence>();
	private String mName;

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

	public FastaSet(final Collection<String> pUniprotIdList) throws IOException
	{
		this((String[]) pUniprotIdList.toArray());
	}

	public FastaSet(final String[] pUniprotIdList) throws IOException
	{
		super();
		for (String id : pUniprotIdList)
		{
			id = id.toUpperCase().trim();
			if (!StringUtils.matches(id, RegexPatterns.sUniProtPattern))
			{
				System.out.println("Not a uniprot id: '" + id
														+ "'\ntrying to locate id within string.");
				List<String> lCandidate = StringUtils.findAllmatches(	id,
																															RegexPatterns.sUniProtPattern);
				if (lCandidate.size() == 1)
				{
					id = lCandidate.get(0);
					System.out.println("Found id: '" + id + "'");
				}
				else if (lCandidate.size() == 0)
				{
					System.out.println("Could not find anything that looks like a uniprot id...");
				}
				else if (lCandidate.size() > 1)
				{
					id = lCandidate.get(0);
					System.out.println("Too many uniprot ids found. Picking first one: '" + id
															+ "'");
				}
			}

			URL lURL = new URL("http://www.uniprot.org/uniprot/" + id + ".fasta");
			InputStream lInputStream = lURL.openStream();
			DataInputStream lDataInputStream = new DataInputStream(new BufferedInputStream(lInputStream));
			addSequencesFromStream(lDataInputStream);
		}
	}

	public FastaSequence addFastaSequence(FastaSequence pFastaSequence)
	{
		return mFastaSequencesMap.put(pFastaSequence.getFastaName(), pFastaSequence);
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

	public void setName(String pName)
	{
		mName = pName;
	}

	public String getName()
	{
		return mName;
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

	public String toAlignmentString()
	{
		final StringBuilder lStringBuilder = new StringBuilder();
		for (final String lId : mFastaSequencesMap.keySet())
		{
			lStringBuilder.append(lId);
			lStringBuilder.append("\n");
		}
		lStringBuilder.append("\n");

		for (final FastaSequence lFastaSequence : mFastaSequencesMap.values())
		{
			lStringBuilder.append(lFastaSequence.getSequenceString());
			lStringBuilder.append("\n");
		}

		lStringBuilder.append("\n");
		double[] lComputedEntropy = Conservation.computeRelativeNegentropy(this);
		String lCodedEntropy = Conservation.encodeAsVisualString(lComputedEntropy);
		lStringBuilder.append(lCodedEntropy);
		lStringBuilder.append("\n");
		lStringBuilder.append("Max Entropy Conservation: " + Conservation.getMaxConservation(lComputedEntropy));

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

	public void toFile(File pFile) throws IOException
	{
		Writer lWriter = LineWriter.getWriter(pFile);

		for (final FastaSequence lFastaSequence : mFastaSequencesMap.values())
		{
			lWriter.append(lFastaSequence.toString());
			lWriter.append("\n");
		}
		lWriter.flush();
		lWriter.close();
	}

}
