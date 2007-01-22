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
public interface IExperimentFunction
{

	public int getInputDimension();

	public int getOutputDimension();

	/**
	 * @param pExperimentInputVector
	 * @return
	 */
	public INumericalVector evaluate(INumericalVector pExperimentInputVector);

}
