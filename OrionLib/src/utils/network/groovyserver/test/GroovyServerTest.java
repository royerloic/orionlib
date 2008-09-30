package utils.network.groovyserver.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;

import org.junit.Test;

import utils.network.groovyserver.GroovyServer;

public class GroovyServerTest
{

	@SuppressWarnings("unchecked")
	@Test
	public void test() throws IOException
	{

		try
		{
			final GroovyServer lGroovyServer = new GroovyServer("password=123");

			lGroovyServer.startServerNonBlocking();
			
			Thread.sleep(100);

			final Socket lSocket = GroovyServer.createLocalSocket();

			// sending password:
			GroovyServer.sendQuery(lSocket, "123");

			// checking the sending of a query and the decoding:
			assertTrue((String) GroovyServer.sendQueryAndDecode(lSocket,
																													"a=\"hello\"") == "hello");

			// send query and get answear as object:
			assertTrue((Integer)GroovyServer.sendQueryAsObject(lSocket,"12")==12);
			assertTrue(((Set<Integer>)GroovyServer.sendQueryAsObject(lSocket,"[1,2,3] as Set")).contains(1));

			// saving session:
			final String lResult = GroovyServer.sendQuery(lSocket,
																										"server.save(\"test\")");
			assertEquals(lResult, "true");

			// Creatig new server and loading
			final GroovyServer lGroovyServer2 = new GroovyServer("port=5555 password=123");

			// loading the previously saved session:
			assertEquals(	GroovyServer.sendQuery(lSocket, "server.load(\"test\")"),
										"true");

			// checking that the session was really saved:
			assertTrue((String) GroovyServer.sendQueryAndDecode(lSocket, "a") == "hello");
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			fail();
		}

	}

}
