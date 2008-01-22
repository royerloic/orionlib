// ©2006 Transinsight GmbH - www.transinsight.com - All rights reserved.
package utils.structures.fast;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

/**
 */
public class FastIntegerGraphUtilsTests
{

	@Test
	public void testReadWriteEdgeFile() 
	{

		try
		{
			FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

			lFastIntegerGraph.addEdge(0, 1);
			lFastIntegerGraph.addEdge(0, 2);
			lFastIntegerGraph.addEdge(0, 3);

			lFastIntegerGraph.addEdge(1, 11);
			lFastIntegerGraph.addEdge(1, 12);
			lFastIntegerGraph.addEdge(1, 13);

			lFastIntegerGraph.addEdge(2, 21);
			lFastIntegerGraph.addEdge(2, 22);
			lFastIntegerGraph.addEdge(2, 23);

			lFastIntegerGraph.addEdge(3, 31);
			lFastIntegerGraph.addEdge(3, 32);
			lFastIntegerGraph.addEdge(3, 33);

			File lEdgeFile = File.createTempFile("temp", "temp");
			System.out.println(lEdgeFile);
			if (lEdgeFile.exists())
				lEdgeFile.delete();

			FileOutputStream lFileOutPutStream = new FileOutputStream(lEdgeFile);
			FastIntegerGraphUtils.writeEdgeFile(lFastIntegerGraph, lFileOutPutStream);
			lFileOutPutStream.close();

			FastIntegerGraph lFastIntegerGraphReadFromFile = new FastIntegerGraph();
			FileInputStream lFileInputStream = new FileInputStream(lEdgeFile);
			FastIntegerGraphUtils.readEdgeFile(	lFastIntegerGraphReadFromFile,
																					lFileInputStream);

			assertTrue(lFastIntegerGraph.equals(lFastIntegerGraphReadFromFile));
		}
		catch (RuntimeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
