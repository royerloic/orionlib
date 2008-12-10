package utils.bioinformatics.mafft;

import java.io.IOException;

import utils.bioinformatics.fasta.Conservation;
import utils.bioinformatics.fasta.FastaSet;
import utils.bioinformatics.fasta.Randomize;
import utils.math.statistics.Histogram;

public class SignificantMultipleAlignment implements MultipleSequenceAlignment
{

	private final MultipleSequenceAlignment mMSA;
	private final String mRandomizationType;
	private double mSignificance;
	private double[] mSignificantConservationScore;
	private double mSignificanceLevel = 0.05;
	private int mRandomizationRuns = 1;

	public SignificantMultipleAlignment(String pRandomizationType,
																			MultipleSequenceAlignment pMSA)
	{
		super();
		mRandomizationType = pRandomizationType;
		mMSA = pMSA;
	}

	public SignificantMultipleAlignment(MultipleSequenceAlignment pMSA)
	{
		super();
		mRandomizationType = "1gram";
		mMSA = pMSA;
	}

	public FastaSet run(FastaSet pInput) throws InterruptedException, IOException
	{
		FastaSet lAlignment = mMSA.run(pInput);
		

		double[] lEntropyOfAlignment = Conservation.computeEntropy(lAlignment);
		
		Histogram lConservationHistogram = new Histogram();
		for (int i = 0; i < mRandomizationRuns ; i++)
		{
			FastaSet lRandomizedInput = Randomize.randomize(mRandomizationType, pInput);
			FastaSet lRandomAligment = mMSA.run(lRandomizedInput);
			double[] lEntropyOfRandomAligment = Conservation.computeEntropy(lRandomAligment);
			lConservationHistogram = Conservation.computeConservationHistogram(lConservationHistogram, lEntropyOfRandomAligment);
		}
		
		lConservationHistogram.determineOptimalNumberOfBins();
		double[] lStatisticArray = lConservationHistogram.getStatistic();

		mSignificance = 0;
		mSignificantConservationScore = new double[lEntropyOfAlignment.length];
		for (int i = 0; i < lEntropyOfAlignment.length; i++)
		{
			double entropy = lEntropyOfAlignment[i];
			mSignificantConservationScore[i] = lConservationHistogram.rightDensity(lStatisticArray,
																																						entropy);
			
			if(mSignificantConservationScore[i]<mSignificanceLevel/lEntropyOfAlignment.length)
				mSignificance++;

		}
		

		return lAlignment;
	}

	public double getSignificance()
	{
		return mSignificance;
	}

	public double[] getSignificantConservationScore()
	{
		return mSignificantConservationScore;
	}

}
