package utils.math.statistics;

import java.util.ArrayList;

import utils.math.statistics.transform.MinMaxTransform;
import utils.math.statistics.transform.NormalizedZTransform;

public class Histogram implements Statistic<double[]>
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

}
