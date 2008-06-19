package utils.math.statistics;

public class StandardDeviation implements Statistic<Double>
{

	int mCount = 0;
	double mTotal = 0;
	double mTotalSquares = 0;
	
	public void reset()
	{
		mCount = 0;
		mTotal = 0;
		mTotalSquares = 0;
	}

	public int enter(double pValue)
	{
		mCount++;
		mTotal += pValue;
		mTotalSquares += pValue*pValue;			
		return mCount;
	}

	public Double getStatistic()
	{
		final double stddev = Math.sqrt(mCount*mTotalSquares-mTotal*mTotal)/mCount;
		return stddev;
	}

	public int getCount()
	{
		return mCount;
	}

}
