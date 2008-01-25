package utils.structures.powergraph.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import utils.structures.graph.Edge;
import utils.structures.powergraph.PowerGraph;

public class PowerEdgeDump<N>
{
	public static <N> void dumpPowerEdges(final PowerGraph<N> pPowerGraph,
																				final File pFile) throws IOException
	{
		{
			if (pFile == null)
				throw new IllegalArgumentException("File should not be null.");

			// declared here only to make visible to finally clause; generic reference
			Writer lWriter = null;
			try
			{
				// use buffering
				// FileWriter always assumes default encoding is OK!
				lWriter = new BufferedWriter(new FileWriter(pFile));

				for (final Edge<Set<N>> lPowerEdge : pPowerGraph.getPowerEdgeSet())
				{
					String lFirst = lPowerEdge.getFirstNode().toString();
					lFirst = lFirst.replace('[', ' ');
					lFirst = lFirst.replace(']', ' ');
					lWriter.append(lFirst);

					lWriter.append("|");

					String lSecond = lPowerEdge.getSecondNode().toString();
					lSecond = lSecond.replace('[', ' ');
					lSecond = lSecond.replace(']', ' ');
					lWriter.append(lSecond);

					lWriter.append("\r\n");
				}

			}
			finally
			{
				// flush and close both "output" and its underlying FileWriter
				if (lWriter != null)
					lWriter.close();
			}
		}

	}
}
