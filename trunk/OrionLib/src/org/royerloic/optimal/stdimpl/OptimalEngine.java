/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package org.royerloic.optimal.stdimpl;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.royerloic.math.INumericalVector;
import org.royerloic.optimal.interf.IDoeStrategy;
import org.royerloic.optimal.interf.IExperiment;
import org.royerloic.optimal.interf.IExperimentDatabase;
import org.royerloic.optimal.interf.IExperimentDatabaseStore;
import org.royerloic.optimal.interf.IExperimentFunctionStub;
import org.royerloic.optimal.interf.IGridDefinition;
import org.royerloic.optimal.interf.IInterpolator;
import org.royerloic.optimal.interf.IObjectiveFunction;
import org.royerloic.optimal.interf.IOptimalEngine;
import org.royerloic.optimal.interf.IOptimalEventListener;

import bsh.Interpreter;

/**
 * 
 * 
 * @author MSc. Ing. Loic Royer
 */
public class OptimalEngine implements IOptimalEngine, IOptimalEventListener
{

	static Logger									mLogger	= Logger.getLogger(OptimalEngine.class);

	private Interpreter						mInterpreter;

	private IExperimentDatabase		mExperimentDatabase;

	private IObjectiveFunction		mObjectiveFunction;

	private IInterpolator					mInterpolator;

	private IDoeStrategy					mDoeStrategy;

	private IOptimalEventListener	mOptimalEventListener;

	private boolean								mEngineValidated;

	private boolean								mFeedExperimentFunctions;

	List													mExperimentFunctionStubsList;

	List													mExperimentQueue;

