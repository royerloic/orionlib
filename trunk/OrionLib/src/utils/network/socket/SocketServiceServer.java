package utils.network.socket;

import java.net.*;
import java.io.*;

public class SocketServiceServer
{
	private ServerSocket mServerSocket = null;
	private final boolean listening = true;
	private final Service mService;
			
	public SocketServiceServer(Service pService)
	{
		super();
		mService = pService;
	}

	public void startListening(int pPort) throws IOException
	{
		try
		{
			mServerSocket = new ServerSocket(pPort);
		}
		catch (IOException e)
		{
			throw new IOException("Could not listen on port: "+pPort+". ",e);
		}

		try
		{
			while (mService.isListening())
				new SocketThread(mServerSocket, mService, mServerSocket.accept()).start();

			if(!mServerSocket.isClosed())
				mServerSocket.close();
		}
		catch (SocketException e)
		{
			if(!e.getMessage().contains("socket closed"))
				e.printStackTrace();
		}
	}
}
