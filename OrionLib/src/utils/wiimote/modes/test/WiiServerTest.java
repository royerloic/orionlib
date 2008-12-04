package utils.wiimote.modes.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import utils.wiimote.modes.WiiServer;
import utils.wiimote.modes.WiiServerClient;

public class WiiServerTest
{

	// @Test
	public void testWiiServerClient() throws IOException, InterruptedException
	{
		WiiServerClient lWiiServerClient = new WiiServerClient();
		lWiiServerClient.start();
		Thread.sleep(100000);
		lWiiServerClient.stop();

	}

	// @Test
	public void testWiiServer() throws IOException
	{
		DatagramSocket aSocket = null;

		try
		{
			aSocket = new DatagramSocket(2345);

			byte[] lBytesOut = "start".getBytes();
			DatagramPacket packetout = new DatagramPacket(lBytesOut, lBytesOut.length);
			packetout.setAddress(InetAddress.getLocalHost());
			packetout.setPort(WiiServer.sWiiServerPort);
			aSocket.send(packetout);

			byte[] bufferin = new byte[100];
			DatagramPacket packetin = new DatagramPacket(bufferin, bufferin.length);
			for (int i = 0; i < 1000000000; i++)
			{
				System.out.println("waiting...");
				aSocket.receive(packetin);
				String lReceived = new String(packetin.getData(),
																			0,
																			packetin.getLength());
				System.out.println(lReceived);
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
