package utils.structures.fast.powergraph.algorythms.test;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import utils.structures.fast.powergraph.algorythms.PowerGraphSpectrum;

/**
 */
public class PowerGraphSpectrumTests
{

	@Test
	public void testGetSpectrumFromBblStream()
	{
		try
		{
			{
				final InputStream lInputStream = PowerGraphSpectrumTests.class.getResourceAsStream("test.bbl");
				final InputStream lInputStreamRewired = PowerGraphSpectrumTests.class.getResourceAsStream("test.rewired.bbl");

				double[][] lSpectrumFromBblStream = PowerGraphSpectrum.getSpectrumFromBblStream(lInputStream);

				assertTrue(lSpectrumFromBblStream[1][1] == 3);
				assertTrue(lSpectrumFromBblStream[1][2] == 0);
				assertTrue(lSpectrumFromBblStream[1][3] == 1);
				assertTrue(lSpectrumFromBblStream[2][1] == 0);
				assertTrue(lSpectrumFromBblStream[2][2] == 0);
				assertTrue(lSpectrumFromBblStream[2][3] == 1);
				assertTrue(lSpectrumFromBblStream[3][1] == 1);
				assertTrue(lSpectrumFromBblStream[3][2] == 1);
				assertTrue(lSpectrumFromBblStream[3][3] == 2);

				System.out.println(PowerGraphSpectrum.writeSpectrumToString(lSpectrumFromBblStream));

			}
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			fail();
		}
	}

}
