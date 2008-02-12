package utils.io.filedb.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import utils.io.StreamToFile;
import utils.io.filedb.FileDB;
import utils.io.filedb.index.FileIndex;

public class FileDBTest
{

	static File sTempFile;
	static
	{
		try
		{
			InputStream lInputStream = FileDBTest.class.getResourceAsStream("test.tab.txt");
			assertTrue(lInputStream != null);
			sTempFile = File.createTempFile("FileDBTest", "temp");
			StreamToFile.streamToFile(lInputStream, sTempFile);
		}
		catch (IOException e)
		{
			fail("Could not create temp file");
			e.printStackTrace();
		}
	}

	@Test
	public void testgetColumnNames() throws IOException
	{
		List<String> lColumnNames = FileDB.getColumnNames(sTempFile);
		assertTrue(lColumnNames.contains("col0"));
		assertTrue(lColumnNames.contains("col1"));
		assertTrue(lColumnNames.contains("col2"));
	}

}
