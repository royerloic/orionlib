package utils.random;

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

	public final void addObject(final O pObject, final Double pProbability)
	{
		this.mObjectToProbabilityMap.put(pObject, pProbability);
	}

	public final double prepare(final int pResolution, final double pDistorsionMax) throws Exception
	{
		if (this.mObjectToProbabilityMap.isEmpty())
			throw new RuntimeException("Must add object with probability in source");

		normalize();

		this.mHitList = new ArrayList<O>();

		for (final Entry<O, Double> lEntry : this.mObjectToProbabilityMap.entrySet())
		{
			final double lRegionSizeDouble = lEntry.getValue() * pResolution;
			int lRegionSize = (int) Math.round(lRegionSizeDouble);

			if (lRegionSize == 0)
				lRegionSize = 1;

			final O lValue = lEntry.getKey();

			for (int i = 0; i < lRegionSize; i++)
				this.mHitList.add(lValue);
		}

		double lMaxProbabilityDistorsion = Double.POSITIVE_INFINITY;
		if (pDistorsionMax < Double.POSITIVE_INFINITY)
		{
			final Map<O, Double> lNewMap = computeMapFromList(this.mHitList);
			lMaxProbabilityDistorsion = computeMaxDistorion(this.mObjectToProbabilityMap, lNewMap);
			// System.out.println("lMaxProbabilityDistorsion =
			// "+lMaxProbabilityDistorsion);
			if (lMaxProbabilityDistorsion >= pDistorsionMax)
				throw new Exception("probability Distribution Distorsion is higher that the specified limit: "
						+ lMaxProbabilityDistorsion + " >= " + pDistorsionMax);
		}

		this.mIsPrepared = true;
		return lMaxProbabilityDistorsion;
	}

	public O getObject(final Random pRandom)
	{
		if (!this.mIsPrepared)
			throw new RuntimeException("Distribution not prepared!! you must call prepare() !");

		final O lObject = RandomUtils.randomElement(pRandom, this.mHitList);

		return lObject;
	}

	@SuppressWarnings("boxing")
	private void normalize()
	{
		double lTotal = 0;
		for (final double lProbability : this.mObjectToProbabilityMap.values())
			lTotal += lProbability;

		final Map<O, Double> mNewObjectToProbabilityMap = new HashMap<O, Double>();
		for (final Entry<O, Double> lEntry : this.mObjectToProbabilityMap.entrySet())
		{
			final double lValue = lEntry.getValue() / lTotal;
			final O lKey = lEntry.getKey();
			mNewObjectToProbabilityMap.put(lKey, lValue);
		}
		this.mObjectToProbabilityMap.clear();
		this.mObjectToProbabilityMap = mNewObjectToProbabilityMap;

	}

	private Map<O, Double> computeMapFromList(final List<O> pHitList)
	{
		final Map<O, Double> mNewProbabilityToObjectMap = new HashMap<O, Double>();

		int lCount = 0;
		O lCurrentObject = pHitList.get(0);
		for (final O lO : pHitList)
			if (lCurrentObject == lO)
				lCount++;
			else
			{
				final Double lProbability = (double) lCount / (double) pHitList.size();
				mNewProbabilityToObjectMap.put(lCurrentObject, lProbability);
				lCount = 0;
				lCurrentObject = lO;
			}
		final Double lProbability = (double) lCount / (double) pHitList.size();
		mNewProbabilityToObjectMap.put(lCurrentObject, lProbability);

		return mNewProbabilityToObjectMap;
	}

	private double computeMaxDistorion(final Map<O, Double> pMap1, final Map<O, Double> pMap2)
	{
		double lMaxDistorsion = 0;

		final Set<O> lAllObjectSet = new HashSet<O>();
		lAllObjectSet.addAll(pMap1.keySet());
		lAllObjectSet.addAll(pMap2.keySet());

		for (final O lObject : lAllObjectSet)
		{
			final double lProbability1 = pMap1.get(lObject);
			final double lProbability2 = pMap2.get(lObject);
			final double lAbsoluteDifference = Math.abs(lProbability1 - lProbability2);
			lMaxDistorsion = Math.max(lMaxDistorsion, lAbsoluteDifference);
		}
		return lMaxDistorsion;
	}

}
