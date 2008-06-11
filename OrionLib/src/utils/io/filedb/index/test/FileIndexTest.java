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
			final InputStream lInputStream = FileIndexTest.class.getResourceAsStream("Test.tab.txt");
			assertTrue(lInputStream != null);
			sTempFile = File.createTempFile("FileIndexTest", "temp");
			StreamToFile.streamToFile(lInputStream, sTempFile);
		}
		catch (final IOException e)
		{
			fail("Could not create temp file");
			e.printStackTrace();
		}
	}

	@Test
	public void testFileIndexTest() throws IOException
	{
		final File lCurrentFolder = new File(".");
		System.out.println(lCurrentFolder.getAbsolutePath());

		final FileIndex lFileIndex = new FileIndex(sTempFile, "\\t");

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
		final FileIndex lFileIndex = new FileIndex(sTempFile, "\\t");

		lFileIndex.buildIndex(1);

		lFileIndex.open();
		assertEquals(lFileIndex.getLineAndCache("bla")[0], "1");
		assertEquals(lFileIndex.getLineAndCache("blu")[0], "2");
		assertEquals(lFileIndex.getLineAndCache("blublu")[0], "3");
		assertEquals(lFileIndex.getLineAndCache("ohoh")[0], "4");

		lFileIndex.close();
	}

}
