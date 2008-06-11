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
				final InputStream lInputStream = PowerGraphSpectrumTests.class.getResourceAsStream("hprd.bbl");
				System.out.println("hprd.bbl");
				PowerGraphSpectrum.getSpectrumFromBblStream(lInputStream);
			}

			{
				final InputStream lInputStream = PowerGraphSpectrumTests.class.getResourceAsStream("Gavin.bbl");
				final InputStream lInputStreamRewired = PowerGraphSpectrumTests.class.getResourceAsStream("Gavin.rewired.bbl");
				System.out.println("Gavin.bbl");
				PowerGraphSpectrum.getSpectrumFromBblStream(lInputStream);
				System.out.println("Gavin.rewired.bbl");
				PowerGraphSpectrum.getSpectrumFromBblStream(lInputStreamRewired);
			}

			{
				final InputStream lInputStream = PowerGraphSpectrumTests.class.getResourceAsStream("Lacount.bbl");
				final InputStream lInputStreamRewired = PowerGraphSpectrumTests.class.getResourceAsStream("Lacount.rewired.bbl");
				System.out.println("Lacount.bbl");
				PowerGraphSpectrum.getSpectrumFromBblStream(lInputStream);
				System.out.println("Lacount.rewired.bbl");
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
