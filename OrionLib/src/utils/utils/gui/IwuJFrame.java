/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package utils.utils.gui;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.JFrame;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public class IwuJFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6366703561353850978L;
	private Image mWindowIcon;

	/**
	 * 
	 */
	private void customize()
	{
		try
		{
			// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
		}
		catch (final Exception lException)
		{
			lException.printStackTrace(System.out);
		} /**/

		final Toolkit kit = Toolkit.getDefaultToolkit();
		final URL lUrl = ClassLoader.getSystemResource("org/royerloic/utils/gui/images/IWU.gif");
		mWindowIcon = kit.getImage(lUrl);
		setIconImage(mWindowIcon); /**/

	}

	/**
	 * @throws java.awt.HeadlessException
	 */
	public IwuJFrame() throws HeadlessException
	{
		super();
		customize();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public IwuJFrame(final GraphicsConfiguration pGraphicsConfiguration)
	{
		super(pGraphicsConfiguration);
		customize();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public IwuJFrame(	final String pString,
										final GraphicsConfiguration pGraphicsConfiguration)
	{
		super(pString, pGraphicsConfiguration);
		customize();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public IwuJFrame(final String pString)
	{
		super(pString);
		customize();
		// TODO Auto-generated constructor stub
	}

} // fin classe BureauTest
