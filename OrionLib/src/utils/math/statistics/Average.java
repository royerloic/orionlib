package utils.math.statistics;

public class Average implements Statistic<Double>
{

	int mCount = 0;
	double mTotal = 0;

	public void reset()
	{
		mCount = 0;
		mTotal = 0;
	}

	public int enter(double pValue)
	{
		mCount++;
		mTotal += pValue;
		return mCount;
	}

	public Double getStatistic()
	{
		return mTotal/mCount;
	}

	public int getCount()
	{
		return mCount;
	}

}
