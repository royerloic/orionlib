package utils.graphics.util;

import java.awt.Image;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class ViewImage
{
	public static void view(Image pImage) throws IOException
	{
		Icon icon = new ImageIcon(pImage);
		JLabel label = new JLabel(icon);

		final JFrame f = new JFrame("ImageIconExample");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(label);
		f.pack();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				f.setLocationRelativeTo(null);
				f.setVisible(true);
			}
		});
	}
}
