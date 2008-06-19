package utils.math.statistics;

public class MinMax implements Statistic<double[]>
{

	int mCount = 0;
	double mMin = Double.POSITIVE_INFINITY;
	double mMax = Double.NEGATIVE_INFINITY;

	public void reset()
	{
		mCount = 0;
		mMin = Double.POSITIVE_INFINITY;
		mMax = Double.NEGATIVE_INFINITY;
	}

	public int enter(double pValue)
	{
		mCount++;
		mMin = Math.min(mMin, pValue);
		mMax = Math.max(mMax, pValue);
		return mCount;
	}

	public double[] getStatistic()
	{
		return new double[]
		{ mMin, mMax };
	}

	public int getCount()
	{
		return mCount;
	}

}
