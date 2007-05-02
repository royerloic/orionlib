package org.royerloic.nlp.regex;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.royerloic.bioinformatics.pubmed.PubMedAccess;
import org.royerloic.nlp.regex.compiler.RegexCompiler;
import org.royerloic.string.StringUtils;

public class QuantityRegexes
{
	static RegexCompiler	cRegexCompiler;
	static
	{
		try
		{
			cRegexCompiler = new RegexCompiler("nq.regex.txt");
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Pattern	cReal					= cRegexCompiler.getPattern("Real");
	public static Pattern	cQuantityUnit	= cRegexCompiler.getPattern("QuantityUnit");
	public static Pattern	cUnits				= cRegexCompiler.getPattern("Units");

	public static final boolean isRealNumber(final CharSequence pCharSequence)
	{
		final Matcher lMatcher = cReal.matcher(pCharSequence);
		return lMatcher.matches();
	}

	public static final boolean isQuantity(final CharSequence pCharSequence)
	{

		final Matcher lMatcher = cQuantityUnit.matcher(pCharSequence);
		return lMatcher.matches();
	}

	public static final boolean isUnit(final CharSequence pCharSequence)
	{
		final Matcher lMatcher = cUnits.matcher(pCharSequence);
		return lMatcher.matches();
	}

	public static void main(final String[] args)
	{

		while (true)
		{
			final String lAbstract = PubMedAccess.getRandomAbstract();
			if (lAbstract != null)
			{
				final String[] lTokenArray = StringUtils.split(lAbstract, "\\s+", -1);

				for (int i = 0; i < lTokenArray.length - 1; i++)
				{
					final String lToken = StringUtils.cleanPunctuationAround(lTokenArray[i]);
					final String lTokenAfter = StringUtils.replaceAll(lTokenArray[i + 1], "[.,;\\(\\)\\[\\]\\{\\}]+", "");
					if (isRealNumber(lToken) && !isUnit(lTokenAfter))
						System.out.println(lTokenAfter);

				}
			}
		}

	}

}
