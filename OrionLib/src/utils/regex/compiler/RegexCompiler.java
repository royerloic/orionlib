package utils.regex.compiler;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import utils.io.LineReader;
import utils.string.StringUtils;
import utils.structures.HashSetMap;
import utils.structures.Matrix;
import utils.structures.Pair;
import utils.structures.SetMap;

public class RegexCompiler implements Iterable<Pair<String>>
{

	private final Map<String, String>			mRegexNameToRegexMap	= new HashMap<String, String>();
	private final SetMap<String, String>	mSetNameToNameSetMap	= new HashSetMap<String, String>();

	private RegexCompiler()
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
		readRules(RegexCompiler.class.getResourceAsStream(pRessource));
	}

	public void readRules(final InputStream pInputStream) throws FileNotFoundException, IOException
	{
		final List<List<String>> lMatrix = LineReader.readMatrixFromStream(pInputStream, false, "\\s+:=\\s+");

		boolean lIsSet = false;
		String lSetName = null;

		for (final List<String> lList : lMatrix)
		{

			if (lList.size() == 2)
			{
				final String lName = lList.get(0).trim();
				String lRegex = lList.get(1).trim();
				
				// Replace Sets
				for (final Map.Entry<String, Set<String>> lEntry : mSetNameToNameSetMap.entrySet())
				{
					final String lSetNameUse = "@" + lEntry.getKey() + "@";
					final String lSetReplacement = mergeRegexSetToRegex(lEntry.getValue());
					lRegex = lRegex.replace(lSetNameUse, lSetReplacement);
				}

				// replace Regexes
				for (final Map.Entry<String, String> lEntry : mRegexNameToRegexMap.entrySet())
				{
					final String lSetNameUse = "@" + lEntry.getKey() + "@";
					final String lSetReplacement = lEntry.getValue();
					lRegex = lRegex.replace(lSetNameUse, lSetReplacement);
				}
				
				lRegex = wrapAroundGroup(lRegex);

				mRegexNameToRegexMap.put(lName, lRegex);

			}
			else if (lList.size() == 1)
			{
				final String lString = lList.get(0).trim();
				if (lString.length()==0)
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
						final String lAddedSetName = lString.substring(1, lString.length()-1);
						mSetNameToNameSetMap.get(lSetName).addAll(mSetNameToNameSetMap.get(lAddedSetName));
					}
					else
					{
						mSetNameToNameSetMap.put(lSetName, lString);
					}
				}
				else if (lString.startsWith("import:"))
				{
					final String[] lStringArray = StringUtils.split(lList.get(0), "\\s*:\\s*", 0);
					final String lImportRulesFileName = lStringArray[1].trim();
					readRules(lImportRulesFileName);
				}
				else if (lString.startsWith("importset:"))
				{
					final String[] lStringArray = StringUtils.split(lString, ":\\s*|\\s+as\\s+", 0);
					final String lImportSetFileName = lStringArray[1].trim();
					final String lImportedSetName = lStringArray[2].trim();
					importRegexFile(lImportedSetName, lImportSetFileName);
				}
				else if (lString.startsWith("set:"))
				{
					lIsSet = true;
					final String[] lStringArray = StringUtils.split(lString, ":\\s*", 0);
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
		importRegexFile(pImportedSetName, LineReader.getInputStreamFromRessource(new RegexCompiler().getClass(),
				pRessourceFileName));
	}

	private void importRegexFile(final String pImportedSetName, final InputStream pImportRegexInputStream)
			throws FileNotFoundException, IOException
	{
		final Matrix<String> lMatrix = LineReader.readMatrixFromStream(pImportRegexInputStream, false);
		for (final List<String> lList : lMatrix)
		{
			final String lEntry = lList.get(0).trim();
			mSetNameToNameSetMap.put(pImportedSetName, "(?:" + lEntry + ")");
		}
	}

	private static String mergeRegexSetToRegex(final Collection<String> pSet)
	{
		if (!pSet.isEmpty())
		{
			final StringBuffer lStringBuffer = new StringBuffer();
			lStringBuffer.append("(?:");
			for (final String lString : pSet)
			{
				lStringBuffer.append(lString + "|");
			}
			lStringBuffer.deleteCharAt(lStringBuffer.length() - 1);
			lStringBuffer.append(")");
			return lStringBuffer.toString();
		}
		return "";
	}

	public String getRegex(final String pName)
	{
		return mRegexNameToRegexMap.get(pName);
	}

	public Pattern getPattern(final String pName)
	{
		return Pattern.compile(mRegexNameToRegexMap.get(pName));
	}
	
	public Pattern getWordPattern(final String pName)
	{
		return Pattern.compile("(?:\\W|^)"+mRegexNameToRegexMap.get(pName)+"(?:\\W|$)");
	}
	
	private String wrapAroundGroup( String pRegex)
	{
		/*if(!(pRegex.startsWith("(?:") && pRegex.endsWith("")))
		{/**/
			return "(?:" + pRegex + ")";
		/*}
		else
			return pRegex;/**/
	}

	public Iterator<Pair<String>> iterator()
	{
		return new Iterator<Pair<String>>(){

			Iterator<Map.Entry<String, String>> mIterator = mRegexNameToRegexMap.entrySet().iterator();
				
			public boolean hasNext()
			{
				return mIterator.hasNext();
			}

			public Pair<String> next()
			{
				Map.Entry<String, String> lEntry = mIterator.next();
				return new Pair<String>(lEntry.getKey(),lEntry.getValue());
			}

			public void remove()
			{
				throw new UnsupportedOperationException("cannot remove from this iterator");				
			}};
	}

}
