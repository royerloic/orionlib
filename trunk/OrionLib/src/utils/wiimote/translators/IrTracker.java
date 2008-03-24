package utils.wiimote.translators;

import java.util.ArrayList;

import utils.wiimote.tools.WiiStatusListener;
import wiiremotej.IRLight;
import wiiremotej.WiiRemoteExtension;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRCombinedEvent;
import wiiremotej.event.WRExtensionEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WRStatusEvent;
import wiiremotej.event.WiiRemoteAdapter;
import wiiremotej.event.WiiRemoteListener;

public class IrTracker extends WiiRemoteAdapter
{

	ArrayList<IrTrackerListener>	lIrTrackerListenerList	= new ArrayList<IrTrackerListener>();
	
	double mTrackedX;
	double mTrackedY;
	
	
	@Override
	public void IRInputReceived(WRIREvent evt)
	{
		
		int i = 0;
		double lClosestX = 0;
		double lClosestY = 0;
		double lMinDistance = Double.POSITIVE_INFINITY;

		for (IRLight light : evt.getIRLights())
		{
			if (light != null)
			{
				/***********************************************************************
				 * System.out.println("Light: " + i); System.out.println("X: " +
				 * light.getX()); System.out.println("Y:" + light.getY());/
				 **********************************************************************/
				double x = light.getX();
				double y = light.getY();
				
				double distance = Math.sqrt((mTrackedX-x)*(mTrackedX-x)+(mTrackedY-y)*(mTrackedY-y));
				
				if(distance<lMinDistance)
				{
					mTrackedX = x;
					mTrackedY = y;
					lMinDistance = distance;
				}
			}
		}

		if (i > 0)
		{
			mX = nmX / (double) i;
			mY = nmY / (double) i;
			
			

			/*************************************************************************
			 * mX = mX*1.10 -0.05; mY = mY*1.10 -0.05;/
			 ************************************************************************/
			
			System.out.println("after X: " + mX);
			System.out.println("after Y:" + mY);
			
			/*************************************************************************
			 * mX = mX<0 ? 0 : (mX>1?1:mX); mY = mY<0 ? 0 : (mY>1?1:mY);/
			 ************************************************************************/
			
			final int x = (int) ((1 - mX) * mScreenRectangle.getWidth());
			final int y = (int) (mY * mScreenRectangle.getHeight());
			mRobot.mouseMove(x, y);

	}


}
