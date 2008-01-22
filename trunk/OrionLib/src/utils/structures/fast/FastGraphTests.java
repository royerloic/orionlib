// ©2006 Transinsight GmbH - www.transinsight.com - All rights reserved.
package utils.structures.fast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;


/**
 */
public class FastGraphTests
{

	@Test
	public void testAddNode()
	{
		FastGraph lFastGraph = new FastGraph();

		lFastGraph.addNode("me");
		lFastGraph.addNode("you");
		lFastGraph.addNode("them");

		assertSame(3, lFastGraph.getNumberOfNodes());

		assertTrue(lFastGraph.isNode("me"));
		assertTrue(lFastGraph.isNode("you"));
		assertTrue(lFastGraph.isNode("them"));
		assertFalse(lFastGraph.isNode("aloa"));

	}
	
	@Test
	public void testReadWriteEdgeFile() 
	{

		try
		{
			FastGraph<String> lFastGraph = new FastGraph<String>();

			lFastGraph.addEdge("0", "1");
			lFastGraph.addEdge("0", "2");
			lFastGraph.addEdge("0", "3");

			lFastGraph.addEdge("1", "11");
			lFastGraph.addEdge("1", "12");
			lFastGraph.addEdge("1", "13");

			lFastGraph.addEdge("2", "21");
			lFastGraph.addEdge("2", "22");
			lFastGraph.addEdge("2", "23");

			lFastGraph.addEdge("3", "31");
			lFastGraph.addEdge("3", "32");
			lFastGraph.addEdge("3", "33");

			File lEdgeFile = File.createTempFile("temp", "temp");
			System.out.println(lEdgeFile);
			if (lEdgeFile.exists())
				lEdgeFile.delete();

			FileOutputStream lFileOutPutStream = new FileOutputStream(lEdgeFile);
			lFastGraph.writeEdgeFile(lFileOutPutStream);
			lFileOutPutStream.close();

			FastGraph<String> lFastGraphReadFromFile = new FastGraph<String>();
			FileInputStream lFileInputStream = new FileInputStream(lEdgeFile);
			lFastGraphReadFromFile = FastGraph.readEdgeFile(lFileInputStream);

			assertTrue(lFastGraph.equals(lFastGraphReadFromFile));
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
