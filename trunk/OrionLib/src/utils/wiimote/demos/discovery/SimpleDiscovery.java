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
package utils.wiimote.demos.discovery;

import utils.wiimote.Mote;
import utils.wiimote.MoteFinder;

/**
 * 
 * <p>
 * 
 * @author <a href="mailto:vfritzsch@users.sourceforge.net">Volker Fritzsch</a>
 */
public class SimpleDiscovery
{

	public static void main(String[] args)
	{
		try
		{
			Mote mote = MoteFinder.getMoteFinder().findMote();
			mote.setPlayerLeds(new boolean[]
			{ true, false, false, false });
			Thread.sleep(5000l);
			mote.disconnect();
		}
		catch (Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
