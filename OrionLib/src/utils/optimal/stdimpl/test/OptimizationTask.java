/*
 * Created on 07.02.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.optimal.stdimpl.test;

import java.util.List;

import utils.optimal.Optimal;
import utils.optimal.interf.IExperiment;
import utils.optimal.interf.IExperimentDatabase;
import utils.optimal.interf.IExperimentFunction;
import utils.optimal.interf.IExperimentFunctionStub;
import utils.optimal.interf.IObjectiveFunction;
import utils.optimal.interf.IOptimalEngine;
import utils.optimal.interf.IOptimalEventListener;
import utils.optimal.stdimpl.ExperimentDatabase;
import utils.optimal.stdimpl.ExperimentFunctionStub;
import utils.optimal.stdimpl.ObjectiveFunction;
import utils.optimal.stdimpl.UniformGrid;
import bsh.Interpreter;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
class OptimizationTask implements IOptimalEventListener
{
	private IOptimalEngine			mOptimalEngine;

	private IObjectiveFunction	mObjectiveFunction;

	private IExperimentFunction	mExperimentFunction;

	private List								mResults;

	private double							mPerformance;

	private boolean							mDone;

	private int									mMaximumIterations;

	private double							mMaxValue;

	private double							mSpeed;

	private IExperiment					mBestExperiment;

	/**
	 * @param pExperimentFunction
	 * @param pMaximumIterations
	 */
	public OptimizationTask(final IOptimalEngine pOptimalEngine,
													final IExperimentFunction pExperimentFunction,
													final int pMaximumIterations)
	{
		super();
		mOptimalEngine = pOptimalEngine;
		mExperimentFunction = pExperimentFunction;
		mMaximumIterations = pMaximumIterations;
	}

	public void launchTest()
	{

		mDone = false;

		final Interpreter lInterpreter = new Optimal();
		mOptimalEngine.setOptimalInterpreter(lInterpreter);

		final IExperimentDatabase lExperimentdatabase = new ExperimentDatabase();
		mOptimalEngine.setExperimentDatabase(lExperimentdatabase);

		final IExperimentFunctionStub lExperimentFunctionStub1 = new ExperimentFunctionStub(mExperimentFunction);
		mOptimalEngine.addExperimentFunctionStub(lExperimentFunctionStub1);

		mObjectiveFunction = new ObjectiveFunction("(y0+y1)/2");
		mOptimalEngine.setObjectiveFunction(mObjectiveFunction);

		final UniformGrid lGridDefinition = new UniformGrid(mExperimentFunction.getInputDimension());
		lGridDefinition.setNumberOfDivisions(2);

		mOptimalEngine.setEventListener(this);

		if (mOptimalEngine.validateEngine())
		{
			mOptimalEngine.pushGriddedExperiments(lGridDefinition);
			mOptimalEngine.start();
		}
		else
			System.out.print("Optimal Engine not validated.");

	}

	/**
	 * @see utils.optimal.interf.IOptimalEventListener#experimentDone(utils.optimal.interf.IExperimentFunctionStub,
	 *      utils.optimal.interf.IExperiment)
	 */
	public void experimentDone(final IExperimentFunctionStub pExperimentFunctionStub, final IExperiment pExperiment)
	{
		// System.out.print(".");
		if (mOptimalEngine.getIterations() >= mMaximumIterations + 1)
		{
			mOptimalEngine.stop();
			mResults = mOptimalEngine.getListOfBestExperimentValues();
			mPerformance = computePerformance(mResults);
			mMaxValue = ((Double) mResults.get(mResults.size() - 1)).doubleValue();
			if (mMaxValue != 0)
				mSpeed = mPerformance / mMaxValue;
			else
				mSpeed = 0;

			final List lList = mOptimalEngine.getListOfBestExperiments();
			mBestExperiment = (IExperiment) lList.get(lList.size() - 1);

			/*************************************************************************
			 * System.out.println(""); System.out.println("Peformance = " +
			 * mPerformance);/
			 ************************************************************************/
			// System.out.println("Best value = " +
			// ((Double)mResults.get(mResults.size()-1)).doubleValue());
			mDone = true;

		}
	}

	public boolean isDone()
	{
		return mDone;
	}

	private double computePerformance(final List pValueList)
	{
		double lPerformance = 0;

		for (int i = 0; i < pValueList.size(); i++)
			lPerformance += ((Double) pValueList.get(i)).doubleValue();
		lPerformance = lPerformance / pValueList.size();

		return lPerformance;
	}

	/**
	 * @see utils.optimal.interf.IOptimalEventListener#newBestExperiment(utils.optimal.interf.IExperimentDatabase,
	 *      utils.optimal.interf.IExperiment)
	 */
	public void newBestExperiment(final IExperimentDatabase pExperimentDatabase, final IExperiment pExperiment)
	{
	}

	/**
	 * @return
	 */
	public double getPerformance()
	{
		return mPerformance;
	}

	/**
	 * @return
	 */
	public double getSpeed()
	{
		return mSpeed;
	}

	/**
	 * @return
	 */
	public double getMaxValue()
	{
		return mMaxValue;
	}

	/**
	 * @return
	 */
	public IExperiment getBestExperiment()
	{
		return mBestExperiment;
	}

}