package org.royerloic.nlp.regex;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.royerloic.bioinformatics.pubmed.PubMedAccess;
import org.royerloic.nlp.regex.compiler.RegexCompiler;
import org.royerloic.string.StringUtils;

public class ChromosomeRegexes
{
	static RegexCompiler	cRegexCompiler;
	static
	{
		try
		{
			cRegexCompiler = new RegexCompiler("chromosome.regex.txt");
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Pattern	cHumanChromosome					= cRegexCompiler.getPattern("HumanChromosome");

	public static final boolean isHumanChromosome(final CharSequence pCharSequence)
	{
		final Matcher lMatcher = cHumanChromosome.matcher(pCharSequence);
		return lMatcher.matches();
	}



	public static void main(final String[] args)
	{

		while (true)
		{
			final String lAbstract = PubMedAccess.getRandomAbstract();
			if (lAbstract != null)
			{
				final List<String> lMatchesList = StringUtils.findAllmatches(lAbstract, cHumanChromosome.toString());

				for (final String lString : lMatchesList)
					System.out.println(lString);

				final String[] lTokenArray = StringUtils.split(lAbstract, "\\s+", -1);
				/***********************************************************************
				 * for (int i = 1; i < lTokenArray.length - 1; i++) { String
				 * lTokenBefore = lTokenArray[i-1]; String lToken =
				 * StringUtils.replaceAll(lTokenArray[i], "[,;\\(\\)\\[\\]\\{\\}]+",
				 * "");; String lTokenAfter = lTokenArray[i+1];
				 * 
				 * if (isChromosomeRegion(lToken)) System.out.println(lTokenBefore+"
				 * "+lToken+" "+lTokenAfter);
				 * 
				 * if
				 * (lToken.equalsIgnoreCase("chromosome")||lToken.equalsIgnoreCase("chromosomes"))
				 * System.out.println("CH: "+lTokenBefore+" "+lToken+" "+lTokenAfter); }/
				 **********************************************************************/

			}
		}

	}

}
