package utils.network.localip;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class LocalIpFinder
{
	public static final String getLocalIp() throws SocketException
	{
		String lIp = getLocalInetAddress().toString();
		return lIp.substring(1);
	}
	
	public static final InetAddress getLocalInetAddress() throws SocketException
	{
		Enumeration<NetworkInterface> networkinterfaces = NetworkInterface.getNetworkInterfaces();
		for (Enumeration e = networkinterfaces; e.hasMoreElements();)
		{
			NetworkInterface networkinterface = (NetworkInterface) e.nextElement();
			if (!networkinterface.isLoopback() && networkinterface.isUp())
			{
				for (InterfaceAddress lInterfaceAddress : networkinterface.getInterfaceAddresses())
				{
					InetAddress address = lInterfaceAddress.getAddress();
					if (!address.toString().contains(":"))
					{
						return address;
					}
				}
			}
		}
		return null;
	}
}
