package org.royerloic.structures.utils;

import java.util.Collection;

public class CollectionUtils
{
	public static Double average(Collection<Double> pCollection)
	{
		double lAverage = 0;
		for (Double lDouble : pCollection)
		{
			lAverage += lDouble;
		}
		lAverage /= pCollection.size();

		return lAverage;
	}

	public static Double max(Collection<Double> pCollection)
	{
		double lMax = 0;
		for (Double lDouble : pCollection)
		{
			lMax = Math.max(lMax, lDouble);
		}

		return lMax;
	}

}
