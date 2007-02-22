package org.royerloic.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

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
	public static final <O> List<O> randomSample(Random pRandom, int pNumberPicked, List<O> pList)
	{
		List<O> lList = new ArrayList<O>();
		for (int i = 0; i < pNumberPicked; i++)
		{
			final int lIndex = pRandom.nextInt(pList.size());
			final O lObject = pList.get(lIndex);
			lList.add(lObject);
		}
		return pList;
	}
	
	public static final <O> O randomElement(Random pRandom, List<O> pList)
	{
		final int lIndex = pRandom.nextInt(pList.size());
		final O lObject = pList.get(lIndex);
		return lObject;
	}
}
