/*
 * Created on 07.01.2005
 * by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.optimal.stdimpl;

import java.util.ArrayList;
import java.util.List;

import utils.math.INumericalVector;
import utils.math.stdimpl.MathFunctions;
import utils.math.stdimpl.NumericalVector;
import utils.optimal.interf.IGridDefinition;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class UniformGrid implements IGridDefinition
{
	private double mRounding;
	private int mDimension;
	private int mNumberOfDivisions;
	private double mPerturbation;

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

	public void setNumberOfDivisions(final int pNumberOfDivisions)
	{
		mNumberOfDivisions = pNumberOfDivisions;
	}

	public void setPerturbation(final double pPerturbation)
	{
		mPerturbation = pPerturbation;
	}

	public void setRounding(final double pRounding)
	{
		mRounding = pRounding;
	}

	/**
	 * @see utils.optimal.interf.IGridDefinition#generateGrid()
	 */
	public List generateGrid()
	{
		final List lResult = new ArrayList();

		final double[] lDelta = new double[mDimension];
		for (int i = 0; i < mDimension; i++)
			lDelta[i] = 1.0 / (mNumberOfDivisions - 1);

		final int[] lIndex = new int[mDimension];
		int lTotal = 1;
		for (int i = 0; i < mDimension; i++)
		{
			lTotal = lTotal * mNumberOfDivisions;
			lIndex[i] = 0;
		}

		final double[] lInputVector = new double[mDimension];

		for (int i = 0; i < lTotal; i++)
		{
			for (int k = 0; k < mDimension; k++)
				if (lIndex[k] >= mNumberOfDivisions)
				{
					lIndex[k] = 0;
					lIndex[k + 1]++;
				}

			for (int k = 0; k < mDimension; k++)
			{
				double lVal = lIndex[k] * lDelta[k]
											+ ((Math.random() * 2) - 1)
											* (lDelta[k] / 2)
											* mPerturbation;

				lVal = (lVal < 0 ? 0 : lVal);
				lVal = (lVal > 1 ? 1 : lVal);

				final double lTruncated = MathFunctions.round(lVal, mRounding);

				lInputVector[k] = lTruncated;
			}

			final INumericalVector lInput = new NumericalVector(lInputVector);

			lResult.add(lInput);

			lIndex[0]++;

		}
		return lResult;
	}

}
