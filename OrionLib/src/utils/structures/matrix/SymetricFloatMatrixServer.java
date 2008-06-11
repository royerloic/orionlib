package utils.structures.matrix;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import utils.network.groovyserver.GroovyServer;
import utils.utils.CmdLine;

public class SymetricFloatMatrixServer
{
	GroovyServer mGroovyServer;

	SymetricFloatMatrix mSymetricFloatMatrix = new SymetricFloatMatrix();

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		try
		{
			final Map<String, String> lParameters = CmdLine.getMap(args);
			final SymetricFloatMatrixServer lSymetricFloatMatrixServer = new SymetricFloatMatrixServer(lParameters);
			lSymetricFloatMatrixServer.startServerBlocking();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

	public SymetricFloatMatrixServer()
	{
		mGroovyServer = new GroovyServer();

		// mGroovyServer.evaluate("import
		// utils.structures.matrix.SymetricFloatMatrix");
		mGroovyServer.getBinding().setVariable("matrix", mSymetricFloatMatrix);
	}

	public SymetricFloatMatrixServer(final Map<String, String> pParameters)
	{
		mGroovyServer = new GroovyServer(pParameters);
		mGroovyServer.getBinding().setVariable("matrix", mSymetricFloatMatrix);
	}

	public SymetricFloatMatrixServer(	final SymetricFloatMatrix pSymetricFloatMatrix,
																		final int pPort,
																		final File pScriptFile)
	{
		if (pSymetricFloatMatrix != null)
		{
			mSymetricFloatMatrix = pSymetricFloatMatrix;
		}
		mGroovyServer = new GroovyServer(pPort, pScriptFile);
		mGroovyServer.getBinding().setVariable("matrix", mSymetricFloatMatrix);
	}

	public void startServerBlocking() throws IOException
	{
		mGroovyServer.startServerBlocking();
	}

	public void startServerNonBlocking() throws IOException
	{
		mGroovyServer.startServerNonBlocking();
	}

}
