package utils.wiimote.tools;

import java.awt.AWTException;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import utils.utils.CmdLine;
import utils.wiimote.WRLImpl;
import utils.wiimote.modes.GoogleEarthMode;
import utils.wiimote.modes.MouseMode;
import utils.wiimote.modes.PenMode;
import utils.wiimote.modes.SlideShowMode;
import utils.wiimote.modes.WiiMode;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WiiRemoteAdapter;
import wiiremotej.event.WiiRemoteDiscoveredEvent;
import wiiremotej.event.WiiRemoteDiscoveryListener;

public class WiiTray extends WiiRemoteAdapter	implements
																							MouseListener,
																							ItemListener
{
	TrayIcon mTrayIcon;

	ArrayList<WiiMode> mModeList;

	private static WiiRemote mWiiRemote;

	public static void main(final String[] args)
	{
		try
		{
			final Map<String, String> lParameters = CmdLine.getMap(args);
			final ArrayList<WiiMode> lModeList = new ArrayList<WiiMode>();

			lModeList.add(new MouseMode());
			lModeList.add(new PenMode());
			lModeList.add(new SlideShowMode());
			lModeList.add(new GoogleEarthMode());

			new WiiTray(lParameters, lModeList);
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

	public WiiTray(	final Map<String, String> pParameters,
									final ArrayList<WiiMode> pModeList)
	{
		super();

		mModeList = pModeList;

		try
		{
			if (SystemTray.isSupported())
			{

				final SystemTray tray = SystemTray.getSystemTray();
				final URL lURL = WiiTray.class.getResource("remote.png");
				final Image image = Toolkit.getDefaultToolkit().getImage(lURL);

				final PopupMenu popup = new PopupMenu();

				final MenuItem lConnectItem = new MenuItem("Connect");
				final ActionListener lConnectItemListener = new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						System.out.println("Connecting...");
						connect();
					}
				};
				lConnectItem.addActionListener(lConnectItemListener);
				popup.add(lConnectItem);

				final MenuItem lDisconnectItem = new MenuItem("Disconnect");
				final ActionListener lDisconnectItemListener = new ActionListener()
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

				for (final WiiMode lWiiMode : mModeList)
				{
					popup.add(lWiiMode.getMenuItem());
					lWiiMode.getMenuItem().addItemListener(this);
					lWiiMode.getMenuItem().setEnabled(false);
				}

				popup.addSeparator();

				final MenuItem lExitItem = new MenuItem("Exit");
				final ActionListener lExitItemListener = new ActionListener()
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

	public void connect()
	{
		final WiiTray lWiiTray = this;

		final Runnable lRunnable = new Runnable()
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

		final Thread lThread = new Thread(lRunnable);
		lThread.start();
	}

	public void disconnect()
	{
		if (mWiiRemote != null)
		{
			mWiiRemote.disconnect();
		}
		disconnected();
	}

	public void mouseClicked(final MouseEvent e)
	{
		System.out.println("Tray Icon - Mouse clicked!");
	}

	public void mouseEntered(final MouseEvent e)
	{
		System.out.println("Tray Icon - Mouse entered!");
	}

	public void mouseExited(final MouseEvent e)
	{
		System.out.println("Tray Icon - Mouse exited!");
	}

	public void mousePressed(final MouseEvent e)
	{
		System.out.println("Tray Icon - Mouse pressed!");
	}

	public void mouseReleased(final MouseEvent e)
	{
		System.out.println("Tray Icon - Mouse released!");
	}

	public void connected()
	{
		for (final WiiMode lWiiMode : mModeList)
		{
			lWiiMode.getMenuItem().setEnabled(true);
		}
	}

	public void disconnected()
	{
		for (final WiiMode lWiiMode : mModeList)
		{
			// lWiiMode.deactivate(mWiiRemote);
			lWiiMode.getMenuItem().setEnabled(false);
		}
		mWiiRemote = null;
	}

	public void itemStateChanged(final ItemEvent pE)
	{
		try
		{
			for (final WiiMode lWiiMode : mModeList)
			{
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
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}

	}

}
