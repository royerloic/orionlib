package utils.io.filedb.index.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import utils.io.StreamToFile;
import utils.io.filedb.index.FileIndex;

public class FileIndexTest
{

	static File sTempFile;
	static
	{
		try
		{
			InputStream lInputStream = FileIndexTest.class.getResourceAsStream("test.tab.txt");
			assertTrue(lInputStream != null);
			sTempFile = File.createTempFile("FileIndexTest", "temp");
			StreamToFile.streamToFile(lInputStream, sTempFile);
		}
		catch (IOException e)
		{
			fail("Could not create temp file");
			e.printStackTrace();
		}
	}

	@Test
	public void testFileIndexTest() throws IOException
	{
		File lCurrentFolder = new File(".");
		System.out.println(lCurrentFolder.getAbsolutePath());

		FileIndex lFileIndex = new FileIndex(sTempFile, "\\t");

		lFileIndex.buildIndex(1);

		lFileIndex.open();
		assertEquals(lFileIndex.getLine("bla")[0], "1");
		assertEquals(lFileIndex.getLine("blu")[0], "2");
		assertEquals(lFileIndex.getLine("blublu")[0], "3");
		assertEquals(lFileIndex.getLine("ohoh")[0], "4");

		lFileIndex.close();
	}

	@Test
	public void testFileIndexTestWithCache() throws IOException
	{
		FileIndex lFileIndex = new FileIndex(sTempFile, "\\t");

		lFileIndex.buildIndex(1);

		lFileIndex.open();
		assertEquals(lFileIndex.getLineAndCache("bla")[0], "1");
		assertEquals(lFileIndex.getLineAndCache("blu")[0], "2");
		assertEquals(lFileIndex.getLineAndCache("blublu")[0], "3");
		assertEquals(lFileIndex.getLineAndCache("ohoh")[0], "4");

		lFileIndex.close();
	}

}
