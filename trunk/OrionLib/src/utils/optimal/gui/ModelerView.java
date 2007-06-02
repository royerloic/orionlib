/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *   
 */

package utils.optimal.gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import utils.math.IScalarFunction;
import utils.math.plot.IPlot;
import utils.math.plot.PlotScalarFuntionFactory;

/**
 * ModelerView is a View for Modeler. Provides a GUI component that reprersents
 * a Modeler State. The main part of this component is the 2d/3d Plot of the
 * interpolated objective function.
 * 
 * @author MSc. Ing. Loic Royer
 */
public class ModelerView extends JPanel
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6952366308707407816L;

	private IPlot	mModelPlot;

	private int		mResolution;

	/**
	 * Constucts a ModelerView given a Modeler.
	 * 
	 * @param pModeler
	 *          Modeler to be viewed.
	 */
	public ModelerView()
	{
		super();
		mResolution = 10;
		final Border lRaisedBevel = BorderFactory.createRaisedBevelBorder();
		final Border lLoweredBevel = BorderFactory.createLoweredBevelBorder();
		setBorder(BorderFactory.createCompoundBorder(lRaisedBevel, lLoweredBevel));
		setLayout(new BorderLayout(5, 5));
	}

	public void initiate()
	{

	};

	public void updateModeler(final IScalarFunction pModeler)
	{
		if (mModelPlot == null)
		{
			try
			{
				mModelPlot = PlotScalarFuntionFactory.build(pModeler, mResolution, "Model");
			}
			catch (final Throwable e)
			{
				throw new RuntimeException("Error while updating modeler.", e);
			}

			mModelPlot.display();
			add((Container) mModelPlot);
			validate();
			setVisible(true);
		}
		else
			mModelPlot.update();
	};

	/**
	 * @see de.fhg.iwu.utils.ISRS#srsStop()
	 */
	public final void terminate()
	{
		mModelPlot.hide();
		removeAll();
	};

}
