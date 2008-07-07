package utils.wiimote.modes;

import java.awt.CheckboxMenuItem;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import wiiremotej.IRLight;
import wiiremotej.WiiRemote;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WiiRemoteAdapter;

public class WiiServerClient implements Runnable
{

	public static final int sWiiServerPort = WiiServer.sWiiServerPort;

	CheckboxMenuItem mSlideShowModeItem = new CheckboxMenuItem(	"Server Mode",
																															false);

	public double[] mX = new double[4];
	public double[] mY = new double[4];
	public int mCount = 0;

	private Thread mThread;
	private boolean mStopSignal = false;

	public WiiServerClient()
	{
		super();
	}

	public void start()
	{
		mStopSignal = false;
		mThread = new Thread(this);
		mThread.setDaemon(true);
		mThread.start();
	}

	public void stop()
	{
		mStopSignal = true;
		mThread = null;
	}

	public void run()
	{

		DatagramSocket aSocket = null;

		try
		{
			aSocket = new DatagramSocket(sWiiServerPort + 1);

			byte[] lBytesOut = "start".getBytes();
			DatagramPacket packetout = new DatagramPacket(lBytesOut, lBytesOut.length);
			packetout.setAddress(InetAddress.getLocalHost());
			packetout.setPort(WiiServer.sWiiServerPort);
			aSocket.send(packetout);

			byte[] bufferin = new byte[100];
			DatagramPacket packetin = new DatagramPacket(bufferin, bufferin.length);
			while (!mStopSignal)
			{
				System.out.println("waiting...");
				aSocket.receive(packetin);
				String lReceived = new String(packetin.getData(),
																			0,
																			packetin.getLength());
				//System.out.println(lReceived);
				if (lReceived.length() > 0)
				{
					String[] lLights = lReceived.split(";");
					mCount = lLights.length;
					int i = 0;
					for (String lLightString : lLights)
					{
						String[] lXY = lLightString.split(",");
						final double lX = Double.parseDouble(lXY[0].trim());
						final double lY = Double.parseDouble(lXY[1].trim());
						synchronized (this)
						{
							mX[i] = lX;
							mY[i] = lY;
						}
						i++;
					}
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
}
