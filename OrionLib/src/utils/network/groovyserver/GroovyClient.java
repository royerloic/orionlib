package utils.network.groovyserver;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class GroovyClient implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int mPort = 4444;
	private Socket mSocket;
	private String mPassword = null;
	private InetAddress mAddresse;

	private GroovyShell mGroovyShell;
	private File mScriptFile;
	private Binding mBinding = new SynchronizedBinding();

	public GroovyClient(final String pAddresse, final int pPort) throws UnknownHostException
	{
		this(pAddresse, pPort, null, null);
	}

	public GroovyClient(final String pAddresse,
											final int pPort,
											final String pPassword,
											final File pScriptFile) throws UnknownHostException
	{
		mAddresse = InetAddress.getByName(pAddresse);
		mScriptFile = pScriptFile;
		mPort = pPort;
		mPassword = pPassword;

		mGroovyShell = new GroovyShell(	GroovyServer.class.getClassLoader(),
																		mBinding);
		mBinding.setVariable("server", this);
	}

	public void setPassword(final String pPassword)
	{
		mPassword = pPassword;
	}

	public String getPassword()
	{
		return mPassword;
	}

	public Binding getBinding()
	{
		return mBinding;
	}

	public boolean connect()
	{
		try
		{
			mSocket = GroovyClient.createClientSocketAndConnect(mAddresse, mPort);
			GroovyClient.sendQueryGetString(mSocket, mPassword);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean disconnect()
	{
		try
		{
			mSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return mSocket.isConnected();
	}

	public String sendQueryGetString(final String pQuery) throws IOException
	{
		return sendQueryGetString(mSocket, pQuery);
	}

	public Object sendQueryGetObject(final String pQuery)	throws IOException,
																												ClassNotFoundException
	{
		return sendQueryGetObject(mSocket, pQuery);
	}

	public Object evaluate(final String pCommand)	throws IOException,
																								ClassNotFoundException
	{
		if (pCommand.startsWith(">>"))
		{
			Object lObject = sendQueryGetObject(pCommand.substring(2));
			mBinding.setVariable("ans", lObject);
			return lObject;

		}
		else
		{
			return mGroovyShell.evaluate(pCommand);
		}
	}

	/**************************************************************************************/

	public static Socket createLocalSocket() throws IOException
	{
		return createClientSocketAndConnect("localhost", 4444);
	}

	public static Socket createClientSocketAndConnect(final String pAddresse,
																										final int pPort) throws IOException
	{
		return createClientSocketAndConnect(InetAddress.getByName(pAddresse), pPort);
	}

	public static Socket createClientSocketAndConnect(final InetAddress pAddresse,
																										final int pPort) throws IOException
	{
		// Create a socket with a timeout

		final SocketAddress sockaddr = new InetSocketAddress(pAddresse, pPort);

		// Create an unbound socket
		final Socket lSocket = new Socket();

		// This method will block no more than timeoutMs.
		// If the timeout occurs, SocketTimeoutException is thrown.
		final int timeoutMs = 3000; // 2 seconds
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

	public static String sendQueryGetString(final Socket pSocket,
																					final String pQuery) throws IOException
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

	public static Object sendQueryGetObject(final Socket pSocket,
																					final String pQuery) throws IOException,
																															ClassNotFoundException
	{
		InputStream lInputStream = pSocket.getInputStream();
		OutputStream lOutputStream = pSocket.getOutputStream();

		final BufferedWriter lBufferedWriter = new BufferedWriter(new OutputStreamWriter(lOutputStream));
		lBufferedWriter.write(pQuery + "//asobject\r\n"/**/);
		lBufferedWriter.flush();

		final ObjectInputStream lObjectInputStream = new ObjectInputStream(lInputStream);
		final Object lObject = lObjectInputStream.readObject();
		return lObject;
	}

	public static Object sendQueryAndDecode(final Socket pSocket,
																					final String pQuery) throws IOException
	{
		final String lResult = sendQueryGetString(pSocket, pQuery);
		final Object lObject = decode(lResult);
		return lObject;
	}

	public static Object decode(final String pResultString)
	{
		final Binding binding = new Binding();
		final GroovyShell lGroovyShell = new GroovyShell(	GroovyClient.class.getClassLoader(),
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

}
