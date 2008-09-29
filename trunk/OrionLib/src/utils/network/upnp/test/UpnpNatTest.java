package utils.network.upnp.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import net.sbbi.upnp.messages.UPNPResponseException;

import org.junit.Test;

import utils.network.groovyserver.GroovyServer;
import utils.network.upnp.UpnpNat;
import utils.web.dyndns.DynDnsUpdater;
import utils.web.dyndns.JddUpdateException;

public class UpnpNatTest
{
	@Test
	public void testDynDnsUpdate() throws InterruptedException
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
