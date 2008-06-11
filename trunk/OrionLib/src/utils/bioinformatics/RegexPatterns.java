package utils.bioinformatics;

import utils.nlp.wordlists.EnglishWordsIdentifier;
import utils.string.StringUtils;

public class RegexPatterns
{

	public static String sPostFixWeakModifierString = "(?:\\-?[0-9]{1}|\\-like|\\-related)?";
	public static String sLowerConsonentsString = "(?:b|c|d|f|g|h|j|k|l|m|n|p|q|r|s|t|v|w|x|y|z)";
	public static String sUpperConsonentsString = "(?:B|C|D|F|G|H|J|K|L|M|N|P|Q|R|S|T|V|W|X|Y|Z)";
	public static String sConsonentsString = "(?:" + sLowerConsonentsString
																						+ "|"
																						+ sUpperConsonentsString
																						+ ")";
	public static String sLeft = "(?:^|\\W)";
	public static String sRight = "(?:$|\\W)";
	public static String sRomanNumbers = "I|II|III|IV|V|VI|VII|VIII";

	public static EnglishWordsIdentifier sEnglishWordsIdentifier = EnglishWordsIdentifier.getUniqueInstance();
	public static String sHugoPattern = "HGNC\\:[0-9]{2,6}";
	public static String sMGCPattern = "MGC:?[0-9]+";
	public static String sUniProtPattern = "(?:(?:O|P|Q)[0-9][0-9A-Z][0-9]{3}(?:-[0-9]+)?)|(?:[0-9A-Z]{2,6}\\_[0-9A-Z]{3,})";
	public static String sFLJPattern = "FLJ(?:[0-9]{5})";
	public static String sDKFZPattern = "DKFZ(?:p|P)(?:[0-9A-Z]{7,9})";
	public static String sOTTHUMPPattern = "OTTHUM(?:P|G)(?:[0-9]{11})";
	public static String sLOCPattern = "LOC(?:[0-9]{4,6})";
	public static String sORFPattern = "[0-9A-Z]{2,3}orf[0-9]+";
	public static String sKIAAPattern = "KIAA[0-9]{1,4}";
	public static String sDJBAPattern = "(?:dJ|DJ|bA|BA)[0-9A-Z]+\\.[0-9]+";
	public static String sRikPattern = "[0-9]{7}[A-Z][0-9]{2}Ri[kK]";

	public static String sIdPattern = "(?:" + sHugoPattern
																		+ ")|"
																		+ "(?:"
																		+ sMGCPattern
																		+ ")|"
																		+ "(?:"
																		+ sUniProtPattern
																		+ ")|"
																		+ "(?:"
																		+ sFLJPattern
																		+ ")|"
																		+ "(?:"
																		+ sDKFZPattern
																		+ ")|"
																		+ "(?:"
																		+ sOTTHUMPPattern
																		+ ")|"
																		+ "(?:"
																		+ sLOCPattern
																		+ ")|"
																		+ "(?:"
																		+ sORFPattern
																		+ ")|"
																		+ "(?:"
																		+ sKIAAPattern
																		+ ")|"
																		+ "(?:"
																		+ sDJBAPattern
																		+ ")|"
																		+ "(?:"
																		+ sRikPattern
																		+ ")";

	public static String sSimilarLikeRelated = sLeft + "((S|s)imilar( to)?)|(L|l)ike|((R|r)elated( to)?)"
																							+ sRight;

	public static String sAseEnzyme = "([a-zA-Z]{0,}" + "(?:"
																		+ sConsonentsString
																		+ "|e|-)"
																		+ "ase)";
	public static String sInDrug = "(?:((?:[a-z]|[A-Z])[a-z]{2,}" + sConsonentsString
																	+ "i(?:n|m)))";

	public static String sYcan = "(?:((?:[a-z]|[A-Z])[a-z]{2,}" + sConsonentsString
																+ "ycan))";

	public static String sReceptor = "([a-zA-Z]{2,}ceptor)";

