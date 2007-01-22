package org.royerloic.nlp.annotation.filtering;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.royerloic.io.MatrixFile;
import org.royerloic.nlp.annotation.Annotation;
import org.royerloic.string.StringUtils;
import org.royerloic.structures.HashSetMap;
import org.royerloic.structures.Matrix;
import org.royerloic.structures.SetMap;
import org.royerloic.utils.RessourceLocator;

public class AnnotationContextFilter
{

	private static class FilterRule
	{
		public FilterRule(String pPreRegex, String pMatchRegex, String pPostRegex)
		{
			mPreRegex = pPreRegex;
			mMatchRegex = pMatchRegex;
			mPostRegex = pPostRegex;
		}
		public String	mPreRegex;
		public String	mMatchRegex;
		public String	mPostRegex;
	}

	private SetMap<String, String>	mSetNameToNameSetMap		= new HashSetMap<String, String>();
	{
		mSetNameToNameSetMap.put("S", Collections.singleton("\\s*"));
		mSetNameToNameSetMap.put("W", Collections.singleton("\\W*"));
		mSetNameToNameSetMap.put("P", Collections.singleton("\\p{Punct}*"));
		mSetNameToNameSetMap.put("WORD", Collections.singleton("(?:[a-zA-z][a-z]+)"));
		mSetNameToNameSetMap.put("ASE", Collections.singleton("(?:[A-Za-z][a-z]*[a-df-z]ase)"));
	}

	private List<FilterRule>				mPositiveFilterRuleList	= new ArrayList<FilterRule>();
	private List<FilterRule>				mNegativeFilterRuleList	= new ArrayList<FilterRule>();
	private FilterRule							mResponsibleFilterRule;

	private AnnotationContextFilter() throws IOException
	{
	}

	public AnnotationContextFilter(final String pRessource) throws IOException
	{
		readRules(pRessource);
	}

	public AnnotationContextFilter(final InputStream pInputStream) throws IOException
	{
		readRules(pInputStream);
	}

	public void readRules(final String pRessource) throws FileNotFoundException, IOException
	{
		readRules(MatrixFile.getInputStreamFromRessource(new AnnotationContextFilter().getClass(), pRessource));
	}

