package utils.structures.matrix.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import utils.structures.matrix.SymetricFloatMatrixServer;

public class SymetricFloatMatrixServerTest
{

	@Test
	public void test() throws IOException
	{
		SymetricFloatMatrixServer lSymetricFloatMatrixServer = new SymetricFloatMatrixServer(	null,
																																													4444,
																																													null);

		lSymetricFloatMatrixServer.startServerNonBlocking();

		Socket lSocket = SymetricFloatMatrixServer.createLocalSocket();

		SymetricFloatMatrixServer.sendQuery(lSocket, "matrix.init(14)");
		Object lObject = SymetricFloatMatrixServer.sendGetQuery(lSocket,
																																										"matrix.get(1,2,3,4)");

		assertEquals(	lObject.toString(),
									"[[0.0], [0.0, 0.0], [0.0, 0.0, 0.0], [0.0, 0.0, 0.0, 0.0]]");
	}

}
