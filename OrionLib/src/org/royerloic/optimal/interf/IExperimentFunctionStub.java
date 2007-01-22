/*
 * Created on 02.12.2004
 * by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.interf;

import org.royerloic.math.INumericalVector;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public interface IExperimentFunctionStub
{

	public void setExperimentFunction(IExperimentFunction pExperimentFunction);

	public void setOptimalEventListener(IOptimalEventListener pTerminationListener);

	/**
	 * @param pExperimentInputVector
	 * @return
	 */
	public void evaluate(INumericalVector pExperimentInputVector);

}
