package org.royerloic.nlp.wordlists;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.royerloic.io.FlatTextTableReader;
import org.royerloic.io.FlatTextTableReader.FlatTextTableReaderHandler;

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
	public void compileIdentificationRulesFromFile(File pFile) throws IOException
	{
		mFlatTextTableReader.readFile(pFile, false);
	}

	/**
	 * @param pBufferedReader
	 * @throws IOException
	 */
	public void compileIdentificationRulesFromReader(BufferedReader pBufferedReader) throws IOException
	{
		mFlatTextTableReader.readStream(pBufferedReader, false);
	}

	public void compileIdentificationRulesFromRessource(Class pClass, String pRessourceName) throws IOException
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
	public boolean handleCell(int pLineCounter, int pColumnCounter, int pSetCounter, String pCellString)
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
							String lWord = normalizeString(pCellString);
							mWordMap.put(lWord, lWord);
							mCurrentWord = lWord;
						}
				}
					break;

				case (1):
				{
					if (pCellString.length() != 0)
					{
						double lValue = Double.parseDouble(pCellString);
						mWordToCountMap.put(mCurrentWord, lValue);
					}
				}

			}
		}
		catch (Throwable exception)
		{
			System.out.println("Rule : '" + pCellString + "' is incorrect.");
		}
		return true;
	}

	public boolean handleEndOfCell(int pLineCounter)
	{
		return true;
	}

	public boolean isEntity(String pToken)
	{
		return mWordMap.containsKey(pToken);
	}

	/**
	 * @param pString
	 * @return normalized copy of pString.
	 */
	public abstract String normalizeString(String pString);

}
