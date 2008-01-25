/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *   
 */

package utils.math;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public interface IScalarFunction extends IFunction
{
	/**
	 * @param pVector
	 * @return
	 */
	double evaluate(final INumericalVector pVector);

	/**
	 * @param pResolution
	 * @return
	 */
	double[][] computePoints(final int pResolution);
}
