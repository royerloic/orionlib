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

		this.mExperimentFunctionStubsList = new ArrayList();
		this.mExperimentQueue = new ArrayList();

		this.mEngineValidated = false;
		final HTMLLayout layout = new HTMLLayout();

		WriterAppender appender = null;
		try
		{
			final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
			final Date lDate = new Date();
			final FileOutputStream output = new FileOutputStream("Optimal." + mDateFormat.format(lDate) + ".log.html");
			appender = new WriterAppender(layout, output);
			mLogger.addAppender(appender);
		}
		catch (final Exception e)
		{
			System.err.println("Could not create log file");
			System.err.println(e.toString());
		}
		mLogger.setLevel(Level.INFO);
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#setOptimalInterpreter(bsh.Interpreter)
	 */
	public void setOptimalInterpreter(final Interpreter pInterpreter)
	{
		mLogger.info("call to setOptimalInterpreter(" + pInterpreter + ")");
		this.mInterpreter = pInterpreter;
	}

	public Interpreter getOptimalInterpreter()
	{
		return this.mInterpreter;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#setExperimentDatabase(org.royerloic.optimal.interf.IExperimentDatabase)
	 */
	public void setExperimentDatabase(final IExperimentDatabase pExperimentDatabase)
	{
		mLogger.info("call to setExperimentDatabase(" + pExperimentDatabase + ")");
		this.mExperimentDatabase = pExperimentDatabase;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#setObjectiveFunction(org.royerloic.optimal.interf.IObjectiveFunction)
	 */
	public void setObjectiveFunction(final IObjectiveFunction pObjectiveFunction)
	{
		mLogger.info("call to setObjectiveFunction(" + pObjectiveFunction + ")");
		this.mObjectiveFunction = pObjectiveFunction;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#setInterpolator(org.royerloic.optimal.interf.IInterpolator)
	 */
	public void setInterpolator(final IInterpolator pInterpolator)
	{
		mLogger.info("call to setInterpolator(" + pInterpolator + ")");
		this.mInterpolator = pInterpolator;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#setDOEStrategy(org.royerloic.optimal.interf.IDoeStrategy)
	 */
	public void setDoeStrategy(final IDoeStrategy pDoeStrategy)
	{
		mLogger.info("call to setDoeStrategy(" + pDoeStrategy + ")");
		this.mDoeStrategy = pDoeStrategy;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#validateEngine()
	 */
	public boolean validateEngine()
	{
		boolean lValidated = true;

		lValidated &= this.mInterpreter != null;
		lValidated &= this.mExperimentDatabase != null;
		lValidated &= this.mObjectiveFunction != null;
		lValidated &= this.mInterpolator != null;
		lValidated &= this.mDoeStrategy != null;
		lValidated &= (this.mExperimentFunctionStubsList.size() >= 1);

		if (lValidated)
		{
			mLogger.info("OptimalEngine validated.");
			this.mExperimentDatabase.setObjectiveFunction(this.mObjectiveFunction);

			this.mObjectiveFunction.setExperimentDatabase(this.mExperimentDatabase);
			this.mObjectiveFunction.setInterpreter(this.mInterpreter);

			this.mInterpolator.setObjectiveFunction(this.mObjectiveFunction);
			this.mInterpolator.setExperimentDatabase(this.mExperimentDatabase);

			this.mDoeStrategy.setExperimentDatabase(this.mExperimentDatabase);
			this.mDoeStrategy.setObjectiveFunction(this.mObjectiveFunction);
			this.mDoeStrategy.setInterpolator(this.mInterpolator);
		}
		else
			mLogger.error("Optimal Engine could not be validated");

		this.mEngineValidated = lValidated;

		return lValidated;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#loadExperimentDatabase(java.io.File)
	 */
	public void loadExperimentDatabase(final IExperimentDatabaseStore pStore)
	{
		if (this.mEngineValidated)
		{
			mLogger.info("call to loadExperimentDatabase(" + pStore + ")");

			pStore.setExperimentDatabase(this.mExperimentDatabase);
			pStore.setObjectiveFunction(this.mObjectiveFunction);
			try
			{
				pStore.load();
			}
			catch (final RuntimeException e)
			{
				mLogger.error("exception in loadExperimentDatabase.", e);
				e.printStackTrace();
			}
		}
		else
			mLogger.error("call to loadExperimentDatabase() from a non validated OptimalEngine.");
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#saveExperimentDatabase(java.io.File)
	 */
	public void saveExperimentDatabase(final IExperimentDatabaseStore pStore)
	{
		if (this.mEngineValidated)
		{
			mLogger.info("call to saveExperimentDatabase: " + pStore);

			pStore.setExperimentDatabase(this.mExperimentDatabase);
			pStore.setObjectiveFunction(this.mObjectiveFunction);
			try
			{
				pStore.save();
			}
			catch (final RuntimeException e)
			{
				mLogger.error("exception in saveExperimentDatabase().", e);
				e.printStackTrace();
			}
		}
		else
			mLogger.error("call to saveExperimentDatabase() from a non validated OptimalEngine.");
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#addExperimentFunctionStub(org.royerloic.optimal.interf.IExperimentFunctionStub)
	 */
	public void addExperimentFunctionStub(final IExperimentFunctionStub pExperimentFunctionStub)
	{
		mLogger.info("adding a IExperimentFunctionStub to the OptimalEngine: " + pExperimentFunctionStub);
		this.mExperimentFunctionStubsList.add(pExperimentFunctionStub);
		pExperimentFunctionStub.setOptimalEventListener(this);
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#designNewExperiment()
	 */
	public INumericalVector designNewExperiment()
	{
		INumericalVector lNewExperimentInputVector = null;
		if (this.mEngineValidated)
		{
			lNewExperimentInputVector = this.mDoeStrategy.designNewExperiment();
			if (lNewExperimentInputVector == null)
				mLogger.error("The DOE strategy: " + this.mDoeStrategy + " could not design a new experiment.");
		}
		else
			mLogger.error("call to designNewExperiment from a non validated OptimalEngine.");
		return lNewExperimentInputVector;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#pushGriddedExperiments(org.royerloic.optimal.interf.IGridDefinition)
	 */
	public void pushGriddedExperiments(final IGridDefinition pGridDefinition)
	{
		if (this.mEngineValidated)
		{
			mLogger.info("call to popExperiment: removing an experiment from the experiment queue");
			final List lExperimentList = pGridDefinition.generateGrid();
			for (int i = 0; i < lExperimentList.size(); i++)
				pushExperiment((INumericalVector) lExperimentList.get(i));
		}
		else
			mLogger.error("call to pushGriddedExperiments from a non validated OptimalEngine.");
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#pushExperiment(org.royerloic.math.INumericalVector)
	 */
	public void pushExperiment(final INumericalVector pInputVector)
	{
		synchronized (this.mExperimentQueue)
		{
			if (this.mEngineValidated)
			{
				mLogger.info("call to pushExperiment(): adding an experiment from the experiment queue");
				this.mExperimentQueue.add(pInputVector);
			}
			else
				mLogger.error("call to pushExperiment() from a non validated OptimalEngine.");
		}
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#popExperiment()
	 */
	public INumericalVector popExperiment()
	{
		synchronized (this.mExperimentQueue)
		{
			if (this.mEngineValidated)
			{
				mLogger.info("call to popExperiment(): removing an experiment from the experiment queue");
				if (this.mExperimentQueue.size() == 0)
				{
					mLogger.info("experiment queue empty: designing a new experiment.");
					this.mExperimentQueue.add(designNewExperiment());
				}
				return (INumericalVector) this.mExperimentQueue.remove(0);
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
		if (this.mEngineValidated)
		{
			mLogger.info("call to start(): starting the optimization.");
			this.mFeedExperimentFunctions = true;
			final int lNumberOfExperimentFunctionStubs = this.mExperimentFunctionStubsList.size();
			for (int i = 0; i < lNumberOfExperimentFunctionStubs; i++)
			{
				final IExperimentFunctionStub lStub = (IExperimentFunctionStub) this.mExperimentFunctionStubsList.get(i);
				final INumericalVector lVector = popExperiment();
				lStub.evaluate(lVector);
			}
		}
		else
			mLogger.error("call to start() from a non validated OptimalEngine.");
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#stop()
	 */
	public void stop()
	{
		mLogger.info("call to stop: stopping the optimization.");
		this.mFeedExperimentFunctions = false;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#setEventListener(org.royerloic.optimal.interf.IOptimalEventListener)
	 */
	public void setEventListener(final IOptimalEventListener pOptimalEventListener)
	{
		mLogger.info("call to setEventListener: " + pOptimalEventListener);
		this.mOptimalEventListener = pOptimalEventListener;
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEventListener#experimentDone(org.royerloic.optimal.interf.IExperimentFunctionStub,
	 *      org.royerloic.optimal.interf.IExperiment)
	 */
	public void experimentDone(final IExperimentFunctionStub pExperimentFunctionStub, final IExperiment pExperiment)
	{
		mLogger.info("experiment finished: " + pExperiment);
		final boolean lBetter = this.mExperimentDatabase.addExperiment(pExperiment);

		this.mInterpolator.update();

		this.mOptimalEventListener.experimentDone(pExperimentFunctionStub, pExperiment);

		if (lBetter)
			newBestExperiment(this.mExperimentDatabase, pExperiment);

		// This should be allways at the end of this call back since it launches a
		// new thread.
		if (this.mFeedExperimentFunctions)
			pExperimentFunctionStub.evaluate(popExperiment());
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEventListener#newBestExperiment(org.royerloic.optimal.interf.IExperimentDatabase,
	 *      org.royerloic.optimal.interf.IExperiment)
	 */
	public void newBestExperiment(final IExperimentDatabase pExperimentDatabase, final IExperiment pExperiment)
	{
		mLogger.info("new best experiment: " + pExperiment);
		this.mOptimalEventListener.newBestExperiment(pExperimentDatabase, pExperiment);
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#setLogLevel(org.apache.log4j.Level)
	 */
	public void setLogLevel(final String pLogLevel)
	{
		mLogger.setLevel(Level.toLevel(pLogLevel));
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#getIterations()
	 */
	public int getIterations()
	{
		return this.mExperimentDatabase.getNumberOfExperiments();
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#getBestExperimentList()
	 */
	public List getListOfBestExperiments()
	{
		return this.mExperimentDatabase.getListOfBestExperiments();
	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEngine#getBestExperimentValuesList()
	 */
	public List getListOfBestExperimentValues()
	{
		return this.mExperimentDatabase.getListOfBestExperimentValues();
	}

}