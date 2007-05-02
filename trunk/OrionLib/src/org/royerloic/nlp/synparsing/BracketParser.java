package org.royerloic.nlp.synparsing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BracketParser
{

	static Pattern	cParenthesisPattern		= Pattern.compile("\\([^\\(\\)]*\\)");
	static Pattern	cCurlyBracketsPattern	= Pattern.compile("\\{[^\\(\\)]*\\}");
	static Pattern	cBracketsPattern			= Pattern.compile("\\[[^\\(\\)]*\\]");
	static Pattern	cDoubleQuotesPattern	= Pattern.compile("\\\"[^\\\"]*\\\"");
	static Pattern	cSingleQuotesPattern	= Pattern.compile("\\\'[^\\\']*\\\'");

	public static final <O> void markAll(final TextTree<O> pTextTree, final O pAnnotationObject)
	{
		mark(cParenthesisPattern, pTextTree, pAnnotationObject);
		mark(cCurlyBracketsPattern, pTextTree, pAnnotationObject);
		mark(cBracketsPattern, pTextTree, pAnnotationObject);
		mark(cDoubleQuotesPattern, pTextTree, pAnnotationObject);
		mark(cSingleQuotesPattern, pTextTree, pAnnotationObject);
	}

	public static final <O> void mark(final Pattern pPattern,
																		final TextTree<O> pTextTree,
																		final O pAnnotationObject)
	{
		final TextTree<O> lWorkingTextTree = pTextTree.clone();

		markRecursive(lWorkingTextTree, pPattern, pTextTree, pAnnotationObject);
	}

	public static final <O> void markRecursive(	final TextTree<O> pWorkingTextTree,
																							final Pattern pPattern,
																							final TextTree<O> pTextTree,
																							final O pAnnotationObject)
	{

		final Matcher lMatcher = pPattern.matcher(pWorkingTextTree);

		boolean wasSomethingMarked = false;
		int lIndex = -1;
		while (lMatcher.find(lIndex + 1))
		{
			lIndex = lMatcher.start();
			final int lLength = lMatcher.end() - lMatcher.start();

			pTextTree.annotate(lIndex, lLength, pAnnotationObject);
			pWorkingTextTree.setCharAt(lIndex, ' ');
			pWorkingTextTree.setCharAt(lIndex + lLength - 1, ' ');

			wasSomethingMarked |= true;
		}

		if (wasSomethingMarked)
			markRecursive(pWorkingTextTree, pPattern, pTextTree, pAnnotationObject);

	}

}
