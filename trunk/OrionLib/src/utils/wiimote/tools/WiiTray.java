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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import utils.utils.CmdLine;

public class WiiTray implements MouseListener, ActionListener, ItemListener, WiiStatusListener
{
	TrayIcon																mTrayIcon;

	HashMap<CheckboxMenuItem, WiiMode.Mode>	mCheckboxMenuIteMap	= new HashMap<CheckboxMenuItem, WiiMode.Mode>();

	private WiiMode													mWiiMode;

	public static void main(String[] args)
	{
		try
		{
			Map<String, String> lParameters = CmdLine.getMap(args);
			WiiTray lWiiTray = new WiiTray(lParameters);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	public WiiTray(Map<String, String> pParameters)
	{
		super();

		try
		{
			mWiiMode = new WiiMode();

			if (SystemTray.isSupported())
			{
				mWiiMode.addDisconnectListener(this);
				

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
							mWiiMode.connect();

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
							mWiiMode.disconnect();
						}
					};
				lDisconnectItem.addActionListener(lDisconnectItemListener);
				popup.add(lDisconnectItem);

				popup.addSeparator();

				CheckboxMenuItem lMouseModeItem = new CheckboxMenuItem(	"Mouse Mode",
																																false);
				lMouseModeItem.addItemListener(this);
				mCheckboxMenuIteMap.put(lMouseModeItem, WiiMode.Mode.mouse);
				popup.add(lMouseModeItem);

				CheckboxMenuItem lPenModeItem = new CheckboxMenuItem("Pen Mode", false);
				lPenModeItem.addItemListener(this);
				mCheckboxMenuIteMap.put(lPenModeItem, WiiMode.Mode.pen);
				popup.add(lPenModeItem);

				CheckboxMenuItem lSlideShowMode = new CheckboxMenuItem(	"SlideShow Mode",
																																false);
				lSlideShowMode.addItemListener(this);
				mCheckboxMenuIteMap.put(lSlideShowMode, WiiMode.Mode.slideshow);
				popup.add(lSlideShowMode);
				
				for (CheckboxMenuItem lCheckboxMenuItem : mCheckboxMenuIteMap.keySet())
				{
					lCheckboxMenuItem.setEnabled(false);
				}

				popup.addSeparator();

				MenuItem lExitItem = new MenuItem("Exit");
				ActionListener lExitItemListener = new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							System.out.println("Exiting...");
							mWiiMode.disconnect();
							System.exit(0);
						}
					};
				lExitItem.addActionListener(lExitItemListener);
				popup.add(lExitItem);

				mTrayIcon = new TrayIcon(image, "WiiTray", popup);

				mTrayIcon.setImageAutoSize(true);
				mTrayIcon.addActionListener(this);
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

	public void actionPerformed(ActionEvent e)
	{
		mTrayIcon.displayMessage(	"Action Event",
															"An Action Event Has Been Performed!",
															TrayIcon.MessageType.INFO);
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

	public void itemStateChanged(ItemEvent pE)
	{
		System.out.println("itemStateChanged: \n " + pE);
		for (CheckboxMenuItem lCheckboxMenuItem : mCheckboxMenuIteMap.keySet())
			if (lCheckboxMenuItem != pE.getSource())
			{
				lCheckboxMenuItem.setState(false);
			}
			else
			{

			}

		WiiMode.Mode lWiiModeMode = mCheckboxMenuIteMap.get(pE.getSource());

		mWiiMode.activate(lWiiModeMode);
	}

	@Override
	public void connected()
	{
		for (CheckboxMenuItem lCheckboxMenuItem : mCheckboxMenuIteMap.keySet())
		{
			lCheckboxMenuItem.setEnabled(true);
		}		
	}
	
	@Override
	public void disconnected()
	{
		for (CheckboxMenuItem lCheckboxMenuItem : mCheckboxMenuIteMap.keySet())
		{
			lCheckboxMenuItem.setEnabled(false);
		}		
	}


}
