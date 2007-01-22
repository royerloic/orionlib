/*
 * Created on 07.01.2005
 * by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.stdimpl;

import java.util.ArrayList;
import java.util.List;

import org.royerloic.math.INumericalVector;
import org.royerloic.math.stdimpl.MathFunctions;
import org.royerloic.math.stdimpl.NumericalVector;
import org.royerloic.optimal.interf.IGridDefinition;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class UniformGrid implements IGridDefinition
{
	private double	mRounding;
	private int			mDimension;
	private int			mNumberOfDivisions;
	private double	mPerturbation;

	/**
	 * 
	 */
	public UniformGrid(final int pDimension)
	{
		super();
		mDimension = pDimension;
		mRounding = 0.0001;
		mNumberOfDivisions = 2;
		mPerturbation = 0.1;

	}

	public void setNumberOfDivisions(int pNumberOfDivisions)
	{
		mNumberOfDivisions = pNumberOfDivisions;
	}

	public void setPerturbation(double pPerturbation)
	{
		mPerturbation = pPerturbation;
	}

	public void setRounding(double pRounding)
	{
		mRounding = pRounding;
	}

	/**
	 * @see org.royerloic.optimal.interf.IGridDefinition#generateGrid()
	 */
	public List generateGrid()
	{
		List lResult = new ArrayList();

		double[] lDelta = new double[mDimension];
		for (int i = 0; i < mDimension; i++)
		{
			lDelta[i] = 1.0 / (double) (mNumberOfDivisions - 1);
		}

		int[] lIndex = new int[mDimension];
		int lTotal = 1;
		for (int i = 0; i < mDimension; i++)
		{
			lTotal = lTotal * mNumberOfDivisions;
			lIndex[i] = 0;
		}

		double[] lInputVector = new double[mDimension];

		for (int i = 0; i < lTotal; i++)
		{
			for (int k = 0; k < mDimension; k++)
			{
				if (lIndex[k] >= mNumberOfDivisions)
				{
					lIndex[k] = 0;
					lIndex[k + 1]++;
				}
			}

			for (int k = 0; k < mDimension; k++)
			{
				double lVal = lIndex[k] * lDelta[k] + ((Math.random() * 2) - 1) * (lDelta[k] / 2) * mPerturbation;

				lVal = (lVal < 0 ? 0 : lVal);
				lVal = (lVal > 1 ? 1 : lVal);

				double lTruncated = MathFunctions.round(lVal, mRounding);

				lInputVector[k] = lTruncated;
			}

			INumericalVector lInput = new NumericalVector(lInputVector);

			lResult.add(lInput);

			lIndex[0]++;

		}
		return lResult;
	}

}
