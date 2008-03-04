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
package utils.wiimote.demos.buttons;

import utils.wiimote.Mote;
import utils.wiimote.MoteFinder;
import utils.wiimote.event.CoreButtonEvent;
import utils.wiimote.event.CoreButtonListener;

/**
 * 
 * <p>
 * 
 * @author <a href="mailto:vfritzsch@users.sourceforge.net">Volker Fritzsch</a>
 */
public class ButtonsDemo
{

	public static void main(String[] args)
	{
		System.out.println("press 'q' to quit.");

		Mote mote = MoteFinder.getMoteFinder().findMote();
		mote.addCoreButtonListener(new CoreButtonListener()
		{

			public void buttonPressed(CoreButtonEvent evt)
			{
				if (evt.isButtonAPressed())
				{
					System.out.println("Button A pressed!");
				}
				if (evt.isButtonBPressed())
				{
					System.out.println("Button B pressed!");
				}
				if (evt.isNoButtonPressed())
				{
					System.out.println("No button pressed.");
				}
			}

		});

		while (true)
		{
			String line = System.console().readLine();
			if (line.indexOf("q") != -1)
			{
				mote.disconnect();
				System.exit(0);
			}
		}
	}
}
