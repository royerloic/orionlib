package utils.wiimote.modes;

import java.awt.CheckboxMenuItem;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import wiiremotej.IRLight;
import wiiremotej.WiiRemote;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WiiRemoteAdapter;

public class WiiServer extends WiiRemoteAdapter implements Runnable, WiiMode
{

	public static final int sWiiServerPort = 1234;

	CheckboxMenuItem mSlideShowModeItem = new CheckboxMenuItem(	"Server Mode",
																															false);

	boolean mActive = false;

	double[] mX = new double[4];
	double[] mY = new double[4];

	private Thread mThread;
	private boolean mStopSignal = false;

	private int mCount;

	public WiiServer()
	{
		super();
	}

	public void activate(WiiRemote pRemote)
	{
		try
		{
			pRemote.addWiiRemoteListener(this);
			pRemote.setAccelerometerEnabled(false);
			pRemote.setIRSensorEnabled(true, WRIREvent.BASIC);
		}
		catch (final Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mStopSignal = false;
		mThread = new Thread(this);
		mThread.setDaemon(true);
		mThread.start();

	}

	public void deactivate(WiiRemote pRemote)
	{
		try
		{
			pRemote.removeWiiRemoteListener(this);
			pRemote.setIRSensorEnabled(false, WRIREvent.BASIC);
		}
		catch (final Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mStopSignal = true;
		mThread = null;
	}

	public CheckboxMenuItem getMenuItem()
	{
		return mSlideShowModeItem;
	}

	@SuppressWarnings("deprecation")
	public void run()
	{

		DatagramSocket aSocket = null;

		try
		{
			aSocket = new DatagramSocket(sWiiServerPort);
			aSocket.setSoTimeout(1);
			byte[] bufferin = new byte[100];
			DatagramPacket packetin = new DatagramPacket(bufferin, bufferin.length);
			byte[] bufferout = new byte[100];
			DatagramPacket packetout = new DatagramPacket(bufferout, bufferout.length);

			StringBuilder lStringBuilderOut = new StringBuilder();

			while (!mStopSignal)
			{

				try
				{
					aSocket.receive(packetin);

					String lDataReceived = new String(packetin.getData(),
																						0,
																						packetin.getLength());
					if (lDataReceived.equals("start"))
					{
						mActive = true;
					}
					else if (lDataReceived.equals("stop"))
					{
						mActive = false;
					}
				}
				catch (IOException e)
				{

				}

				if (mActive)
				{
					lStringBuilderOut.setLength(0);
					for (int i = 0; i < mCount; i++)
					{
						lStringBuilderOut.append(" " + mX[i] + ", " + mY[i] + " ;");
					}
					String lDataString = lStringBuilderOut.toString();
					byte[] lBytes = lDataString.getBytes();
					packetout.setData(lBytes, 0, lBytes.length);

					packetout.setSocketAddress(packetin.getSocketAddress());
					packetout.setPort(packetin.getPort());
					aSocket.send(packetout);
				}
			}
		}
		catch (SocketException e)
		{
			System.out.println("Socket: " + e.getMessage());
		}
		catch (IOException e)
		{
			System.out.println("IO: " + e.getMessage());
		}
		finally
		{
			if (aSocket != null)
				aSocket.close();
		}

	}

	public void IRInputReceived(final WRIREvent pWRIREvent)
	{
		int i = 0;
		for (final IRLight light : pWRIREvent.getIRLights())
		{
			if (light != null)
			{
				synchronized (this)
				{
					mX[i] = light.getX();
					mY[i] = light.getY();
				}
				i++;
			}
		}

		mCount = i;

	}

}
