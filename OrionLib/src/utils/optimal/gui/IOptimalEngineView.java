/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package utils.optimal.gui;

import java.util.List;

import utils.math.IScalarFunction;
import utils.optimal.interf.IExperiment;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public interface IOptimalEngineView
{

	/**
	 * Initiate the OptimalEngineView
	 */
	void initiate();

	/**
	 * Terminate the OptimalEngineView
	 */
	void terminate();

	/**
	 * Sets the project name.
	 * 
	 * @param pName
	 *          name of the project
	 */
	void setProjectName(final String pName);

	/**
	 * Updates the best Experiment.
	 * 
	 * @param pExperiment
	 *          the new best Experiment
	 * @param pValue
	 *          The objective value.
	 */
	void updateBestExperiment(IExperiment pExperiment, final double pValue);

	/**
	 * Updates the maximum evolution Array.
	 * 
	 * @param pMaximumEvolutionArray
	 *          new maximum evolution Aarray.
	 */
	void updateMaximumEvolution(List pMaximumEvolutionList);

	/**
	 * Updates the number of iterations done.
	 * 
	 * @param pIterations
	 *          new number of iterations
	 */
	void updateIterations(int pIterations);

	/**
	 * Updates the Designer status.
	 * 
	 * @param pStatus
	 *          new Designer status
	 */
	void updateDesignerStatus(final String pStatus);

	/**
	 * Sets the Control Listener.
	 * 
	 * @param pIOptimalEngineControl
	 *          Control Listener
	 */
	void setControlListener(IOptimalEngineControl pIOptimalEngineControl);

	/**
	 * Notifies an error(exception).
	 * 
	 * @param pError
	 *          Error notified
	 * @param pException
	 *          associated Exception
	 */
	void notifyError(final String pError, final Throwable pException);

	/**
	 * Sets the view only mode.
	 * 
	 * @param pMode
	 *          true -> view only and false -> normal
	 */
	void setViewOnlyMode(final boolean pMode);

	/**
	 * Updates the ModelerView.
	 * 
	 * @param pModeler
	 *          Modeler from which to update the View
	 */
	void updateModelerView(IScalarFunction pModeler);

}