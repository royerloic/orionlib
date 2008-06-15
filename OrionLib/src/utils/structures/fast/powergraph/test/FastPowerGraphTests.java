package utils.structures.fast.powergraph.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.IOException;
import org.junit.Test;
import utils.structures.fast.powergraph.FastPowerGraph;

/**
 */
public class FastPowerGraphTests
{

	@Test
	public void testReadWrite()
	{
		try
		{
			final FastPowerGraph pg = FastPowerGraph.readBblStream(FastPowerGraphTests.class.getResourceAsStream("lesmis.bbl"));

			// System.out.println(pg);
			final FastPowerGraph pgcloned = FastPowerGraph.readBblFile(pg.toTabDel());

			assertEquals(pg.getPowerEdgeSet(), pgcloned.getPowerEdgeSet());
			assertEquals(pg, pgcloned);

		}
		catch (final IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

}
