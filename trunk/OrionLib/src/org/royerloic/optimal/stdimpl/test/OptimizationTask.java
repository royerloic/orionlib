/*
 * Created on 07.02.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.stdimpl.test;

import java.util.List;

import org.royerloic.optimal.Optimal;
import org.royerloic.optimal.gui.OptimalEngineJFrame;
import org.royerloic.optimal.interf.IExperiment;
import org.royerloic.optimal.interf.IExperimentDatabase;
import org.royerloic.optimal.interf.IExperimentFunction;
import org.royerloic.optimal.interf.IExperimentFunctionStub;
import org.royerloic.optimal.interf.IInterpolator;
import org.royerloic.optimal.interf.IObjectiveFunction;
import org.royerloic.optimal.interf.IOptimalEngine;
import org.royerloic.optimal.interf.IOptimalEventListener;
import org.royerloic.optimal.stdimpl.ExperimentDatabase;
import org.royerloic.optimal.stdimpl.ExperimentFunctionStub;
import org.royerloic.optimal.stdimpl.ObjectiveFunction;
import org.royerloic.optimal.stdimpl.UniformGrid;

import bsh.Interpreter;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
class OptimizationTask implements IOptimalEventListener
{
	private OptimalEngineJFrame	mOptimalEngineGui;

	private IOptimalEngine			mOptimalEngine;

	private IObjectiveFunction	mObjectiveFunction;

	private IInterpolator				mInterpolator;

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
													int pMaximumIterations)
	{
		super();
		mOptimalEngine = pOptimalEngine;
		mExperimentFunction = pExperimentFunction;
		mMaximumIterations = pMaximumIterations;
	}

	public void launchTest()
	{

		mDone = false;

		Interpreter lInterpreter = new Optimal();
		mOptimalEngine.setOptimalInterpreter(lInterpreter);

		IExperimentDatabase lExperimentdatabase = new ExperimentDatabase();
		mOptimalEngine.setExperimentDatabase(lExperimentdatabase);

		IExperimentFunctionStub lExperimentFunctionStub1 = new ExperimentFunctionStub(mExperimentFunction);
		mOptimalEngine.addExperimentFunctionStub(lExperimentFunctionStub1);

		mObjectiveFunction = new ObjectiveFunction("(y0+y1)/2");
		mOptimalEngine.setObjectiveFunction(mObjectiveFunction);

		UniformGrid lGridDefinition = new UniformGrid(mExperimentFunction.getInputDimension());
		lGridDefinition.setNumberOfDivisions(2);

		mOptimalEngine.setEventListener(this);

		if (mOptimalEngine.validateEngine())
		{
			mOptimalEngine.pushGriddedExperiments(lGridDefinition);
			mOptimalEngine.start();
		}
		else
		{
			System.out.print("Optimal Engine not validated.");
		}

	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEventListener#experimentDone(org.royerloic.optimal.interf.IExperimentFunctionStub,
	 *      org.royerloic.optimal.interf.IExperiment)
	 */
	public void experimentDone(IExperimentFunctionStub pExperimentFunctionStub, IExperiment pExperiment)
	{
		// System.out.print(".");
		if (mOptimalEngine.getIterations() >= mMaximumIterations + 1)
		{
			mOptimalEngine.stop();
			mResults = mOptimalEngine.getListOfBestExperimentValues();
			mPerformance = computePerformance(mResults);
			mMaxValue = ((Double) mResults.get(mResults.size() - 1)).doubleValue();
			if (mMaxValue != 0)
			{
				mSpeed = mPerformance / mMaxValue;
			}
			else
			{
				mSpeed = 0;
			}

			List lList = mOptimalEngine.getListOfBestExperiments();
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
		{
			lPerformance += ((Double) pValueList.get(i)).doubleValue();
		}
		lPerformance = lPerformance / pValueList.size();

		return lPerformance;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEventListener#newBestExperiment(org.royerloic.optimal.interf.IExperimentDatabase,
	 *      org.royerloic.optimal.interf.IExperiment)
	 */
	public void newBestExperiment(IExperimentDatabase pExperimentDatabase, IExperiment pExperiment)
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