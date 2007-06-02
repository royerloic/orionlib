package utils.nlp.synparsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.nlp.annotation.filtering.AnnotationContextFilter;

public class SentenceSplitter
{

	static Pattern									cSentenceSplitPattern	= Pattern.compile("([\\.\\!\\?\\;\\:])\\s");

	static AnnotationContextFilter	cAnnotationContextFilter;

	static
	{
		try
		{
			cAnnotationContextFilter = new AnnotationContextFilter("SentenceSplitting.3rules.txt");
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	public static final List<Integer> findSplitPoints(final CharSequence pCharSequence)
	{
		final List<Integer> lIntegerList = new ArrayList<Integer>();

		final Matcher lMatcher = cSentenceSplitPattern.matcher(pCharSequence);

		int lIndex = 0;
		while (lMatcher.find(lIndex + 1))
		{
			lIndex = lMatcher.start();

			final CharSequence lBefore = pCharSequence.subSequence(0, Math.max(0, lIndex));
			final CharSequence lSplit = pCharSequence.subSequence(lIndex, lIndex + 1);
			final CharSequence lAfter = pCharSequence.subSequence(Math.min(pCharSequence.length() - 1, lIndex + 1),
					pCharSequence.length());

			final boolean lFiltered = cAnnotationContextFilter.filter(lBefore, lSplit, lAfter);

			if (!lFiltered)
				lIntegerList.add(Math.min(lIndex + 2, pCharSequence.length() - 1));
		}

		return lIntegerList;
	}

	public static final <O> void split(final TextTree pTextTree, final O pSentenceAnnotation)
	{
		final List<Integer> lIntegerList = findSplitPoints(pTextTree);
		pTextTree.addChildrenAccordingToCutPoints(lIntegerList, pSentenceAnnotation);
	}

}
