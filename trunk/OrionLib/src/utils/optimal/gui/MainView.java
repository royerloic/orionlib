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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import utils.math.INumericalVector;
import utils.math.IScalarFunction;
import utils.math.plot.IPlot;
import utils.math.plot.PlotScalarFuntionFactory;
import utils.optimal.interf.IExperiment;

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
	private static final long serialVersionUID = -572313894039683950L;

	/**
	 * <code>cBEST_EXPERIMENT_VIEW_HEIGHT</code> is the constant height of the
	 * Experiment View.
	 */
	private static final int cBEST_EXPERIMENT_VIEW_HEIGHT = 6;

	/**
	 * <code>cBEST_EXPERIMENT_VIEW_WIDTH</code> is the constant width of the
	 * Experiment View.
	 */
	private static final int cBEST_EXPERIMENT_VIEW_WIDTH = 10;

	/**
	 * <code>mBestExperimentView</code> is the best Experiment View.
	 */
	private JTextArea mBestExperimentView;

	/**
	 * <code>mControlContainer</code> is the control Container.
	 */
	private Container mControlContainer;

	/**
	 * <code>mDatabaseSizeView</code> is the Database size View JTextField.
	 */
	private JTextField mDatabaseSizeView;

	/**
	 * <code>mMainContainer</code> is the main Container.
	 */
	private Container mMainContainer;

	/**
	 * <code>mMaximumEvolutionPlot</code> is the maximum evolution plot.
	 */
	private IPlot mMaximumEvolutionPlot;

	/**
	 * <code>mStatusContainer</code> is the status Container.
	 */
	private Container mStatusContainer;

	/**
	 * <code>mMaximumEvolutionList</code> is the maximum evolution Array.
	 */
	private List mMaximumEvolutionList;

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

		mControlContainer = new JPanel();
		mControlContainer.setLayout(new BoxLayout(mControlContainer,
																							BoxLayout.X_AXIS));

		mMainContainer = new JPanel();
		mMainContainer.setLayout(new BoxLayout(mMainContainer, BoxLayout.Y_AXIS));

		try
		{
			mMaximumEvolutionPlot = PlotScalarFuntionFactory.build(this, 0, "Maximum");
		}
		catch (final Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace(System.out);
		}

		mBestExperimentView = new JTextArea(cBEST_EXPERIMENT_VIEW_HEIGHT,
																				cBEST_EXPERIMENT_VIEW_WIDTH);
		mBestExperimentView.setEditable(false);
		mMainContainer.add(new JScrollPane(mBestExperimentView));

		mStatusContainer = new JPanel();
		mStatusContainer.setLayout(new BoxLayout(mStatusContainer, BoxLayout.X_AXIS));

		mDatabaseSizeView = new JTextField("...");
		mDatabaseSizeView.setEditable(false);
		mStatusContainer.add(mDatabaseSizeView);

		add(mControlContainer, BorderLayout.NORTH);
		add(mMainContainer, BorderLayout.CENTER);
		add(mStatusContainer, BorderLayout.SOUTH);

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
	private String getBestExperimentText(	final IExperiment pExperiment,
																				final double pValue)
	{
		if (pExperiment == null)
			return "No Experiment in Database yet.";
		else
			return "Best Experiment: \n" + " Value: "
							+ pValue
							+ "\n"
							+ pExperiment.toString();

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
		mBestExperimentView.setText("...");

		setVisible(true);
	}

	/**
	 * Terminates the main view
	 */
	public final void terminate()
	{
		setVisible(false);
		mMaximumEvolutionPlot.hide();

	}

	/**
	 * Updates the maximum evolution Array.
	 * 
	 * @param pList
	 *          new maximum evolution Array
	 */
	public final void updateMaximumEvolutionArray(final List pList)
	{
		if (mMaximumEvolutionList == null)
		{
			mMaximumEvolutionList = pList;
			mMaximumEvolutionPlot.display();
			mMainContainer.add((Container) mMaximumEvolutionPlot);
			mMainContainer.validate();
		}

		mMaximumEvolutionList = pList;
		mDatabaseSizeView.setText(getDatabaseSizeText(pList.size()));

		mMaximumEvolutionPlot.update();
	}

	/**
	 * Updates best Experiment
	 * 
	 * @param pExperiment
	 *          new best Experiment
	 * @param pValue
	 *          objective value of the new best Experiment
	 */
	public final void updateBestExperiment(	final IExperiment pExperiment,
																					final double pValue)
	{
		mBestExperimentView.setText(getBestExperimentText(pExperiment, pValue));
	}

	/**
	 * Reformats the maximum evolution array of the databse to be plotted.
	 * 
	 * @see de.fhg.iwu.utils.math.IScalarFunction#computePoints(int)
	 */
	public final double[][] computePoints(final int pResolution)
	{
		final int lSize = mMaximumEvolutionList.size();

		double[][] lPlotArray;
		if (mMaximumEvolutionList != null)
		{
			lPlotArray = new double[lSize][2];
			for (int i = 0; i < lSize; i++)
			{
				lPlotArray[i][0] = i;
				lPlotArray[i][1] = ((Double) (mMaximumEvolutionList.get(i))).doubleValue();
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
		return mMaximumEvolutionList.size();
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