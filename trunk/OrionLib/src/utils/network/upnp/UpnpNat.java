package utils.network.upnp;

import java.io.IOException;
import java.net.InetAddress;

import net.sbbi.upnp.messages.UPNPResponseException;
import utils.network.localip.LocalIpFinder;

public class UpnpNat
{

	public static final boolean mapPort(String description,
																			int internalPort,
																			int externalPort,
																			String protocol) throws IOException,
																											UPNPResponseException
	{
		int discoveryTimeout = 5000; // 5 secs to receive a response from devices

		InternetGatewayDevice[] IGDs = InternetGatewayDevice.getDevices(discoveryTimeout);
		if (IGDs != null)
		{
			// let's the the first device found
			InternetGatewayDevice testIGD = IGDs[0];
			System.out.println("number of devices: " + IGDs.length);
			System.out.println("Found device " + testIGD.getIGDRootDevice()
																									.getModelName());
			// now let's open the port
			String localHostIP = LocalIpFinder.getLocalIp();
			System.out.println("localHostIP= '" + localHostIP + "'");
			boolean mapped = testIGD.addPortMapping(description,
																							null,
																							internalPort,
																							externalPort,
																							localHostIP,
																							0,
																							"TCP");
			return mapped;
		}
		return false;
	}

	public static final boolean unmapPort(int externalPort, String protocol) throws IOException,
																																					UPNPResponseException
	{
		int discoveryTimeout = 5000; // 5 secs to receive a response from devices

		InternetGatewayDevice[] IGDs = InternetGatewayDevice.getDevices(discoveryTimeout);
		if (IGDs != null)
		{
			// let's the the first device found
			InternetGatewayDevice testIGD = IGDs[0];
			System.out.println("Found device " + testIGD.getIGDRootDevice()
																									.getModelName());
			// now let's open the port
			String localHostIP = InetAddress.getLocalHost().getHostAddress();

			boolean unmapped = testIGD.deletePortMapping(null, externalPort, protocol);
			return unmapped;
		}
		return false;
	}

}
