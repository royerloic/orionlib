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
		mGroovyServer = new GroovyServer();
		
		//mGroovyServer.evaluate("import utils.structures.matrix.SymetricFloatMatrix");
		mGroovyServer.getBinding().setVariable("matrix", mSymetricFloatMatrix);
	}

	public SymetricFloatMatrixServer(Map<String, String> pParameters)
	{
		mGroovyServer = new GroovyServer(pParameters);
		mGroovyServer.getBinding().setVariable("matrix", mSymetricFloatMatrix);
	}

	public SymetricFloatMatrixServer(	SymetricFloatMatrix pSymetricFloatMatrix,
																		int pPort,
																		File pScriptFile)
	{
		if (pSymetricFloatMatrix != null)
			mSymetricFloatMatrix = pSymetricFloatMatrix;
		mGroovyServer = new GroovyServer(pPort,pScriptFile);
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
