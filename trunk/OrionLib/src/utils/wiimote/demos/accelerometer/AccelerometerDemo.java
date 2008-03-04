/*
 * Copyright 2007-2008 Volker Fritzsch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package utils.wiimote.demos.accelerometer;

import utils.wiimote.Mote;
import utils.wiimote.MoteFinder;
import utils.wiimote.event.AccelerometerEvent;
import utils.wiimote.event.AccelerometerListener;
import utils.wiimote.request.ReportModeRequest;

/**
 * 
 * <p>
 * 
 * @author <a href="mailto:vfritzsch@users.sourceforge.net">Volker Fritzsch</a>
 */
public class AccelerometerDemo
{

	public static void main(String[] args) throws InterruptedException
	{
		AccelerometerListener listener = new AccelerometerListener()
		{

			public void accelerometerChanged(AccelerometerEvent evt)
			{
				System.out.println(evt.getX() + " : " + evt.getY() + " : " + evt.getZ());
			}

		};

		Mote mote = MoteFinder.getMoteFinder().findMote();
		mote.addAccelerometerListener(listener);
		mote.setReportMode(ReportModeRequest.DATA_REPORT_0x31);

		Thread.sleep(60000l);

		mote.setReportMode(ReportModeRequest.DATA_REPORT_0x30);
		mote.disconnect();
	}
}
