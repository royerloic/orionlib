/*
 * Created on 02.12.2004
 * by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.optimal.interf;

import utils.math.INumericalVector;
import bsh.Interpreter;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public interface IObjectiveFunction
{
	public void setInterpreter(Interpreter pInterpreter);

	public void setExperimentDatabase(IExperimentDatabase pDatabase);

	public double evaluate(final INumericalVector pVector);

}
