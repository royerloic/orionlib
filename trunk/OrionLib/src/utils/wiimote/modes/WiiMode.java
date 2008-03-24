package utils.wiimote.modes;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.GraphicsEnvironment;
import java.awt.MenuItem;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.ArrayList;

import utils.wiimote.WRLImpl;
import wiiremotej.IRLight;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteExtension;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRCombinedEvent;
import wiiremotej.event.WRExtensionEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WRStatusEvent;
import wiiremotej.event.WiiRemoteDiscoveredEvent;
import wiiremotej.event.WiiRemoteDiscoveryListener;
import wiiremotej.event.WiiRemoteListener;

public interface WiiMode
{
	public CheckboxMenuItem getMenuItem();

	public void activate(WiiRemote pRemote);

	public void deactivate(WiiRemote pRemote);

}
