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

	public Annotation(String pText, O pAnnotationObject, String pAnnotatedFragment, Integer pStart)
	{
		super();
		mText = pText;
		mAnnotationObject = pAnnotationObject;
		mAnnotatedFragment = pAnnotatedFragment;
		mStart = pStart;

	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (this.hashCode() != ((Annotation) obj).hashCode())
		{
			return false;
		}
		Annotation lAnnotation = (Annotation) obj;

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
		return mText.hashCode() + mAnnotatedFragment.hashCode() + mStart + mAnnotationObject.hashCode();// +
		// mConfidence.hashCode();
	}

	@Override
	public String toString()
	{
		return mAnnotationObject + "\t" + mAnnotatedFragment + "\t" + mConfidence + "\t" + mStart + "\t"
				+ mAnnotatedFragment.length() + "\t" + mCaracteristics.toString() + "\t" + mAnnotatorName;
	}

	public List<String> toList()
	{
		return Arrays.asList(new String[]
		{ mAnnotationObject.toString(), mAnnotatedFragment, mConfidence.toString(), mStart.toString(),
				Integer.toString(mAnnotatedFragment.length()) });
	}

	public static Annotation fromList(List<String> pList)
	{
		String lEntrezIdString = pList.get(0);
		Integer lEntrezIdInteger = Integer.parseInt(lEntrezIdString);
		String lAnnotatedFragment = pList.get(1);
		String lConfidenceString = pList.get(2);
		Double lConfidenceDouble = Double.parseDouble(lConfidenceString);
		String lStartString = pList.get(3);
		Integer lStart = Integer.parseInt(lStartString);

		// String lAnnotatorName = pList.get(5);

		Annotation lAnnotation = new Annotation(lAnnotatedFragment, lEntrezIdInteger, lAnnotatedFragment, lStart);
		lAnnotation.mConfidence = lConfidenceDouble;
		// lAnnotation.mAnnotatorName = lAnnotatorName;
		return lAnnotation;
	}

	public int compareTo(Annotation pO)
	{
		return -(int) (mConfidence - pO.mConfidence > 0 ? 1 : -1);
	}

	public static void renormalizeDistribution(List<Annotation> pAnnotationsList)
	{
		if (!pAnnotationsList.isEmpty())
		{
			double lTotal = 0;
			for (Annotation lAnnotation : pAnnotationsList)
				lTotal += lAnnotation.mConfidence;

			if (lTotal == 0)
			{
				for (Annotation lAnnotation : pAnnotationsList)
				{
					lAnnotation.mConfidence = 1.0;
				}
				renormalizeDistribution(pAnnotationsList);
			}
			else
			{
				for (Annotation lAnnotation : pAnnotationsList)
				{
					lAnnotation.mConfidence = lAnnotation.mConfidence / lTotal;
				}
			}
		}
	}

	public static void sort(List<Annotation> pAnnotationsList)
	{
		Collections.sort(pAnnotationsList);
	}

	public static List<Annotation> keepBestAnnotationsAssumingSorted(List<Annotation> pAnnotationsList)
	{
		List<Annotation> lAnnotationsList = new ArrayList<Annotation>();
		if (!pAnnotationsList.isEmpty())
		{
			double lMaxConfidence = pAnnotationsList.get(0).mConfidence;
			for (Annotation lAnnotation : pAnnotationsList)
				if (lAnnotation.mConfidence == lMaxConfidence)
				{
					lAnnotationsList.add(lAnnotation);
				}
		}
		return lAnnotationsList;
	}

	public static List<Annotation> filterAnnotations(List<Annotation> pAnnotationsList, double pThreshold)
	{
		List<Annotation> lAnnotationsList = new ArrayList<Annotation>();
		if (!pAnnotationsList.isEmpty())
		{
			for (Annotation lAnnotation : pAnnotationsList)
				if (lAnnotation.mConfidence > pThreshold)
				{
					lAnnotationsList.add(lAnnotation);
				}
		}
		return lAnnotationsList;
	}

	public static void addOffset(Collection<Annotation> pAnnotationSet, int pSentenceOffset)
	{
		for (Annotation lAnnotation : pAnnotationSet)
		{
			lAnnotation.mStart += pSentenceOffset;
		}
	}

	public static void setTextForAll(String pText, Set<Annotation> pAnnotationSet)
	{
		for (Annotation lAnnotation : pAnnotationSet)
			lAnnotation.mText = pText;

	}

	public static <O> void computeLevenshteinSimilarity(Map<O, Set<String>> pMasterListMap,
																											Collection<Annotation> pAnnotationSetForAbstract)
	{
		for (Annotation<O> lAnnotation : pAnnotationSetForAbstract)
		{
			O lAnnotationObject = lAnnotation.mAnnotationObject;
			String lAnnotationString = lAnnotation.mAnnotatedFragment;
			Set<String> lSynonymSet = pMasterListMap.get(lAnnotationObject);

			double lMaxSimilarity = 0;
			for (String lSynonym : lSynonymSet)
			{
				double lSimilarity = LevenshteinDistance.similarity(lSynonym, lAnnotationString);
				if (lSimilarity > lMaxSimilarity)
				{
					lMaxSimilarity = lSimilarity;
				}
			}

			lAnnotation.mConfidence = lMaxSimilarity;

		}

	}

	public String getText()
	{
		return mText;
	}

	public Integer getStart()
	{
		return mStart;
	}

	public String getMatch()
	{
		return mAnnotatedFragment;
	}

	public Integer getLength()
	{
		return mAnnotatedFragment.length();
	}

	public O getAnnotation()
	{
		return mAnnotationObject;
	}

}
