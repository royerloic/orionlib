/*
 * Created on 07.01.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.stdimpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.royerloic.math.INumericalVector;
import org.royerloic.math.IScalarFunction;
import org.royerloic.math.stdimpl.NumericalVector;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class DoeStrategyHelper
{

	/**
	 * 
	 */
	private DoeStrategyHelper()
	{

	}

	static INumericalVector generateRandomVector(final int pDimension)
	{
		final INumericalVector lVector = new NumericalVector(pDimension);
		for (int i = 0; i < pDimension; i++)
		{
			final double mCoordinate = (Math.random() * 2.0) - 1.0;
			lVector.set(i, mCoordinate);
		}
		return lVector;
	}

	static INumericalVector stochmax(	final IScalarFunction pFunction,
																		final INumericalVector pCenter,
																		final double pRadius,
																		final int pIterations)
	{
		INumericalVector lBestVector = null;
		double lBestValue = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < pIterations; i++)
		{
			final INumericalVector lVector = generateRandomVector(pFunction.getInputDimension());
			lVector.timesEquals(pRadius);
			lVector.plusEquals(pCenter);
			final double lValue = pFunction.evaluate(lVector);

			if (lValue > lBestValue)
			{
				lBestValue = lValue;
				lBestVector = lVector;
			}
		}
		return lBestVector;
	}

	static INumericalVector multiStepsStochmax(	final IScalarFunction pFunction,
																							final int pSteps,
																							final int pIterations)
	{
		final int lDimension = pFunction.getInputDimension();
		INumericalVector lCenter = new NumericalVector(lDimension);
		for (int i = 0; i < lDimension; i++)
			lCenter.set(i, 0.5);
		final int lIterations = pIterations;
		double lRadius = 0.5;
		final double lFactor = 3 / Math.pow(lIterations, 1 / ((double) lDimension));
		// System.out.print("lFactor = " + lFactor);
		for (int lSteps = 1; lSteps <= pSteps; lSteps++)
		{
			lCenter = stochmax(pFunction, lCenter, lRadius, lIterations);

			for (int i = 0; i < lCenter.getDimension(); i++)
			{
				double lValue = lCenter.get(i);
				lValue = (lValue < 0 ? 0 : lValue);
				lValue = (lValue > 1 ? 1 : lValue);
				lCenter.set(i, lValue);
			}

			lRadius = lRadius * lFactor;
		}

		return lCenter;
	}

	static INumericalVector genetic(final IScalarFunction pFunction, final int pSteps, final int pSize)
	{
		final int lDimension = pFunction.getInputDimension();
		final INumericalVector lCenter = new NumericalVector(lDimension);
		for (int i = 0; i < lDimension; i++)
			lCenter.set(i, 0.5);
		double lRadius = 0.5;

		List lPopulation = new ArrayList();
		lPopulation.add(lCenter);

		lPopulation = geneticInternal(pFunction, lPopulation, lRadius, pSize);

		lRadius = 0.1;
		for (int i = 1; i < pSteps; i++)
			lPopulation = geneticInternal(pFunction, lPopulation, lRadius, pSize);

		INumericalVector lBestVector = null;
		double lBestValue = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < lPopulation.size(); i++)
		{
			final INumericalVector lVector = (INumericalVector) lPopulation.get(i);
			final double lValue = pFunction.evaluate(lVector);

			if (lValue > lBestValue)
			{
				lBestValue = lValue;
				lBestVector = lVector;
			}
		}
		return lBestVector;

	}

	static List geneticInternal(final IScalarFunction pFunction,
															final List pInitialPopulation,
															final double pRadius,
															final int pPopulationLimit)
	{
		final List lNewPopulation = new ArrayList();
		final int lNumberOfSonsPerParent = pPopulationLimit / pInitialPopulation.size();

		double lBestValue = Double.NEGATIVE_INFINITY;
		for (int lIndividual = 0; (lIndividual < pInitialPopulation.size()); lIndividual++)
		{
			final INumericalVector lCurrentIndividual = (INumericalVector) pInitialPopulation.get(lIndividual);
			for (int i = 0; i < lNumberOfSonsPerParent; i++)
			{
				final INumericalVector lSon = generateRandomVector(pFunction.getInputDimension());
				lSon.timesEquals(pRadius);
				lSon.plusEquals(lCurrentIndividual);

				for (int j = 0; j < lSon.getDimension(); j++)
				{
					double lValue = lSon.get(j);
					lValue = (lValue < 0 ? 0 : lValue);
					lValue = (lValue > 1 ? 1 : lValue);
					lSon.set(j, lValue);
				}

				final double lValue = pFunction.evaluate(lSon);
				if (lValue > lBestValue)
					lBestValue = lValue;
				lNewPopulation.add(lSon);
			}
		}

		for (final Iterator lIterator = lNewPopulation.iterator(); lIterator.hasNext();)
		{
			final INumericalVector lSon = (INumericalVector) lIterator.next();
			final double lValue = pFunction.evaluate(lSon);
			if ((lValue < lBestValue * 0.90) && (lNewPopulation.size() > 1))
				lIterator.remove();
		}

		return lNewPopulation;
	}

}