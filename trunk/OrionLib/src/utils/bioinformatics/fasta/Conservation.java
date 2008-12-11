package utils.bioinformatics.fasta;

import java.util.Collection;

import utils.math.statistics.Average;
import utils.math.statistics.Entropy;
import utils.math.statistics.Histogram;
import utils.math.statistics.MinMax;
import utils.math.statistics.transform.MinMaxTransform;
import utils.math.statistics.transform.NormalizedZTransform;
import utils.math.statistics.transform.Transform;

public class Conservation
{

	public static final double[] computeRelativeNegentropy(FastaSet pFastaSet)
	{
		Collection<FastaSequence> lFastaSequences = pFastaSet.getFastaSequences();
		int lNumberOfSequences = lFastaSequences.size();

		char[][] lAlignment = new char[lNumberOfSequences][];

		{
			int i = 0;
			for (FastaSequence lFastaSequence : lFastaSequences)
			{
				char[] lCharArray = lFastaSequence.getSequenceString().toCharArray();
				lAlignment[i] = lCharArray;
				i++;
			}
		}

		double[] lConservationArray = new double[lAlignment[0].length];
		double lMaxEntropy = Math.log10(lNumberOfSequences) / Math.log10(2);
		for (int i = 0; i < lConservationArray.length; i++)
			lConservationArray[i] = lMaxEntropy;

		Entropy lEntropy = new Entropy();
		for (int i = 0; i < lAlignment[0].length; i++)
		{
			double gapscount = 0;
			for (int j = 0; j < lNumberOfSequences; j++)
				if (lAlignment[j][i] == '-')
				{
					gapscount++;
				}
				else
				{
					lEntropy.enter(lAlignment[j][i]);
				}

			lConservationArray[i] = lConservationArray[i] - lEntropy.getStatistic();
			lConservationArray[i] = lConservationArray[i] * ((lNumberOfSequences - gapscount) / lNumberOfSequences);
			lConservationArray[i] = lConservationArray[i] / lMaxEntropy;
			lEntropy.reset();
		}

		return lConservationArray;
	}

	public static final double computeAverageRelativeNegentropy(FastaSet pFastaSet)
	{
		Average lAverage = new Average();
		for (double value : computeRelativeNegentropy(pFastaSet))
		{
			lAverage.enter(value);
		}
		return lAverage.getStatistic();
	}

	public static String encodeAsString(double[] pComputeEntropy)
	{

		MinMaxTransform lTransform = new MinMaxTransform();
		for (double lEntropyValue : pComputeEntropy)
		{
			lTransform.enter(lEntropyValue);
		}

		StringBuilder lStringBuilder = new StringBuilder();
		for (double lEntropyValue : pComputeEntropy)
		{
			double lTransformedEntropyValue = lTransform.transform(lEntropyValue);
			int lEncodedValue = (int) Math.floor(9 * (lTransformedEntropyValue));
			lStringBuilder.append(lEncodedValue);
		}

		return lStringBuilder.toString();
	}

	static final char[] code = " -~oO@#".toCharArray();

	public static String encodeAsVisualString(double[] pComputeEntropy)
	{

		MinMaxTransform lTransform = new MinMaxTransform();
		for (double lEntropyValue : pComputeEntropy)
		{
			lTransform.enter(lEntropyValue);
		}

		StringBuilder lStringBuilder = new StringBuilder();
		for (double lEntropyValue : pComputeEntropy)
		{
			double lTransformedEntropyValue = lTransform.transform(lEntropyValue);
			int lEncodedValue = (int) Math.floor(6 * (lTransformedEntropyValue));
			lStringBuilder.append(code[lEncodedValue]);
		}

		return lStringBuilder.toString();
	}

	public static double getMaxConservation(double[] pComputeEntropy)
	{

		MinMax lMinMax = new MinMax();
		for (double lEntropyValue : pComputeEntropy)
		{
			lMinMax.enter(lEntropyValue);
		}

		return lMinMax.getStatistic()[1];
	}

	public static Histogram computeConservationHistogram(	Histogram pHistogram,
																												double[] pConservationArray)
	{
		for (double value : pConservationArray)
		{
			pHistogram.enter(value);
		}
		return pHistogram;
	}

}
