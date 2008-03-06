package utils.network.socket;

import java.net.*;
import java.io.*;

public class SocketServiceServer
{
	private ServerSocket mServerSocket = null;
	private boolean listening = true;
	private ServiceFactory mServiceFactory;
	private int mThreadCounter = 0;

	public SocketServiceServer(ServiceFactory pServiceFactory)
	{
		super();
		mServiceFactory = pServiceFactory;
	}

	public void startListening(int pPort) throws IOException
	{
		try
		{
			mServerSocket = new ServerSocket(pPort);
		}
		catch (IOException e)
		{
			throw new IOException("Could not listen on port: " + pPort + ". ", e);
		}

		try
		{
			while (listening)
			{
				Service lService = mServiceFactory.newService();
				new SocketThread(	mServerSocket,
													lService,
													mServerSocket.accept(),
													lService.getName() + mThreadCounter).start();
				mThreadCounter++;
			}

			if (!mServerSocket.isClosed())
				mServerSocket.close();
		}
		catch (SocketException e)
		{
			if (!e.getMessage().contains("socket closed"))
				e.printStackTrace();
		}
	}

	public void stopListening()
	{
		listening = false;
	}

}
