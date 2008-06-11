package utils.io.filedb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import utils.io.StreamToFile;
import utils.io.filedb.FileDB;

public class FileDBTest
{

	static File sTempFile;
	static
	{
		try
		{
			final InputStream lInputStream = FileDBTest.class.getResourceAsStream("Test.tab.txt");
			assertTrue(lInputStream != null);
			sTempFile = File.createTempFile("FileDBTest", "temp");
			StreamToFile.streamToFile(lInputStream, sTempFile);
		}
		catch (final IOException e)
		{
			fail("Could not create temp file");
			e.printStackTrace();
		}
	}

	@Test
	public void testGetColumnNames() throws IOException
	{
		final List<String> lColumnNames = FileDB.getColumnNames(sTempFile);
		assertTrue(lColumnNames.contains("col0"));
		assertTrue(lColumnNames.contains("col1"));
		assertTrue(lColumnNames.contains("col2"));
	}

	@Test
	public void testGetColumnType() throws IOException
	{
		assertEquals(	FileDB.getColumnType(sTempFile, 0, 1000).getSimpleName(),
									"Double");
		assertEquals(	FileDB.getColumnType(sTempFile, 1, 1000).getSimpleName(),
									"String");
		assertEquals(	FileDB.getColumnType(sTempFile, 2, 1000).getSimpleName(),
									"Double");
	}

}
