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
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.runtime.metaclass.MissingPropertyExceptionNoStack;

import utils.network.socket.Service;
import utils.network.socket.ServiceFactory;
import utils.network.socket.SocketServiceServer;
import utils.utils.CmdLine;

public class GroovyServer implements Runnable, Serializable
{

	int							mPort			= 4444;
	private File		mScriptFile;

	Binding					mBinding	= new SynchronizedBinding();

	private String	mPassword	= null;
	private boolean	mOnlyLocal;
	private SocketServiceServer	mSocketServiceServer;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			Map<String, String> lParameters = CmdLine.getMap(args);
			GroovyServer lGroovyServer = new GroovyServer(lParameters);
			lGroovyServer.startServerBlocking();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	public GroovyServer()
	{
		// mBinding.setVariable("shell", mGroovyShell);
		mBinding.setVariable("binding", mBinding);
		mBinding.getVariables();
		mBinding.setVariable("server", this);
	}

	public GroovyServer(String pParametersLine)
	{
		this(CmdLine.getMap(pParametersLine.split("\\s+")));
	}

	public GroovyServer(Map<String, String> pParameters)
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

	public GroovyServer(int pPort, File pScriptFile)
	{
		this();
		mScriptFile = pScriptFile;
		mPort = pPort;
	}

	public void setPassword(String pPassword)
	{
		mPassword = pPassword;
	}

	public String getPassword()
	{
		return mPassword;
	}

	public boolean save(String pFileName) throws IOException
	{
		return save(new File(pFileName));
	}

	public boolean save(File pFile) throws IOException
	{
		synchronized (this)
		{
			FileOutputStream lFileOutputStream = null;
			ObjectOutputStream lObjectOutputStream = null;

			lFileOutputStream = new FileOutputStream(pFile);
			BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(lFileOutputStream,10000000);
			lObjectOutputStream = new ObjectOutputStream(lBufferedOutputStream);
			lObjectOutputStream.writeObject(mBinding);
			lObjectOutputStream.close();

			return true;
		}
	}

	public boolean load(String pFileName) throws IOException
	{
		return load(new File(pFileName));
	}

	public boolean load(File pFile) throws IOException
	{
		synchronized (this)
		{
			FileInputStream lFileInputStream = null;
			ObjectInputStream lObjectInputStream = null;

			lFileInputStream = new FileInputStream(pFile);
			BufferedInputStream lBufferedInputStream = new BufferedInputStream(lFileInputStream,10000000);
			lObjectInputStream = new ObjectInputStream(lBufferedInputStream);
			Binding lBinding;
			try
			{
				lBinding = (Binding) lObjectInputStream.readObject();
				mBinding = lBinding;
				return true;
			}
			catch (ClassNotFoundException e)
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
		Thread lThread = new Thread(this, "GroovyServer");
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
				GroovyShell lGroovyShell = new GroovyShell(	GroovyServer.class.getClassLoader(),
																										mBinding);
				lGroovyShell.evaluate(mScriptFile);
				System.out.println("Ended   execution of init script");
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		}

		final GroovyServer lGroovyServer = this;
		ServiceFactory lServiceFactory = new ServiceFactory()
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
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static Socket createLocalSocket() throws IOException
	{
		return createClientSocketAndConnect("localhost", 4444);
	}

	public static Socket createClientSocketAndConnect(String pAddresse, int pPort) throws IOException
	{
		// Create a socket with a timeout

		InetAddress addr = InetAddress.getByName(pAddresse);
		SocketAddress sockaddr = new InetSocketAddress(addr, pPort);

		// Create an unbound socket
		Socket lSocket = new Socket();

		// This method will block no more than timeoutMs.
		// If the timeout occurs, SocketTimeoutException is thrown.
		int timeoutMs = 2000; // 2 seconds
		lSocket.connect(sockaddr, timeoutMs);

		BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(lSocket.getInputStream()));

		String lFirstLine = lBufferedReader.readLine();
		if (lFirstLine.contains(GroovyService.sWelcomeMessage))
		{
			return lSocket;
		}
		else
			return null;

	}

	public static Object sendQueryAndDecode(Socket pSocket, String pQuery) throws IOException
	{
		String lResult = sendQuery(pSocket, pQuery);
		Object lObject = decode(lResult);
		return lObject;
	}

	public static String sendQuery(Socket pSocket, String pQuery) throws IOException
	{
		BufferedWriter lBufferedWriter = new BufferedWriter(new OutputStreamWriter(pSocket.getOutputStream()));
		BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(pSocket.getInputStream()));

		lBufferedWriter.write(pQuery + "\r\n");
		lBufferedWriter.flush();
		String lEchoLine = lBufferedReader.readLine();
		if (lEchoLine.equals(pQuery))
		{
			String lResultLine = lBufferedReader.readLine();
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

	public static Object decode(String pResultString)
	{
		final Binding binding = new Binding();
		GroovyShell lGroovyShell = new GroovyShell(	GroovyServer.class.getClassLoader(),
																								binding);

		binding.setVariable("obj", null);

		try
		{
			lGroovyShell.evaluate("obj = " + pResultString);
		}
		catch (MissingPropertyException e)
		{
			lGroovyShell.evaluate("obj = \"" + pResultString + "\"");
		}

		Object lObject = binding.getVariable("obj");

		return lObject;
	}

	public Binding getBinding()
	{
		return mBinding;
	}

	

}
