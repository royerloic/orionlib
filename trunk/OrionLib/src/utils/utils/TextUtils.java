package utils.utils;

public class TextUtils
{

	public static String filterMarkupTags(final String pString)
	{
		return pString.replaceAll("<[\\p{Alpha}]*[^>]*>", " ");
	}

	public static String getFirstSentence(final String pString,
																				final int pMinimalSentenceSize,
																				final int pMaxSentenceSize)
	{
		String lFirstSentence = pString;
		final int lFirstPoint = pString.indexOf('.');
		if (lFirstPoint > pMinimalSentenceSize)
		{
			lFirstSentence = pString.substring(0, lFirstPoint + 1);
		}
		else
		{
			final int lFirstSemiColon = pString.indexOf(';');
			if (lFirstSemiColon > pMinimalSentenceSize)
			{
				lFirstSentence = pString.substring(0, lFirstSemiColon + 1);
			}
			else
			{
				final int lFirstComa = pString.indexOf(',');
				if (lFirstComa > pMinimalSentenceSize)
				{
					lFirstSentence = pString.substring(0, lFirstComa + 1);
				}
				else
				{
					final int lFirstNewLine = pString.indexOf('\n');
					if (lFirstNewLine > pMinimalSentenceSize)
					{
						lFirstSentence = pString.substring(0, lFirstNewLine + 1);
					}
				}
			}
		}

		if (lFirstSentence.length() > pMaxSentenceSize)
		{
			lFirstSentence = lFirstSentence.substring(0, pMaxSentenceSize);
		}

		if (lFirstSentence.length() < pString.length())
		{
			lFirstSentence += "(...)";
		}

		return lFirstSentence;
	}

	// lower quality extraction of sentences...
	public static String getFirstSentences(	final String pString,
																					final int pNumberOfSentences)
	{
		String lFirstSentences = "";

		final String[] lStringArray = pString.split("[.;]");

		int lSentenceCounter = 0;
		for (final String lSentence : lStringArray)
		{
			lSentenceCounter++;
			lFirstSentences += lSentence + ".";
			if (lSentenceCounter >= pNumberOfSentences)
			{
				break;
			}
		}

		if (lFirstSentences.length() < pString.length())
		{
			lFirstSentences += "(...)";
		}

		return lFirstSentences;
	}
}
