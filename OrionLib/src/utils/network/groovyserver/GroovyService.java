package utils.network.groovyserver;

import groovy.lang.GroovyShell;

import java.io.Serializable;
import java.net.Socket;
import java.util.regex.Pattern;

import utils.network.socket.Service;
import utils.network.socket.SocketThread;

public class GroovyService implements Service, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Pattern lRemoveNewLines = Pattern.compile("(\\n|\\r)+");

	public static final String sWelcomeMessage = "Welcome to GroovyServer";
	private boolean mExit = false;
	boolean authenticated = false;

	final int sCleanCycle = 1000;
	int sCounter = 0;
	boolean mListening = true;
	private Socket mSocket;

	GroovyShell mGroovyShell = null;
	private GroovyServer mGroovyServer = null;

	@SuppressWarnings("unused")
	private GroovyService()
	{

	}

	public GroovyService(final GroovyServer pGroovyServer)
	{
		super();
		mGroovyServer = pGroovyServer;
		mGroovyShell = new GroovyShell(	GroovyServer.class.getClassLoader(),
																		pGroovyServer.getBinding());
	}

	public void onConnection(final Socket pSocket)
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
		return sWelcomeMessage + " on: " + mSocket.getLocalSocketAddress();
	}

	public boolean isListening()
	{
		return mListening;
	}

	public Object processInput(final String pInputLine)
	{
		if (authenticated || mGroovyServer.getPassword() == null)
		{
			return executeCommand(pInputLine);
		}
		else
		{
			final String lTrimmedString = pInputLine;
			if (mGroovyServer.getPassword().equals(lTrimmedString))
			{
				authenticated = true;
			}
			else
			{
				mExit = true;
			}
			return lTrimmedString + "\r\n" + authenticated;
		}
	}

	private Object executeCommand(final String pInputLine)
	{
		mGroovyShell = new GroovyShell(	GroovyServer.class.getClassLoader(),
																		mGroovyServer.getBinding());

		System.out.println(mSocket.getLocalSocketAddress() + ">'"
												+ pInputLine.replaceAll("(\\n|\\r)+", "|")
												+ "'");
		if (pInputLine.equals(getShutdownCommand()))
		{
			mListening = false;
		}

		if (pInputLine.length() > 0)
		{
			Object lAnswer = null;
			try
			{
				if (pInputLine.endsWith("//asobject"))
				{
					lAnswer = mGroovyShell.evaluate(pInputLine);
				}
				else
				{
					lAnswer = pInputLine + SocketThread.sEndofLine;
					final String lString = mGroovyShell.evaluate(pInputLine).toString();
					lAnswer = ((String) lAnswer) + lString.toString();
				}

				/***********************************************************************
				 * sCounter++; if (sCounter >= sCleanCycle) { // just to make sure that
				 * nothing gets accumulated inside of // GroovyShell, we get read of the
				 * old // and instanciate a new one every sCleanCycle calls...
				 * System.out.println("Resetting GroovyShell now!"); mGroovyShell = new
				 * GroovyShell( GroovyServer.class.getClassLoader(),
				 * mGroovyShell.getContext()); }/
				 **********************************************************************/
			}
			catch (final Throwable e)
			{
				lAnswer = e.getMessage();
				e.printStackTrace();
			}

			if (lAnswer != null)
				System.out.println(mSocket.getLocalSocketAddress() + "<'"
														+ lAnswer.toString().replaceAll("(\\n|\\r)+", "|")
														+ "'");
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
		return GroovyServer.class.getSimpleName();
	}

	public boolean exit()
	{
		return mExit;
	}

};
