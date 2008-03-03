package utils.network.socket;

import java.net.*;
import java.io.*;

public class SocketThread extends Thread
{
	private Socket mSocket = null;
	private final Service mService;
	private final ServerSocket mServerSocket;

	public SocketThread(ServerSocket pServerSocket, Service pService, Socket pSocket)
	{
		super("SocketThread");
		mServerSocket = pServerSocket;
		mService = pService;
		this.mSocket = pSocket;
	}

	public void run()
	{
		if (mService.isListening())
			try
			{
				PrintWriter out = new PrintWriter(mSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

				mService.onConnection(mSocket);
				
				String inputLine, outputLine;
				outputLine = mService.getWelcomeMessage();
				out.println(outputLine);
				

				while ((inputLine = in.readLine()) != null)
				{
					if (inputLine.equals(mService.getExitCommand()))
						break;
					outputLine = mService.processInput(inputLine);

					if (mService.isListening())
						out.println(outputLine);
					else
					{
						if(!mServerSocket.isClosed())
							mServerSocket.close();
						break;
					}

				}

				mService.onDisconnection();
								
				out.close();
				in.close();
				mSocket.close();

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
	}
}
