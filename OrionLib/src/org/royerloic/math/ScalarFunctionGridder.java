/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *   
 */

package org.royerloic.math;

import org.royerloic.math.stdimpl.NumericalVector;

/**
 * @author MSc. Ing. Loic Royer
 *  
 */
public class ScalarFunctionGridder
{
	private IScalarFunction mFunction;

	/**
	 * @param pFunction
	 */
	public ScalarFunctionGridder(final IScalarFunction pFunction)
	{
		super();
		mFunction = pFunction;

	}

	/**
	 * @param pResolution
	 * @return
	 */
	public final double[][] computeGridPoints(final int pResolution)
	{
		if (pResolution <= 0)
		{
			return new double[0][0];
		}
		int lDimension = mFunction.getInputDimension();
		int[] lIndex = new int[lDimension];
		int[] lRes = new int[lDimension];
		double[] lDelta = new double[lDimension];
		double[] lInputArray = new double[lDimension];

		int lTotal = 1;
		for (int i = 0; i < lDimension; i++)
		{
			lRes[i] = pResolution;
			lDelta[i] =
				(mFunction.getInputMax(i) - mFunction.getInputMin(i)) / lRes[i];
			lTotal = lTotal * (lRes[i] + 1);
			lIndex[i] = 0;
		}

		double[][] lComputedPoints = new double[lTotal][lDimension + 1];

		for (int i = 0; i < lTotal; i++)
		{
			for (int k = 0; k < lDimension; k++)
			{
				if (lIndex[k] > lRes[k])
				{
					lIndex[k] = 0;
					lIndex[k + 1]++;
				}
			}

			for (int j = 0; j < lDimension; j++)
			{
				lComputedPoints[i][j] =
					mFunction.getInputMin(j) + lIndex[j] * lDelta[j];
				lInputArray[j] = lComputedPoints[i][j];
			}

			INumericalVector lInput = new NumericalVector(lInputArray);

			lComputedPoints[i][lDimension] = mFunction.evaluate(lInput);

			lIndex[0]++;
		}

		return lComputedPoints;
	}

}
