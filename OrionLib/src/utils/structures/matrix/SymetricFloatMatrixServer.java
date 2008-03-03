package utils.structures.matrix;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.net.Socket;

import org.codehaus.groovy.control.CompilationFailedException;

import utils.network.socket.Service;
import utils.network.socket.SocketServiceServer;

public class SymetricFloatMatrixServer
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		SymetricFloatMatrix lSymetricFloatMatrix = new SymetricFloatMatrix();
				
		final Binding binding = new Binding();
		final GroovyShell lGroovyShell = new GroovyShell(	SymetricFloatMatrixServer.class.getClassLoader(),
																								binding);
		lGroovyShell.evaluate("import utils.structures.matrix.SymetricFloatMatrix");

		binding.setVariable("matrix", lSymetricFloatMatrix);
				
		try
		{
			Service lService = new Service()
			{

				boolean mListening = true;

				public void onConnection(Socket pSocket)
				{
					System.out.println("Connection received from: "+pSocket);				
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
					return "Welcome to the Symetric Float Matrix Server ";
				}

				public boolean isListening()
				{
					return mListening;
				}

				public String processInput(String pInputLine)
				{
					System.out.println("Received:\n  '"+pInputLine+"'");
					if (pInputLine.equals(getShutdownCommand()))
						mListening = false;
					
					String lAnswear;
					try
					{
						lAnswear = (String) lGroovyShell.evaluate(pInputLine).toString();
					}
					catch (Throwable e)
					{
						lAnswear = e.getMessage();
						e.printStackTrace();
					}

					System.out.println("Reply:\n  '"+lAnswear+"'");
					return lAnswear;
				}

				


			};

			SocketServiceServer lSocketServiceServer = new SocketServiceServer(lService);
			
			lSocketServiceServer.startListening(4444);
			
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
