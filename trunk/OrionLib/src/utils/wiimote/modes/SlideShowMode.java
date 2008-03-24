package utils.wiimote.modes;

import java.awt.CheckboxMenuItem;
import java.awt.MenuItem;

import wiiremotej.WiiRemote;


public class SlideShowMode implements WiiMode
{
	CheckboxMenuItem mSlideShowModeItem = new CheckboxMenuItem(	"SlideShow Mode",
																												false);
	
	public CheckboxMenuItem getMenuItem()
	{
		return mSlideShowModeItem;
	}

	
	public void activate(WiiRemote pRemote)
	{
		// TODO Auto-generated method stub
		
	}

	
	public void deactivate(WiiRemote pRemote)
	{
		// TODO Auto-generated method stub
		
	}

}
