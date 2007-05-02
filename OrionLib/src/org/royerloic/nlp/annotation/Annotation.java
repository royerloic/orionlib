package org.royerloic.nlp.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.royerloic.nlp.LevenshteinDistance;

public class Annotation<O> implements ExchangeAnnotation<O>, Comparable<Annotation>
{
	public O										mAnnotationObject;
	public String								mAnnotatedFragment;
	public Integer							mStart;
	public String								mText;

	public Double								mConfidence			= 1.0;

	// Fields not part of the identity of the object (not used in equals and
	// hascode)
	public Map<String, Double>	mCaracteristics	= new HashMap<String, Double>();
	public String								mAnnotatorName;

	public Annotation(final String pText, final O pAnnotationObject, final String pAnnotatedFragment, final Integer pStart)
	{
		super();
		this.mText = pText;
		this.mAnnotationObject = pAnnotationObject;
		this.mAnnotatedFragment = pAnnotatedFragment;
		this.mStart = pStart;

	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		else if (this.hashCode() != ((Annotation) obj).hashCode())
			return false;
		final Annotation lAnnotation = (Annotation) obj;

		boolean lEquals = true;
		lEquals &= this.mText.equals(lAnnotation.mText);
		lEquals &= this.mAnnotatedFragment.equals(lAnnotation.mAnnotatedFragment);
		lEquals &= this.mStart.equals(lAnnotation.mStart);
		lEquals &= this.mAnnotationObject.equals(lAnnotation.mAnnotationObject);
		// lEquals &= this.mConfidence.equals(lAnnotation.mConfidence);

		return lEquals;
	}

	@Override
	public int hashCode()
	{
		return this.mText.hashCode() + this.mAnnotatedFragment.hashCode() + this.mStart + this.mAnnotationObject.hashCode();// +
		// mConfidence.hashCode();
	}

	@Override
	public String toString()
	{
		return this.mAnnotationObject + "\t" + this.mAnnotatedFragment + "\t" + this.mConfidence + "\t" + this.mStart + "\t"
				+ this.mAnnotatedFragment.length() + "\t" + this.mCaracteristics.toString() + "\t" + this.mAnnotatorName;
	}

	public List<String> toList()
	{
		return Arrays.asList(new String[]
		{ this.mAnnotationObject.toString(), this.mAnnotatedFragment, this.mConfidence.toString(), this.mStart.toString(),
				Integer.toString(this.mAnnotatedFragment.length()) });
	}

	public static Annotation fromList(final List<String> pList)
	{
		final String lEntrezIdString = pList.get(0);
		final Integer lEntrezIdInteger = Integer.parseInt(lEntrezIdString);
		final String lAnnotatedFragment = pList.get(1);
		final String lConfidenceString = pList.get(2);
		final Double lConfidenceDouble = Double.parseDouble(lConfidenceString);
		final String lStartString = pList.get(3);
		final Integer lStart = Integer.parseInt(lStartString);

		// String lAnnotatorName = pList.get(5);

		final Annotation lAnnotation = new Annotation(lAnnotatedFragment, lEntrezIdInteger, lAnnotatedFragment, lStart);
		lAnnotation.mConfidence = lConfidenceDouble;
		// lAnnotation.mAnnotatorName = lAnnotatorName;
		return lAnnotation;
	}

	public int compareTo(final Annotation pO)
	{
		return -(this.mConfidence - pO.mConfidence > 0 ? 1 : -1);
	}

	public static void renormalizeDistribution(final List<Annotation> pAnnotationsList)
	{
		if (!pAnnotationsList.isEmpty())
		{
			double lTotal = 0;
			for (final Annotation lAnnotation : pAnnotationsList)
				lTotal += lAnnotation.mConfidence;

			if (lTotal == 0)
			{
				for (final Annotation lAnnotation : pAnnotationsList)
					lAnnotation.mConfidence = 1.0;
				renormalizeDistribution(pAnnotationsList);
			}
			else
				for (final Annotation lAnnotation : pAnnotationsList)
					lAnnotation.mConfidence = lAnnotation.mConfidence / lTotal;
		}
	}

	public static void sort(final List<Annotation> pAnnotationsList)
	{
		Collections.sort(pAnnotationsList);
	}

	public static List<Annotation> keepBestAnnotationsAssumingSorted(final List<Annotation> pAnnotationsList)
	{
		final List<Annotation> lAnnotationsList = new ArrayList<Annotation>();
		if (!pAnnotationsList.isEmpty())
		{
			final double lMaxConfidence = pAnnotationsList.get(0).mConfidence;
			for (final Annotation lAnnotation : pAnnotationsList)
				if (lAnnotation.mConfidence == lMaxConfidence)
					lAnnotationsList.add(lAnnotation);
		}
		return lAnnotationsList;
	}

	public static List<Annotation> filterAnnotations(final List<Annotation> pAnnotationsList, final double pThreshold)
	{
		final List<Annotation> lAnnotationsList = new ArrayList<Annotation>();
		if (!pAnnotationsList.isEmpty())
			for (final Annotation lAnnotation : pAnnotationsList)
				if (lAnnotation.mConfidence > pThreshold)
					lAnnotationsList.add(lAnnotation);
		return lAnnotationsList;
	}

	public static void addOffset(final Collection<Annotation> pAnnotationSet, final int pSentenceOffset)
	{
		for (final Annotation lAnnotation : pAnnotationSet)
			lAnnotation.mStart += pSentenceOffset;
	}

	public static void setTextForAll(final String pText, final Set<Annotation> pAnnotationSet)
	{
		for (final Annotation lAnnotation : pAnnotationSet)
			lAnnotation.mText = pText;

	}

	public static <O> void computeLevenshteinSimilarity(final Map<O, Set<String>> pMasterListMap,
																											final Collection<Annotation> pAnnotationSetForAbstract)
	{
		for (final Annotation<O> lAnnotation : pAnnotationSetForAbstract)
		{
			final O lAnnotationObject = lAnnotation.mAnnotationObject;
			final String lAnnotationString = lAnnotation.mAnnotatedFragment;
			final Set<String> lSynonymSet = pMasterListMap.get(lAnnotationObject);

			double lMaxSimilarity = 0;
			for (final String lSynonym : lSynonymSet)
			{
				final double lSimilarity = LevenshteinDistance.similarity(lSynonym, lAnnotationString);
				if (lSimilarity > lMaxSimilarity)
					lMaxSimilarity = lSimilarity;
			}

			lAnnotation.mConfidence = lMaxSimilarity;

		}

	}

	public String getText()
	{
		return this.mText;
	}

	public Integer getStart()
	{
		return this.mStart;
	}

	public String getMatch()
	{
		return this.mAnnotatedFragment;
	}

	public Integer getLength()
	{
		return this.mAnnotatedFragment.length();
	}

	public O getAnnotation()
	{
		return this.mAnnotationObject;
	}

}
