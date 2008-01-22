package utils.structures.fast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.regex.Pattern;

public class FastIntegerGraphUtils
{

	public static void writeEdgeFile(	FastIntegerGraph pIntegerGraph,
																		OutputStream pOutputStream) throws IOException
	{
		final Writer lWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream));

		for (int[] lEdge : pIntegerGraph.getEdgeSet())
		{
			lWriter.append("EDGE\t" + lEdge[0] + "\t" + lEdge[1] + "\n");
		}
	}

	public static FastIntegerGraph writeEdgeFile(	FastIntegerGraph pIntegerGraph,
																								InputStream pInputStream) throws IOException
	{
		BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(pInputStream));

		Pattern lPattern = Pattern.compile("\t");

		for (int[] lEdge : pIntegerGraph.getEdgeSet())

		{
			final String lLine = lBufferedReader.readLine();
			if (lLine.startsWith("EDGE"))
			{
				final String[] lArray = lPattern.split(lLine, -1);
				final String lFirstNodeString = lArray[1];
				final String lSecondNodeString = lArray[2];
				final int node1 = Integer.parseInt(lFirstNodeString);
				final int node2 = Integer.parseInt(lSecondNodeString);
				pIntegerGraph.addEdge(node1, node2);
			}
		}
		
		return pIntegerGraph;
	}

}
