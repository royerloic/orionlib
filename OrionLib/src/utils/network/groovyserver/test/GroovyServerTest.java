package utils.network.groovyserver.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

import utils.network.groovyserver.GroovyServer;

public class GroovyServerTest
{

	@Test
	public void test() throws IOException
	{
		
		try
		{
			GroovyServer lGroovyServer = new GroovyServer("password=123");

			lGroovyServer.startServerNonBlocking();

			Socket lSocket = GroovyServer.createLocalSocket();

			//sending password:
			GroovyServer.sendQuery(lSocket, "123");

			//checking the sending of a query and the decoding:
			assertTrue((String)GroovyServer.sendQueryAndDecode(lSocket, "a=\"hello\"")=="hello");

			// saving session:
			String lResult = GroovyServer.sendQuery(lSocket, "server.save(\"test\")");
			assertEquals(lResult,"true");
			
				
			// Creatig new server and loading
			GroovyServer lGroovyServer2 = new GroovyServer("port=5555 password=123");
			
			// loading the previously saved session:
			assertEquals(lGroovyServer2.sendQuery(lSocket, "server.load(\"test\")"),"true");
			
			//checking that the session was really saved:
			assertTrue((String)GroovyServer.sendQueryAndDecode(lSocket, "a")=="hello");
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail();
		}
		
		
	}

}
