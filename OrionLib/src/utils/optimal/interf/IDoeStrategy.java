/*
 * Created on 02.12.2004
 * by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.optimal.interf;

import utils.math.INumericalVector;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public interface IDoeStrategy
{

	/**
	 * @param pExperimentDatabase
	 */
	void setExperimentDatabase(IExperimentDatabase pExperimentDatabase);

	/**
	 * @param pInterpolator
	 */
	void setInterpolator(IInterpolator pInterpolator);

	void setObjectiveFunction(IObjectiveFunction pObjectiveFunction);

	/**
	 * @return
	 */
	INumericalVector designNewExperiment();

}
