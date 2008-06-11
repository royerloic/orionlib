package utils.structures.fast.powergraph.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

import utils.structures.fast.graph.FastIntegerGraph;
import utils.structures.fast.powergraph.FastIntegerPowerGraph;
import utils.structures.fast.powergraph.FastPowerGraph;
import utils.structures.fast.set.FastBoundedIntegerSet;

/**
 */
public class FastPowerGraphTests
{


	@Test
	public void testReadWrite()
	{
		try
		{
			FastPowerGraph pg = FastPowerGraph.readEdgeFile(FastPowerGraphTests.class.getResourceAsStream("lesmis.bbl"));
			
			//System.out.println(pg);
			FastPowerGraph pgcloned = FastPowerGraph.readEdgeFile(pg.toTabDel());
			
			System.out.println(pg.getPowerEdgeList());
			System.out.println(pgcloned.getPowerEdgeList());
			
			assertEquals(pg.getPowerEdgeSet(),pgcloned.getPowerEdgeSet());
			assertEquals(pg,pgcloned);
			
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

}
