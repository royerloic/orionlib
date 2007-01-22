/*
 * Created on 07.01.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.stdimpl;

import org.royerloic.math.INumericalVector;
import org.royerloic.math.IScalarFunction;
import org.royerloic.optimal.interf.IDoeStrategy;
import org.royerloic.optimal.interf.IExperiment;
import org.royerloic.optimal.interf.IExperimentDatabase;
import org.royerloic.optimal.interf.IInterpolator;
import org.royerloic.optimal.interf.IObjectiveFunction;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class DoeStrategy implements IDoeStrategy
{
	IExperimentDatabase	mExperimentDatabase;

	IObjectiveFunction	mObjectiveFunction;

	IInterpolator				mInterpolator;

	double							mRewardThreshold;

	/**
	 * 
	 */
	public DoeStrategy()
	{
		super();

		mRewardThreshold = 0.01; // 1 percent better;
	}

	/**
	 * @see org.royerloic.optimal.interf.IDoeStrategy#setExperimentDatabase(org.royerloic.optimal.interf.IExperimentDatabase)
	 */
	public void setExperimentDatabase(IExperimentDatabase pExperimentDatabase)
	{
		mExperimentDatabase = pExperimentDatabase;
	}

	/**
	 * @see org.royerloic.optimal.interf.IDoeStrategy#setObjectiveFunction(org.royerloic.optimal.interf.IObjectiveFunction)
	 */
	public void setObjectiveFunction(IObjectiveFunction pObjectiveFunction)
	{
		mObjectiveFunction = pObjectiveFunction;
	}

	/**
	 * @see org.royerloic.optimal.interf.IDoeStrategy#setInterpolator(org.royerloic.optimal.interf.IInterpolator)
	 */
	public void setInterpolator(IInterpolator pInterpolator)
	{
		mInterpolator = pInterpolator;
	}

	/**
	 * @see org.royerloic.optimal.interf.IDoeStrategy#designNewExperiment()
	 */
	public INumericalVector designNewExperiment()
	{
		if (mExperimentDatabase.getNumberOfExperiments() != 0)
		{
			/*************************************************************************
			 * INumericalVector lNewVector =
			 * DoeStrategyHelper.multiStepsStochmax(mInterpolator,4,3000);/
			 ************************************************************************/
			INumericalVector lNewVector = DoeStrategyHelper.genetic(mInterpolator, 4, 3000);

			double lEstimatedValue = mInterpolator.evaluate(lNewVector);
			double lCurrentBestValue = getCurrentBestValue();
			double lEstimatedReward = ((lEstimatedValue - lCurrentBestValue) / lCurrentBestValue);
			if (lEstimatedReward > mRewardThreshold)
			{
				return lNewVector;
			}
			else
			{
				// lNewVector =
				// DoeStrategyHelper.genetic(getStochFillFunction(),4,3000);
				return lNewVector;
			}

		}
		else
		{
			return null;
		}
	}

	private double getCurrentBestValue()
	{
		double lValue = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < mExperimentDatabase.getNumberOfExperiments(); i++)
		{
			IExperiment lExperiment = mExperimentDatabase.getExperiment(i);
			lValue = Math.max(lValue, mObjectiveFunction.evaluate(lExperiment.getOutput()));
		}
		return lValue;
	}

	private IScalarFunction getStochFillFunction()
	{
		return new IScalarFunction()
		{

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputDimension()
			 */
			public final int getInputDimension()
			{
				return mInterpolator.getInputDimension();
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getOutputDimension()
			 */
			public final int getOutputDimension()
			{
				return mInterpolator.getOutputDimension();
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputMin(int)
			 */
			public final double getInputMin(final int pIndex)
			{
				return mInterpolator.getInputMin(pIndex);
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputMax(int)
			 */
			public final double getInputMax(final int pIndex)
			{
				return mInterpolator.getInputMax(pIndex);
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputDelta(int)
			 */
			public final double getInputDelta(final int pIndex)
			{
				return mInterpolator.getInputDelta(pIndex);
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#normalizeInputVector(de.fhg.iwu.utils.math.MVector)
			 */
			public final void normalizeInputVector(final INumericalVector pVector)
			{

			}

			/**
			 * @see de.fhg.iwu.utils.math.IScalarFunction#evaluate(de.fhg.iwu.utils.math.MVector)
			 */
			public final double evaluate(final INumericalVector pVector)
			{
				INumericalVector lNVector = mExperimentDatabase.getNeighboor(pVector).getInput();
				double lDistance = lNVector.euclideanDistanceTo(pVector);
				return lDistance;
			}

			/**
			 * @see de.fhg.iwu.utils.math.IScalarFunction#computePoints(int)
			 */
			public final double[][] computePoints(final int pResolution)
			{
				return null;
			}
		};
	}

}