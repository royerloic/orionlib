/*
 * Created on 02.12.2004
 * by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.interf;

import org.royerloic.java.IObject;
import org.royerloic.math.INumericalVector;
import org.royerloic.math.IScalarFunction;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public interface IInterpolator extends IObject, IScalarFunction
{

	/**
	 * @param pObjectiveFunction
	 */
	void setObjectiveFunction(IObjectiveFunction pObjectiveFunction);

	/**
	 * @param pExperimentDatabase
	 */
	void setExperimentDatabase(IExperimentDatabase pExperimentDatabase);

	void update();

	int getInputDimension();

	double evaluate(INumericalVector pVector);

}
