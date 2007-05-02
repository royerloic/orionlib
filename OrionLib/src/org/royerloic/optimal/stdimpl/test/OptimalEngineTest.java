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

		this.mOptimalEngine = new OptimalEngine();

		this.mCsvDatabaseStore = new CsvDatabaseStore(new File("database.txt"));

		this.mOptimalEngine.setOptimalInterpreter(lInterpreter);

		final IExperimentDatabase lExperimentdatabase = new ExperimentDatabase();
		this.mOptimalEngine.setExperimentDatabase(lExperimentdatabase);

		final IExperimentFunction lExperimentFunction1 = new TestFunctions(8);
		final IExperimentFunctionStub lExperimentFunctionStub1 = new ExperimentFunctionStub(lExperimentFunction1);
		this.mOptimalEngine.addExperimentFunctionStub(lExperimentFunctionStub1);

		/***************************************************************************
		 * IExperimentFunction lExperimentFunction2 = new TwoPics("function 2");
		 * IExperimentFunctionStub lExperimentFunctionStub2 = new
		 * ExperimentFunctionStub(lExperimentFunction2);
		 * mOptimalEngine.addExperimentFunctionStub(lExperimentFunctionStub2);/
		 **************************************************************************/

		this.mObjectiveFunction = new ObjectiveFunction("y0");
		this.mOptimalEngine.setObjectiveFunction(this.mObjectiveFunction);

		this.mInterpolator = new PiInterpolator(AgrInterpolator.class); // SvmInterpolator();
		this.mOptimalEngine.setInterpolator(this.mInterpolator);

		this.mDoeStrategy = new DoeStrategyClassic();
		this.mOptimalEngine.setDoeStrategy(this.mDoeStrategy);

		final UniformGrid lGridDefinition = new UniformGrid(lExperimentFunction1.getInputDimension());
		lGridDefinition.setNumberOfDivisions(2);

		this.mOptimalEngine.setEventListener(this);

		if (this.mOptimalEngine.validateEngine())
		{
			this.mOptimalEngine.loadExperimentDatabase(this.mCsvDatabaseStore);

			this.mOptimalEngineGui = new OptimalEngineJFrame();

			this.mOptimalEngineGui.initiate();
			this.mOptimalEngineGui.setEnabled(true);
			this.mOptimalEngineGui.setVisible(true);/**/

			/*************************************************************************
			 * try { Thread.sleep(1000); } catch (InterruptedException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }/
			 ************************************************************************/

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
		this.mOptimalEngineGui.updateIterations(this.mOptimalEngine.getIterations());
		this.mOptimalEngineGui.updateDesignerStatus("Designer Status");
		this.mOptimalEngineGui.updateMaximumEvolution(this.mOptimalEngine.getListOfBestExperimentValues());
		this.mOptimalEngineGui.updateModelerView(this.mInterpolator);/**/

		this.mOptimalEngine.saveExperimentDatabase(this.mCsvDatabaseStore);

	}

	/**
	 * @see org.royerloic.optimal.interf.IOptimalEventListener#newBestExperiment(org.royerloic.optimal.interf.IExperimentDatabase,
	 *      org.royerloic.optimal.interf.IExperiment)
	 */
	public void newBestExperiment(final IExperimentDatabase pExperimentDatabase, final IExperiment pExperiment)
	{
		System.out.println(pExperiment);
		this.mOptimalEngineGui.updateBestExperiment(pExperiment, this.mObjectiveFunction.evaluate(pExperiment.getOutput()));

	}

}
