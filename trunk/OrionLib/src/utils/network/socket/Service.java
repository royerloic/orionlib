package utils.network.socket;

import java.net.Socket;

public interface Service
{

	String getName();

	String getWelcomeMessage();

	Object processInput(String pInputLine);

	String getShutdownCommand();

	String getExitCommand();

	boolean isListening();

	void onConnection(Socket pSocket);

	void onDisconnection();

	boolean exit();

}
