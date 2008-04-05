package utils.structures.matrix.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

import utils.network.groovyserver.GroovyServer;
import utils.structures.matrix.SymetricFloatMatrixServer;

public class SymetricFloatMatrixServerTest
{

	@Test
	public void test() throws IOException, InterruptedException
	{
		SymetricFloatMatrixServer lSymetricFloatMatrixServer = new SymetricFloatMatrixServer(	null,
																																													4444,
																																													null);

		lSymetricFloatMatrixServer.startServerNonBlocking();

		Socket lSocket = GroovyServer.createLocalSocket();

		Thread.sleep(500);
		GroovyServer.sendQuery(lSocket, "matrix.init(14)");
		Object lObject = GroovyServer.sendQueryAndDecode(	lSocket,
																											"matrix.get(1,2,3,4)");

		assertEquals(	lObject.toString(),
									"[[0.0], [0.0, 0.0], [0.0, 0.0, 0.0], [0.0, 0.0, 0.0, 0.0]]");

		lObject = GroovyServer.sendQueryAndDecode(lSocket, "matrix.set(11,12,3f)");
	}

}
