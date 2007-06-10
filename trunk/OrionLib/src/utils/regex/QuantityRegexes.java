package utils.regex;


import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.regex.compiler.RegexCompiler;

public class QuantityRegexes
{

	static RegexCompiler				cRegexCompiler;
	static
	{
		try
		{
			cRegexCompiler = new RegexCompiler("/utils/regex/compiler/nq.regex.txt");
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static final Pattern	cRealWord					= cRegexCompiler.getWordPattern("Real");
	public static final Pattern	cQuantityUnitWord	= cRegexCompiler.getWordPattern("QuantityUnit");
	public static final Pattern	cUnitsWord				= cRegexCompiler.getWordPattern("Units");

	public static final Pattern	cReal							= cRegexCompiler.getPattern("Real");
	public static final Pattern	cStrictReal				= cRegexCompiler.getPattern("StrictReal");
	public static final Pattern	cQuantityUnit			= cRegexCompiler.getPattern("QuantityUnit");
	public static final Pattern	cUnits						= cRegexCompiler.getPattern("Units");
	public static final Pattern	cInequality				= cRegexCompiler.getPattern("Inequality");
	public static final Pattern	cUncertainty			= cRegexCompiler.getPattern("Uncertainty");
	public static final Pattern	cRange						= cRegexCompiler.getPattern("Range");

	public static final boolean isRealNumber(final CharSequence pCharSequence)
	{
		final Matcher lMatcher = cRealWord.matcher(pCharSequence);
		return lMatcher.matches();
	}

	public static final boolean isQuantity(final CharSequence pCharSequence)
	{

		final Matcher lMatcher = cQuantityUnitWord.matcher(pCharSequence);
		return lMatcher.matches();
	}

	public static final boolean isUnit(final CharSequence pCharSequence)
	{
		final Matcher lMatcher = cUnitsWord.matcher(pCharSequence);
		return lMatcher.matches();
	}

}
