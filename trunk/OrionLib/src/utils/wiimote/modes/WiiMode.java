package utils.wiimote.modes;

import java.awt.CheckboxMenuItem;

import wiiremotej.WiiRemote;

public interface WiiMode
{
	public CheckboxMenuItem getMenuItem();

	public void activate(WiiRemote pRemote);

	public void deactivate(WiiRemote pRemote);

}
