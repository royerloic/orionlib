package utils.structures.fast.powergraph.algorythms.test;

import static org.junit.Assert.fail;

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
	public void testgetSpectrumFromBblStream()
	{
		try
		{
			{
				final InputStream lInputStream = PowerGraphSpectrumTests.class.getResourceAsStream("test.bbl");
				final InputStream lInputStreamRewired = PowerGraphSpectrumTests.class.getResourceAsStream("test.rewired.bbl");
				System.out.println("test.bbl");
				PowerGraphSpectrum.getSpectrumFromBblStream(lInputStream);
				System.out.println("test.rewired.bbl");
				PowerGraphSpectrum.getSpectrumFromBblStream(lInputStreamRewired);
			}
		}
		catch (final FileNotFoundException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			fail();
		}
	}

}
