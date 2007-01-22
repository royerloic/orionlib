package org.royerloic.nlp.regex.compiler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.royerloic.io.MatrixFile;
import org.royerloic.string.StringUtils;
import org.royerloic.structures.HashSetMap;
import org.royerloic.structures.Matrix;
import org.royerloic.structures.SetMap;

public class RegexCompiler
{

	private Map<String, String>			mRegexNameToRegexMap	= new HashMap<String, String>();
	private SetMap<String, String>	mSetNameToNameSetMap	= new HashSetMap<String, String>();

	private RegexCompiler() throws IOException
	{
	}

	public RegexCompiler(final String pRessource) throws IOException
	{
		readRules(pRessource);
	}

	public RegexCompiler(final InputStream pInputStream) throws IOException
	{
		readRules(pInputStream);
	}

	public void readRules(final String pRessource) throws FileNotFoundException, IOException
	{
		readRules(MatrixFile.getInputStreamFromRessource(new RegexCompiler().getClass(), pRessource));
	}

	public void readRules(final InputStream pInputStream) throws FileNotFoundException, IOException
	{
		List<List<String>> lMatrix = MatrixFile.readMatrixFromStream(pInputStream, false, "\\s+:=\\s+");

		boolean lIsSet = false;
		String lSetName = null;

		for (List<String> lList : lMatrix)
		{

			if (lList.size() == 2)
			{
				String lName = lList.get(0).trim();
				String lRegex = lList.get(1).trim();
				lRegex = "(?:" + lRegex + ")";

				// Replace Sets
				for (Map.Entry<String, Set<String>> lEntry : mSetNameToNameSetMap.entrySet())
				{
					String lSetNameUse = "@" + lEntry.getKey() + "@";
					String lSetReplacement = mergeRegexSetToRegex(lEntry.getValue());
					lRegex = lRegex.replace(lSetNameUse, lSetReplacement);
				}

				// replace Regexes
				for (Map.Entry<String, String> lEntry : mRegexNameToRegexMap.entrySet())
				{
					String lSetNameUse = "@" + lEntry.getKey() + "@";
					String lSetReplacement = lEntry.getValue();
					lRegex = lRegex.replace(lSetNameUse, lSetReplacement);
				}

				mRegexNameToRegexMap.put(lName, lRegex);

			}
			else if (lList.size() == 1)
			{
				String lString = lList.get(0).trim();
				if (lString.isEmpty())
				{
					lIsSet = false;
				}
				else if (lString.startsWith("//"))
				{
					// just ignore
				}
				else if (lIsSet)
				{
					if (lString.startsWith("@") && lString.endsWith("@"))
					{
						String lAddedSetName = lString.substring(1, lString.length() - 1);
						mSetNameToNameSetMap.get(lSetName).addAll(mSetNameToNameSetMap.get(lAddedSetName));
					}
					else
					{
						mSetNameToNameSetMap.put(lSetName, lString);
					}
				}
				else if (lString.startsWith("import:"))
				{
					String[] lStringArray = StringUtils.split(lList.get(0), "\\s*:\\s*", 0);
					String lImportRulesFileName = lStringArray[1].trim();
					readRules(lImportRulesFileName);
				}
				else if (lString.startsWith("importset:"))
				{
					String[] lStringArray = StringUtils.split(lString, ":\\s*|\\s+as\\s+", 0);
					String lImportSetFileName = lStringArray[1].trim();
					String lImportedSetName = lStringArray[2].trim();
					importRegexFile(lImportedSetName, lImportSetFileName);
				}
				else if (lString.startsWith("set:"))
				{
					lIsSet = true;
					String[] lStringArray = StringUtils.split(lString, ":\\s*", 0);
					lSetName = lStringArray[1].trim();
					mSetNameToNameSetMap.put(lSetName);
					continue;
				}

			}

		}
	}

	private void importRegexFile(final String pImportedSetName, final String pRessourceFileName)
			throws FileNotFoundException, IOException
	{
		importRegexFile(pImportedSetName, MatrixFile.getInputStreamFromRessource(new RegexCompiler().getClass(),
				pRessourceFileName));
	}

	private void importRegexFile(final String pImportedSetName, final InputStream pImportRegexInputStream)
			throws FileNotFoundException, IOException
	{
		Matrix<String> lMatrix = MatrixFile.readMatrixFromStream(pImportRegexInputStream, false);
		for (List<String> lList : lMatrix)
		{
			String lEntry = lList.get(0).trim();
			mSetNameToNameSetMap.put(pImportedSetName, "(?:" + lEntry + ")");
		}
	}

	private static String mergeRegexSetToRegex(final Collection<String> pSet)
	{
		if (!pSet.isEmpty())
		{
			StringBuffer lStringBuffer = new StringBuffer();
			lStringBuffer.append("(?:");
			for (String lString : pSet)
			{
				lStringBuffer.append(lString + "|");
			}
			lStringBuffer.deleteCharAt(lStringBuffer.length() - 1);
			lStringBuffer.append(")");
			return lStringBuffer.toString();
		}
		return "";
	}

	public String getRegex(String pName)
	{
		return mRegexNameToRegexMap.get(pName);
	}

	public Pattern getPattern(String pName)
	{
		return Pattern.compile(mRegexNameToRegexMap.get(pName));
	}

}
