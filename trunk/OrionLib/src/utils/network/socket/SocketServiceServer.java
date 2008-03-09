package utils.network.socket;

import java.net.*;
import java.io.*;

public class SocketServiceServer
{
	private ServerSocket		mServerSocket								= null;
	private boolean					mListening									= true;
	private ServiceFactory	mServiceFactory;
	private int							mThreadCounter							= 0;

	boolean									mAcceptOnlyLocalConnections	= false;

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
			while (mListening)
			{
				Service lService = mServiceFactory.newService();
				Socket lSocket = mServerSocket.accept();
				InetAddress lInetAddress = lSocket.getInetAddress();
				System.out.println("Conection request from: " + lInetAddress);
				if (mAcceptOnlyLocalConnections == false || lInetAddress.getHostName()
																																.equals("localhost"))
				{
					new SocketThread(	mServerSocket,
														lService,
														lSocket,
														lService.getName() + mThreadCounter).start();
				}
				mThreadCounter++;
			}

			if (!mServerSocket.isClosed()) mServerSocket.close();
		}
		catch (SocketException e)
		{
			if (!e.getMessage().contains("socket closed")) e.printStackTrace();
		}
	}

	public void stopListening()
	{
		mListening = false;
	}

	public boolean isAcceptOnlyLocalConnections()
	{
		return mAcceptOnlyLocalConnections;
	}

	public void setAcceptOnlyLocalConnections(boolean pAcceptOnlyLocalConnections)
	{
		mAcceptOnlyLocalConnections = pAcceptOnlyLocalConnections;
	}

}
