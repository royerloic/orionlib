/*
 * Created on 02.12.2004
 * by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.optimal.interf;

import utils.java.IObject;
import utils.math.INumericalVector;
import utils.math.IScalarFunction;

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
