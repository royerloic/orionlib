package utils.wiimote;

import java.io.IOException;
import java.util.EventListener;

public interface DisconnectionListener extends EventListener
{
	public void disconnected(IOException pEx);
}
