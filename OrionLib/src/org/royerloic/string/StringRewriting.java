package org.royerloic.string;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.royerloic.io.FlatTextTableReader;
import org.royerloic.io.FlatTextTableReader.FlatTextTableReaderHandler;

/**
 * @author loic (<href>royer@biotec.tu-dresden.de</href>) Nov 21, 2005
 * 
 */
public class StringRewriting implements FlatTextTableReaderHandler
{
	private static final Logger		cLogger	= Logger.getLogger(StringRewriting.class);

	private FlatTextTableReader		mFlatTextTableReader;

	private List<Pattern>					mPatternList;

	private Map<Pattern, String>	mSubstitutionMap;

	private List<String>					mCurrentRegexList;

	private Pattern								mCurrentPattern;

	private String								mCurrentSubstitutionString;

	/**
	 * @param pString
	 * @return a StringRewriting object constructed by reading a rewriting rules
	 *         file with name: "<<pString>>.rewriting.txt"
	 */
	public static StringRewriting getStringRewriting(String pString)
	{
		StringRewriting lStringRewriting = null;
		InputStream lInputStream;
		try
		{
			lStringRewriting = new StringRewriting();
			String lPackagePrefix = StringRewriting.class.getPackage().getName();
			String lRessourceName = lPackagePrefix.replace('.', '/') + "/" + pString + ".rewriting.txt";
			lInputStream = ClassLoader.getSystemResource(lRessourceName).openStream();
			InputStreamReader lInputStreamReader = new InputStreamReader(lInputStream);
			BufferedReader lBufferedReader = new BufferedReader(lInputStreamReader);

			lStringRewriting.compileRewritingRulesFromReader(lBufferedReader);
		}
		catch (IOException exception)
		{
			cLogger.error(exception);
		}
		return lStringRewriting;
	}

	/**
	 * @throws IOException
	 */
	public StringRewriting() throws IOException
	{
		super();
		mPatternList = new ArrayList<Pattern>();
		mSubstitutionMap = new HashMap<Pattern, String>();
		mFlatTextTableReader = new FlatTextTableReader(this);
		mFlatTextTableReader.setColumnSplitRegex("[\\s]+");
		mFlatTextTableReader.setSetSplitRegex("#@nosetsplitregex@#");
		mFlatTextTableReader.setNullRegex("#@nonullregex@#");
		mCurrentRegexList = new ArrayList<String>();
	}

	/**
	 * @param pFile
	 * @throws IOException
	 */
	public void compileRewritingRulesFromFile(File pFile) throws IOException
	{
		mFlatTextTableReader.readFile(pFile, false);
	}

	/**
	 * @param pBufferedReader
	 * @throws IOException
	 */
	public void compileRewritingRulesFromReader(BufferedReader pBufferedReader) throws IOException
	{
		mFlatTextTableReader.readStream(pBufferedReader, false);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see de.tud.biotec.protein.interaction.helper.FlatTextTableReader.FlatTextTableReaderHandler#handleCell(int,
	 *      int, int, java.lang.String)
	 */
	@SuppressWarnings("unused")
	public boolean handleCell(int pLineCounter, int pColumnCounter, int pSetCounter, String pCellString)
	{
		pCellString = pCellString.trim();
		try
		{
			pCellString = pCellString.substring(1, pCellString.length() - 1);
			switch (pColumnCounter)
			{
				case (0):
					if ((pCellString.charAt(0) != '#'))
					{
						mCurrentRegexList.add(pCellString);
					}
					break;

				case (2):
					for (String lRegex : mCurrentRegexList)
					{
						mCurrentSubstitutionString = pCellString;
						mCurrentPattern = Pattern.compile(lRegex);
						mPatternList.add(mCurrentPattern);
						mSubstitutionMap.put(mCurrentPattern, mCurrentSubstitutionString);
					}
					mCurrentRegexList.clear();
					break;
			}
		}
		catch (Throwable exception)
		{
			System.out.println("Rule : '" + pCellString + "' is incorrect.");
			mCurrentRegexList.clear();
		}
		return true;
	}

	public boolean handleEndOfCell(int pLineCounter)
	{
		return true;
	}

	/**
	 * @param pString
	 * @return a rewriten copy of pString
	 */
	public String applyTo(String pString)
	{
		for (Pattern lPattern : mPatternList)
		{
			String lSubstitution = mSubstitutionMap.get(lPattern);
			pString = lPattern.matcher(pString).replaceAll(lSubstitution);
		}
		return new String(pString);
	}

}
