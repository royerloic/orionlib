package utils.nlp.wordlists;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * @author loic (<href>royer@biotec.tu-dresden.de</href>) Nov 21, 2005
 * 
 */
public class CountWordIdentifier extends GenericWordIdentifier
{

	@SuppressWarnings("unused")
	//private static final Logger	cLogger	= Logger.getLogger(CountWordIdentifier.class);

	private final boolean				mCaseSensitive;

	public CountWordIdentifier(final Class pClass, final String[] pResourceArray, final boolean pCaseSensitive)
	{
		super();
		mCaseSensitive = pCaseSensitive;
		for (final String lRessourceName : pResourceArray)
			try
			{
				this.compileIdentificationRulesFromRessource(pClass, lRessourceName);
			}
			catch (final IOException e)
			{
				e.printStackTrace();
				//cLogger.info(e);
			}
	}

	/**
	 * @see de.tud.biotec.protein.interaction.attic.identifiers.EntityIdentifier#normalizeString(java.lang.String)
	 */
	@Override
	public String normalizeString(String pString)
	{
		pString = pString.trim();
		if (!mCaseSensitive)
			pString = pString.toLowerCase();
		return pString;
	}

}
