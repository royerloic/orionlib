package org.royerloic.nlp;

public class PrecisionRecall
{

	public static final double recall(double pTruePositiveCount, double pFalseNegativeCount)
	{
		double lDenominator = pTruePositiveCount + pFalseNegativeCount;
		lDenominator = lDenominator == 0 ? 1 : lDenominator;
		return pTruePositiveCount / lDenominator;
	}

	public static final double precision(double pTruePositiveCount, double pFalsePositiveCount)
	{
		double lDenominator = pTruePositiveCount + pFalsePositiveCount;
		lDenominator = lDenominator == 0 ? 1 : lDenominator;
		return pTruePositiveCount / lDenominator;
	}

	public static final double geometricMean(double pA, double pB)
	{
		return (2 * pA * pB) / (pA + pB);
	}

	public static final double fmeasure(double pPrecision, double pRecall)
	{
		return geometricMean(pPrecision, pRecall);
	}

	public static final double fmeasure(double pPrecision, double pRecall, double pAlpha)
	{
		return ((1 + pAlpha) * pPrecision * pRecall) / (pAlpha * pPrecision + pRecall);
	}

	public static final double fmeasurePrecision(double pPrecision, double pRecall)
	{
		return fmeasure(pPrecision, pRecall, 0.5);
	}

	public static final double fmeasureRecall(double pPrecision, double pRecall)
	{
		return fmeasure(pPrecision, pRecall, 2);
	}

}
