/*
 * Created on 01.12.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.interf;

import java.util.List;

import org.royerloic.math.INumericalVector;

import bsh.Interpreter;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public interface IOptimalEngine
{

	public void setOptimalInterpreter(Interpreter pInterpreter);

	public Interpreter getOptimalInterpreter();

	public void loadExperimentDatabase(final IExperimentDatabaseStore pStore);

	public void saveExperimentDatabase(final IExperimentDatabaseStore pStore);

	public void addExperimentFunctionStub(final IExperimentFunctionStub pExperimentFunctionStub);

	public void setExperimentDatabase(IExperimentDatabase pExperimentDatabase);

	public void setObjectiveFunction(final IObjectiveFunction pObjectiveFunction);

	public void setInterpolator(final IInterpolator pInterpolator);

	public void setDoeStrategy(final IDoeStrategy pDoeStrategy);

	public boolean validateEngine();

	public INumericalVector designNewExperiment();

	public void pushGriddedExperiments(final IGridDefinition pGridDefinition);

	public void pushExperiment(final INumericalVector pInputVector);

	public INumericalVector popExperiment();

	public void start();

	public void stop();

	public void setEventListener(IOptimalEventListener pOptimalEventListener);

	public void setLogLevel(final String pLogLevel);

	public int getIterations();

	public List getListOfBestExperiments();

	public List getListOfBestExperimentValues();
	/**
	 * @return
	 */

}
