package utils.nlp.wordlists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author loic (<href>royer@biotec.tu-dresden.de</href>) Nov 21, 2005
 * 
 */
public class EnglishWordsIdentifier extends GenericWordIdentifier
{

	@SuppressWarnings("unused")
	private static EnglishWordsIdentifier cUniqueInstanceEnglishWordsIdentifier;

	/**
	 * @return An unique instance of EnglishWordsRecognizer.
	 */
	public static EnglishWordsIdentifier getUniqueInstance()
	{
		if (cUniqueInstanceEnglishWordsIdentifier == null)
		{
			cUniqueInstanceEnglishWordsIdentifier = new EnglishWordsIdentifier();
			final InputStream lStream = EnglishWordsIdentifier.class.getClassLoader()
																															.getResourceAsStream("utils/nlp/wordlists/lists/EnglishWords.word.txt");
			final InputStreamReader lInputStreamReader = new InputStreamReader(lStream);
			final BufferedReader lBufferedReader = new BufferedReader(lInputStreamReader);
			try
			{
				cUniqueInstanceEnglishWordsIdentifier.compileIdentificationRulesFromReader(lBufferedReader);
			}
			catch (final IOException exception)
			{
				exception.printStackTrace();
			}
		}
		return cUniqueInstanceEnglishWordsIdentifier;
	}

	/**
	 * @see de.tud.biotec.protein.interaction.attic.identifiers.EntityIdentifier#normalizeString(java.lang.String)
	 */
	@Override
	public String normalizeString(String pString)
	{
		return pString.trim();
	}

	@Override
	public boolean isEntity(final String pToken)
	{
		if (pToken.length() == 0)
			return false;
		final String lAfterFirstCaracterString = pToken.substring(1);
		final boolean lCorrectCapitalization = lAfterFirstCaracterString.toLowerCase()
																																		.equals(lAfterFirstCaracterString);
		return lCorrectCapitalization && super.isEntity(pToken.toLowerCase());
	}
}
