package org.royerloic.random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

public class DistributionSource<O>
{
	private Map<O, Double>	mObjectToProbabilityMap	= new HashMap<O, Double>();
	private List<O>					mHitList;
	private boolean					mIsPrepared							= false;

	public DistributionSource()
	{
		super();
	}

	public final void addObject(final O pObject, Double pProbability)
	{
		mObjectToProbabilityMap.put(pObject, pProbability);
	}

	public final double prepare(final int pResolution, final double pDistorsionMax) throws Exception
	{
		if (mObjectToProbabilityMap.isEmpty())
			throw new RuntimeException("Must add object with probability in source");

		normalize();

		mHitList = new ArrayList<O>();

		for (Entry<O, Double> lEntry : mObjectToProbabilityMap.entrySet())
		{
			double lRegionSizeDouble = lEntry.getValue() * pResolution;
			int lRegionSize = (int) Math.round(lRegionSizeDouble);

			if (lRegionSize == 0)
			{
				lRegionSize = 1;
			}

			final O lValue = lEntry.getKey();

			for (int i = 0; i < lRegionSize; i++)
				mHitList.add(lValue);
		}

		double lMaxProbabilityDistorsion = Double.POSITIVE_INFINITY;
		if (pDistorsionMax < Double.POSITIVE_INFINITY)
		{
			Map<O, Double> lNewMap = computeMapFromList(mHitList);
			lMaxProbabilityDistorsion = computeMaxDistorion(mObjectToProbabilityMap, lNewMap);
			// System.out.println("lMaxProbabilityDistorsion =
			// "+lMaxProbabilityDistorsion);
			if (lMaxProbabilityDistorsion >= pDistorsionMax)
				throw new Exception("probability Distribution Distorsion is higher that the specified limit: "
						+ lMaxProbabilityDistorsion + " >= " + pDistorsionMax);
		}

		mIsPrepared = true;
		return lMaxProbabilityDistorsion;
	}

	public O getObject(final Random pRandom)
	{
		if (!mIsPrepared)
			throw new RuntimeException("Distribution not prepared!! you must call prepare() !");

		O lObject = RandomUtils.randomElement(pRandom, mHitList);

		return lObject;
	}

	@SuppressWarnings("boxing")
	private void normalize()
	{
		double lTotal = 0;
		for (double lProbability : mObjectToProbabilityMap.values())
		{
			lTotal += lProbability;
		}

		final Map<O, Double> mNewObjectToProbabilityMap = new HashMap<O, Double>();
		for (Entry<O, Double> lEntry : mObjectToProbabilityMap.entrySet())
		{
			final double lValue = lEntry.getValue() / lTotal;
			final O lKey = lEntry.getKey();
			mNewObjectToProbabilityMap.put(lKey, lValue);
		}
		mObjectToProbabilityMap.clear();
		mObjectToProbabilityMap = mNewObjectToProbabilityMap;

	}

	private Map<O, Double> computeMapFromList(List<O> pHitList)
	{
		final Map<O, Double> mNewProbabilityToObjectMap = new HashMap<O, Double>();

		int lCount = 0;
		O lCurrentObject = pHitList.get(0);
		for (O lO : pHitList)
		{
			if (lCurrentObject == lO)
			{
				lCount++;
			}
			else
			{
				final Double lProbability = (double) lCount / (double) pHitList.size();
				mNewProbabilityToObjectMap.put(lCurrentObject, lProbability);
				lCount = 0;
				lCurrentObject = lO;
			}
		}
		final Double lProbability = (double) lCount / (double) pHitList.size();
		mNewProbabilityToObjectMap.put(lCurrentObject, lProbability);

		return mNewProbabilityToObjectMap;
	}

	private double computeMaxDistorion(Map<O, Double> pMap1, Map<O, Double> pMap2)
	{
		double lMaxDistorsion = 0;

		Set<O> lAllObjectSet = new HashSet<O>();
		lAllObjectSet.addAll(pMap1.keySet());
		lAllObjectSet.addAll(pMap2.keySet());

		for (O lObject : lAllObjectSet)
		{
			final double lProbability1 = pMap1.get(lObject);
			final double lProbability2 = pMap2.get(lObject);
			final double lAbsoluteDifference = Math.abs(lProbability1 - lProbability2);
			lMaxDistorsion = Math.max(lMaxDistorsion, lAbsoluteDifference);
		}
		return lMaxDistorsion;
	}

}
