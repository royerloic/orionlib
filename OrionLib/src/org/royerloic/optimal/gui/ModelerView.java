/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *   
 */

package org.royerloic.optimal.gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.royerloic.math.IScalarFunction;
import org.royerloic.math.plot.IPlot;
import org.royerloic.math.plot.PlotScalarFuntionFactory;

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
		this.mResolution = 10;
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
		if (this.mModelPlot == null)
		{
			try
			{
				this.mModelPlot = PlotScalarFuntionFactory.build(pModeler, this.mResolution, "Model");
			}
			catch (final Throwable e)
			{
				throw new RuntimeException("Error while updating modeler.", e);
			}

			this.mModelPlot.display();
			add((Container) this.mModelPlot);
			validate();
			setVisible(true);
		}
		else
			this.mModelPlot.update();
	};

	/**
	 * @see de.fhg.iwu.utils.ISRS#srsStop()
	 */
	public final void terminate()
	{
		this.mModelPlot.hide();
		removeAll();
	};

}
