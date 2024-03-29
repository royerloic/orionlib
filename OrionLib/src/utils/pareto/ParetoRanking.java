package utils.pareto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ParetoRanking<O>
{
	public class Vector
	{
		public O mObject;
		public double[] mValues;

		@Override
		public String toString()
		{
			return mObject.toString() + Arrays.toString(mValues);
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + (mObject == null ? 0 : mObject.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			final Vector other = (Vector) obj;
			if (mObject == null)
			{
				if (other.mObject != null)
				{
					return false;
				}
			}
			else if (!mObject.equals(other.mObject))
			{
				return false;
			}
			return true;
		}
	}

	HashMap<O, Vector> mVectorMap = new HashMap<O, Vector>();

	HashMap<Vector, Double> mRankingMap = new HashMap<Vector, Double>();

	public final void addVector(final O pObject, final double... pValues)
	{
		final Vector lVector = new Vector();
		lVector.mObject = pObject;
		lVector.mValues = pValues;

		mVectorMap.put(pObject, lVector);
	}

	public final void addVector(final O pObject, final List<Double> pValues)
	{
		final Vector lVector = new Vector();
		lVector.mObject = pObject;
		final double[] lValues = new double[pValues.size()];
		for (int i = 0; i < pValues.size(); i++)
		{
			lValues[i] = pValues.get(i);
		}
		lVector.mValues = lValues;

		mVectorMap.put(pObject, lVector);
	}

	public final int computeRanking()
	{
		return computeRanking(Integer.MAX_VALUE);
	}

	public final int computeRanking(final int pMaxLayer)
	{
		final HashSet<Vector> lWorkingSet = new HashSet<Vector>(mVectorMap.values());

		final HashSet<Vector> lParetoFront = new HashSet<Vector>();
		final HashSet<Vector> lParetoFrontDel = new HashSet<Vector>();
		final HashSet<Vector> lParetoFrontAdd = new HashSet<Vector>();

		double lLayer = 0;
		while (!lWorkingSet.isEmpty() && lLayer <= pMaxLayer)
		{
			for (final Vector lVector : lWorkingSet)
			{
				lParetoFrontDel.clear();
				lParetoFrontAdd.clear();
				for (final Vector lVectorFromFront : lParetoFront)
				{
					if (dominate(lVector.mValues, lVectorFromFront.mValues))
					{
						// new vector dominates vector in front
						lParetoFrontDel.add(lVectorFromFront);
					}
					else if (dominate(lVectorFromFront.mValues, lVector.mValues))
					{
						// new vector dominated by vector in front, do nothing
					}
					else
					{
						// new vector is not dominated nor does it domine, thus it is added
						// to front
						lParetoFrontAdd.add(lVector);
					}
				}

				// Either the new vector dominated another vector in this case we add
				// it, or there
				// is nothing yet in front and we also add it.
				if (lParetoFrontDel.size() > 0 || lParetoFront.size() == 0)
				{
					lParetoFrontAdd.add(lVector);
				}

				// we update the front:
				lParetoFront.removeAll(lParetoFrontDel);
				lParetoFront.addAll(lParetoFrontAdd);
			}

			for (final Vector lVector : lParetoFront)
			{
				mRankingMap.put(lVector, lLayer);
			}
			lWorkingSet.removeAll(lParetoFront);
			lParetoFront.clear();
			lLayer++;
		}

		return (int) lLayer;// number of layers
	}

	public final HashMap<Vector, Double> getRankings()
	{
		return mRankingMap;
	}

	public final double getRanking(final O pObject)
	{
		final Double lRank = mRankingMap.get(mVectorMap.get(pObject));
		return lRank == null ? Double.POSITIVE_INFINITY : lRank;
	}

	@Override
	public String toString()
	{
		return mRankingMap.toString();
	}

	public String toTabDel()
	{
		final StringBuilder lStringBuilder = new StringBuilder();
		for (final Map.Entry<Vector, Double> lEntry : mRankingMap.entrySet())
		{
			final Vector lVector = lEntry.getKey();
			final O lObject = lVector.mObject;
			final double[] lValues = lVector.mValues;
			final double lRank = lEntry.getValue();
			lStringBuilder.append(lObject);
			lStringBuilder.append("\t");
			lStringBuilder.append(lRank);
			lStringBuilder.append("\t");
			for (final double lValue : lValues)
			{
				lStringBuilder.append(lValue);
				lStringBuilder.append("\t");
			}
			lStringBuilder.append("\n");
		}
		return lStringBuilder.toString();
	}

	private static final boolean dominate(final double[] v1, final double[] v2)
	{
		assert v1.length == v2.length;

		if (equal(v1, v2))
		{
			return false;
		}

		for (int i = 0; i < v1.length; i++)
		{
			if (v1[i] < v2[i])
			{
				return false;
			}
		}
		return true;
	}

	private static final boolean equal(final double[] v1, final double[] v2)
	{
		assert v1.length == v2.length;

		for (int i = 0; i < v1.length; i++)
		{
			if (v1[i] != v2[i])
			{
				return false;
			}
		}

		return true;
	}

}
