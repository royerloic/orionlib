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

		this.mRewardThreshold = 0.01; // 1 percent better;
	}

	/**
	 * @see org.royerloic.optimal.interf.IDoeStrategy#setExperimentDatabase(org.royerloic.optimal.interf.IExperimentDatabase)
	 */
	public void setExperimentDatabase(final IExperimentDatabase pExperimentDatabase)
	{
		this.mExperimentDatabase = pExperimentDatabase;
	}

	/**
	 * @see org.royerloic.optimal.interf.IDoeStrategy#setObjectiveFunction(org.royerloic.optimal.interf.IObjectiveFunction)
	 */
	public void setObjectiveFunction(final IObjectiveFunction pObjectiveFunction)
	{
		this.mObjectiveFunction = pObjectiveFunction;
	}

	/**
	 * @see org.royerloic.optimal.interf.IDoeStrategy#setInterpolator(org.royerloic.optimal.interf.IInterpolator)
	 */
	public void setInterpolator(final IInterpolator pInterpolator)
	{
		this.mInterpolator = pInterpolator;
	}

	/**
	 * @see org.royerloic.optimal.interf.IDoeStrategy#designNewExperiment()
	 */
	public INumericalVector designNewExperiment()
	{
		if (this.mExperimentDatabase.getNumberOfExperiments() != 0)
		{
			/*************************************************************************
			 * INumericalVector lNewVector =
			 * DoeStrategyHelper.multiStepsStochmax(mInterpolator,4,3000);/
			 ************************************************************************/
			final INumericalVector lNewVector = DoeStrategyHelper.genetic(this.mInterpolator, 4, 3000);

			final double lEstimatedValue = this.mInterpolator.evaluate(lNewVector);
			final double lCurrentBestValue = getCurrentBestValue();
			final double lEstimatedReward = ((lEstimatedValue - lCurrentBestValue) / lCurrentBestValue);
			if (lEstimatedReward > this.mRewardThreshold)
				return lNewVector;
			else
				// lNewVector =
				// DoeStrategyHelper.genetic(getStochFillFunction(),4,3000);
				return lNewVector;

		}
		else
			return null;
	}

	private double getCurrentBestValue()
	{
		double lValue = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < this.mExperimentDatabase.getNumberOfExperiments(); i++)
		{
			final IExperiment lExperiment = this.mExperimentDatabase.getExperiment(i);
			lValue = Math.max(lValue, this.mObjectiveFunction.evaluate(lExperiment.getOutput()));
		}
		return lValue;
	}

}