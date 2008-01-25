package utils.utils;

import java.util.Collection;

public class CollectionUtils
{
	public static Double average(final Collection<Double> pCollection)
	{
		double lAverage = 0;
		for (final Double lDouble : pCollection)
		{
			lAverage += lDouble;
		}
		lAverage /= pCollection.size();

		return lAverage;
	}

	public static Double max(final Collection<Double> pCollection)
	{
		double lMax = 0;
		for (final Double lDouble : pCollection)
		{
			lMax = Math.max(lMax, lDouble);
		}

		return lMax;
	}

}
