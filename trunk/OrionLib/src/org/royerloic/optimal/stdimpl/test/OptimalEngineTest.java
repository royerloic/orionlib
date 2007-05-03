/*
 * Created on 06.12.2004
 * by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.stdimpl.test;

import java.io.File;

import org.royerloic.optimal.Optimal;
import org.royerloic.optimal.gui.OptimalEngineJFrame;
import org.royerloic.optimal.interf.IExperiment;
import org.royerloic.optimal.interf.IExperimentDatabase;
import org.royerloic.optimal.interf.IExperimentDatabaseStore;
import org.royerloic.optimal.interf.IExperimentFunction;
import org.royerloic.optimal.interf.IExperimentFunctionStub;
import org.royerloic.optimal.interf.IInterpolator;
import org.royerloic.optimal.interf.IObjectiveFunction;
import org.royerloic.optimal.interf.IOptimalEngine;
import org.royerloic.optimal.interf.IOptimalEventListener;
import org.royerloic.optimal.stdimpl.AgrInterpolator;
import org.royerloic.optimal.stdimpl.CsvDatabaseStore;
import org.royerloic.optimal.stdimpl.DoeStrategyClassic;
import org.royerloic.optimal.stdimpl.ExperimentDatabase;
import org.royerloic.optimal.stdimpl.ExperimentFunctionStub;
import org.royerloic.optimal.stdimpl.ObjectiveFunction;
import org.royerloic.optimal.stdimpl.OptimalEngine;
import org.royerloic.optimal.stdimpl.PiInterpolator;
import org.royerloic.optimal.stdimpl.UniformGrid;
import org.royerloic.optimal.stdimpl.test.functions.TestFunctions;

import bsh.Interpreter;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class OptimalEngineTest implements IOptimalEventListener
{

	private OptimalEngineJFrame				mOptimalEngineGui;
	private IOptimalEngine						mOptimalEngine;
	private IObjectiveFunction				mObjectiveFunction;
	private IInterpolator							mInterpolator;
	private DoeStrategyClassic				mDoeStrategy;
	private IExperimentDatabaseStore	mCsvDatabaseStore;

	public static void main(final String[] args)
	{
		final OptimalEngineTest lOptimalEngineTest = new OptimalEngineTest();
		lOptimalEngineTest.testOptimalEngine();
	}

	public void testOptimalEngine()
	{
		final Interpreter lInterpreter = new Optimal();

		mOptimalEngine = new OptimalEngine();

		mCsvDatabaseStore = new CsvDatabaseStore(new File("database.txt"));

		mOptimalEngine.setOptimalInterpreter(lInterpreter);

		final IExperimentDatabase lExperimentdatabase = new ExperimentDatabase();
		mOptimalEngine.setExperimentDatabase(lExperimentdatabase);

		final IExperimentFunction lExperimentFunction1 = new TestFunctions(8);
		final IExperimentFunctionStub lExperimentFunctionStub1 = new ExperimentFunctionStub(lExperimentFunction1);
		mOptimalEngine.addExperimentFunctionStub(lExperimentFunctionStub1);

		/***************************************************************************
		 * IExperimentFunction lExperimentFunction2 = new TwoPics("function 2");
		 * IExperimentFunctionStub lExperimentFunctionStub2 = new
		 * ExperimentFunctionStub(lExperimentFunction2);
		 * mOptimalEngine.addExperimentFunctionStub(lExperimentFunctionStub2);/
		 **************************************************************************/

		mObjectiveFunction = new ObjectiveFunction("y0");
		mOptimalEngine.setObjectiveFunction(mObjectiveFunction);

		mInterpolator = new PiInterpolator(AgrInterpolator.class); // SvmInterpolator();
		mOptimalEngine.setInterpolator(mInterpolator);

		mDoeStrategy = new DoeStrategyClassic();
		mOptimalEngine.setDoeStrategy(mDoeStrategy);

		final UniformGrid lGridDefinition = new UniformGrid(lExperimentFunction1.getInputDimension());
		lGridDefinition.setNumberOfDivisions(2);

		mOptimalEngine.setEventListener(this);

		if (mOptimalEngine.validateEngine())
		{
			mOptimalEngine.loadExperimentDatabase(mCsvDatabaseStore);

			mOptimalEngineGui = new OptimalEngineJFrame();

			mOptimalEngineGui.initiate();
			mOptimalEngineGui.setEnabled(true);
			mOptimalEngineGui.setVisible(true);/**/

			/*************************************************************************
			 * try { Thread.sleep(1000); } catch (InterruptedException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }/
			 ************************************************************************/

			mOptimalEngine.pushGriddedExperiments(lGridDefinition);
			mOptimalEngine.start();
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
		mOptimalEngineGui.updateIterations(mOptimalEngine.getIterations());
		mOptimalEngineGui.updateDesignerStatus("Designer Status");
		mOptimalEngineGui.updateMaximumEvolution(mOptimalEngine.getListOfBestExperimentValues());
		mOptimalEngineGui.updateModelerView(mInterpolator);/**/

		mOptimalEngine.saveExperimentDatabase(mCsvDatabaseStore);

	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEventListener#newBestExperiment(org.royerloic.optimal.interf.IExperimentDatabase,
	 *      org.royerloic.optimal.interf.IExperiment)
	 */
	public void newBestExperiment(final IExperimentDatabase pExperimentDatabase, final IExperiment pExperiment)
	{
		System.out.println(pExperiment);
		mOptimalEngineGui.updateBestExperiment(pExperiment, mObjectiveFunction.evaluate(pExperiment.getOutput()));

	}

}
