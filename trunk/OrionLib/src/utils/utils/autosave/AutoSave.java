package utils.utils.autosave;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.Map;

import utils.utils.CmdLine;

public class AutoSave implements MouseListener, ItemListener
{
	TrayIcon mTrayIcon;
	private CheckboxMenuItem mActiveItem;
	private AutoSaveRunnable mAutoSaveRunnable;
	private Image mActiveImage;
	private Image mInactiveImage;

	public static void main(final String[] args)
	{
		try
		{
			final Map<String, String> lParameters = CmdLine.getMap(args);

			final AutoSave lAutoSave = new AutoSave(lParameters);
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

	public AutoSave(final Map<String, String> pParameters)
	{
		super();

		try
		{
			if (SystemTray.isSupported())
			{

				final SystemTray tray = SystemTray.getSystemTray();
				final URL lActiveImageURL = AutoSave.class.getResource("autosave.active.png");
				mActiveImage = Toolkit.getDefaultToolkit().getImage(lActiveImageURL);
				final URL lInactiveImageURL = AutoSave.class.getResource("autosave.inactive.png");
				mInactiveImage = Toolkit.getDefaultToolkit()
																.getImage(lInactiveImageURL);

				final PopupMenu popup = new PopupMenu();

				mActiveItem = new CheckboxMenuItem("Active", false);
				popup.add(mActiveItem);
				mActiveItem.addItemListener(this);
				mActiveItem.setEnabled(true);

				popup.addSeparator();

				final MenuItem lExitItem = new MenuItem("Exit");
				final ActionListener lExitItemListener = new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						mAutoSaveRunnable.mKeepAlive = false;
						System.out.println("Exiting...");
						System.exit(0);
					}
				};
				lExitItem.addActionListener(lExitItemListener);
				popup.add(lExitItem);

				mTrayIcon = new TrayIcon(mInactiveImage, "AutoSave", popup);

				mTrayIcon.setImageAutoSize(true);
				mTrayIcon.addMouseListener(this);

				try
				{
					tray.add(mTrayIcon);

					mAutoSaveRunnable = new AutoSaveRunnable(30, new int[]
					{ KeyEvent.VK_CONTROL, KeyEvent.VK_S });
					final Thread lThread = new Thread(mAutoSaveRunnable, "AutoSaveThread");
					lThread.setDaemon(true);
					lThread.setPriority(Thread.MAX_PRIORITY);
					lThread.start();
				}
				catch (final AWTException e)
				{
					System.out.println("TrayIcon could not be added.");
				}

			}
			else
			{
				System.out.println("not supported");
			}
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

	public void mouseClicked(final MouseEvent e)
	{

	}

	public void mouseEntered(final MouseEvent e)
	{

	}

	public void mouseExited(final MouseEvent e)
	{

	}

	public void mousePressed(final MouseEvent e)
	{

	}

	public void mouseReleased(final MouseEvent e)
	{

	}

	public void itemStateChanged(final ItemEvent pE)
	{
		if (mActiveItem.getState())
		{
			mActiveItem.setLabel("Deactivate");
			mAutoSaveRunnable.mActive = true;
			mTrayIcon.setImage(mActiveImage);
		}
		else
		{
			mActiveItem.setLabel("Activate");
			mAutoSaveRunnable.mActive = false;
			mTrayIcon.setImage(mInactiveImage);
		}

	}

}
