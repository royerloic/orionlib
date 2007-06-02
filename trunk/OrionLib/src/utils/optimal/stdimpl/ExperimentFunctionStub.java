/*
 * Created on 04.01.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.optimal.stdimpl;

import utils.math.INumericalVector;
import utils.optimal.interf.IExperiment;
import utils.optimal.interf.IExperimentFunction;
import utils.optimal.interf.IExperimentFunctionStub;
import utils.optimal.interf.IOptimalEventListener;

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
		mExperimentFunction = pExperimentFunction;
	}

	/**
	 * @see utils.optimal.interf.IExperimentFunctionStub#setExperimentFunction(utils.optimal.interf.IExperimentFunction)
	 */
	public void setExperimentFunction(final IExperimentFunction pExperimentFunction)
	{
		mExperimentFunction = pExperimentFunction;
	}

	/**
	 * @see utils.optimal.interf.IExperimentFunctionStub#setOptimalEventListener(utils.optimal.interf.IOptimalEventListener)
	 */
	public void setOptimalEventListener(final IOptimalEventListener pTerminationListener)
	{
		mTerminationListener = pTerminationListener;
	}

	/**
	 * @see utils.optimal.interf.IExperimentFunctionStub#evaluate(utils.math.INumericalVector)
	 */
	public void evaluate(final INumericalVector pExperimentInputVector)
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
		final IExperiment lExperiment = new Experiment();

		lExperiment.begin();
		final INumericalVector lExperimentOutputVector = mExperimentFunction.evaluate(mExperimentInputVector);
		lExperiment.end();

		lExperiment.set(mExperimentInputVector, lExperimentOutputVector);

		mTerminationListener.experimentDone(this, lExperiment);

	}

}