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
		this.mDimension = pDimension;
		this.mRounding = 0.0001;
		this.mNumberOfDivisions = 2;
		this.mPerturbation = 0.1;

	}

	public void setNumberOfDivisions(final int pNumberOfDivisions)
	{
		this.mNumberOfDivisions = pNumberOfDivisions;
	}

	public void setPerturbation(final double pPerturbation)
	{
		this.mPerturbation = pPerturbation;
	}

	public void setRounding(final double pRounding)
	{
		this.mRounding = pRounding;
	}

	/**
	 * @see org.royerloic.optimal.interf.IGridDefinition#generateGrid()
	 */
	public List generateGrid()
	{
		final List lResult = new ArrayList();

		final double[] lDelta = new double[this.mDimension];
		for (int i = 0; i < this.mDimension; i++)
			lDelta[i] = 1.0 / (this.mNumberOfDivisions - 1);

		final int[] lIndex = new int[this.mDimension];
		int lTotal = 1;
		for (int i = 0; i < this.mDimension; i++)
		{
			lTotal = lTotal * this.mNumberOfDivisions;
			lIndex[i] = 0;
		}

		final double[] lInputVector = new double[this.mDimension];

		for (int i = 0; i < lTotal; i++)
		{
			for (int k = 0; k < this.mDimension; k++)
				if (lIndex[k] >= this.mNumberOfDivisions)
				{
					lIndex[k] = 0;
					lIndex[k + 1]++;
				}

			for (int k = 0; k < this.mDimension; k++)
			{
				double lVal = lIndex[k] * lDelta[k] + ((Math.random() * 2) - 1) * (lDelta[k] / 2) * this.mPerturbation;

				lVal = (lVal < 0 ? 0 : lVal);
				lVal = (lVal > 1 ? 1 : lVal);

				final double lTruncated = MathFunctions.round(lVal, this.mRounding);

				lInputVector[k] = lTruncated;
			}

			final INumericalVector lInput = new NumericalVector(lInputVector);

			lResult.add(lInput);

			lIndex[0]++;

		}
		return lResult;
	}

}
