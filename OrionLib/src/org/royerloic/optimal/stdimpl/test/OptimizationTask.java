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
		this.mOptimalEngine = pOptimalEngine;
		this.mExperimentFunction = pExperimentFunction;
		this.mMaximumIterations = pMaximumIterations;
	}

	public void launchTest()
	{

		this.mDone = false;

		final Interpreter lInterpreter = new Optimal();
		this.mOptimalEngine.setOptimalInterpreter(lInterpreter);

		final IExperimentDatabase lExperimentdatabase = new ExperimentDatabase();
		this.mOptimalEngine.setExperimentDatabase(lExperimentdatabase);

		final IExperimentFunctionStub lExperimentFunctionStub1 = new ExperimentFunctionStub(this.mExperimentFunction);
		this.mOptimalEngine.addExperimentFunctionStub(lExperimentFunctionStub1);

		this.mObjectiveFunction = new ObjectiveFunction("(y0+y1)/2");
		this.mOptimalEngine.setObjectiveFunction(this.mObjectiveFunction);

		final UniformGrid lGridDefinition = new UniformGrid(this.mExperimentFunction.getInputDimension());
		lGridDefinition.setNumberOfDivisions(2);

		this.mOptimalEngine.setEventListener(this);

		if (this.mOptimalEngine.validateEngine())
		{
			this.mOptimalEngine.pushGriddedExperiments(lGridDefinition);
			this.mOptimalEngine.start();
		}
		else
			System.out.print("Optimal Engine not validated.");

	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEventListener#experimentDone(org.royerloic.optimal.interf.IExperimentFunctionStub,
	 *      org.royerloic.optimal.interf.IExperiment)
	 */
	public void experimentDone(final IExperimentFunctionStub pExperimentFunctionStub, final IExperiment pExperiment)
	{
		// System.out.print(".");
		if (this.mOptimalEngine.getIterations() >= this.mMaximumIterations + 1)
		{
			this.mOptimalEngine.stop();
			this.mResults = this.mOptimalEngine.getListOfBestExperimentValues();
			this.mPerformance = computePerformance(this.mResults);
			this.mMaxValue = ((Double) this.mResults.get(this.mResults.size() - 1)).doubleValue();
			if (this.mMaxValue != 0)
				this.mSpeed = this.mPerformance / this.mMaxValue;
			else
				this.mSpeed = 0;

			final List lList = this.mOptimalEngine.getListOfBestExperiments();
			this.mBestExperiment = (IExperiment) lList.get(lList.size() - 1);

			/*************************************************************************
			 * System.out.println(""); System.out.println("Peformance = " +
			 * mPerformance);/
			 ************************************************************************/
			// System.out.println("Best value = " +
			// ((Double)mResults.get(mResults.size()-1)).doubleValue());
			this.mDone = true;

		}
	}

	public boolean isDone()
	{
		return this.mDone;
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
	 * @see org.royerloic.optimal.interf.IOptimalEventListener#newBestExperiment(org.royerloic.optimal.interf.IExperimentDatabase,
	 *      org.royerloic.optimal.interf.IExperiment)
	 */
	public void newBestExperiment(final IExperimentDatabase pExperimentDatabase, final IExperiment pExperiment)
	{
	}

	/**
	 * @return
	 */
	public double getPerformance()
	{
		return this.mPerformance;
	}

	/**
	 * @return
	 */
	public double getSpeed()
	{
		return this.mSpeed;
	}

	/**
	 * @return
	 */
	public double getMaxValue()
	{
		return this.mMaxValue;
	}

	/**
	 * @return
	 */
	public IExperiment getBestExperiment()
	{
		return this.mBestExperiment;
	}

}