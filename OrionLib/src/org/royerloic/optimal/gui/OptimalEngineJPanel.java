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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import org.royerloic.math.IScalarFunction;
import org.royerloic.optimal.interf.IExperiment;
import org.royerloic.optimal.stdimpl.Experiment;

/**
 * OptimalEngineJPanel is a View for an Optimal Engine.
 * 
 * @author MSc. Ing. Loic Royer
 */
public class OptimalEngineJPanel extends JPanel implements ActionListener, IOptimalEngineView
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2907702805084618887L;

	private IOptimalEngineControl	mIOptimalEngineControl;

	private Container							mEngineControl;

	private JButton								mPauseResumeButton;

	private JSplitPane						mMainContainerView;

	private ModelerView						mModelerView;

	private MainView							mMainView;

	private Container							mEngineStatusView;

	private JTextField						mEngineStatusTextField;

	/**
	 * Constructs an OptimalEngineJPanel given an OptimalEngine.
	 * 
	 * @param pOptimalEngine
	 *          optimal engine.
	 */
	public OptimalEngineJPanel()
	{
		super();

		setLayout(new BorderLayout(5, 5));

		mEngineControl = new JPanel();
		mEngineControl.setLayout(new BoxLayout(mEngineControl, BoxLayout.X_AXIS));

		mPauseResumeButton = new JButton("Start");
		mPauseResumeButton.addActionListener(this);

		mEngineControl.add(mPauseResumeButton);

		mEngineStatusView = new JPanel();
		mEngineStatusView.setLayout(new BoxLayout(mEngineStatusView, BoxLayout.X_AXIS));

		mEngineStatusTextField = new JTextField();
		mEngineStatusTextField.setEditable(false);
		mEngineStatusView.add(mEngineStatusTextField);

		mMainView = new MainView();
		mModelerView = new ModelerView();
		mMainContainerView = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mModelerView, mMainView);
		mMainContainerView.setOneTouchExpandable(true);
		mMainContainerView.setResizeWeight(0.75);

		add(mEngineControl, BorderLayout.NORTH);
		add(mMainContainerView, BorderLayout.CENTER);
		add(mEngineStatusView, BorderLayout.SOUTH);

		validate();
	}

	/**
	 * @see IOptimalEngineView#updateIterations(int)
	 */
	public void updateIterations(final int mIterations)
	{

		mEngineStatusTextField.setText("The Optimal Engine as performed " + Integer.toString(mIterations)
				+ " iterations.");

		mEngineStatusTextField.repaint();

	}

	/**
	 * @see IOptimalEngineView#setControlListener(de.fhg.iwu.utils.EnhancedThread)
	 */
	public void setControlListener(final IOptimalEngineControl pIOptimalEngineControl)
	{
		mIOptimalEngineControl = pIOptimalEngineControl;
	}

	public void actionPerformed(final ActionEvent pEvent)
	{
		if (!mIOptimalEngineControl.isStarted())
		{
			mEngineStatusTextField.setText("Please wait until engine started...");
			mIOptimalEngineControl.doStart();
			mPauseResumeButton.setText("Pause");
		}
		else if (mIOptimalEngineControl.isPaused())
		{
			mIOptimalEngineControl.doResume();
			mPauseResumeButton.setText("Pause");
		}
		else if (!mIOptimalEngineControl.isPaused())
		{
			mIOptimalEngineControl.doPause();
			mPauseResumeButton.setText("Resume");
		}

	}

	/**
	 * @see IOptimalEngineView#initiate()
	 */
	public void initiate()
	{
		mMainView.initiate();
	}

	/**
	 * @see IOptimalEngineView#terminate()
	 */
	public void terminate()
	{
		mMainView.terminate();
	}

	/**
	 * @see IOptimalEngineView#updateBestExperiment(Experiment, double)
	 */
	public void updateBestExperiment(final IExperiment pExperiment, final double pValue)
	{

		mMainView.updateBestExperiment(pExperiment, pValue);

	}

	/**
	 * @see IOptimalEngineView#updateMaximumEvolution(double[])
	 */
	public void updateMaximumEvolution(final List pList)
	{

		mMainView.updateMaximumEvolutionArray(pList);

	}

	/**
	 * @see IOptimalEngineView#updateDesignerStatus(java.lang.String)
	 */
	public void updateDesignerStatus(final String pStatus)
	{

	}

	/**
	 * @see IOptimalEngineView#updateModelerView(de.fhg.iwu.utils.math.IScalarFunction)
	 */
	public void updateModelerView(final IScalarFunction mModeler)
	{

		mModelerView.updateModeler(mModeler);

	}

	/**
	 * @see IOptimalEngineView#setProjectName(java.lang.String)
	 */
	public void setProjectName(final String pName)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see IOptimalEngineView#notifyError(java.lang.String, java.lang.Throwable)
	 */
	public void notifyError(final String pError, final Throwable pException)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see IOptimalEngineView#setViewOnlyMode(boolean)
	 */
	public void setViewOnlyMode(final boolean pMode)
	{
		mPauseResumeButton.setEnabled(!pMode);
	}

}