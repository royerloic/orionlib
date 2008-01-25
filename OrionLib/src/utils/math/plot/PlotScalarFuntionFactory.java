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
import utils.utils.Plugin;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public class PlotScalarFuntionFactory
{

	/**
	 * Hiding default constructor.
	 */
	private PlotScalarFuntionFactory()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pFunction
	 * @param pResolution
	 * @param pName
	 * @return
	 */
	public static final IPlot build(final IScalarFunction pFunction,
																	final int pResolution,
																	final String pName) throws Exception
	{
		if (pFunction.getInputDimension() <= 2)
		{
			final IPlot lIPlot = (IPlot) Plugin.load("org.royerloic.math.plot.PlotScalarFuntion" + Integer.toString(pFunction.getInputDimension() + 1));
			lIPlot.setFuntion(pFunction);
			lIPlot.setResolution(pResolution);
			lIPlot.setPlotName(pName);
			lIPlot.setMode("default");
			return lIPlot;
		}
		else
			throw new Exception("bad dimension");

	};
}