	/**
	 * 
	 */
	public OptimalEngine()
	{
		super();

		mExperimentFunctionStubsList = new ArrayList();
		mExperimentQueue = new ArrayList();

		mEngineValidated = false;
		HTMLLayout layout = new HTMLLayout();

		WriterAppender appender = null;
		try
		{
			SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
			Date lDate = new Date();
			FileOutputStream output = new FileOutputStream("Optimal." + mDateFormat.format(lDate) + ".log.html");
			appender = new WriterAppender(layout, output);
			mLogger.addAppender(appender);
		}
		catch (Exception e)
		{
			System.err.println("Could not create log file");
			System.err.println(e.toString());
		}
		mLogger.setLevel((Level) Level.INFO);
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#setOptimalInterpreter(bsh.Interpreter)
	 */
	public void setOptimalInterpreter(Interpreter pInterpreter)
	{
		mLogger.info("call to setOptimalInterpreter(" + pInterpreter + ")");
		mInterpreter = pInterpreter;
	}

	public Interpreter getOptimalInterpreter()
	{
		return mInterpreter;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#setExperimentDatabase(org.royerloic.optimal.interf.IExperimentDatabase)
	 */
	public void setExperimentDatabase(IExperimentDatabase pExperimentDatabase)
	{
		mLogger.info("call to setExperimentDatabase(" + pExperimentDatabase + ")");
		mExperimentDatabase = pExperimentDatabase;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#setObjectiveFunction(org.royerloic.optimal.interf.IObjectiveFunction)
	 */
	public void setObjectiveFunction(final IObjectiveFunction pObjectiveFunction)
	{
		mLogger.info("call to setObjectiveFunction(" + pObjectiveFunction + ")");
		mObjectiveFunction = pObjectiveFunction;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#setInterpolator(org.royerloic.optimal.interf.IInterpolator)
	 */
	public void setInterpolator(final IInterpolator pInterpolator)
	{
		mLogger.info("call to setInterpolator(" + pInterpolator + ")");
		mInterpolator = pInterpolator;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#setDOEStrategy(org.royerloic.optimal.interf.IDoeStrategy)
	 */
	public void setDoeStrategy(final IDoeStrategy pDoeStrategy)
	{
		mLogger.info("call to setDoeStrategy(" + pDoeStrategy + ")");
		mDoeStrategy = pDoeStrategy;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#validateEngine()
	 */
	public boolean validateEngine()
	{
		boolean lValidated = true;

		lValidated &= mInterpreter != null;
		lValidated &= mExperimentDatabase != null;
		lValidated &= mObjectiveFunction != null;
		lValidated &= mInterpolator != null;
		lValidated &= mDoeStrategy != null;
		lValidated &= (mExperimentFunctionStubsList.size() >= 1);

		if (lValidated)
		{
			mLogger.info("OptimalEngine validated.");
			mExperimentDatabase.setObjectiveFunction(mObjectiveFunction);

			mObjectiveFunction.setExperimentDatabase(mExperimentDatabase);
			mObjectiveFunction.setInterpreter(mInterpreter);

			mInterpolator.setObjectiveFunction(mObjectiveFunction);
			mInterpolator.setExperimentDatabase(mExperimentDatabase);

			mDoeStrategy.setExperimentDatabase(mExperimentDatabase);
			mDoeStrategy.setObjectiveFunction(mObjectiveFunction);
			mDoeStrategy.setInterpolator(mInterpolator);
		}
		else
		{
			mLogger.error("Optimal Engine could not be validated");
		}

		mEngineValidated = lValidated;

		return lValidated;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#loadExperimentDatabase(java.io.File)
	 */
	public void loadExperimentDatabase(final IExperimentDatabaseStore pStore)
	{
		if (mEngineValidated)
		{
			mLogger.info("call to loadExperimentDatabase(" + pStore + ")");

			pStore.setExperimentDatabase(mExperimentDatabase);
			pStore.setObjectiveFunction(mObjectiveFunction);
			try
			{
				pStore.load();
			}
			catch (RuntimeException e)
			{
				mLogger.error("exception in loadExperimentDatabase.", e);
				e.printStackTrace();
			}
		}
		else
		{
			mLogger.error("call to loadExperimentDatabase() from a non validated OptimalEngine.");
		}
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#saveExperimentDatabase(java.io.File)
	 */
	public void saveExperimentDatabase(final IExperimentDatabaseStore pStore)
	{
		if (mEngineValidated)
		{
			mLogger.info("call to saveExperimentDatabase: " + pStore);

			pStore.setExperimentDatabase(mExperimentDatabase);
			pStore.setObjectiveFunction(mObjectiveFunction);
			try
			{
				pStore.save();
			}
			catch (RuntimeException e)
			{
				mLogger.error("exception in saveExperimentDatabase().", e);
				e.printStackTrace();
			}
		}
		else
		{
			mLogger.error("call to saveExperimentDatabase() from a non validated OptimalEngine.");
		}
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#addExperimentFunctionStub(org.royerloic.optimal.interf.IExperimentFunctionStub)
	 */
	public void addExperimentFunctionStub(IExperimentFunctionStub pExperimentFunctionStub)
	{
		mLogger.info("adding a IExperimentFunctionStub to the OptimalEngine: " + pExperimentFunctionStub);
		mExperimentFunctionStubsList.add(pExperimentFunctionStub);
		pExperimentFunctionStub.setOptimalEventListener(this);
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#designNewExperiment()
	 */
	public INumericalVector designNewExperiment()
	{
		INumericalVector lNewExperimentInputVector = null;
		if (mEngineValidated)
		{
			lNewExperimentInputVector = mDoeStrategy.designNewExperiment();
			if (lNewExperimentInputVector == null)
			{
				mLogger.error("The DOE strategy: " + mDoeStrategy + " could not design a new experiment.");
			}
		}
		else
		{
			mLogger.error("call to designNewExperiment from a non validated OptimalEngine.");
		}
		return lNewExperimentInputVector;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#pushGriddedExperiments(org.royerloic.optimal.interf.IGridDefinition)
	 */
	public void pushGriddedExperiments(final IGridDefinition pGridDefinition)
	{
		if (mEngineValidated)
		{
			mLogger.info("call to popExperiment: removing an experiment from the experiment queue");
			List lExperimentList = pGridDefinition.generateGrid();
			for (int i = 0; i < lExperimentList.size(); i++)
			{
				pushExperiment((INumericalVector) lExperimentList.get(i));
			}
		}
		else
		{
			mLogger.error("call to pushGriddedExperiments from a non validated OptimalEngine.");
		}
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#pushExperiment(org.royerloic.math.INumericalVector)
	 */
	public void pushExperiment(INumericalVector pInputVector)
	{
		synchronized (mExperimentQueue)
		{
			if (mEngineValidated)
			{
				mLogger.info("call to pushExperiment(): adding an experiment from the experiment queue");
				mExperimentQueue.add(pInputVector);
			}
			else
			{
				mLogger.error("call to pushExperiment() from a non validated OptimalEngine.");
			}
		}
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#popExperiment()
	 */
	public INumericalVector popExperiment()
	{
		synchronized (mExperimentQueue)
		{
			if (mEngineValidated)
			{
				mLogger.info("call to popExperiment(): removing an experiment from the experiment queue");
				if (mExperimentQueue.size() == 0)
				{
					mLogger.info("experiment queue empty: designing a new experiment.");
					mExperimentQueue.add(designNewExperiment());
				}
				return (INumericalVector) mExperimentQueue.remove(0);
			}
			else
			{
				mLogger.error("call to popExperiment() from a non validated OptimalEngine.");
				return null;
			}
		}
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#start()
	 */
	public void start()
	{
		if (mEngineValidated)
		{
			mLogger.info("call to start(): starting the optimization.");
			mFeedExperimentFunctions = true;
			int lNumberOfExperimentFunctionStubs = mExperimentFunctionStubsList.size();
			for (int i = 0; i < lNumberOfExperimentFunctionStubs; i++)
			{
				IExperimentFunctionStub lStub = (IExperimentFunctionStub) mExperimentFunctionStubsList.get(i);
				INumericalVector lVector = (INumericalVector) popExperiment();
				lStub.evaluate(lVector);
			}
		}
		else
		{
			mLogger.error("call to start() from a non validated OptimalEngine.");
		}
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#stop()
	 */
	public void stop()
	{
		mLogger.info("call to stop: stopping the optimization.");
		mFeedExperimentFunctions = false;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#setEventListener(org.royerloic.optimal.interf.IOptimalEventListener)
	 */
	public void setEventListener(IOptimalEventListener pOptimalEventListener)
	{
		mLogger.info("call to setEventListener: " + pOptimalEventListener);
		mOptimalEventListener = pOptimalEventListener;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEventListener#experimentDone(org.royerloic.optimal.interf.IExperimentFunctionStub,
	 *      org.royerloic.optimal.interf.IExperiment)
	 */
	public void experimentDone(IExperimentFunctionStub pExperimentFunctionStub, IExperiment pExperiment)
	{
		mLogger.info("experiment finished: " + pExperiment);
		boolean lBetter = mExperimentDatabase.addExperiment(pExperiment);

		mInterpolator.update();

		mOptimalEventListener.experimentDone(pExperimentFunctionStub, pExperiment);

		if (lBetter)
		{
			newBestExperiment(mExperimentDatabase, pExperiment);
		}

		// This should be allways at the end of this call back since it launches a
		// new thread.
		if (mFeedExperimentFunctions)
		{
			pExperimentFunctionStub.evaluate(popExperiment());
		}
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEventListener#newBestExperiment(org.royerloic.optimal.interf.IExperimentDatabase,
	 *      org.royerloic.optimal.interf.IExperiment)
	 */
	public void newBestExperiment(IExperimentDatabase pExperimentDatabase, IExperiment pExperiment)
	{
		mLogger.info("new best experiment: " + pExperiment);
		mOptimalEventListener.newBestExperiment(pExperimentDatabase, pExperiment);
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#setLogLevel(org.apache.log4j.Level)
	 */
	public void setLogLevel(final String pLogLevel)
	{
		mLogger.setLevel((Level) Level.toLevel(pLogLevel));
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#getIterations()
	 */
	public int getIterations()
	{
		return mExperimentDatabase.getNumberOfExperiments();
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#getBestExperimentList()
	 */
	public List getListOfBestExperiments()
	{
		return mExperimentDatabase.getListOfBestExperiments();
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#getBestExperimentValuesList()
	 */
	public List getListOfBestExperimentValues()
	{
		return mExperimentDatabase.getListOfBestExperimentValues();
	}

}