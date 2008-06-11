package utils.network.socket;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class SocketServiceServer implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ServerSocket mServerSocket = null;
	private boolean mListening = true;
	private final ServiceFactory mServiceFactory;
	private int mThreadCounter = 0;

	boolean mAcceptOnlyLocalConnections = false;

	public SocketServiceServer(final ServiceFactory pServiceFactory)
	{
		super();
		mServiceFactory = pServiceFactory;
	}

	public void startListening(final int pPort) throws IOException
	{
		try
		{
			mServerSocket = new ServerSocket(pPort);
		}
		catch (final IOException e)
		{
			throw new IOException("Could not listen on port: " + pPort + ". ", e);
		}

		try
		{
			while (mListening)
			{
				final Service lService = mServiceFactory.newService();
				final Socket lSocket = mServerSocket.accept();
				final InetAddress lInetAddress = lSocket.getInetAddress();
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

			if (!mServerSocket.isClosed())
			{
				mServerSocket.close();
			}
		}
		catch (final SocketException e)
		{
			if (!e.getMessage().contains("socket closed"))
			{
				e.printStackTrace();
			}
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

	public void setAcceptOnlyLocalConnections(final boolean pAcceptOnlyLocalConnections)
	{
		mAcceptOnlyLocalConnections = pAcceptOnlyLocalConnections;
	}

}
