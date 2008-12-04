package utils.network.upnp.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import net.sbbi.upnp.messages.UPNPResponseException;
import utils.network.groovyserver.GroovyServer;
import utils.network.upnp.UpnpNat;

public class UpnpNatTest
{
	// @Test
	public void testUpnpNatTest() throws InterruptedException
	{
		try
		{
			final GroovyServer lGroovyServer = new GroovyServer();

			lGroovyServer.startServerNonBlocking();
			assertTrue(UpnpNat.mapPort(" ", 4444, 4444, ""));
			Thread.sleep(1000000);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (UPNPResponseException e)
		{
			e.printStackTrace();
			fail();
		}

	}

}
