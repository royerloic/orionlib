package utils.utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplaceEntities
{
	private static final Pattern entity_pattern = Pattern.compile("&([a-zA-Z]+);");

	public static String doReplaceEntities(final String content)
	{

		final StringBuffer buff = new StringBuffer(content.length() + 1000);

		final Matcher m = entity_pattern.matcher(content);

		int begin = 0;

		boolean hasFilterDecodedEntities = false;
		boolean hasFilterFoundUnknownEntity = false;

		while (m.find())
		{

			buff.append(content.substring(begin, m.start()));

			final String entity = m.group(1);

			final String value = (String) sEntitiesMap.get(entity);

			if (value != null)
			{
				buff.append(value);

				hasFilterDecodedEntities = true;
			}
			else
			{

				buff.append(" ");

				hasFilterFoundUnknownEntity = true;
			}

			begin = m.end(0);

		}
		buff.append(content.substring(begin, content.length()));

		if (hasFilterFoundUnknownEntity)
		{
			System.out.println("Filter encountered unknown entities");
		}

		if (hasFilterDecodedEntities)
		{
			System.out.println("Filter has replaced entities.");
		}

		return buff.toString();

	}

	/**
	 * These are probably HTML 3.2 level... as it looks like some HTML 4 entities
	 * are not present.
	 */
	private static final String[][] ENTITIES =
	{
	/*
	 * We probably don't want to filter regular ASCII chars so we leave them out
	 */
	{ "&", "amp" },
	{ "<", "lt" },
	{ ">", "gt" },
	{ "\"", "quot" },

	{ "\u0083", "#131" },
	{ "\u0084", "#132" },
	{ "\u0085", "#133" },
	{ "\u0086", "#134" },
	{ "\u0087", "#135" },
	{ "\u0089", "#137" },
	{ "\u008A", "#138" },
	{ "\u008B", "#139" },
	{ "\u008C", "#140" },
	{ "\u0091", "#145" },
	{ "\u0092", "#146" },
	{ "\u0093", "#147" },
	{ "\u0094", "#148" },
	{ "\u0095", "#149" },
	{ "\u0096", "#150" },
	{ "\u0097", "#151" },
	{ "\u0099", "#153" },
	{ "\u009A", "#154" },
	{ "\u009B", "#155" },
	{ "\u009C", "#156" },
	{ "\u009F", "#159" },

	{ "\u00A0", "nbsp" },
	{ "\u00A1", "iexcl" },
	{ "\u00A2", "cent" },
	{ "\u00A3", "pound" },
	{ "\u00A4", "curren" },
	{ "\u00A5", "yen" },
	{ "\u00A6", "brvbar" },
	{ "\u00A7", "sect" },
	{ "\u00A8", "uml" },
	{ "\u00A9", "copy" },
	{ "\u00AA", "ordf" },
	{ "\u00AB", "laquo" },
	{ "\u00AC", "not" },
	{ "\u00AD", "shy" },
	{ "\u00AE", "reg" },
	{ "\u00AF", "macr" },
	{ "\u00B0", "deg" },
	{ "\u00B1", "plusmn" },
	{ "\u00B2", "sup2" },
	{ "\u00B3", "sup3" },

	{ "\u00B4", "acute" },
	{ "\u00B5", "micro" },
	{ "\u00B6", "para" },
	{ "\u00B7", "middot" },
	{ "\u00B8", "cedil" },
	{ "\u00B9", "sup1" },
	{ "\u00BA", "ordm" },
	{ "\u00BB", "raquo" },
	{ "\u00BC", "frac14" },
	{ "\u00BD", "frac12" },
	{ "\u00BE", "frac34" },
	{ "\u00BF", "iquest" },

	{ "\u00C0", "Agrave" },
	{ "\u00C1", "Aacute" },
	{ "\u00C2", "Acirc" },
	{ "\u00C3", "Atilde" },
	{ "\u00C4", "Auml" },
	{ "\u00C5", "Aring" },
	{ "\u00C6", "AElig" },
	{ "\u00C7", "Ccedil" },
	{ "\u00C8", "Egrave" },
	{ "\u00C9", "Eacute" },
	{ "\u00CA", "Ecirc" },
	{ "\u00CB", "Euml" },
	{ "\u00CC", "Igrave" },
	{ "\u00CD", "Iacute" },
	{ "\u00CE", "Icirc" },
	{ "\u00CF", "Iuml" },

	{ "\u00D0", "ETH" },
	{ "\u00D1", "Ntilde" },
	{ "\u00D2", "Ograve" },
	{ "\u00D3", "Oacute" },
	{ "\u00D4", "Ocirc" },
	{ "\u00D5", "Otilde" },
	{ "\u00D6", "Ouml" },
	{ "\u00D7", "times" },
	{ "\u00D8", "Oslash" },
	{ "\u00D9", "Ugrave" },
	{ "\u00DA", "Uacute" },
	{ "\u00DB", "Ucirc" },
	{ "\u00DC", "Uuml" },
	{ "\u00DD", "Yacute" },
	{ "\u00DE", "THORN" },
	{ "\u00DF", "szlig" },

	{ "\u00E0", "agrave" },
	{ "\u00E1", "aacute" },
	{ "\u00E2", "acirc" },
	{ "\u00E3", "atilde" },
	{ "\u00E4", "auml" },
	{ "\u00E5", "aring" },
	{ "\u00E6", "aelig" },
	{ "\u00E7", "ccedil" },
	{ "\u00E8", "egrave" },
	{ "\u00E9", "eacute" },
	{ "\u00EA", "ecirc" },
	{ "\u00EB", "euml" },
	{ "\u00EC", "igrave" },
	{ "\u00ED", "iacute" },
	{ "\u00EE", "icirc" },
	{ "\u00EF", "iuml" },

	{ "\u00F0", "eth" },
	{ "\u00F1", "ntilde" },
	{ "\u00F2", "ograve" },
	{ "\u00F3", "oacute" },
	{ "\u00F4", "ocirc" },
	{ "\u00F5", "otilde" },
	{ "\u00F6", "ouml" },
	{ "\u00F7", "divid" },
	{ "\u00F8", "oslash" },
	{ "\u00F9", "ugrave" },
	{ "\u00FA", "uacute" },
	{ "\u00FB", "ucirc" },
	{ "\u00FC", "uuml" },
	{ "\u00FD", "yacute" },
	{ "\u00FE", "thorn" },
	{ "\u00FF", "yuml" },
	{ "\u0080", "euro" } };

	private static final HashMap<String, String> sEntitiesMap = new HashMap<String, String>(ENTITIES.length);

	static
	{
		final int l = ENTITIES.length;
		final StringBuffer temp = new StringBuffer();

		for (final String[] lPair : ENTITIES)
		{
			sEntitiesMap.put(lPair[1], lPair[0]);
		}
	}

}
