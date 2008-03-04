package utils.network.socket.test;

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
import utils.network.socket.Service;
import utils.network.socket.SocketServiceServer;

public class SocketServiceTest
{

	@Test
	public void testGetColumnNames() throws IOException
	{
		Service lService = new Service()
		{

			boolean mListening = true;

			public String getName()
			{
				return "test";
			}

			public void onConnection(Socket pSocket)
			{
				System.out.println("Connection received from: " + pSocket);
			}

			public void onDisconnection()
			{
				System.out.println("Disconnection.");
			}

			public String getExitCommand()
			{
				return "exit";
			}

			public String getShutdownCommand()
			{
				return "shutdown";
			}

			public String getWelcomeMessage()
			{
				return "Hello world, this service is a test service";
			}

			public boolean isListening()
			{
				return mListening;
			}

			public String processInput(String pInputLine)
			{
				System.out.println("Recived:\n  '" + pInputLine + "'");
				if (pInputLine.equals(getShutdownCommand()))
					mListening = false;
				String lAnswear = pInputLine.replaceAll("clinton", "obama");
				System.out.println("Reply:\n  '" + lAnswear + "'");
				return lAnswear;
			}

		};

		SocketServiceServer lSocketServiceServer = new SocketServiceServer(lService);

		lSocketServiceServer.startListening(2068);
	}

}