	public static String sFactor = "((?:F|f)actor)";

	public static String sActivator = "((?:A|a)ctivator)";

	public static String sZincFinger = "((?:Z|z)inc\\W(?:F|f)inger)";

	public static String sProtein = "[A-Z]?[a-z]*((?:P|p)rotein)";

	public static String sDomain = "((?:D|d)omain)";

	public static String sChannel = "((?:C|c)hannel)";

	public static String sNumericPattern = "[0-9]+";

	public static String sMultiWordPattern = ".*\\s+.*";

	public static String sProteinWeightPattern = sLeft + "[0-9]+(\\.[0-9]+)?(\\s|-)*(kD|kDa|kDA|KDA)"
																								+ sRight;

	public static String sAbbreviationPatternType1 = "[A-Z]{3,}";
	public static String sAbbreviationPatternType2 = "[A-Za-z0-9\\-/]{2,}";

	// public static String sAbbreviationPatternType3 = "[A-Za-z0-9 ]{2,}";

	private RegexPatterns()
	{
		super();
	}

	public static boolean isAbbreviationType1(final CharSequence pCharSequence)
	{
		return StringUtils.matches(pCharSequence, sAbbreviationPatternType1);
	}

	public static boolean isAbbreviationType2(final CharSequence pCharSequence)
	{
		return StringUtils.matches(pCharSequence, sAbbreviationPatternType2) && StringUtils.countUpperCase(pCharSequence) + StringUtils.countDigits(pCharSequence) > pCharSequence.length() / 4;
	}

	public static boolean isAbbreviationType3(final CharSequence pCharSequence)
	{
		final String[] lTokenArray = StringUtils.split(pCharSequence, "\\W+", 0);
		boolean isAbbreviationType3 = true;
		for (final String lToken : lTokenArray)
		{
			isAbbreviationType3 &= isAbbreviationType1(lToken) || isAbbreviationType2(lToken)
															|| isNumber(lToken);
		}
		return isAbbreviationType3;
	}

	private static boolean isNumber(final String pToken)
	{
		return StringUtils.matches(pToken, "[0-9]+");
	}

	public static boolean isAbbreviationType4(final CharSequence pCharSequence)
	{
		return false;
	}

	public static boolean isId(final CharSequence pCharSequence)
	{
		boolean lIsId = false;
		lIsId |= StringUtils.matches(pCharSequence, sHugoPattern);
		lIsId |= StringUtils.matches(pCharSequence, sMGCPattern);
		lIsId |= StringUtils.matches(pCharSequence, sUniProtPattern);
		lIsId |= StringUtils.matches(pCharSequence, sFLJPattern);
		lIsId |= StringUtils.matches(pCharSequence, sDKFZPattern);
		lIsId |= StringUtils.matches(pCharSequence, sOTTHUMPPattern);
		lIsId |= StringUtils.matches(pCharSequence, sLOCPattern);
		lIsId |= StringUtils.matches(pCharSequence, sORFPattern);
		lIsId |= StringUtils.matches(pCharSequence, sKIAAPattern);
		lIsId |= StringUtils.matches(pCharSequence, sDJBAPattern);
		lIsId |= StringUtils.matches(pCharSequence, sRikPattern);
		return lIsId;
	}

	public static boolean isEnglishWord(final String pString)
	{
		return sEnglishWordsIdentifier.isEntity(pString);
	}

	public static boolean isMultiWord(final String pString)
	{
		return StringUtils.matches(pString, sMultiWordPattern);
	}

	public static boolean isGarbage(final CharSequence pCharSequence)
	{
		if (StringUtils.submatches(pCharSequence, "(H|h)ypothetical"))
		{
			return true;
		}
		else if (StringUtils.submatches(pCharSequence,
																		"(C|c)hromosome.*(O|o)pen\\W+(R|r)eading\\W+(F|f)rame"))
		{
			return true;
		}
		else if (StringUtils.submatches(pCharSequence, "pseudogene"))
		{
			return true;
		}
		return false;
	}

}
