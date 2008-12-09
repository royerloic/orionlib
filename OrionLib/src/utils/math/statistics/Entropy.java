package utils.math.statistics;

import java.io.Serializable;

public class Entropy implements Serializable, Statistic<Double>
{
	private static final long serialVersionUID = 1L;

	Distinct mDistinct = new Distinct();
	Histogram mHistogram = new Histogram();
	private double[] mStatistic;

	public int enter(double pValue)
	{
		mDistinct.enter(pValue);
		return mHistogram.enter(pValue);
	}

	public int getCount()
	{
		return mHistogram.getCount();
	}

	public Double getStatistic()
	{
		mHistogram.setNumberOfBins(mDistinct.getStatistic());
		mStatistic = mHistogram.getStatistic();

		double entropy = 0;
		for (double prob : mStatistic)
			if (prob > 0)
			{
				entropy += prob * Math.log10(prob) / Math.log10(2);
			}
		entropy = -entropy;

		return entropy;
	}

	public double[] getHistogram()
	{
		return mStatistic;
	}

	public void reset()
	{
		mDistinct.reset();
		mHistogram.reset();
		mStatistic = null;
	}

}
