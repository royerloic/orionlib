package utils.structures.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RingList<O>
{
	List<O> mList = Collections.emptyList();

	int mCursor = 0;

	public RingList(List<O> pList)
	{
		super();
		mList = pList;
	}

	public RingList(O[] pValues)
	{
		super();
		mList = new ArrayList<O>();
		for (O lO : pValues)
		{
			mList.add(lO);
		}
	}

	public RingList()
	{
	}

	public void setList(List<O> pList)
	{
		mList = pList;
	}

	public void resetCursor()
	{
		mCursor = 0;
	}

	public O next()
	{
		if (!mList.isEmpty())
		{
			mCursor = (mCursor + 1) % mList.size();
			return mList.get(mCursor);
		}
		else
		{
			return null;
		}
	}

	public O previous()
	{
		if (!mList.isEmpty())
		{
			if (mCursor == 0)
				mCursor = mList.size() - 1;
			else
				mCursor--;

			return mList.get(mCursor);
		}
		else
		{
			return null;
		}
	}

	public O get()
	{
		if (!mList.isEmpty())
		{
			return mList.get(mCursor);
		}
		else
		{
			return null;
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + mCursor;
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
		final RingList other = (RingList) obj;
		if (mCursor != other.mCursor)
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

	@Override
	public String toString()
	{
		O lO = get();
		if (lO != null)
		{
			return get().toString();
		}
		else
		{
			return null;
		}
	}

}
