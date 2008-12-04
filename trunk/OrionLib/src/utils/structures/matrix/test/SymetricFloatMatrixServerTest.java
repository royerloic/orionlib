package utils.structures.matrix.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.Socket;

import org.junit.Test;

import utils.network.groovyserver.GroovyClient;
import utils.structures.matrix.SymetricFloatMatrixServer;

public class SymetricFloatMatrixServerTest
{

	@Test
	public void test()
	{
		try
		{
			final SymetricFloatMatrixServer lSymetricFloatMatrixServer = new SymetricFloatMatrixServer(	null,
																																																	6283,
																																																	null);

			lSymetricFloatMatrixServer.startServerNonBlocking();

			Thread.sleep(500);
			final Socket lSocket = GroovyClient.createClientSocketAndConnect(	"localhost",
																																				6283);

			Thread.sleep(500);
			GroovyClient.sendQueryGetString(lSocket, "matrix.init(14)");
			Thread.sleep(100);
			Object lObject = GroovyClient.sendQueryAndDecode(	lSocket,
																												"matrix.get(1,2,3,4)");

			assertEquals(	lObject.toString(),
										"[[0.0], [0.0, 0.0], [0.0, 0.0, 0.0], [0.0, 0.0, 0.0, 0.0]]");

			lObject = GroovyClient.sendQueryAndDecode(lSocket, "matrix.set(11,12,3f)");
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
