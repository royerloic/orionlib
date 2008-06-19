package utils.math.statistics.transform;

import utils.math.statistics.Average;
import utils.math.statistics.StandardDeviation;
import utils.math.statistics.Statistic;

public class ZTransform implements Statistic<double[]>, Transform
{

	Average mAverage = new Average();
	StandardDeviation mStandardDeviation = new StandardDeviation();

	public void reset()
	{
		mAverage.reset();
		mStandardDeviation.reset();
	}

	public int enter(double pValue)
	{
		mAverage.enter(pValue);
		mStandardDeviation.enter(pValue);

		return mAverage.getCount();
	}

	public double[] getStatistic()
	{
		return new double[]
		{ mAverage.getStatistic(), mStandardDeviation.getStatistic() };
	}

	public int getCount()
	{
		return mAverage.getCount();
	}

	public double transform(double pValue)
	{
		final double lAverage = mAverage.getStatistic();
		final double lStandardDeviation = mStandardDeviation.getStatistic();
		final double lZScore = (pValue - lAverage) / lStandardDeviation;
		return lZScore;
	}

}
