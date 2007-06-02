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
public interface IFunction
{
	/**
	 * @return
	 */
	int getInputDimension();

	/**
	 * @return
	 */
	int getOutputDimension();

	/**
	 * @param pIndex
	 * @return
	 */
	double getInputMin(final int pIndex);

	/**
	 * @param pIndex
	 * @return
	 */
	double getInputMax(final int pIndex);

	/**
	 * @param pIndex
	 * @return
	 */
	double getInputDelta(final int pIndex);

	/**
	 * @param pVector
	 */
	void normalizeInputVector(INumericalVector pVector);

}
