package org.royerloic.nlp.synparsing;

import static org.royerloic.nlp.regex.QuantityRegexes.cQuantityUnit;
import static org.royerloic.nlp.regex.QuantityRegexes.cReal;
import static org.royerloic.nlp.regex.QuantityRegexes.cUnits;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuantityParser
{

	public static final <O> void markAll(final TextTree<O> pTextTree, final O pAnnotationObject)
	{
		mark(cReal, pTextTree, pAnnotationObject);
		mark(cUnits, pTextTree, pAnnotationObject);
		mark(cQuantityUnit, pTextTree, pAnnotationObject);
	}

	public static final <O> void mark(final Pattern pPattern,
																		final TextTree<O> pTextTree,
																		final O pAnnotationObject)
	{
		final Matcher lMatcher = pPattern.matcher(pTextTree);

		boolean wasSomethingMarked = false;
		int lIndex = 0;
		int lLength = 0;
		while (lMatcher.find(lIndex + lLength))
		{
			lIndex = lMatcher.start();
			lLength = lMatcher.end() - lMatcher.start();

			pTextTree.annotate(lIndex, lLength, pAnnotationObject);

			wasSomethingMarked |= true;
		}
	}

}
