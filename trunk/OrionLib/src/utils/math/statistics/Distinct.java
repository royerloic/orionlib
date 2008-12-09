package utils.math.statistics;

import java.io.Serializable;
import java.util.HashSet;

public class Distinct implements Statistic<Integer>, Serializable
{
	private static final long serialVersionUID = 1L;

	int mCount=0;
	HashSet<Double> mValueSet = new HashSet<Double>();

	public void reset()
	{
		mCount=0;
		mValueSet.clear();
	}

	public int enter(double pValue)
	{
		mValueSet.add(pValue);
		return mCount;
	}

	public Integer getStatistic()
	{
		return mValueSet.size();
	}

	public int getCount()
	{
		return mCount;
	}

}
