package utils.wiimote.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import utils.network.groovyserver.GroovyServer;
import utils.utils.CmdLine;
import utils.wiimote.Mote;
import utils.wiimote.MoteFinder;
import utils.wiimote.MoteFinderListener;

public class WiiMoteServer
{
	GroovyServer	mGroovyServer;

	private Mote	mMote;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			Map<String, String> lParameters = CmdLine.getMap(args);
			WiiMoteServer lWiiMoteServer = new WiiMoteServer(lParameters);
			lWiiMoteServer.startServerBlocking();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	public WiiMoteServer()
	{
		mGroovyServer = new GroovyServer();

		mGroovyServer.getBinding().setVariable("server", this);
		mGroovyServer.getBinding().setVariable("mote", mMote);

	}

	public WiiMoteServer(String pParameters)
	{
		mGroovyServer = new GroovyServer(pParameters);
		start();
	}

	public WiiMoteServer(Map<String, String> pParameters)
	{
		mGroovyServer = new GroovyServer(pParameters);
		start();
	}

	private boolean start()
	{
		mMote = MoteFinder.getMoteFinder().findMote();

		return true;
	};

	private boolean stop()
	{
		System.out.println("Stopping discovery..");

		mMote.disconnect();
		return true;
	};

	public void startServerBlocking() throws IOException
	{
		start();
		mGroovyServer.startServerBlocking();
		stop();
	}

	public void startServerNonBlocking() throws IOException
	{
		start();
		mGroovyServer.startServerNonBlocking();
	}

	public void stopServerNonBlocking() throws IOException
	{
		stop();
		mGroovyServer.stopServerNonBlocking();
	}

}
