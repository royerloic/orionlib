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
	public ExperimentFunctionStub(final IExperimentFunction pExperimentFunction)
	{
		super();
		this.mExperimentFunction = pExperimentFunction;
	}

	/**
	 * @see org.royerloic.optimal.interf.IExperimentFunctionStub#setExperimentFunction(org.royerloic.optimal.interf.IExperimentFunction)
	 */
	public void setExperimentFunction(final IExperimentFunction pExperimentFunction)
	{
		this.mExperimentFunction = pExperimentFunction;
	}

	/**
	 * @see org.royerloic.optimal.interf.IExperimentFunctionStub#setOptimalEventListener(org.royerloic.optimal.interf.IOptimalEventListener)
	 */
	public void setOptimalEventListener(final IOptimalEventListener pTerminationListener)
	{
		this.mTerminationListener = pTerminationListener;
	}

	/**
	 * @see org.royerloic.optimal.interf.IExperimentFunctionStub#evaluate(org.royerloic.math.INumericalVector)
	 */
	public void evaluate(final INumericalVector pExperimentInputVector)
	{
		this.mExperimentInputVector = pExperimentInputVector;
		this.mThread = new Thread(this);
		this.mThread.start();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		final IExperiment lExperiment = new Experiment();

		lExperiment.begin();
		final INumericalVector lExperimentOutputVector = this.mExperimentFunction.evaluate(this.mExperimentInputVector);
		lExperiment.end();

		lExperiment.set(this.mExperimentInputVector, lExperimentOutputVector);

		this.mTerminationListener.experimentDone(this, lExperiment);

	}

}