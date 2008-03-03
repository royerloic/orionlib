package utils.network.socket;

import java.net.Socket;

public interface Service
{
	String getWelcomeMessage();

	String processInput(String pInputLine);

	String getShutdownCommand();

	String getExitCommand();

	boolean isListening();

	void onConnection(Socket pSocket);

	void onDisconnection();
}
