package org.royerloic.nlp.wordlists;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * @author loic (<href>royer@biotec.tu-dresden.de</href>) Nov 21, 2005
 * 
 */
public class CountWordIdentifier extends GenericWordIdentifier
{

	@SuppressWarnings("unused")
	private static final Logger	cLogger	= Logger.getLogger(CountWordIdentifier.class);

	private final boolean				mCaseSensitive;

	public CountWordIdentifier(Class pClass, String[] pResourceArray, boolean pCaseSensitive)
	{
		super();
		mCaseSensitive = pCaseSensitive;
		for (String lRessourceName : pResourceArray)
		{
			try
			{
				this.compileIdentificationRulesFromRessource(pClass, lRessourceName);
			}
			catch (IOException e)
			{
				cLogger.info(e);
			}
		}
	}

	/**
	 * @see de.tud.biotec.protein.interaction.attic.identifiers.EntityIdentifier#normalizeString(java.lang.String)
	 */
	public String normalizeString(String pString)
	{
		pString = pString.trim();
		if (!mCaseSensitive)
		{
			pString = pString.toLowerCase();
		}
		return pString;
	}

}
