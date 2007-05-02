package org.royerloic.string;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils
{

	private static final Map<String, Pattern>	lStringToPatternMap	= new HashMap<String, Pattern>();

	public static final String[] captures(final String pString, final String pRegex)
	{
		Pattern lPattern = lStringToPatternMap.get(pRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pRegex);
			lStringToPatternMap.put(pRegex, lPattern);
		}
		final Matcher lMatcher = lPattern.matcher(pString);
		if (lMatcher.matches())
		{
			final String[] lGroupArray = new String[lMatcher.groupCount()];
			for (int i = 1; i <= lMatcher.groupCount(); i++)
				lGroupArray[i - 1] = lMatcher.group(i);

			return lGroupArray;
		}
		else
			return new String[]
			{};
	}

	public static final String[] split(final String pString, final String pRegex, final int pLimit)
	{
		Pattern lPattern = lStringToPatternMap.get(pRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pRegex);
			lStringToPatternMap.put(pRegex, lPattern);
		}
		return lPattern.split(pString, pLimit);
	}

	public static final List<String> findAllmatches(final String pString, final String pRegex)
	{
		Pattern lPattern = lStringToPatternMap.get(pRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pRegex);
			lStringToPatternMap.put(pRegex, lPattern);
		}
		final Matcher lMatcher = lPattern.matcher(pString);

		final List<String> lMatchesList = new ArrayList<String>();

		while (lMatcher.find())
			lMatchesList.add(lMatcher.group());

		return lMatchesList;
	}

	public static final List<String> findAllmatches(final String pString, final String pRegex, final int pGroup)
	{
		Pattern lPattern = lStringToPatternMap.get(pRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pRegex);
			lStringToPatternMap.put(pRegex, lPattern);
		}
		final Matcher lMatcher = lPattern.matcher(pString);

		final List<String> lMatchesList = new ArrayList<String>();

		while (lMatcher.find())
			lMatchesList.add(lMatcher.group(pGroup));

		return lMatchesList;
	}

	public static final int countMatches(final String pString, final String pRegex)
	{
		Pattern lPattern = lStringToPatternMap.get(pRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pRegex);
			lStringToPatternMap.put(pRegex, lPattern);
		}
		final Matcher lMatcher = lPattern.matcher(pString);

		int lCount = 0;
		while (lMatcher.find())
			lCount++;

		return lCount;
	}

	public static final boolean matches(final CharSequence pCharSequence, final String pRegex)
	{
		Pattern lPattern = lStringToPatternMap.get(pRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pRegex);
			lStringToPatternMap.put(pRegex, lPattern);
		}
		final Matcher lMatcher = lPattern.matcher(pCharSequence);
		return lMatcher.matches();
	}

	public static final boolean submatches(final String pString, final String pRegex)
	{
		return matches(pString, ".*" + pRegex + ".*");
	}

	public static final String replaceAll(final String pString, final String pRegex, final String pReplacement)
	{
		Pattern lPattern = lStringToPatternMap.get(pRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pRegex);
			lStringToPatternMap.put(pRegex, lPattern);
		}
		final StringBuffer myStringBuffer = new StringBuffer();
		final Matcher lMatcher = lPattern.matcher(pString);
		while (lMatcher.find())
			lMatcher.appendReplacement(myStringBuffer, pReplacement);
		lMatcher.appendTail(myStringBuffer);

		return myStringBuffer.toString();
	}

	public static final String replaceAllInContextWithExceptions(	final String pString,
																																final String pRegex,
																																final Set<String> pLeftExclusionSet,
																																final Set<String> pRightExclusionSet,
																																final String pReplacement)
	{
		Pattern lPattern = lStringToPatternMap.get(pRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pRegex);
			lStringToPatternMap.put(pRegex, lPattern);
		}
		final StringBuffer myStringBuffer = new StringBuffer();
		final Matcher lMatcher = lPattern.matcher(pString);
		if (lMatcher.groupCount() != 2)
			throw new RuntimeException("There must be at least two groups in the pattern");
		while (lMatcher.find())
		{
			final String lLeftContext = lMatcher.group(1);
			final String lRightContext = lMatcher.group(2);
			if (pLeftExclusionSet.contains(lLeftContext) || pRightExclusionSet.contains(lRightContext))
				lMatcher.appendReplacement(myStringBuffer, lMatcher.group());
			else
				lMatcher.appendReplacement(myStringBuffer, lLeftContext + pReplacement + lRightContext);
		}
		lMatcher.appendTail(myStringBuffer);

		return myStringBuffer.toString();
	}

	public static void cleanWhiteSpacesForAll(final Collection<String> pStringCollection)
	{
		final List<String> lStringList = new ArrayList<String>(pStringCollection);
		pStringCollection.clear();

		for (String lString : lStringList)
		{
			lString = StringUtils.cleanPunctuationAround(lString);
			pStringCollection.add(lString);
		}
	}

	public static final String cleanWhiteSpaces(String pString)
	{
		pString = replaceAll(pString, "\\s+", " ");
		pString = pString.trim();
		return pString;
	}

	public static final String cleanPunctuationAround(String pString)
	{
		pString = replaceAll(pString, "\\W", " ");
		pString = cleanWhiteSpaces(pString);
		return pString;
	}

	public static final String fastCleanWhiteSpaces(String pString)
	{
		pString = pString.replace("    ", " ");
		pString = pString.replace("   ", " ");
		pString = pString.replace("  ", " ");
		pString = pString.trim();
		return pString;
	}

	public static String merge(final List<String> pStringList, final String pSeparatorString)
	{
		final StringBuffer lStringBuffer = new StringBuffer();
		for (int i = 0; i < pStringList.size(); i++)
		{
			lStringBuffer.append(pStringList.get(i));
			if (i != pStringList.size() - 1)
				lStringBuffer.append(pSeparatorString);
		}
		return lStringBuffer.toString();
	}

	public static String merge(final List<String> pStringList, final String pBefore, final String pAfter)
	{
		final StringBuffer lStringBuffer = new StringBuffer();
		for (int i = 0; i < pStringList.size(); i++)
		{
			lStringBuffer.append(pBefore);
			lStringBuffer.append(pStringList.get(i));
			lStringBuffer.append(pAfter);
		}
		return lStringBuffer.toString();
	}

	public static String merge(final String[] pStringArray, final String pSeparatorString)
	{
		final StringBuffer lStringBuffer = new StringBuffer();
		for (int i = 0; i < pStringArray.length; i++)
		{
			lStringBuffer.append(pStringArray[i]);
			if (i != pStringArray.length - 1)
				lStringBuffer.append(pSeparatorString);
		}
		return lStringBuffer.toString();
	}

	public static String maskDelete(final String pText, final String pMask, final String pTransparentCaracter)
	{
		final StringBuffer lStringBuffer = new StringBuffer();
		for (int i = 0; i < pText.length(); i++)
			if (pMask.equals(pTransparentCaracter))
				lStringBuffer.append(pText.charAt(i));

		return lStringBuffer.toString();
	}

	public static String maskReplace(	final String pText,
																		final String pMask,
																		final String pTransparentCaracters,
																		final Character pReplaceCaracter)
	{
		final StringBuffer lStringBuffer = new StringBuffer();
		for (int i = 0; i < Math.min(pText.length(), pMask.length()); i++)
		{
			final String lCharAt = new String() + pMask.charAt(i);
			if (pTransparentCaracters.contains(lCharAt))
				lStringBuffer.append(pText.charAt(i));
			else
				lStringBuffer.append(pReplaceCaracter);
		}

		return lStringBuffer.toString();
	}

	public static String[] superSplit(final String pText, final String pSplitRegex)
	{
		// First we get the pattern:
		Pattern lPattern = lStringToPatternMap.get(pSplitRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pSplitRegex);
			lStringToPatternMap.put(pSplitRegex, lPattern);
		}

		// This is the array where we put the tokens:
		String[] lTokenArray;

		// here starts code stolen from pattern.split
		int lIndex = 0;
		final ArrayList<String> lMatchList = new ArrayList<String>();
		final Matcher lMatcher = lPattern.matcher(pText);

		// Add segments before each match found
		while (lMatcher.find())
		{
			final String lTokenMatch = pText.substring(lIndex, lMatcher.start());
			lMatchList.add(lTokenMatch);
			final String lInterToken = lMatcher.group();
			lMatchList.add(lInterToken);
			lIndex = lMatcher.end();
		}

		// If no match was found, return this
		if (lIndex == 0)
			lTokenArray = new String[]
			{ pText };
		else
		{
			// Add remaining segment
			lMatchList.add(pText.substring(lIndex, pText.length()));

			// Construct result
			int resultSize = lMatchList.size();
			while ((resultSize > 0) && lMatchList.get(resultSize - 1).equals(""))
				resultSize--;
			lTokenArray = new String[resultSize];
			lTokenArray = lMatchList.subList(0, resultSize).toArray(lTokenArray);
		}

		return lTokenArray;

	}

	public static int findTokenIndexFromCharacterIndex(	final String[] pSuperSplittedString,
																											final String pSplitRegex,
																											final int pCharacterIndex)
	{
		final String[] lTokenArray = pSuperSplittedString; // superSplit(pText,
		// pSplitRegex);

		int lTokenIndex = 0;
		int lCharacterIndex = 0;

		for (final String lToken : lTokenArray)
		{
			if (!StringUtils.matches(lToken, pSplitRegex))
			{
				final boolean lInside = (lCharacterIndex <= pCharacterIndex)
						&& (pCharacterIndex <= lCharacterIndex + lToken.length());
				if (lInside)
					break;
				lTokenIndex++;
			}
			lCharacterIndex += lToken.length();
		}

		return lTokenIndex;
	}

	public static Integer findCharacterIndexFromTokenIndex(	final String[] pSuperSplittedString,
																													final String pSplitRegex,
																													final int pTokenIndex)
	{
		final String[] lTokenArray = pSuperSplittedString; // superSplit(pText,
		// pSplitRegex);

		int lTokenIndex = 0;
		int lCharacterIndex = 0;

		for (final String lToken : lTokenArray)
		{
			if (!StringUtils.matches(lToken, pSplitRegex))
			{
				final boolean lRightToken = (lTokenIndex == pTokenIndex);
				if (lRightToken)
					break;
				lTokenIndex++;
			}
			lCharacterIndex += lToken.length();
		}

		return lCharacterIndex;
	}

	public static int countUpperCase(final String pSynonym)
	{
		int lCount = 0;
		for (final Character lChar : pSynonym.toCharArray())
			lCount += Character.isUpperCase(lChar) ? 1 : 0;

		return lCount;
	}

	public static int countLowerCase(final String pSynonym)
	{
		int lCount = 0;
		for (final Character lChar : pSynonym.toCharArray())
			lCount += Character.isLowerCase(lChar) ? 1 : 0;

		return lCount;
	}

	public static int countDigits(final String pSynonym)
	{
		int lCount = 0;
		for (final Character lChar : pSynonym.toCharArray())
		{
			boolean isDigit = false;
			isDigit |= lChar.equals('0');
			isDigit |= lChar.equals('1');
			isDigit |= lChar.equals('2');
			isDigit |= lChar.equals('3');
			isDigit |= lChar.equals('4');
			isDigit |= lChar.equals('5');
			isDigit |= lChar.equals('6');
			isDigit |= lChar.equals('7');
			isDigit |= lChar.equals('8');
			isDigit |= lChar.equals('9');

			lCount += isDigit ? 1 : 0;
		}

		return lCount;
	}

	public static String readStreamToString(final InputStream pInputStream) throws IOException
	{
		final byte[] b = new byte[pInputStream.available()];
		pInputStream.read(b);
		pInputStream.close();
		final String result = new String(b);
		return result;
	}

	public static String erase(final String pString, final String pRegex, final char pErasureCharacter)
	{
		String lString = pString;
		final List<String> lStringList = StringUtils.findAllmatches(lString, pRegex);

		for (final String lMatchedString : lStringList)
		{
			final char[] lCharArray = new char[lMatchedString.length()];
			for (int i = 0; i < lCharArray.length; i++)
				lCharArray[i] = pErasureCharacter;
			lString = lString.replace(lMatchedString, new String(lCharArray));
		}

		return lString;
	}
}
