package utils.regex;


import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.regex.compiler.RegexCompiler;

public class ChromosomeRegexes
{
	public static RegexCompiler				cRegexCompiler;
	static
	{
		try
		{
			cRegexCompiler = new RegexCompiler("/utils/regex/compiler/chromosome.regex.txt");
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static final Pattern	cHumanChromosomeWordPattern		= cRegexCompiler
																																.getWordPattern("HumanChromosome");
	public static final Pattern	cGenericChromosomeWordPattern	= cRegexCompiler
																																.getWordPattern("Chromosome");
	public static final Pattern	cGenericChromosomePattern	= cRegexCompiler
																																.getPattern("Chromosome");

	public static final boolean isHumanChromosome(final CharSequence pCharSequence)
	{
		final Matcher lMatcher = cHumanChromosomeWordPattern.matcher(pCharSequence);
		return lMatcher.matches();
	}
	
	public static final boolean isChromosome(final CharSequence pCharSequence)
	{
		final Matcher lMatcher = cGenericChromosomePattern.matcher(pCharSequence);
		return lMatcher.matches();
	}

}
