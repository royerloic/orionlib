package utils.math.statistics.transform;

import utils.math.statistics.Statistic;

public class NormalizedZTransform implements Statistic<double[]>, Transform
{

	ZTransform mZTransform = new ZTransform();

	public void reset()
	{
		mZTransform.reset();
	}

	public int enter(double pValue)
	{
		mZTransform.enter(pValue);

		return mZTransform.getCount();
	}

	public double[] getStatistic()
	{
		return mZTransform.getStatistic();
	}

	public int getCount()
	{
		return mZTransform.getCount();
	}

	public double transform(double pValue)
	{
		final double lZScore = mZTransform.transform(pValue);
		final double lNormalizedZScore = Math.tanh(lZScore);
		return lNormalizedZScore;
	}
}
