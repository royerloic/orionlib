package utils.network.groovyserver;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;

import utils.network.socket.Service;
import utils.network.socket.ServiceFactory;
import utils.network.socket.SocketServiceServer;
import utils.utils.CmdLine;

public class GroovyServer implements Runnable, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int mPort = 4444;
	private File mScriptFile;

	Binding mBinding = new SynchronizedBinding();

	private String mPassword = null;
	private boolean mOnlyLocal;
	volatile private SocketServiceServer mSocketServiceServer;

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		try
		{
			final Map<String, String> lParameters = CmdLine.getMap(args);
			final GroovyServer lGroovyServer = new GroovyServer(lParameters);
			lGroovyServer.startServerBlocking();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

	public GroovyServer()
	{
		// mBinding.setVariable("shell", mGroovyShell);
		// mBinding.setVariable("binding", mBinding);
		mBinding.getVariables();
		mBinding.setVariable("server", this);
	}

	public GroovyServer(final String pParametersLine)
	{
		this(CmdLine.getMap(pParametersLine.split("\\s+")));
	}

	public GroovyServer(final Map<String, String> pParameters)
	{
		this();

		if (pParameters.containsKey("script"))
		{
			mScriptFile = new File(pParameters.get("script"));
		}

		if (pParameters.containsKey("port"))
		{
			mPort = Integer.parseInt(pParameters.get("port"));
		}

		if (pParameters.containsKey("onlylocal"))
		{
			mOnlyLocal = true;
		}

		setPassword(pParameters.get("password"));

	}

	public GroovyServer(final int pPort, final File pScriptFile)
	{
		this();
		mScriptFile = pScriptFile;
		mPort = pPort;
	}

	public void setPassword(final String pPassword)
	{
		mPassword = pPassword;
	}

	public String getPassword()
	{
		return mPassword;
	}

	public boolean save(final String pFileName) throws IOException
	{
		return save(new File(pFileName));
	}

	public boolean save(final File pFile) throws IOException
	{
		synchronized (this)
		{

			FileOutputStream lFileOutputStream = null;
			ObjectOutputStream lObjectOutputStream = null;

			lFileOutputStream = new FileOutputStream(pFile);
			final BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(lFileOutputStream,
																																									10000000);
			lObjectOutputStream = new ObjectOutputStream(lBufferedOutputStream);

			mBinding.setVariable("server", null); // we don't want this to be saved...
			lObjectOutputStream.writeObject(mBinding);
			mBinding.setVariable("server", this);

			lObjectOutputStream.close();

			return true;
		}
	}

	public boolean load(final String pFileName) throws IOException
	{
		return load(new File(pFileName));
	}

	public boolean load(final File pFile) throws IOException
	{
		synchronized (this)
		{
			FileInputStream lFileInputStream = null;
			ObjectInputStream lObjectInputStream = null;

			lFileInputStream = new FileInputStream(pFile);
			final BufferedInputStream lBufferedInputStream = new BufferedInputStream(	lFileInputStream,
																																								10000000);
			lObjectInputStream = new ObjectInputStream(lBufferedInputStream);
			Binding lBinding;
			try
			{
				lBinding = (Binding) lObjectInputStream.readObject();
				mBinding = lBinding;
				mBinding.setVariable("server", this);
				return true;
			}
			catch (final ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			lObjectInputStream.close();
			return false;
		}
	}

	public void startServerBlocking() throws IOException
	{
		run();
	}

	public void startServerNonBlocking() throws IOException
	{
		final Thread lThread = new Thread(this, "GroovyServer");
		lThread.start();
	}

	public void stopServerNonBlocking()
	{
		mSocketServiceServer.stopListening();
	}

	public void run()
	{

		if (mScriptFile != null)
		{
			try
			{
				System.out.println("Started execution of init script");
				final GroovyShell lGroovyShell = new GroovyShell(	GroovyServer.class.getClassLoader(),
																													mBinding);
				lGroovyShell.evaluate(mScriptFile);
				System.out.println("Ended   execution of init script");
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
			}
		}

		final GroovyServer lGroovyServer = this;
		final ServiceFactory lServiceFactory = new ServiceFactory()
		{
			public Service newService()
			{
				return new GroovyService(lGroovyServer);
			}
		};

		mSocketServiceServer = new SocketServiceServer(lServiceFactory);
		mSocketServiceServer.setAcceptOnlyLocalConnections(mOnlyLocal);

		try
		{
			mSocketServiceServer.startListening(mPort);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/*************************************************************************************/

	public static Socket createLocalSocket() throws IOException
	{
		return createClientSocketAndConnect("localhost", 4444);
	}

	public static Socket createClientSocketAndConnect(final String pAddresse,
																										final int pPort) throws IOException
	{
		// Create a socket with a timeout

		final InetAddress addr = InetAddress.getByName(pAddresse);
		final SocketAddress sockaddr = new InetSocketAddress(addr, pPort);

		// Create an unbound socket
		final Socket lSocket = new Socket();

		// This method will block no more than timeoutMs.
		// If the timeout occurs, SocketTimeoutException is thrown.
		final int timeoutMs = 2000; // 2 seconds
		lSocket.connect(sockaddr, timeoutMs);

		OutputStream lOutputStream = lSocket.getOutputStream();
		lOutputStream.flush();
		InputStream lInputStream = lSocket.getInputStream();
		
		final BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(lInputStream));

		final String lFirstLine = lBufferedReader.readLine();
		if (lFirstLine.contains(GroovyService.sWelcomeMessage))
		{
			return lSocket;
		}
		else
		{
			return null;
		}

	}

	public static Object sendQueryAndDecode(final Socket pSocket,
																					final String pQuery) throws IOException
	{
		final String lResult = sendQuery(pSocket, pQuery);
		final Object lObject = decode(lResult);
		return lObject;
	}

	public static String sendQuery(final Socket pSocket, final String pQuery) throws IOException
	{
		final BufferedWriter lBufferedWriter = new BufferedWriter(new OutputStreamWriter(pSocket.getOutputStream()));
		final BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(pSocket.getInputStream()));

		lBufferedWriter.write(pQuery + "\r\n");
		lBufferedWriter.flush();
		final String lEchoLine = lBufferedReader.readLine();
		if (lEchoLine.equals(pQuery))
		{
			final String lResultLine = lBufferedReader.readLine();
			return lResultLine;
		}
		else
		{
			throw new IOException("Received answer: '" + lEchoLine
														+ "' instead of '"
														+ pQuery
														+ "'");
		}
	}

	public static Object decode(final String pResultString)
	{
		final Binding binding = new Binding();
		final GroovyShell lGroovyShell = new GroovyShell(	GroovyServer.class.getClassLoader(),
																											binding);

		binding.setVariable("obj", null);

		try
		{
			lGroovyShell.evaluate("obj = " + pResultString);
		}
		catch (final MissingPropertyException e)
		{
			lGroovyShell.evaluate("obj = \"" + pResultString + "\"");
		}

		final Object lObject = binding.getVariable("obj");

		return lObject;
	}

	public static Object sendQueryAsObject(	final Socket pSocket,
																					final String pQuery) throws IOException,
																															ClassNotFoundException
	{
		InputStream lInputStream = pSocket.getInputStream();
		OutputStream lOutputStream = pSocket.getOutputStream();		
		
		final BufferedWriter lBufferedWriter = new BufferedWriter(new OutputStreamWriter(lOutputStream));
		lBufferedWriter.write(pQuery+ "//asobject\r\n"/**/);
		lBufferedWriter.flush();
		
		final ObjectInputStream lObjectInputStream = new ObjectInputStream(lInputStream);
		final Object lObject = lObjectInputStream.readObject();
		return lObject;
	}

	public Binding getBinding()
	{
		return mBinding;
	}

}
