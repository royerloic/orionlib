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
		final SymetricFloatMatrixServer lSymetricFloatMatrixServer = new SymetricFloatMatrixServer(	null,
																																																6283,
																																																null);

		lSymetricFloatMatrixServer.startServerNonBlocking();

		final Socket lSocket = GroovyServer.createClientSocketAndConnect(	"localhost",
																																			6283);

		Thread.sleep(500);
		GroovyServer.sendQuery(lSocket, "matrix.init(14)");
		Thread.sleep(100);
		Object lObject = GroovyServer.sendQueryAndDecode(	lSocket,
																											"matrix.get(1,2,3,4)");

		assertEquals(	lObject.toString(),
									"[[0.0], [0.0, 0.0], [0.0, 0.0, 0.0], [0.0, 0.0, 0.0, 0.0]]");

		lObject = GroovyServer.sendQueryAndDecode(lSocket, "matrix.set(11,12,3f)");
	}

}
