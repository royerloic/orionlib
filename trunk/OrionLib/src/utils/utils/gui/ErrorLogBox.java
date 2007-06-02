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
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public class ErrorLogBox extends IwuJFrame
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7832967664116855037L;

	private final boolean		mResult					= false;

	private String		mMessage;

	private JTextArea	mJTextArea;

	public boolean getResult()
	{
		return mResult;
	}

	/**
	 * @param frame
	 * @param msg
	 * @param okcan
	 */
	public ErrorLogBox(	final Frame pFrame,
											final String pTitle,
											final String pMessage,
											final boolean pOkCancelButtons)
	{
		super(pTitle);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		mMessage = pMessage;

		mJTextArea = new JTextArea();
		mJTextArea.setEditable(false);
		mJTextArea.setMinimumSize(new Dimension(400, 130));

		getContentPane().add("Center", new JScrollPane(mJTextArea));

		setResizable(true);
		setTitle(pTitle);
		// centerFrame();
		pack();

		setSize(400, 130);
		setVisible(true);
	}

	public void addErrorLog(final String pString)
	{
		mMessage = mMessage + "-Error-\n" + pString + "\n\n";
		mJTextArea.setText(mMessage);
		if (!isVisible())
			setVisible(true);
	}

}