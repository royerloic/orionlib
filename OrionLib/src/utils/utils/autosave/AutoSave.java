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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import utils.utils.CmdLine;
import utils.wiimote.WRLImpl;
import utils.wiimote.modes.SlideShowMode;
import utils.wiimote.modes.MouseMode;
import utils.wiimote.modes.PenMode;
import utils.wiimote.modes.GoogleEarthMode;
import utils.wiimote.modes.WiiMode;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WiiRemoteAdapter;
import wiiremotej.event.WiiRemoteDiscoveredEvent;
import wiiremotej.event.WiiRemoteDiscoveryListener;
import wiiremotej.event.WiiRemoteListener;

public class AutoSave implements MouseListener, ItemListener
{
	TrayIcon mTrayIcon;
	private CheckboxMenuItem mActiveItem;
	private AutoSaveRunnable mAutoSaveRunnable;
	private Image mActiveImage;
	private Image mInactiveImage;

	public static void main(String[] args)
	{
		try
		{
			Map<String, String> lParameters = CmdLine.getMap(args);

			AutoSave lAutoSave = new AutoSave(lParameters);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	public AutoSave(Map<String, String> pParameters)
	{
		super();

		try
		{
			if (SystemTray.isSupported())
			{

				SystemTray tray = SystemTray.getSystemTray();
				URL lActiveImageURL = AutoSave.class.getResource("autosave.active.png");
				mActiveImage = Toolkit.getDefaultToolkit().getImage(lActiveImageURL);
				URL lInactiveImageURL = AutoSave.class.getResource("autosave.inactive.png");
				mInactiveImage = Toolkit.getDefaultToolkit().getImage(lInactiveImageURL);

				PopupMenu popup = new PopupMenu();

				mActiveItem = new CheckboxMenuItem("Active", false);
				popup.add(mActiveItem);
				mActiveItem.addItemListener(this);
				mActiveItem.setEnabled(true);

				popup.addSeparator();

				MenuItem lExitItem = new MenuItem("Exit");
				ActionListener lExitItemListener = new ActionListener()
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
					Thread lThread = new Thread(mAutoSaveRunnable, "AutoSaveThread");
					lThread.setDaemon(true);
					lThread.setPriority(Thread.MAX_PRIORITY);
					lThread.start();
				}
				catch (AWTException e)
				{
					System.out.println("TrayIcon could not be added.");
				}

			}
			else
			{
				System.out.println("not supported");
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	public void mouseClicked(MouseEvent e)
	{

	}

	public void mouseEntered(MouseEvent e)
	{

	}

	public void mouseExited(MouseEvent e)
	{

	}

	public void mousePressed(MouseEvent e)
	{

	}

	public void mouseReleased(MouseEvent e)
	{

	}

	public void itemStateChanged(ItemEvent pE)
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
