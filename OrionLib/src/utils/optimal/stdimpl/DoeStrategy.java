/*
 * Created on 07.01.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.optimal.stdimpl;

import utils.math.INumericalVector;
import utils.optimal.interf.IDoeStrategy;
import utils.optimal.interf.IExperiment;
import utils.optimal.interf.IExperimentDatabase;
import utils.optimal.interf.IInterpolator;
import utils.optimal.interf.IObjectiveFunction;

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
	 * @see utils.optimal.interf.IDoeStrategy#setExperimentDatabase(utils.optimal.interf.IExperimentDatabase)
	 */
	public void setExperimentDatabase(final IExperimentDatabase pExperimentDatabase)
	{
		mExperimentDatabase = pExperimentDatabase;
	}

	/**
	 * @see utils.optimal.interf.IDoeStrategy#setObjectiveFunction(utils.optimal.interf.IObjectiveFunction)
	 */
	public void setObjectiveFunction(final IObjectiveFunction pObjectiveFunction)
	{
		mObjectiveFunction = pObjectiveFunction;
	}

	/**
	 * @see utils.optimal.interf.IDoeStrategy#setInterpolator(utils.optimal.interf.IInterpolator)
	 */
	public void setInterpolator(final IInterpolator pInterpolator)
	{
		mInterpolator = pInterpolator;
	}

	/**
	 * @see utils.optimal.interf.IDoeStrategy#designNewExperiment()
	 */
	public INumericalVector designNewExperiment()
	{
		if (mExperimentDatabase.getNumberOfExperiments() != 0)
		{
			/*************************************************************************
			 * INumericalVector lNewVector =
			 * DoeStrategyHelper.multiStepsStochmax(mInterpolator,4,3000);/
			 ************************************************************************/
			final INumericalVector lNewVector = DoeStrategyHelper.genetic(mInterpolator, 4, 3000);

			final double lEstimatedValue = mInterpolator.evaluate(lNewVector);
			final double lCurrentBestValue = getCurrentBestValue();
			final double lEstimatedReward = ((lEstimatedValue - lCurrentBestValue) / lCurrentBestValue);
			if (lEstimatedReward > mRewardThreshold)
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

		for (int i = 0; i < mExperimentDatabase.getNumberOfExperiments(); i++)
		{
			final IExperiment lExperiment = mExperimentDatabase.getExperiment(i);
			lValue = Math.max(lValue, mObjectiveFunction.evaluate(lExperiment.getOutput()));
		}
		return lValue;
	}

}