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
	private double mSignificanceLevel = 0.001;
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
		FastaSet lAlignment = new FastaSet();
		try
		{
			if(pInput.size()==0)
				return lAlignment;
			
			lAlignment = mMSA.run(pInput);

			double[] lEntropyOfAlignment = Conservation.computeRelativeNegentropy(lAlignment);
			
			Histogram lConservationHistogram = new Histogram();
			for (int i = 0; i < mRandomizationRuns ; i++)
			{
				FastaSet lRandomizedInput = Randomize.randomize(mRandomizationType, pInput);
				FastaSet lRandomAligment = mMSA.run(lRandomizedInput);
				//System.out.println("What follows is suposed to be random...");
				//System.out.println(lRandomAligment.toAlignmentString());
				double[] lEntropyOfRandomAligment = Conservation.computeRelativeNegentropy(lRandomAligment);
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
			
			mSignificance = mSignificance/lEntropyOfAlignment.length;
			
			return lAlignment;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			return null;
		}
		
	}

	public double getSignificance()
	{
		return mSignificance;
	}

	public double[] getSignificantConservationScore()
	{
		return mSignificantConservationScore;
	}

	public void setRandomizationRuns(int pI)
	{
		mRandomizationRuns=pI;
	}

}
