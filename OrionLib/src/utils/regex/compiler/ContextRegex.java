package utils.regex.compiler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import utils.io.MatrixFile;
import utils.string.StringUtils;
import utils.structures.Pair;
import utils.structures.map.HashSetMap;
import utils.structures.map.SetMap;
import utils.structures.matrix.Matrix;

public class ContextRegex
{

	private static class FilterRule
	{
		public FilterRule(final String pPreRegex,
											final String pMatchRegex,
											final String pPostRegex)
		{
			mPreRegex = Pattern.compile(pPreRegex, Pattern.DOTALL);
			mMatchRegex = Pattern.compile(pMatchRegex, Pattern.DOTALL);
			mPostRegex = Pattern.compile(pPostRegex, Pattern.DOTALL);
		}

		public Pattern mPreRegex;
		public Pattern mMatchRegex;
		public Pattern mPostRegex;

		@Override
		public String toString()
		{
			return mPreRegex + " | " + mMatchRegex + " | " + mPostRegex;
		}

	}

	private final SetMap<String, String> mSetNameToNameSetMap = new HashSetMap<String, String>();
	{
		mSetNameToNameSetMap.put("S", Collections.singleton("\\s+"));
		mSetNameToNameSetMap.put("W", Collections.singleton("\\W+"));
		mSetNameToNameSetMap.put("P", Collections.singleton("\\p{Punct}+"));
		mSetNameToNameSetMap.put(	"WORD",
															Collections.singleton("(?:[a-zA-z][a-z]+)"));
		mSetNameToNameSetMap.put(	"ASE",
															Collections.singleton("(?:[A-Za-z][a-z]*[a-df-z]ase)"));
	}

	private final List<FilterRule> mPositiveFilterRuleList = new ArrayList<FilterRule>();
	private final List<FilterRule> mNegativeFilterRuleList = new ArrayList<FilterRule>();

	public ContextRegex(final String pRessource) throws IOException
	{
		readRules(pRessource);
	}

	public ContextRegex(final InputStream pInputStream) throws IOException
	{
		readRules(pInputStream);
	}

	public void readRules(final String pRessource) throws FileNotFoundException,
																								IOException
	{
		readRules(ContextRegex.class.getClassLoader()
																.getResourceAsStream(pRessource));
	}

