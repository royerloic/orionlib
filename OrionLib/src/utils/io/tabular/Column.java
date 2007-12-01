package utils.io.tabular;

import java.util.ArrayList;
import java.util.Collections;

public class Column<O extends Comparable<O>>
{
	ArrayList<O> mList = new ArrayList<O>();
	ArrayList<Double> mPercentileList = new ArrayList<Double>();
	
	private final Class<O>	mClass;

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
		if(mClass==Double.class || mClass==Integer.class)
		{
			ArrayList<O> lSortedList = new ArrayList<O>(mList);
			Collections.<O>sort(lSortedList);
			
			final int lSize = lSortedList.size();
			for(int i=0; i< lSize; i++)
			{
				final double lPercentile = ((double)i)/((double)lSize); 
				mPercentileList.add(lPercentile);				
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
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final Column other = (Column) obj;
		if (mClass == null)
		{
			if (other.mClass != null) return false;
		}
		else if (!mClass.equals(other.mClass)) return false;
		if (mList == null)
		{
			if (other.mList != null) return false;
		}
		else if (!mList.equals(other.mList)) return false;
		return true;
	}


	
	
}
