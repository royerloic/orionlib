package utils.structures.fast.powergraph.measure.test;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import utils.structures.fast.powergraph.FastPowerGraph;
import utils.structures.fast.powergraph.measure.PowerGraphStatistic;
import utils.utils.Arrays;

/**
 */
public class PowerGraphStatisticTests
{

	@Test
	public void testPowerGraphStatistic()
	{
		try
		{
			final FastPowerGraph pg = FastPowerGraph.readBblStream(PowerGraphStatisticTests.class.getResourceAsStream("lesmis.bbl"));
			double[] lPowerGraphPValue = PowerGraphStatistic.powerGraphPValue(pg, 1);
			System.out.println(Arrays.toString(lPowerGraphPValue));

			final FastPowerGraph pgrewired = FastPowerGraph.readBblStream(PowerGraphStatisticTests.class.getResourceAsStream("lesmis.rewired.bbl"));
			double[] lPowerGraphRewiredPValue = PowerGraphStatistic.powerGraphPValue(	pgrewired,
																																								1);
			System.out.println(Arrays.toString(lPowerGraphRewiredPValue));
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

}