	public void readRules(final InputStream pInputStream)	throws FileNotFoundException,
																												IOException
	{
		final List<List<String>> lMatrix = MatrixFile.readMatrixFromStream(	pInputStream,
																																				false,
																																				"\\s+\\|\\s+");

		boolean lIsPositive = false;
		boolean lIsNegative = false;
		boolean lIsSet = false;
		String lSetName = null;

		for (final List<String> lList : lMatrix)
		{
			final String lFirstString = lList.get(0).trim();
			if (lFirstString.length() == 0 || lFirstString.startsWith("//"))
			{
				continue;
			}
			else if (lFirstString.startsWith("importset:"))
			{
				final String[] lStringArray = StringUtils.split(lFirstString,
																												":|\\s+as\\s+",
																												0);
				final String lImportSetFileName = lStringArray[1].trim().toLowerCase();
				final String lImportedSetName = lStringArray[2].trim().toLowerCase();
				importRegexFile(lImportedSetName, lImportSetFileName);
				continue;
			}
			else if (lFirstString.startsWith("importrules:"))
			{
				final String[] lStringArray = StringUtils.split(lFirstString, ":", 0);
				final String lImportRulesFileName = lStringArray[1]	.trim()
																														.toLowerCase();
				readRules(lImportRulesFileName);
				continue;
			}
			else if (lFirstString.startsWith("importregex:"))
			{
				final String[] lStringArray = StringUtils.split(lFirstString, ":", 0);
				final String lImportRegexFileName = lStringArray[1]	.trim()
																														.toLowerCase();
				final RegexCompiler lRegexCompiler = new RegexCompiler(lImportRegexFileName);
				for (final Pair<String> lPair : lRegexCompiler)
				{
					mSetNameToNameSetMap.put(lPair.mA, lPair.mB);
				}
				continue;
			}
			else if (lFirstString.startsWith("set:"))
			{
				lIsSet = true;
				lIsPositive = false;
				lIsNegative = false;
				final String[] lStringArray = StringUtils.split(lFirstString, ":", 0);
				lSetName = lStringArray[1].trim().toLowerCase();
				mSetNameToNameSetMap.put(lSetName);
				continue;
			}
			else if (lFirstString.equalsIgnoreCase("positive:"))
			{
				lIsSet = false;
				lIsPositive = true;
				lIsNegative = false;
				continue;
			}
			else if (lFirstString.equalsIgnoreCase("negative:"))
			{
				lIsSet = false;
				lIsPositive = false;
				lIsNegative = true;
				continue;
			}
			else if (lIsPositive || lIsNegative)
			{
				final String lPreFixRegexRaw = lList.get(0).trim();
				final String lMatchRegexRaw = lList.get(1).trim();
				final String lPostFixRegexRaw = lList.get(2).trim();

				String lPreFixRegex = (lPreFixRegexRaw.startsWith(".*") ? "" : ".*") + lPreFixRegexRaw;
				String lMatchRegex = lMatchRegexRaw;
				String lPostFixRegex = lPostFixRegexRaw + (lPostFixRegexRaw.endsWith(".*") ? ""
																																									: ".*");

				for (final Map.Entry<String, Set<String>> lEntry : mSetNameToNameSetMap.entrySet())
				{
					final String lSetNameUse = "@" + lEntry.getKey() + "@";
					final String lSetReplacement = mergeRegexSetToRegex(lEntry.getValue());
					lPreFixRegex = lPreFixRegex.replace(lSetNameUse, lSetReplacement);
					lMatchRegex = lMatchRegex.replace(lSetNameUse, lSetReplacement);
					lPostFixRegex = lPostFixRegex.replace(lSetNameUse, lSetReplacement);
				}

				final FilterRule lFilterRule = new FilterRule(lPreFixRegex,
																											lMatchRegex,
																											lPostFixRegex);
				if (lIsPositive)
				{
					mPositiveFilterRuleList.add(lFilterRule);
				}
				else if (lIsNegative)
				{
					mNegativeFilterRuleList.add(lFilterRule);
				}
			}
			else if (lIsSet)
			{
				mSetNameToNameSetMap.put(lSetName, lFirstString);
			}

		}
	}

	private void importRegexFile(	final String pImportedSetName,
																final String pImportRegexFileName) throws FileNotFoundException,
																																	IOException
	{
		final InputStream lInputStream = getInputStreamFromName(pImportRegexFileName);
		final Matrix<String> lMatrix = MatrixFile.readMatrixFromStream(	lInputStream,
																																		false);
		for (final List<String> lList : lMatrix)
		{
			final String lEntry = lList.get(0).trim();
			mSetNameToNameSetMap.put(pImportedSetName, lEntry);
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

	public final boolean match(	final CharSequence pPreFix,
															final CharSequence pMatch,
															final CharSequence pPostFix)
	{

		boolean isPositiveMatched = false;
		for (final FilterRule lFilterRule : mPositiveFilterRuleList)
		{
			if (pPostFix.length() == 0 || lFilterRule.mPostRegex.matcher(pPostFix)
																													.matches())
			{
				if (pPreFix.length() == 0 || lFilterRule.mPreRegex.matcher(pPreFix)
																													.matches())
				{
					if (lFilterRule.mMatchRegex.matcher(pMatch).matches())
					{
						isPositiveMatched = true;
						break;
					}
				}
			}
		}

		boolean isNegativeMatched = false;
		if (isPositiveMatched)
		{
			for (final FilterRule lFilterRule : mNegativeFilterRuleList)
			{
				if (pPostFix.length() == 0 || lFilterRule.mPostRegex.matcher(pPostFix)
																														.matches())
				{
					if (pPreFix.length() == 0 || lFilterRule.mPreRegex.matcher(pPreFix)
																														.matches())
					{
						if (lFilterRule.mMatchRegex.matcher(pMatch).matches())
						{
							isNegativeMatched = true;
							break;
						}
					}
				}
			}
		}

		return isPositiveMatched && !isNegativeMatched;
	}

	static public InputStream getInputStreamFromName(final String pString) throws IOException
	{
		final ClassLoader lClassLoader = ClassLoader.getSystemClassLoader();
		final URL lURL = lClassLoader.getResource(pString);
		if (lURL != null)
		{
			return lURL.openStream();
		}
		else
		{
			return ContextRegex.class.getResourceAsStream(pString);
		}
	}

}
