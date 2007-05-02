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

		this.mEngineControl = new JPanel();
		this.mEngineControl.setLayout(new BoxLayout(this.mEngineControl, BoxLayout.X_AXIS));

		this.mPauseResumeButton = new JButton("Start");
		this.mPauseResumeButton.addActionListener(this);

		this.mEngineControl.add(this.mPauseResumeButton);

		this.mEngineStatusView = new JPanel();
		this.mEngineStatusView.setLayout(new BoxLayout(this.mEngineStatusView, BoxLayout.X_AXIS));

		this.mEngineStatusTextField = new JTextField();
		this.mEngineStatusTextField.setEditable(false);
		this.mEngineStatusView.add(this.mEngineStatusTextField);

		this.mMainView = new MainView();
		this.mModelerView = new ModelerView();
		this.mMainContainerView = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.mModelerView, this.mMainView);
		this.mMainContainerView.setOneTouchExpandable(true);
		this.mMainContainerView.setResizeWeight(0.75);

		add(this.mEngineControl, BorderLayout.NORTH);
		add(this.mMainContainerView, BorderLayout.CENTER);
		add(this.mEngineStatusView, BorderLayout.SOUTH);

		validate();
	}

	/**
	 * @see IOptimalEngineView#updateIterations(int)
	 */
	public void updateIterations(final int mIterations)
	{

		this.mEngineStatusTextField.setText("The Optimal Engine as performed " + Integer.toString(mIterations)
				+ " iterations.");

		this.mEngineStatusTextField.repaint();

	}

	/**
	 * @see IOptimalEngineView#setControlListener(de.fhg.iwu.utils.EnhancedThread)
	 */
	public void setControlListener(final IOptimalEngineControl pIOptimalEngineControl)
	{
		this.mIOptimalEngineControl = pIOptimalEngineControl;
	}

	public void actionPerformed(final ActionEvent pEvent)
	{
		if (!this.mIOptimalEngineControl.isStarted())
		{
			this.mEngineStatusTextField.setText("Please wait until engine started...");
			this.mIOptimalEngineControl.doStart();
			this.mPauseResumeButton.setText("Pause");
		}
		else if (this.mIOptimalEngineControl.isPaused())
		{
			this.mIOptimalEngineControl.doResume();
			this.mPauseResumeButton.setText("Pause");
		}
		else if (!this.mIOptimalEngineControl.isPaused())
		{
			this.mIOptimalEngineControl.doPause();
			this.mPauseResumeButton.setText("Resume");
		}

	}

	/**
	 * @see IOptimalEngineView#initiate()
	 */
	public void initiate()
	{
		this.mMainView.initiate();
	}

	/**
	 * @see IOptimalEngineView#terminate()
	 */
	public void terminate()
	{
		this.mMainView.terminate();
	}

	/**
	 * @see IOptimalEngineView#updateBestExperiment(Experiment, double)
	 */
	public void updateBestExperiment(final IExperiment pExperiment, final double pValue)
	{

		this.mMainView.updateBestExperiment(pExperiment, pValue);

	}

	/**
	 * @see IOptimalEngineView#updateMaximumEvolution(double[])
	 */
	public void updateMaximumEvolution(final List pList)
	{

		this.mMainView.updateMaximumEvolutionArray(pList);

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

		this.mModelerView.updateModeler(mModeler);

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
		this.mPauseResumeButton.setEnabled(!pMode);
	}

}