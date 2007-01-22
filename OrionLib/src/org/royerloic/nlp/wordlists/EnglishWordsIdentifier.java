package org.royerloic.nlp.wordlists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * @author loic (<href>royer@biotec.tu-dresden.de</href>) Nov 21, 2005
 * 
 */
public class EnglishWordsIdentifier extends GenericWordIdentifier
{

	@SuppressWarnings("unused")
	private static final Logger						cLogger	= Logger.getLogger(EnglishWordsIdentifier.class);

	private static EnglishWordsIdentifier	cUniqueInstanceEnglishWordsIdentifier;

	/**
	 * @return An unique instance of EnglishWordsRecognizer.
	 */
	public static EnglishWordsIdentifier getUniqueInstance()
	{
		if (cUniqueInstanceEnglishWordsIdentifier == null)
		{
			cUniqueInstanceEnglishWordsIdentifier = new EnglishWordsIdentifier();
			InputStream lStream = EnglishWordsIdentifier.class.getClassLoader().getResourceAsStream(
					"org/royerloic/nlp/wordlists/lists/EnglishWords.word.txt");
			InputStreamReader lInputStreamReader = new InputStreamReader(lStream);
			BufferedReader lBufferedReader = new BufferedReader(lInputStreamReader);
			try
			{
				cUniqueInstanceEnglishWordsIdentifier.compileIdentificationRulesFromReader(lBufferedReader);
			}
			catch (IOException exception)
			{
				cLogger.error(exception);
			}
		}
		return cUniqueInstanceEnglishWordsIdentifier;
	}

	/**
	 * @see de.tud.biotec.protein.interaction.attic.identifiers.EntityIdentifier#normalizeString(java.lang.String)
	 */
	public String normalizeString(String pString)
	{
		pString = pString.trim();
		return pString;
	}

	public boolean isEntity(String pToken)
	{
		if (pToken.length() == 0)
			return false;
		String lAfterFirstCaracterString = pToken.substring(1);
		boolean lCorrectCapitalization = lAfterFirstCaracterString.toLowerCase()
				.equals(lAfterFirstCaracterString);
		return lCorrectCapitalization && super.isEntity(pToken.toLowerCase());
	}
}
