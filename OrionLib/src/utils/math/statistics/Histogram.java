package utils.math.statistics;

import java.io.Serializable;
import java.util.ArrayList;

import utils.math.statistics.transform.MinMaxTransform;

public class Histogram implements Statistic<double[]>, Serializable
{
	private final MinMaxTransform mTransform = new MinMaxTransform();
	ArrayList<Double> mValueList = new ArrayList<Double>();
	private int mNumberOfBins = 20;

	public Histogram()
	{
	}

	public Histogram(final int lNumberOfBins)
	{
		super();
		mNumberOfBins = lNumberOfBins;
	}

	public int enter(double pValue)
	{
		mValueList.add(pValue);
		return mTransform.enter(pValue);
	}

	public int getCount()
	{
		return mTransform.getCount();
	}

	public int determineOptimalNumberOfBins()
	{
		mNumberOfBins = 2 *(int) Math.max(20, Math.sqrt(mTransform.getCount()));
		return mNumberOfBins;
	}

	public double[] getStatistic()
	{
		final double[] bins = new double[mNumberOfBins];

		for (double value : mValueList)
		{
			double tfvalue = mTransform.transform(value);
			int index = (int) Math.round(tfvalue * (mNumberOfBins - 1));
			bins[index]++;
		}

		final double total = mTransform.getCount();
		for (int i = 0; i < bins.length; i++)
		{
			if(total==0)
				bins[i]=0;
			else
			  bins[i] = bins[i] / total;
		}

		return bins;
	}

	public void reset()
	{
		mTransform.reset();
		mValueList.clear();
	}

	public int getNumberOfBins()
	{
		return mNumberOfBins;
	}

	public void setNumberOfBins(int pNumberOfBins)
	{
		mNumberOfBins = pNumberOfBins;
	}

	public double getWidth()
	{
		return 1 / ((double) mNumberOfBins);
	}

	public double getNormalizedWidth()
	{
		return mTransform.getWidth() / ((double) mNumberOfBins);
	}

	public double[] getMinMax()
	{
		return mTransform.getStatistic();
	}

	public double rightDensity(double[] pHistogramArray, double pX)
	{
		double[] lMinMax = getMinMax();
		double min = lMinMax[0];
		double max = lMinMax[1];
		
		if(pX<min)
			return 1;
		else if(pX>max)
			return 0;

		int index = (int) (mNumberOfBins * (pX - min) / (max - min));

		
		
		double lCumulativeDensity = 0;
		for (int i = index; i < pHistogramArray.length; i++)
		{
			lCumulativeDensity += pHistogramArray[i];
		}

		return lCumulativeDensity;
	}

}