	public void readRules(final InputStream pInputStream) throws FileNotFoundException, IOException
	{
		List<List<String>> lMatrix = MatrixFile.readMatrixFromStream(pInputStream, false, "\\s+\\|\\s+");

		boolean lIsPositive = false;
		boolean lIsNegative = false;
		boolean lIsSet = false;
		String lSetName = null;

		for (List<String> lList : lMatrix)
		{
			String lFirstString = lList.get(0).trim();
			if (lFirstString.equals(""))
			{
				continue;
			}
			else if (lFirstString.startsWith("importset:"))
			{
				String[] lStringArray = StringUtils.split(lFirstString, ":|\\s+as\\s+", 0);
				String lImportSetFileName = lStringArray[1].trim().toLowerCase();
				String lImportedSetName = lStringArray[2].trim().toLowerCase();
				importRegexFile(lImportedSetName, lImportSetFileName);
				continue;
			}
			else if (lFirstString.startsWith("importrules:"))
			{
				String[] lStringArray = StringUtils.split(lFirstString, ":", 0);
				String lImportRulesFileName = lStringArray[1].trim().toLowerCase();
				readRules(lImportRulesFileName);
				continue;
			}
			else if (lFirstString.startsWith("set:"))
			{
				lIsSet = true;
				lIsPositive = false;
				lIsNegative = false;
				String[] lStringArray = StringUtils.split(lFirstString, ":", 0);
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
			else
			{

				if (lIsPositive || lIsNegative)
				{
					String lPreFixRegex = (lList.get(0).startsWith(".*") ? "" : ".*@W@") + lList.get(0);
					String lMatchRegex = lList.get(1);
					String lPostFixRegex = lList.get(2) + (lList.get(2).endsWith(".*") ? "" : "@W@.*");

					for (Map.Entry<String, Set<String>> lEntry : mSetNameToNameSetMap.entrySet())
					{
						String lSetNameUse = "@" + lEntry.getKey() + "@";
						String lSetReplacement = mergeRegexSetToRegex(lEntry.getValue());
						lPreFixRegex = lPreFixRegex.replace(lSetNameUse, lSetReplacement);
						lMatchRegex = lMatchRegex.replace(lSetNameUse, lSetReplacement);
						lPostFixRegex = lPostFixRegex.replace(lSetNameUse, lSetReplacement);
					}

					FilterRule lFilterRule = new FilterRule(lPreFixRegex, lMatchRegex, lPostFixRegex);
					if (lIsPositive)
						mPositiveFilterRuleList.add(lFilterRule);
					else if (lIsNegative)
						mNegativeFilterRuleList.add(lFilterRule);
				}
				else if (lIsSet)
				{
					mSetNameToNameSetMap.put(lSetName, lFirstString);
				}
			}

		}
	}

	private void importRegexFile(final String pImportedSetName, final String pImportRegexFileName)
			throws FileNotFoundException, IOException
	{
		File lFile = RessourceLocator.getFileFromName(pImportRegexFileName);
		Matrix<String> lMatrix = MatrixFile.readMatrixFromFile(lFile, false);
		for (List<String> lList : lMatrix)
		{
			String lEntry = lList.get(0).trim();
			mSetNameToNameSetMap.put(pImportedSetName, lEntry);
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

	public void filter(final Set<Annotation> pAnnotationSetForAbstract, final int pSentenceOffset)
	{
		List<Annotation> lAnnotationList = new ArrayList<Annotation>(pAnnotationSetForAbstract);
		for (Annotation lAnnotation : lAnnotationList)
		{
			final String lText = lAnnotation.mText;

			String lMatch = lAnnotation.mAnnotatedFragment;

			int lEnd = lText.length();
			int lStart = lAnnotation.mStart + lAnnotation.getLength() - pSentenceOffset;
			lStart = Math.min(lStart, lEnd);
			String lPostFix = lText.substring(lStart, lEnd);

			lStart = 0;
			lEnd = lAnnotation.mStart - 1 - pSentenceOffset;
			lEnd = Math.max(lStart, lEnd);

			String lPreFix = lText.substring(lStart, lEnd);

			final boolean lFiltered = filter(lPreFix, lMatch, lPostFix);

			if (lFiltered)
			{
				System.out.println("REMOVING: " + lAnnotation + "\n because of: " + mResponsibleFilterRule);
				pAnnotationSetForAbstract.remove(lAnnotation);
			}

		}
	}

	public final boolean filter(final CharSequence pPreFix,
															final CharSequence pMatch,
															final CharSequence pPostFix)
	{

		boolean isPositiveMatched = false;
		for (FilterRule lFilterRule : mPositiveFilterRuleList)
			if (StringUtils.matches(pPostFix, lFilterRule.mPostRegex))
				if (StringUtils.matches(pPreFix, lFilterRule.mPreRegex))
					if (StringUtils.matches(pMatch, lFilterRule.mMatchRegex))
					{
						isPositiveMatched = true;
						break;
					}

		boolean isNegativeMatched = false;
		if (!isPositiveMatched)
			for (FilterRule lFilterRule : mNegativeFilterRuleList)
				if (StringUtils.matches(pPostFix, lFilterRule.mPostRegex))
					if (StringUtils.matches(pPreFix, lFilterRule.mPreRegex))
						if (StringUtils.matches(pMatch, lFilterRule.mMatchRegex))
						{
							mResponsibleFilterRule = lFilterRule;
							isNegativeMatched = true;
							break;
						}

		return isNegativeMatched && !isPositiveMatched;
	}

}
