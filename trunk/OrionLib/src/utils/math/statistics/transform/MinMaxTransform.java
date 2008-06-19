package utils.math.statistics.transform;

import utils.math.statistics.MinMax;
import utils.math.statistics.Statistic;

public class MinMaxTransform implements Statistic<double[]>, Transform
{

	MinMax mMinMax = new MinMax();

	public void reset()
	{
		mMinMax.reset();
	}

	public int enter(double pValue)
	{
		mMinMax.enter(pValue);
		return mMinMax.getCount();
	}

	public double[] getStatistic()
	{
		return mMinMax.getStatistic();
	}

	public int getCount()
	{
		return mMinMax.getCount();
	}

	public double transform(double pValue)
	{
		final double[] lMinMaxArray = mMinMax.getStatistic();
		final double mMin = lMinMaxArray[0];
		final double mMax = lMinMaxArray[1];
		final double lTransformedValue = (pValue-mMin)/(mMax-mMin);
		return lTransformedValue;
	}

}
