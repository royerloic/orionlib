package utils.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class RandomUtils
{
	/**
	 * @param <O>
	 * @param pRandom
	 * @param pNumberPicked
	 * @param pList
	 * @return
	 */
	public static final <O> List<O> randomSample(final Random pRandom, final double pProbability, final Iterable<O> pIterable)
	{
		final List<O> lList = new ArrayList<O>();
		for (final O lO : pIterable)
		{
			final double lRND = pRandom.nextDouble();
			if(lRND<pProbability)
				lList.add(lO);			
		}
		return lList;
	}
	
	public static final <O> O randomElement(final Random pRandom, final List<O> pList)
	{
		final int lIndex = pRandom.nextInt(pList.size());
		final O lObject = pList.get(lIndex);
		return lObject;
	}

	public static final int doubleToInteger(final Random pRandom, final double pExpectedAverage)
	{
		int lFloor = (int)pExpectedAverage;
		final double lRemainder = pExpectedAverage-lFloor;
		if(pRandom.nextDouble()<lRemainder)
			lFloor+=1;
		return lFloor;
	}
	
	public static final long longGaussian(final Random pRandom, final double pExpectedAverage, final double pStandardDeviation)
	{
		final long lGaussian = Math.round(pExpectedAverage+pStandardDeviation*pRandom.nextGaussian());
		return lGaussian;
	}

	public static final double DoubleWithin(final Random pRandom, final double pBegin, final int pEnd)
	{
		return pBegin+(pRandom.nextDouble()*(pEnd-pBegin));
	}
	
}
