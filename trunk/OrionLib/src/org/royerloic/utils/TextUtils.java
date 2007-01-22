package org.royerloic.utils;

public class TextUtils
{

	public static String filterMarkupTags(String pString)
	{
		return pString.replaceAll("<[\\p{Alpha}]*[^>]*>", " ");
	}

	public static String getFirstSentence(String pString, int pMinimalSentenceSize, int pMaxSentenceSize)
	{
		String lFirstSentence = pString;
		int lFirstPoint = pString.indexOf('.');
		if (lFirstPoint > pMinimalSentenceSize)
		{
			lFirstSentence = pString.substring(0, lFirstPoint + 1);
		}
		else
		{
			int lFirstSemiColon = pString.indexOf(';');
			if (lFirstSemiColon > pMinimalSentenceSize)
			{
				lFirstSentence = pString.substring(0, lFirstSemiColon + 1);
			}
			else
			{
				int lFirstComa = pString.indexOf(',');
				if (lFirstComa > pMinimalSentenceSize)
				{
					lFirstSentence = pString.substring(0, lFirstComa + 1);
				}
				else
				{
					int lFirstNewLine = pString.indexOf('\n');
					if (lFirstNewLine > pMinimalSentenceSize)
					{
						lFirstSentence = pString.substring(0, lFirstNewLine + 1);
					}
				}
			}
		}

		if (lFirstSentence.length() > pMaxSentenceSize)
			lFirstSentence = lFirstSentence.substring(0, pMaxSentenceSize);

		if (lFirstSentence.length() < pString.length())
			lFirstSentence += "(...)";

		return lFirstSentence;
	}

	// lower quality extraction of sentences...
	public static String getFirstSentences(String pString, int pNumberOfSentences)
	{
		String lFirstSentences = "";

		String[] lStringArray = pString.split("[.;]");

		int lSentenceCounter = 0;
		for (String lSentence : lStringArray)
		{
			lSentenceCounter++;
			lFirstSentences += lSentence + ".";
			if (lSentenceCounter >= pNumberOfSentences)
				break;
		}

		if (lFirstSentences.length() < pString.length())
			lFirstSentences += "(...)";

		return lFirstSentences;
	}
}
