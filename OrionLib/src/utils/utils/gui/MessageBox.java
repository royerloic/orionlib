/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package utils.utils.gui;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public class MessageBox extends JDialog implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1959586325540923937L;

	private boolean mResult = false;

	private Button mButtonOk, mButtonCancel;

	public boolean getResult()
	{
		return mResult;
	}

	/**
	 * @param frame
	 * @param msg
	 * @param okcan
	 */
	public MessageBox(final Frame pFrame,
										final String pTitle,
										final String pMessage,
										final boolean pOkCancelButtons)
	{
		super(pFrame, pMessage, true);
		getContentPane().setLayout(new BorderLayout());
		final String lSpaces = "            ";
		final String lMessage = lSpaces + pMessage + lSpaces;
		getContentPane().add("Center", new Label(lMessage));
		addOKCancelPanel(pOkCancelButtons);
		setResizable(false);
		setTitle(pTitle);
		createFrame();
		pack();
		setVisible(true);
	}

	/**
	 * @param okcan
	 */
	private void addOKCancelPanel(final boolean pOkCancelButtons)
	{
		final Panel lPanel = new Panel();
		lPanel.setLayout(new FlowLayout());
		createOKButton(lPanel);
		if (pOkCancelButtons)
			createCancelButton(lPanel);
		getContentPane().add("South", lPanel);
	}

	/**
	 * @param p
	 */
	private void createOKButton(final Panel pPanel)
	{
		pPanel.add(mButtonOk = new Button("OK"));
		mButtonOk.addActionListener(this);
	}

	/**
	 * @param p
	 */
	private void createCancelButton(final Panel pPanel)
	{
		mButtonCancel = new Button("Cancel");
		pPanel.add(mButtonCancel);
		mButtonCancel.addActionListener(this);
	}

	/**
	 * 
	 */
	private void createFrame()
	{
		final Dimension lDimension = getToolkit().getScreenSize();
		setLocation(lDimension.width / 3, lDimension.height / 3);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(final ActionEvent pActionEvent)
	{
		if (pActionEvent.getSource() == mButtonOk)
		{
			mResult = true;
			setVisible(false);
		}
		else if (pActionEvent.getSource() == mButtonCancel)
		{
			mResult = false;
			setVisible(false);
		}
	}
}