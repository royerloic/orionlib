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

	private static Map<String, Pattern>	lStringToPatternMap	= new HashMap<String, Pattern>();

	public static final String[] captures(String pString, String pRegex)
	{
		Pattern lPattern = lStringToPatternMap.get(pRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pRegex);
			lStringToPatternMap.put(pRegex, lPattern);
		}
		Matcher lMatcher = lPattern.matcher(pString);
		if (lMatcher.matches())
		{
			String[] lGroupArray = new String[lMatcher.groupCount()];
			for (int i = 1; i <= lMatcher.groupCount(); i++)
				lGroupArray[i - 1] = lMatcher.group(i);

			return lGroupArray;
		}
		else
		{
			return new String[]
			{};
		}
	}

	public static final String[] split(String pString, String pRegex, int pLimit)
	{
		Pattern lPattern = lStringToPatternMap.get(pRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pRegex);
			lStringToPatternMap.put(pRegex, lPattern);
		}
		return lPattern.split(pString, pLimit);
	}

	public static final List<String> findAllmatches(String pString, String pRegex)
	{
		Pattern lPattern = lStringToPatternMap.get(pRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pRegex);
			lStringToPatternMap.put(pRegex, lPattern);
		}
		Matcher lMatcher = lPattern.matcher(pString);

		List<String> lMatchesList = new ArrayList<String>();

		while (lMatcher.find())
		{
			lMatchesList.add(lMatcher.group());
		}

		return lMatchesList;
	}

	public static final List<String> findAllmatches(String pString, String pRegex, int pGroup)
	{
		Pattern lPattern = lStringToPatternMap.get(pRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pRegex);
			lStringToPatternMap.put(pRegex, lPattern);
		}
		Matcher lMatcher = lPattern.matcher(pString);

		List<String> lMatchesList = new ArrayList<String>();

		while (lMatcher.find())
		{
			lMatchesList.add(lMatcher.group(pGroup));
		}

		return lMatchesList;
	}

	public static final int countMatches(String pString, String pRegex)
	{
		Pattern lPattern = lStringToPatternMap.get(pRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pRegex);
			lStringToPatternMap.put(pRegex, lPattern);
		}
		Matcher lMatcher = lPattern.matcher(pString);

		int lCount = 0;
		while (lMatcher.find())
			lCount++;

		return lCount;
	}

	public static final boolean matches(CharSequence pCharSequence, String pRegex)
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

	public static final boolean submatches(String pString, String pRegex)
	{
		return matches(pString, ".*" + pRegex + ".*");
	}

	public static final String replaceAll(String pString, String pRegex, String pReplacement)
	{
		Pattern lPattern = lStringToPatternMap.get(pRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pRegex);
			lStringToPatternMap.put(pRegex, lPattern);
		}
		StringBuffer myStringBuffer = new StringBuffer();
		Matcher lMatcher = lPattern.matcher(pString);
		while (lMatcher.find())
		{
			lMatcher.appendReplacement(myStringBuffer, pReplacement);
		}
		lMatcher.appendTail(myStringBuffer);

		return myStringBuffer.toString();
	}

	public static final String replaceAllInContextWithExceptions(	String pString,
																																String pRegex,
																																Set<String> pLeftExclusionSet,
																																Set<String> pRightExclusionSet,
																																String pReplacement)
	{
		Pattern lPattern = lStringToPatternMap.get(pRegex);
		if (lPattern == null)
		{
			lPattern = Pattern.compile(pRegex);
			lStringToPatternMap.put(pRegex, lPattern);
		}
		StringBuffer myStringBuffer = new StringBuffer();
		Matcher lMatcher = lPattern.matcher(pString);
		if (lMatcher.groupCount() != 2)
		{
			throw new RuntimeException("There must be at least two groups in the pattern");
		}
		while (lMatcher.find())
		{
			String lLeftContext = lMatcher.group(1);
			String lRightContext = lMatcher.group(2);
			if (pLeftExclusionSet.contains(lLeftContext) || pRightExclusionSet.contains(lRightContext))
			{ // we don't replace, one of the contexts contains a forbidden word.
				lMatcher.appendReplacement(myStringBuffer, lMatcher.group());
			}
			else
			{
				lMatcher.appendReplacement(myStringBuffer, lLeftContext + pReplacement + lRightContext);
			}
		}
		lMatcher.appendTail(myStringBuffer);

		return myStringBuffer.toString();
	}

	public static void cleanWhiteSpacesForAll(Collection<String> pStringCollection)
	{
		List<String> lStringList = new ArrayList<String>(pStringCollection);
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

	public static String merge(List<String> pStringList, String pSeparatorString)
	{
		StringBuffer lStringBuffer = new StringBuffer();
		for (int i = 0; i < pStringList.size(); i++)
		{
			lStringBuffer.append(pStringList.get(i));
			if (i != pStringList.size() - 1)
				lStringBuffer.append(pSeparatorString);
		}
		return lStringBuffer.toString();
	}

	public static String merge(List<String> pStringList, String pBefore, String pAfter)
	{
		StringBuffer lStringBuffer = new StringBuffer();
		for (int i = 0; i < pStringList.size(); i++)
		{
			lStringBuffer.append(pBefore);
			lStringBuffer.append(pStringList.get(i));
			lStringBuffer.append(pAfter);
		}
		return lStringBuffer.toString();
	}

	public static String merge(String[] pStringArray, String pSeparatorString)
	{
		StringBuffer lStringBuffer = new StringBuffer();
		for (int i = 0; i < pStringArray.length; i++)
		{
			lStringBuffer.append(pStringArray[i]);
			if (i != pStringArray.length - 1)
				lStringBuffer.append(pSeparatorString);
		}
		return lStringBuffer.toString();
	}

	public static String maskDelete(String pText, String pMask, String pTransparentCaracter)
	{
		StringBuffer lStringBuffer = new StringBuffer();
		for (int i = 0; i < pText.length(); i++)
		{
			if (pMask.equals(pTransparentCaracter))
			{
				lStringBuffer.append(pText.charAt(i));
			}
		}

		return lStringBuffer.toString();
	}

	public static String maskReplace(	String pText,
																		String pMask,
																		String pTransparentCaracters,
																		Character pReplaceCaracter)
	{
		StringBuffer lStringBuffer = new StringBuffer();
		for (int i = 0; i < Math.min(pText.length(), pMask.length()); i++)
		{
			String lCharAt = new String() + pMask.charAt(i);
			if (pTransparentCaracters.contains(lCharAt))
			{
				lStringBuffer.append(pText.charAt(i));
			}
			else
			{
				lStringBuffer.append(pReplaceCaracter);
			}
		}

		return lStringBuffer.toString();
	}

	public static String[] superSplit(String pText, String pSplitRegex)
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
		ArrayList<String> lMatchList = new ArrayList<String>();
		Matcher lMatcher = lPattern.matcher(pText);

		// Add segments before each match found
		while (lMatcher.find())
		{
			String lTokenMatch = pText.substring(lIndex, lMatcher.start());
			lMatchList.add(lTokenMatch);
			String lInterToken = lMatcher.group();
			lMatchList.add(lInterToken);
			lIndex = lMatcher.end();
		}

		// If no match was found, return this
		if (lIndex == 0)
		{
			lTokenArray = new String[]
			{ pText };
		}
		else
		{
			// Add remaining segment
			lMatchList.add(pText.substring(lIndex, pText.length()));

			// Construct result
			int resultSize = lMatchList.size();
			while (resultSize > 0 && lMatchList.get(resultSize - 1).equals(""))
				resultSize--;
			lTokenArray = new String[resultSize];
			lTokenArray = lMatchList.subList(0, resultSize).toArray(lTokenArray);
		}

		return lTokenArray;

	}

	public static int findTokenIndexFromCharacterIndex(	String[] pSuperSplittedString,
																											String pSplitRegex,
																											int pCharacterIndex)
	{
		String[] lTokenArray = pSuperSplittedString; // superSplit(pText,
		// pSplitRegex);

		int lTokenIndex = 0;
		int lCharacterIndex = 0;

		for (String lToken : lTokenArray)
		{
			if (!StringUtils.matches(lToken, pSplitRegex))
			{
				boolean lInside = (lCharacterIndex <= pCharacterIndex)
						&& (pCharacterIndex <= lCharacterIndex + lToken.length());
				if (lInside)
					break;
				lTokenIndex++;
			}
			lCharacterIndex += lToken.length();
		}

		return lTokenIndex;
	}

	public static Integer findCharacterIndexFromTokenIndex(	String[] pSuperSplittedString,
																													String pSplitRegex,
																													int pTokenIndex)
	{
		String[] lTokenArray = pSuperSplittedString; // superSplit(pText,
		// pSplitRegex);

		int lTokenIndex = 0;
		int lCharacterIndex = 0;

		for (String lToken : lTokenArray)
		{
			if (!StringUtils.matches(lToken, pSplitRegex))
			{
				boolean lRightToken = (lTokenIndex == pTokenIndex);
				if (lRightToken)
					break;
				lTokenIndex++;
			}
			lCharacterIndex += lToken.length();
		}

		return lCharacterIndex;
	}

	public static int countUpperCase(String pSynonym)
	{
		int lCount = 0;
		for (Character lChar : pSynonym.toCharArray())
		{
			lCount += Character.isUpperCase(lChar) ? 1 : 0;
		}

		return lCount;
	}

	public static int countLowerCase(String pSynonym)
	{
		int lCount = 0;
		for (Character lChar : pSynonym.toCharArray())
		{
			lCount += Character.isLowerCase(lChar) ? 1 : 0;
		}

		return lCount;
	}

	public static int countDigits(String pSynonym)
	{
		int lCount = 0;
		for (Character lChar : pSynonym.toCharArray())
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

	public static String readStreamToString(InputStream pInputStream) throws IOException
	{
		byte[] b = new byte[pInputStream.available()];
		pInputStream.read(b);
		pInputStream.close();
		String result = new String(b);
		return result;
	}

	public static String erase(String pString, String pRegex, char pErasureCharacter)
	{
		String lString = pString;
		List<String> lStringList = StringUtils.findAllmatches(lString, pRegex);

		for (String lMatchedString : lStringList)
		{
			char[] lCharArray = new char[lMatchedString.length()];
			for (int i = 0; i < lCharArray.length; i++)
				lCharArray[i] = pErasureCharacter;
			lString = lString.replace(lMatchedString, new String(lCharArray));
		}

		return lString;
	}
}
