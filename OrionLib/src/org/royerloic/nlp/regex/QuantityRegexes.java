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
			int dummy = 0;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Pattern	cReal					= cRegexCompiler.getPattern("Real");
	public static Pattern	cQuantityUnit	= cRegexCompiler.getPattern("QuantityUnit");
	public static Pattern	cUnits				= cRegexCompiler.getPattern("Units");

	public static final boolean isRealNumber(CharSequence pCharSequence)
	{
		Matcher lMatcher = cReal.matcher(pCharSequence);
		return lMatcher.matches();
	}

	public static final boolean isQuantity(CharSequence pCharSequence)
	{

		Matcher lMatcher = cQuantityUnit.matcher(pCharSequence);
		return lMatcher.matches();
	}

	public static final boolean isUnit(CharSequence pCharSequence)
	{
		Matcher lMatcher = cUnits.matcher(pCharSequence);
		return lMatcher.matches();
	}

	public static void main(String[] args)
	{

		while (true)
		{
			String lAbstract = PubMedAccess.getRandomAbstract();
			if (lAbstract != null)
			{
				String[] lTokenArray = StringUtils.split(lAbstract, "\\s+", -1);

				for (int i = 0; i < lTokenArray.length - 1; i++)
				{
					String lToken = StringUtils.cleanPunctuationAround(lTokenArray[i]);
					String lTokenAfter = StringUtils.replaceAll(lTokenArray[i + 1], "[.,;\\(\\)\\[\\]\\{\\}]+", "");
					if (isRealNumber(lToken) && !isUnit(lTokenAfter))
						System.out.println(lTokenAfter);

				}
			}
		}

	}

}
