package utils.structures.fast.powergraph.measure.test;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import utils.structures.fast.graph.Edge;
import utils.structures.fast.powergraph.FastPowerGraph;
import utils.structures.fast.powergraph.measure.PowerGraphStatistic;
import utils.structures.map.HashDoubleMap;
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
			Map lPowerGraphStats = PowerGraphStatistic.computePowerGraphPValue(pg, 1);
			System.out.println(lPowerGraphStats);

			final FastPowerGraph pgrewired = FastPowerGraph.readBblStream(PowerGraphStatisticTests.class.getResourceAsStream("lesmis.rewired.bbl"));
			Map lRewiredPowerGraphStats = PowerGraphStatistic.computePowerGraphPValue(	pgrewired,
																																					1);
			System.out.println(lRewiredPowerGraphStats);
			
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
	
	@Test
	public void testPowerNodePvalues()
	{
		try
		{
			final FastPowerGraph pg = FastPowerGraph.readBblStream(PowerGraphStatisticTests.class.getResourceAsStream("lesmis.bbl"));
	
			HashDoubleMap<String> lComputePowerNodePValues = PowerGraphStatistic.computePowerNodePValues(pg, 1);
			
			
			
			System.out.println(lComputePowerNodePValues);
			
			for (Map.Entry<String, Double> lEntry : lComputePowerNodePValues.entrySet())
			{
				String lPowerNodeName = lEntry.getKey();
				Set lPowerNodeContent = pg.getPowerNodeContent(lPowerNodeName);
				System.out.println(lPowerNodeContent+" -> "+lEntry.getValue());
			}
			
			//assertTrue(lComputePowerNodePValues.get(new Edge("Babet","Gueulemer"))==0.001585464906564775);
			
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPowerNodePvaluesForBiggerNetwork()
	{
		try
		{
			final FastPowerGraph pg = FastPowerGraph.readBblStream(PowerGraphStatisticTests.class.getResourceAsStream("phosphatases.bbl"));
			
			HashDoubleMap<String> lComputePowerNodePValues = PowerGraphStatistic.computePowerNodePValues(pg, 1);
			
			
			System.out.println(lComputePowerNodePValues);
			
			for (Map.Entry<String, Double> lEntry : lComputePowerNodePValues.entrySet())
			{
				String lPowerNodeName = lEntry.getKey();
				Set lPowerNodeContent = pg.getPowerNodeContent(lPowerNodeName);
				System.out.println(lPowerNodeContent+" -> "+lEntry.getValue());
			}
			
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
	

}
