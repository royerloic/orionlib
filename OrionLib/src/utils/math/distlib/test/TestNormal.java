/*
 * Created on Apr 15, 2007
 */
package utils.math.distlib.test;

import utils.math.distlib.normal;
import junit.framework.TestCase;

public class TestNormal extends TestCase
{

	public void testCumulative()
	{
		assertEquals(0.8508, normal.cumulative(1.04, 0.0, 1.0), 1e-4);
		assertEquals(0.9975, normal.cumulative(2.81, 0.0, 1.0), 1e-4);
	}
}
