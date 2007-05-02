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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.royerloic.math.INumericalVector;
import org.royerloic.math.IScalarFunction;
import org.royerloic.math.plot.IPlot;
import org.royerloic.math.plot.PlotScalarFuntionFactory;
import org.royerloic.optimal.interf.IExperiment;

/**
 * MainView is the main view of the Database content.
 * 
 * @see Database
 * 
 * @author MSc. Ing. Loic Royer
 */
public class MainView extends JPanel implements IScalarFunction
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -572313894039683950L;

	/**
	 * <code>cBEST_EXPERIMENT_VIEW_HEIGHT</code> is the constant height of the
	 * Experiment View.
	 */
	private static final int	cBEST_EXPERIMENT_VIEW_HEIGHT	= 6;

	/**
	 * <code>cBEST_EXPERIMENT_VIEW_WIDTH</code> is the constant width of the
	 * Experiment View.
	 */
	private static final int	cBEST_EXPERIMENT_VIEW_WIDTH		= 10;

	/**
	 * <code>mBestExperimentView</code> is the best Experiment View.
	 */
	private JTextArea					mBestExperimentView;

	/**
	 * <code>mControlContainer</code> is the control Container.
	 */
	private Container					mControlContainer;

	/**
	 * <code>mDatabaseSizeView</code> is the Database size View JTextField.
	 */
	private JTextField				mDatabaseSizeView;

	/**
	 * <code>mMainContainer</code> is the main Container.
	 */
	private Container					mMainContainer;

	/**
	 * <code>mMaximumEvolutionPlot</code> is the maximum evolution plot.
	 */
	private IPlot							mMaximumEvolutionPlot;

	/**
	 * <code>mStatusContainer</code> is the status Container.
	 */
	private Container					mStatusContainer;

	/**
	 * <code>mMaximumEvolutionList</code> is the maximum evolution Array.
	 */
	private List							mMaximumEvolutionList;

	/**
	 * Initializes the main View.
	 */
	public MainView()
	{
		super();

		final Border lRaisedBevel = BorderFactory.createRaisedBevelBorder();
		final Border lLoweredBevel = BorderFactory.createLoweredBevelBorder();
		setBorder(BorderFactory.createCompoundBorder(lRaisedBevel, lLoweredBevel));

		setLayout(new BorderLayout(5, 5));

		this.mControlContainer = new JPanel();
		this.mControlContainer.setLayout(new BoxLayout(this.mControlContainer, BoxLayout.X_AXIS));

		this.mMainContainer = new JPanel();
		this.mMainContainer.setLayout(new BoxLayout(this.mMainContainer, BoxLayout.Y_AXIS));

		try
		{
			this.mMaximumEvolutionPlot = PlotScalarFuntionFactory.build(this, 0, "Maximum");
		}
		catch (final Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace(System.out);
		}

		this.mBestExperimentView = new JTextArea(cBEST_EXPERIMENT_VIEW_HEIGHT, cBEST_EXPERIMENT_VIEW_WIDTH);
		this.mBestExperimentView.setEditable(false);
		this.mMainContainer.add(new JScrollPane(this.mBestExperimentView));

		this.mStatusContainer = new JPanel();
		this.mStatusContainer.setLayout(new BoxLayout(this.mStatusContainer, BoxLayout.X_AXIS));

		this.mDatabaseSizeView = new JTextField("...");
		this.mDatabaseSizeView.setEditable(false);
		this.mStatusContainer.add(this.mDatabaseSizeView);

		add(this.mControlContainer, BorderLayout.NORTH);
		add(this.mMainContainer, BorderLayout.CENTER);
		add(this.mStatusContainer, BorderLayout.SOUTH);

		validate();

	}

	/**
	 * Returns a string intended to be read on a GUI that gives the current best
	 * experiment in the database.
	 * 
	 * @param pExperiment
	 *          the best Experiment
	 * @param pValue
	 *          the objective value of the best Experiment
	 * @return string giving the best current experiment.
	 */
	private String getBestExperimentText(final IExperiment pExperiment, final double pValue)
	{
		if (pExperiment == null)
			return "No Experiment in Database yet.";
		else
			return "Best Experiment: \n" + " Value: " + pValue + "\n" + pExperiment.toString();

	}

	/**
	 * Returns a string intended to be read on a GUI that gives the size of the
	 * Database.
	 * 
	 * @param pDatabaseSize
	 *          Database size to be formated
	 * @return string giving the size of the database.
	 */
	private String getDatabaseSizeText(final int pDatabaseSize)
	{
		return "Database Size: " + Integer.toString(pDatabaseSize) + " experiments";
	}

	/**
	 * Runs on cycle of the view.
	 * 
	 * @see View#srsRun()
	 */
	public final boolean srsRun()
	{

		return true;
	}

	/**
	 * Initiates the main view
	 */
	public final void initiate()
	{
		this.mBestExperimentView.setText("...");

		setVisible(true);
	}

	/**
	 * Terminates the main view
	 */
	public final void terminate()
	{
		setVisible(false);
		this.mMaximumEvolutionPlot.hide();

	}

	/**
	 * Updates the maximum evolution Array.
	 * 
	 * @param pList
	 *          new maximum evolution Array
	 */
	public final void updateMaximumEvolutionArray(final List pList)
	{
		if (this.mMaximumEvolutionList == null)
		{
			this.mMaximumEvolutionList = pList;
			this.mMaximumEvolutionPlot.display();
			this.mMainContainer.add((Container) this.mMaximumEvolutionPlot);
			this.mMainContainer.validate();
		}

		this.mMaximumEvolutionList = pList;
		this.mDatabaseSizeView.setText(getDatabaseSizeText(pList.size()));

		this.mMaximumEvolutionPlot.update();
	}

	/**
	 * Updates best Experiment
	 * 
	 * @param pExperiment
	 *          new best Experiment
	 * @param pValue
	 *          objective value of the new best Experiment
	 */
	public final void updateBestExperiment(final IExperiment pExperiment, final double pValue)
	{
		this.mBestExperimentView.setText(getBestExperimentText(pExperiment, pValue));
	}

	/**
	 * Reformats the maximum evolution array of the databse to be plotted.
	 * 
	 * @see de.fhg.iwu.utils.math.IScalarFunction#computePoints(int)
	 */
	public final double[][] computePoints(final int pResolution)
	{
		final int lSize = this.mMaximumEvolutionList.size();

		double[][] lPlotArray;
		if (this.mMaximumEvolutionList != null)
		{
			lPlotArray = new double[lSize][2];
			for (int i = 0; i < lSize; i++)
			{
				lPlotArray[i][0] = i;
				lPlotArray[i][1] = ((Double) (this.mMaximumEvolutionList.get(i))).doubleValue();
			}
		}
		else
		{
			lPlotArray = new double[1][2];
			lPlotArray[0][0] = 0;
			lPlotArray[0][1] = 0;
		}
		return lPlotArray;
	}

	/**
	 * @see de.fhg.iwu.utils.math.IScalarFunction#evaluate(de.fhg.iwu.utils.math.MVector)
	 */
	public final double evaluate(final INumericalVector pVector)
	{
		return 0;
	}

	/**
	 * @see de.fhg.iwu.utils.math.IFunction#getInputDelta(int)
	 */
	public final double getInputDelta(final int pIndex)
	{
		return 1;
	}

	/**
	 * @see de.fhg.iwu.utils.math.IFunction#getInputDimension()
	 */
	public final int getInputDimension()
	{
		return 1;
	}

	/**
	 * @see de.fhg.iwu.utils.math.IFunction#getInputMax(int)
	 */
	public final double getInputMax(final int pIndex)
	{
		return this.mMaximumEvolutionList.size();
	}

	/**
	 * @see de.fhg.iwu.utils.math.IFunction#getInputMin(int)
	 */
	public final double getInputMin(final int pIndex)
	{
		return 0;
	}

	/**
	 * @see de.fhg.iwu.utils.math.IFunction#normalizeInputVector(de.fhg.iwu.utils.math.MVector)
	 */
	public final void normalizeInputVector(final INumericalVector pVector)
	{
		// no need to implement this
	}

	/**
	 * @see de.fhg.iwu.utils.math.IFunction#getOutputDimension()
	 */
	public int getOutputDimension()
	{
		// TODO Auto-generated method stub
		return 0;
	}

}