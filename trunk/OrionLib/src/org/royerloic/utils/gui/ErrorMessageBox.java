/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package org.royerloic.utils.gui;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public class ErrorMessageBox extends JDialog implements ActionListener
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7755620000168959195L;

	private boolean		mResult					= false;

	private Button		mButtonOk, mButtonCancel;

	private JTextArea	mJTextArea;

	public boolean getResult()
	{
		return this.mResult;
	}

	/**
	 * @param frame
	 * @param msg
	 * @param okcan
	 */
	public ErrorMessageBox(	final Frame pFrame,
													final String pTitle,
													final String pMessage,
													final boolean pOkCancelButtons)
	{
		super(pFrame, pMessage, true);
		getContentPane().setLayout(new BorderLayout());
		final String lSpaces = "";
		final String lMessage = lSpaces + pMessage + lSpaces;

		this.mJTextArea = new JTextArea(lMessage);
		this.mJTextArea.setEditable(false);
		this.mJTextArea.setSize(200, 30);

		getContentPane().add("Center", new JScrollPane(this.mJTextArea));
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
		pPanel.add(this.mButtonOk = new Button("OK"));
		this.mButtonOk.addActionListener(this);
	}

	/**
	 * @param p
	 */
	private void createCancelButton(final Panel pPanel)
	{
		this.mButtonCancel = new Button("Cancel");
		pPanel.add(this.mButtonCancel);
		this.mButtonCancel.addActionListener(this);
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
		if (pActionEvent.getSource() == this.mButtonOk)
		{
			this.mResult = true;
			setVisible(false);
		}
		else if (pActionEvent.getSource() == this.mButtonCancel)
		{
			this.mResult = false;
			setVisible(false);
		}
	}
}