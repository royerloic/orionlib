package utils.network.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketThread extends Thread
{
	public static final String sEndofLine = "\r\n";

	private Socket mSocket = null;
	private final Service mService;

	public SocketThread(final ServerSocket pServerSocket,
											final Service pService,
											final Socket pSocket,
											final String pThreadName)
	{
		super(pThreadName);
		mService = pService;
		this.mSocket = pSocket;
	}

	public void run()
	{

		try
		{
			OutputStream lOutputStream = mSocket.getOutputStream();
			ObjectOutputStream lObjectOutputStream = new ObjectOutputStream(lOutputStream);
			final PrintWriter out = new PrintWriter(lOutputStream, true);
			final BufferedReader in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

			mService.onConnection(mSocket);

			String inputLine, outputLine;
			outputLine = mService.getWelcomeMessage();
			out.print(outputLine + sEndofLine);
			out.flush();

			while ((inputLine = in.readLine()) != null)
			{
				if (inputLine.equals(mService.getExitCommand()) || mService.exit())
				{
					break;
				}
				Object lObject = mService.processInput(inputLine);
				if (lObject instanceof String)
				{
					outputLine = (String) lObject;
					out.print(outputLine + sEndofLine);
				}
				else
				{
					out.print("//objectbegin\n");
					out.flush();
					lObjectOutputStream.writeObject(lObject);
					lObjectOutputStream.flush();
					out.print("//objectend\n");
				}
				out.flush();
			}

			mService.onDisconnection();

			out.close();
			in.close();
			mSocket.close();

		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}
}
