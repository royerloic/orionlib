package utils.network.groovyserver.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

import org.junit.Test;

import utils.io.StreamToFile;
import utils.io.filedb.FileDB;
import utils.network.groovyserver.GroovyServer;
import utils.network.socket.Service;
import utils.network.socket.SocketServiceServer;

public class GroovyServerTest
{

	@Test
	public void test() throws IOException
	{
		GroovyServer lGroovyServer = new GroovyServer("password=123");

		lGroovyServer.startServerNonBlocking();

		Socket lSocket = GroovyServer.createLocalSocket();

		//sending password:
		GroovyServer.sendQuery(lSocket, "123");

		//checking the sending of a query and the decoding:
		assertTrue((String)GroovyServer.sendQueryAndDecode(lSocket, "a=\"hello\"")=="hello");

		// saving session:
		assertEquals(GroovyServer.sendQuery(lSocket, "server.save(\"test\")"),"true");
		
			
		// Creatig new server and loading
		GroovyServer lGroovyServer2 = new GroovyServer("port=5555 password=123");
		
		// loading the previously saved session:
		assertEquals(lGroovyServer2.sendQuery(lSocket, "server.load(\"test\")"),"true");
		
		//checking that the session was really saved:
		assertTrue((String)GroovyServer.sendQueryAndDecode(lSocket, "a")=="hello");
		
		
	}

}
