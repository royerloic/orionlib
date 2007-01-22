/*
 * Created on 04.01.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.stdimpl;

import org.royerloic.math.INumericalVector;
import org.royerloic.optimal.interf.IExperiment;
import org.royerloic.optimal.interf.IExperimentFunction;
import org.royerloic.optimal.interf.IExperimentFunctionStub;
import org.royerloic.optimal.interf.IOptimalEventListener;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class ExperimentFunctionStub implements IExperimentFunctionStub, Runnable
{

	private INumericalVector			mExperimentInputVector;
	private IExperimentFunction		mExperimentFunction;
	private IOptimalEventListener	mTerminationListener;

	private Thread								mThread;

	/**
	 * @param pExperimentFunction
	 */
	public ExperimentFunctionStub(IExperimentFunction pExperimentFunction)
	{
		super();
		mExperimentFunction = pExperimentFunction;
	}

	/**
	 * @see org.royerloic.optimal.interf.IExperimentFunctionStub#setExperimentFunction(org.royerloic.optimal.interf.IExperimentFunction)
	 */
	public void setExperimentFunction(IExperimentFunction pExperimentFunction)
	{
		mExperimentFunction = pExperimentFunction;
	}

	/**
	 * @see org.royerloic.optimal.interf.IExperimentFunctionStub#setOptimalEventListener(org.royerloic.optimal.interf.IOptimalEventListener)
	 */
	public void setOptimalEventListener(IOptimalEventListener pTerminationListener)
	{
		mTerminationListener = pTerminationListener;
	}

	/**
	 * @see org.royerloic.optimal.interf.IExperimentFunctionStub#evaluate(org.royerloic.math.INumericalVector)
	 */
	public void evaluate(INumericalVector pExperimentInputVector)
	{
		mExperimentInputVector = pExperimentInputVector;
		mThread = new Thread(this);
		mThread.start();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		IExperiment lExperiment = new Experiment();

		lExperiment.begin();
		INumericalVector lExperimentOutputVector = mExperimentFunction.evaluate(mExperimentInputVector);
		lExperiment.end();

		lExperiment.set(mExperimentInputVector, lExperimentOutputVector);

		mTerminationListener.experimentDone(this, lExperiment);

	}

}