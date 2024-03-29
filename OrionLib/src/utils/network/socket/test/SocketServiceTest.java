package utils.network.socket.test;

import static org.junit.Assert.fail;

import java.net.Socket;

import org.junit.Test;

import utils.network.socket.Service;
import utils.network.socket.ServiceFactory;
import utils.network.socket.SocketServiceServer;

public class SocketServiceTest
{

	@Test
	public void testGetColumnNames()
	{
		try
		{
			final Service lService = new Service()
			{

				boolean mListening = true;

				public String getName()
				{
					return "Test";
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
					return "Hello world, this service is a Test service";
				}

				public boolean isListening()
				{
					return mListening;
				}

				public String processInput(String pInputLine)
				{
					System.out.println("Recived:\n  '" + pInputLine + "'");
					if (pInputLine.equals(getShutdownCommand()))
					{
						mListening = false;
					}
					String lAnswear = pInputLine.replaceAll("clinton", "obama");
					System.out.println("Reply:\n  '" + lAnswear + "'");
					return lAnswear;
				}

				public boolean exit()
				{
					return false;
				}

			};

			final ServiceFactory lServiceFactory = new ServiceFactory()
			{
				public Service newService()
				{
					return lService;
				}
			};
			final SocketServiceServer lSocketServiceServer = new SocketServiceServer(lServiceFactory);

			// lSocketServiceServer.startListening(2068);
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			fail();
		}

	}

}
