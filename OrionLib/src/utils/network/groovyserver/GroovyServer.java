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

	public Binding getBinding()
	{
		return mBinding;
	}

}
