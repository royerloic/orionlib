package utils.network.groovyserver.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.Socket;
import java.util.Set;

import org.junit.Test;

import utils.network.groovyserver.GroovyClient;
import utils.network.groovyserver.GroovyServer;

public class GroovyServerTest
{

	@Test
	public void test()
	{

		try
		{
			final GroovyServer lGroovyServer = new GroovyServer("password=123");

			lGroovyServer.startServerNonBlocking();

			Thread.sleep(100);

			final Socket lSocket = GroovyClient.createLocalSocket();

			// sending password:
			GroovyClient.sendQueryGetString(lSocket, "123");

			// checking the sending of a query and the decoding:
			assertTrue((String) GroovyClient.sendQueryAndDecode(lSocket,
																													"a=\"hello\"") == "hello");

			// send query and get answear as object:
			assertTrue((Integer) GroovyClient.sendQueryGetObject(lSocket, "12") == 12);
			assertTrue(((Set<Integer>) GroovyClient.sendQueryGetObject(	lSocket,
																																	"[1,2,3] as Set")).contains(1));

			// saving session:
			final String lResult = GroovyClient.sendQueryGetString(	lSocket,
																															"server.save(\"test\")");
			assertEquals(lResult, "true");

			// Creatig new server and loading
			final GroovyServer lGroovyServer2 = new GroovyServer("port=5555 password=123");

			// loading the previously saved session:
			assertEquals(	GroovyClient.sendQueryGetString(lSocket,
																										"server.load(\"test\")"),
										"true");

			// checking that the session was really saved:
			assertTrue((String) GroovyClient.sendQueryAndDecode(lSocket, "a") == "hello");
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			fail();
		}

	}

}
