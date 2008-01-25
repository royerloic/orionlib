/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *   
 */

package utils.math.plot;

import utils.math.IScalarFunction;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public interface IPlot
{
	/**
	 * @param pFunction
	 */
	void setFuntion(final IScalarFunction pFunction);

	/**
	 * @return
	 */
	IScalarFunction getFuntion();

	/**
	 * @param pResolution
	 */
	void setResolution(final int pResolution);

	/**
	 * @return
	 */
	int getResolution();

	/**
	 * @param pName
	 */
	void setPlotName(String pName);

	/**
	 * 
	 */
	String getPlotName();

	/**
	 * @param string
	 */
	void setMode(String pMode);

	/**
	 * 
	 */
	String getMode();

	/**
	 * 
	 */
	void display();

	/**
	 * 
	 */
	void update();

	/**
	 * 
	 */
	void hide();

}
