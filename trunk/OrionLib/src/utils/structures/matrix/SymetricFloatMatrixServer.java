package utils.structures.matrix;

import groovy.lang.Binding;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import utils.network.socket.Service;
import utils.network.socket.SocketServiceServer;
import utils.network.socket.SocketThread;
import utils.utils.CmdLine;

public class SymetricFloatMatrixServer implements Runnable
{
	SymetricFloatMatrix mSymetricFloatMatrix = new SymetricFloatMatrix();

	private static final String mWelcomeMessage = "Welcome to the Symetric Float Matrix Server";
	int mPort = 4444;
	private File mScriptFile;

	Binding mBinding = new Binding();
	GroovyShell mGroovyShell = new GroovyShell(	SymetricFloatMatrixServer.class.getClassLoader(),
																							mBinding);

	int sCounter = 0;
	final int sCleanCycle = 1000;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			Map<String, String> lParameters = CmdLine.getMap(args);
			SymetricFloatMatrixServer lSymetricFloatMatrixServer = new SymetricFloatMatrixServer(lParameters);
			lSymetricFloatMatrixServer.startServerBlocking();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	public SymetricFloatMatrixServer()
	{
		mGroovyShell.evaluate("import utils.structures.matrix.SymetricFloatMatrix");
		mBinding.setVariable("matrix", mSymetricFloatMatrix);
	}

	public SymetricFloatMatrixServer(Map<String, String> pParameters)
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

	}

	public SymetricFloatMatrixServer(	SymetricFloatMatrix pSymetricFloatMatrix,
																		int pPort,
																		File pScriptFile)
	{
		this();
		if (pSymetricFloatMatrix != null)
			mSymetricFloatMatrix = pSymetricFloatMatrix;
		mScriptFile = pScriptFile;
		mPort = pPort;
	}

	public void startServerBlocking() throws IOException
	{
		run();
	}

	public void startServerNonBlocking() throws IOException
	{
		Thread lThread = new Thread(this, "SymetricFloatMatrixServer");
		lThread.start();
	}

	public void run()
	{

		if (mScriptFile != null)
		{
			try
			{
				System.out.println("Started execution of init script");
				mGroovyShell.evaluate(mScriptFile);
				System.out.println("Ended   execution of init script");
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		}

		Service lService = new Service()
		{

			boolean mListening = true;
			private Socket mSocket;

			public void onConnection(Socket pSocket)
			{
				mSocket = pSocket;
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
				return mWelcomeMessage + " on: " + mSocket.getLocalSocketAddress();
			}

			public boolean isListening()
			{
				return mListening;
			}

			public String processInput(String pInputLine)
			{
				System.out.println("Received:\n  '" + pInputLine + "'");
				if (pInputLine.equals(getShutdownCommand()))
					mListening = false;

				if (pInputLine.length() > 0)
				{
					String lAnswer;
					try
					{
						lAnswer = pInputLine + SocketThread.sEndofLine;
						lAnswer += (String) mGroovyShell.evaluate(pInputLine).toString();
						sCounter++;
						if (sCounter >= sCleanCycle)
						{
							// just to make sure that nothing gets accumulated inside of
							// GroovyShell, we get read of the old
							// and instanciate a new one every sCleanCycle calls...
							System.out.println("Resetting GroovyShell now!");
							mGroovyShell = new GroovyShell(	SymetricFloatMatrixServer.class.getClassLoader(),
																							mBinding);
						}
					}
					catch (Throwable e)
					{
						lAnswer = e.getMessage();
						e.printStackTrace();
					}

					System.out.println("Reply:\n  '" + lAnswer + "'");
					return lAnswer;
				}
				else
				{
					System.out.println("Empty command received...");
					return "";
				}

			}

			public String getName()
			{
				return SymetricFloatMatrixServer.class.getSimpleName();
			}

		};

		SocketServiceServer lSocketServiceServer = new SocketServiceServer(lService);

		try
		{
			lSocketServiceServer.startListening(mPort);
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
		if (lFirstLine.contains(mWelcomeMessage))
		{
			return lSocket;
		}
		else
			return null;

	}

	public static Object sendGetQuery(Socket pSocket, String pQuery) throws IOException
	{
		String lResult = sendQuery(pSocket, pQuery);
		Object lObject = decodeFloatListList(lResult);
		return lObject;
	}

	public static String sendQuery(Socket pSocket, String pQuery) throws IOException
	{
		BufferedWriter lBufferedWriter = new BufferedWriter(new OutputStreamWriter(pSocket.getOutputStream()));
		BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(pSocket.getInputStream()));

		lBufferedWriter.write(pQuery+"\r\n");
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

	public static Object decodeFloatListList(String pListString)
	{
		final Binding binding = new Binding();
		GroovyShell lGroovyShell = new GroovyShell(	SymetricFloatMatrixServer.class.getClassLoader(),
																								binding);

		binding.setVariable("obj",null);
		
		lGroovyShell.evaluate("obj = " + pListString);
		
		Object lObject = binding.getVariable("obj");

		return lObject;
	}

}
