package utils.wiimote.tools;

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

public class WiiTray extends WiiRemoteAdapter implements MouseListener, ItemListener
{
	TrayIcon mTrayIcon;

	ArrayList<WiiMode> mModeList;

	private static WiiRemote mWiiRemote;

	public static void main(String[] args)
	{
		try
		{
			Map<String, String> lParameters = CmdLine.getMap(args);
			ArrayList<WiiMode> lModeList = new ArrayList<WiiMode>();

			lModeList.add(new MouseMode());
			lModeList.add(new PenMode());
			lModeList.add(new GoogleEarthMode());
			lModeList.add(new SlideShowMode());

			WiiTray lWiiTray = new WiiTray(lParameters, lModeList);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	public WiiTray(Map<String, String> pParameters, ArrayList<WiiMode> pModeList)
	{
		super();

		mModeList = pModeList;

		try
		{
			if (SystemTray.isSupported())
			{

				SystemTray tray = SystemTray.getSystemTray();
				URL lURL = WiiTray.class.getResource("remote.png");
				Image image = Toolkit.getDefaultToolkit().getImage(lURL);

				PopupMenu popup = new PopupMenu();

				MenuItem lConnectItem = new MenuItem("Connect");
				ActionListener lConnectItemListener = new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						System.out.println("Connecting...");
						connect();
					}
				};
				lConnectItem.addActionListener(lConnectItemListener);
				popup.add(lConnectItem);

				MenuItem lDisconnectItem = new MenuItem("Disconnect");
				ActionListener lDisconnectItemListener = new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						System.out.println("Disconnecting...");
						disconnect();
					}
				};
				lDisconnectItem.addActionListener(lDisconnectItemListener);
				popup.add(lDisconnectItem);

				popup.addSeparator();

				for (WiiMode lWiiMode : mModeList)
				{
					popup.add(lWiiMode.getMenuItem());
					lWiiMode.getMenuItem().addItemListener(this);
					lWiiMode.getMenuItem().setEnabled(false);
				}

				popup.addSeparator();

				MenuItem lExitItem = new MenuItem("Exit");
				ActionListener lExitItemListener = new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						System.out.println("Exiting...");
						disconnect();
						System.exit(0);
					}
				};
				lExitItem.addActionListener(lExitItemListener);
				popup.add(lExitItem);

				mTrayIcon = new TrayIcon(image, "WiiTray", popup);

				mTrayIcon.setImageAutoSize(true);
				mTrayIcon.addMouseListener(this);

				try
				{
					tray.add(mTrayIcon);
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

	public void connect()
	{
		final WiiTray lWiiTray = this;
		
		Runnable lRunnable = new Runnable()
		{
			public void run()
			{
				try
				{
					WiiRemoteDiscoveryListener listener = new WiiRemoteDiscoveryListener()
					{
						public void wiiRemoteDiscovered(WiiRemoteDiscoveredEvent evt)
						{
							evt	.getWiiRemote()
									.addWiiRemoteListener(new WRLImpl(evt.getWiiRemote()));
						}

						public void findFinished(int numberFound)
						{
							System.out.println("Found " + numberFound + " remotes!");
						}
					};

					// Find and connect to a Wii Remote
					mWiiRemote = WiiRemoteJ.findRemote();

					if (mWiiRemote != null)
					{
						mWiiRemote.setLEDIlluminated(0, true);
						connected();
						mWiiRemote.addWiiRemoteListener(lWiiTray);
					}
					else
					{
						// Connection failed
						System.out.println("Connection failed.");
					}

				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};

		Thread lThread = new Thread(lRunnable);
		lThread.start();
	}

	public void disconnect()
	{
		if (mWiiRemote != null)
			mWiiRemote.disconnect();
		disconnected();
	}

	public void mouseClicked(MouseEvent e)
	{
		System.out.println("Tray Icon - Mouse clicked!");
	}

	public void mouseEntered(MouseEvent e)
	{
		System.out.println("Tray Icon - Mouse entered!");
	}

	public void mouseExited(MouseEvent e)
	{
		System.out.println("Tray Icon - Mouse exited!");
	}

	public void mousePressed(MouseEvent e)
	{
		System.out.println("Tray Icon - Mouse pressed!");
	}

	public void mouseReleased(MouseEvent e)
	{
		System.out.println("Tray Icon - Mouse released!");
	}

	public void connected()
	{
		for (WiiMode lWiiMode : mModeList)
		{
			lWiiMode.getMenuItem().setEnabled(true);
		}
	}

	public void disconnected()
	{
		for (WiiMode lWiiMode : mModeList)
		{
			//lWiiMode.deactivate(mWiiRemote);
			lWiiMode.getMenuItem().setEnabled(false);
		}
		mWiiRemote=null;
	}

	public void itemStateChanged(ItemEvent pE)
	{
		try
		{
			for (WiiMode lWiiMode : mModeList)
				if (pE.getSource().equals(lWiiMode.getMenuItem()))
				{
					lWiiMode.getMenuItem().setState(true);
					lWiiMode.activate(mWiiRemote);
				}
				else
				{
					lWiiMode.getMenuItem().setState(false);
					lWiiMode.deactivate(mWiiRemote);
				}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	
	}

}
