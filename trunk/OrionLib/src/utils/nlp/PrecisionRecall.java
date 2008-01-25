package utils.nlp;

public class PrecisionRecall
{

	public static final double recall(final double pTruePositiveCount,
																		final double pFalseNegativeCount)
	{
		double lDenominator = pTruePositiveCount + pFalseNegativeCount;
		lDenominator = lDenominator == 0 ? 1 : lDenominator;
		return pTruePositiveCount / lDenominator;
	}

	public static final double precision(	final double pTruePositiveCount,
																				final double pFalsePositiveCount)
	{
		double lDenominator = pTruePositiveCount + pFalsePositiveCount;
		lDenominator = lDenominator == 0 ? 1 : lDenominator;
		return pTruePositiveCount / lDenominator;
	}

	public static final double geometricMean(final double pA, final double pB)
	{
		return (2 * pA * pB) / (pA + pB);
	}

	public static final double fmeasure(final double pPrecision,
																			final double pRecall)
	{
		return geometricMean(pPrecision, pRecall);
	}

	public static final double fmeasure(final double pPrecision,
																			final double pRecall,
																			final double pAlpha)
	{
		return ((1 + pAlpha) * pPrecision * pRecall) / (pAlpha * pPrecision + pRecall);
	}

	public static final double fmeasurePrecision(	final double pPrecision,
																								final double pRecall)
	{
		return fmeasure(pPrecision, pRecall, 0.5);
	}

	public static final double fmeasureRecall(final double pPrecision,
																						final double pRecall)
	{
		return fmeasure(pPrecision, pRecall, 2);
	}

}
