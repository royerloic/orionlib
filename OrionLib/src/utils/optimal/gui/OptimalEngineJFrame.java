/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *   
 */

package utils.optimal.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import utils.math.IScalarFunction;
import utils.optimal.interf.IExperiment;
import utils.optimal.stdimpl.Experiment;
import utils.utils.gui.ErrorLogBox;
import utils.utils.gui.IwuJFrame;

/**
 * OptimalEngineJFrame wraps an optimal engine and its view with a IwuJFrame.
 * 
 * @author MSc. Ing. Loic Royer
 */
public class OptimalEngineJFrame extends IwuJFrame implements
																									WindowListener,
																									IOptimalEngineView
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1016157805708654249L;
	private static final int cOPTIMAL_ENGINE_FRAME_HEIGHT = 600;
	private static final int cOPTIMAL_ENGINE_FRAME_WIDTH = 900;

	private ErrorLogBox mErrorLogBox;

	private OptimalEngineJPanel mOptimalEngineJPanel;

	private IOptimalEngineControl mIOptimalEngineControl;

	public OptimalEngineJFrame()
	{
		super();
		setTitle("-Optimal- Press Start...");
		setVisible(false);
		addWindowListener(this);
		mOptimalEngineJPanel = new OptimalEngineJPanel();
		setContentPane(mOptimalEngineJPanel);
		setSize(cOPTIMAL_ENGINE_FRAME_WIDTH, cOPTIMAL_ENGINE_FRAME_HEIGHT);
		validate();
		setVisible(true);
	}

	/**
	 * @see IOptimalEngineView#start()
	 */
	public void initiate()
	{
		mOptimalEngineJPanel.initiate();
	}

	/**
	 * @see IOptimalEngineView#stop()
	 */
	public void terminate()
	{
		setVisible(false);
		removeWindowListener(this);
		mOptimalEngineJPanel.terminate();

	}

	/**
	 * @see IOptimalEngineView#updateMaximumEvolution(double[])
	 */
	public void updateMaximumEvolution(final List pList)
	{
		mOptimalEngineJPanel.updateMaximumEvolution(pList);
	}

	/**
	 * @see IOptimalEngineView#updateIterations(int)
	 */
	public void updateIterations(final int mIterations)
	{
		mOptimalEngineJPanel.updateIterations(mIterations);
	}

	/**
	 * @see IOptimalEngineView#notifyError(java.lang.String, java.lang.Throwable)
	 */
	public void notifyError(final String pError, final Throwable pException)
	{
		if (mErrorLogBox == null)
			mErrorLogBox = new ErrorLogBox(this, "Error", "", false);

		mErrorLogBox.addErrorLog(pError + "\n" + pException.toString());

		System.out.println(pError);
		pException.printStackTrace(System.out);

	}

	/**
	 * @see IOptimalEngineView#updateBestExperiment(Experiment, double)
	 */
	public void updateBestExperiment(	final IExperiment pExperiment,
																		final double pValue)
	{
		mOptimalEngineJPanel.updateBestExperiment(pExperiment, pValue);
	}

	/**
	 * @see IOptimalEngineView#updateDesignerStatus(java.lang.String)
	 */
	public void updateDesignerStatus(final String pStatus)
	{
		mOptimalEngineJPanel.updateDesignerStatus(pStatus);
	}

	/**
	 * @see IOptimalEngineView#setControlListener(de.fhg.iwu.utils.EnhancedThread)
	 */
	public void setControlListener(final IOptimalEngineControl pIOptimalEngineControl)
	{
		mIOptimalEngineControl = pIOptimalEngineControl;
		mOptimalEngineJPanel.setControlListener(pIOptimalEngineControl);
	}

	/**
	 * @see IOptimalEngineView#updateModelerView(de.fhg.iwu.utils.math.IScalarFunction)
	 */
	public void updateModelerView(final IScalarFunction mModeler)
	{
		mOptimalEngineJPanel.updateModelerView(mModeler);

	}

	/**
	 * @see IOptimalEngineView#setProjectName(java.lang.String)
	 */
	public void setProjectName(final String pName)
	{
		setTitle("-Optimal- Engine Running on Project: '" + pName + "'");
	}

	private void exit()
	{
		terminate();
		mIOptimalEngineControl.doStop();
	}

	/**
	 * @see IOptimalEngineView#setViewOnlyMode(boolean)
	 */
	public void setViewOnlyMode(final boolean pMode)
	{
		mOptimalEngineJPanel.setViewOnlyMode(pMode);
	}

	// ******************************************************************************/

	public void windowActivated(final WindowEvent pWindowEvent)
	{
		// System.out.println(pWindowEvent);
	}

	public void windowClosed(final WindowEvent pWindowEvent)
	{
		// System.out.println(pWindowEvent);
	}

	public void windowClosing(final WindowEvent pWindowEvent)
	{
		// System.out.println(pWindowEvent);
		exit();
	}

	public void windowDeactivated(final WindowEvent pWindowEvent)
	{
		// System.out.println(pWindowEvent);
	}

	public void windowDeiconified(final WindowEvent pWindowEvent)
	{
		// TODO Auto-generated method stub

	}

	public void windowIconified(final WindowEvent pWindowEvent)
	{
		// TODO Auto-generated method stub

	}

	public void windowOpened(final WindowEvent pWindowEvent)
	{
		// TODO Auto-generated method stub

	}

}
