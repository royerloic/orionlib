package utils.nlp.wordlists;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import utils.io.FlatTextTableReader;
import utils.io.FlatTextTableReader.FlatTextTableReaderHandler;

/**
 * @author loic (<href>royer@biotec.tu-dresden.de</href>) Nov 21, 2005
 * 
 */
public abstract class GenericWordIdentifier implements FlatTextTableReaderHandler
{

	private FlatTextTableReader		mFlatTextTableReader;

	protected Map<String, String>	mWordMap;
	protected Map<String, Double>	mWordToCountMap;

	private String								mCurrentWord;

	/**
	 */
	public GenericWordIdentifier()
	{
		super();
		mWordMap = new HashMap<String, String>();
		mWordToCountMap = new HashMap<String, Double>();
		mFlatTextTableReader = new FlatTextTableReader(this);
		mFlatTextTableReader.setColumnSplitRegex("\t+");
		mFlatTextTableReader.setSetSplitRegex("#@nosetsplitregex@#");
		mFlatTextTableReader.setNullRegex("#@nonullregex@#");
	}

	/**
	 * @param pFile
	 * @throws IOException
	 */
	public void compileIdentificationRulesFromFile(final File pFile) throws IOException
	{
		mFlatTextTableReader.readFile(pFile, false);
	}

	/**
	 * @param pBufferedReader
	 * @throws IOException
	 */
	public void compileIdentificationRulesFromReader(final BufferedReader pBufferedReader) throws IOException
	{
		mFlatTextTableReader.readStream(pBufferedReader, false);
	}

	public void compileIdentificationRulesFromRessource(final Class pClass, final String pRessourceName) throws IOException
	{
		mFlatTextTableReader.readRessource(pClass, pRessourceName, false);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @param pLineCounter
	 * @param pColumnCounter
	 * @param pSetCounter
	 * @param pCellString
	 * 
	 */
	@SuppressWarnings("unused")
	public boolean handleCell(final int pLineCounter, final int pColumnCounter, final int pSetCounter, String pCellString)
	{
		pCellString = pCellString.trim();
		try
		{
			switch (pColumnCounter)
			{
				case (0):
				{
					if (pCellString.length() != 0)
						if ((pCellString.charAt(0) != '#'))
						{
							final String lWord = normalizeString(pCellString);
							mWordMap.put(lWord, lWord);
							mCurrentWord = lWord;
						}
				}
					break;

				case (1):
				{
					if (pCellString.length() != 0)
					{
						final double lValue = Double.parseDouble(pCellString);
						mWordToCountMap.put(mCurrentWord, lValue);
					}
				}

			}
		}
		catch (final Throwable exception)
		{
			System.out.println("Rule : '" + pCellString + "' is incorrect.");
		}
		return true;
	}

	public boolean handleEndOfCell(final int pLineCounter)
	{
		return true;
	}

	public boolean isEntity(final String pToken)
	{
		return mWordMap.containsKey(pToken);
	}

	/**
	 * @param pString
	 * @return normalized copy of pString.
	 */
	public abstract String normalizeString(String pString);

}
