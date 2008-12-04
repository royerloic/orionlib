package utils.network.groovyserver.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import utils.network.groovyserver.GroovyClient;
import utils.network.groovyserver.GroovyServer;

public class GroovyClientTest
{

	@SuppressWarnings("unchecked")
	@Test
	public void test()
	{

		try
		{
			final GroovyServer lGroovyServer = new GroovyServer("password=123");

			lGroovyServer.startServerNonBlocking();

			Thread.sleep(100);

			GroovyClient lGroovyClient = new GroovyClient("localhost",
																										4444,
																										"123",
																										null);

			assertTrue(lGroovyClient.connect());

			assertEquals(3, lGroovyClient.evaluate("1+2"));
			assertEquals(3, lGroovyClient.evaluate(">>1+2"));
			assertEquals(3, lGroovyClient.evaluate("ans"));

			lGroovyServer.stopServerNonBlocking();

		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			fail();
		}

	}

}
