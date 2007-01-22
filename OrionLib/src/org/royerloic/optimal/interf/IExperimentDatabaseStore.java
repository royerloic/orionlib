/*
 * Created on 06.12.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.interf;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public interface IExperimentDatabaseStore
{

	void setExperimentDatabase(IExperimentDatabase pExperimentDatabase);

	public void setObjectiveFunction(IObjectiveFunction pObjectiveFunction);

	/**
	 * @param pEngine
	 */
	void load();

	/**
	 * @param pEngine
	 */
	void save();

}