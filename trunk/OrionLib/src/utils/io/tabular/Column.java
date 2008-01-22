package utils.io.tabular;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class Column<O extends Comparable<O>>
{
	ArrayList<O> mList = new ArrayList<O>();
	ArrayList<Double> mPercentileList = new ArrayList<Double>();

	private final Class<O> mClass;

	public Column(Class<O> pClass)
	{
		mClass = pClass;
		// TODO Auto-generated constructor stub
	}

	public final Class<O> getColumnItemClass()
	{
		return mClass;
	}

	public final ArrayList<O> getList()
	{
		return mList;
	}

	public final ArrayList<Double> getNormalisedList()
	{
		return mPercentileList;
	}

	public final void normalise()
	{
		if (mClass == Double.class || mClass == Integer.class)
		{
			double[] lBucketArray = new double[256];

			double lMax = Double.NEGATIVE_INFINITY;
			double lMin = Double.POSITIVE_INFINITY;
			for (int i = 0; i < mList.size(); i++)
			{
				if (mClass == Integer.class)
				{
					lMax = Math.max(lMax, (Integer) mList.get(i));
					lMin = Math.min(lMin, (Integer) mList.get(i));
				}
				else
				{
					lMax = Math.max(lMax, (Double) mList.get(i));
					lMin = Math.min(lMin, (Double) mList.get(i));
				}
			}

			if (lMin < lMax)
			{

				for (int i = 0; i < mList.size(); i++)
				{
					double lValue;
					if (mClass == Integer.class)
					{
						lValue = (Integer) mList.get(i);
					}
					else
					{
						lValue = (Double) mList.get(i);
					}

					final double lNValue = (lValue - lMin) / (lMax - lMin);
					final int lIndex = (int) (lNValue * 255);
					lBucketArray[lIndex]++;
				}

				double lSum = 0;
				for (int i = 0; i < 256; i++)
				{
					lSum += lBucketArray[i];
					lBucketArray[i] = lSum;
				}
				for (int i = 0; i < 256; i++)
				{
					lBucketArray[i] /= lSum;
				}

				for (int i = 0; i < mList.size(); i++)
				{
					double lValue;
					if (mClass == Integer.class)
					{
						lValue = (Integer) mList.get(i);
					}
					else
					{
						lValue = (Double) mList.get(i);
					}
					final double lNValue = (lValue - lMin) / (lMax - lMin);
					final int lIndex = (int) (lNValue * 255);
					final double lPercentile = lBucketArray[lIndex];
					mPercentileList.add(lPercentile);
				}
			}
			else
			{
				for (int i = 0; i < mList.size(); i++)
				{
					mPercentileList.add(0.5);
				}
			}

		}
		else if (mClass == String.class)
		{
			TreeSet<String> lSet = new TreeSet<String>();
			for (Object lObject : mList)
			{
				lSet.add(lObject.toString());
				if (lSet.size() > 256)
					return;
			}

			ArrayList<String> lList = new ArrayList<String>(lSet);
			HashMap<String, Double> lMap = new HashMap<String, Double>();

			if (lSet.size() > 1)
			{
				final double lDelta = 1 / (((double) lSet.size()) - 1);
				double lPercentileValue = 0;
				for (String lItem : lList)
				{
					lMap.put(lItem, lPercentileValue);
					lPercentileValue += lDelta;
				}

				for (int i = 0; i < mList.size(); i++)
				{
					String lValue = (String) mList.get(i);
					double lPercentile = lMap.get(lValue);
					lPercentile = lPercentile>1 ? 1 : lPercentile;
					mPercentileList.add(lPercentile);
				}
			}

		}

	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mClass == null) ? 0 : mClass.hashCode());
		result = prime * result + ((mList == null) ? 0 : mList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Column other = (Column) obj;
		if (mClass == null)
		{
			if (other.mClass != null)
				return false;
		}
		else if (!mClass.equals(other.mClass))
			return false;
		if (mList == null)
		{
			if (other.mList != null)
				return false;
		}
		else if (!mList.equals(other.mList))
			return false;
		return true;
	}

}
