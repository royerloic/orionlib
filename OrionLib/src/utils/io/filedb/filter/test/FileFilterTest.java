package utils.io.filedb.filter.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import utils.io.filedb.filter.FileFilter;

public class FileFilterTest
{

	@Test
	public void testFilterOut() throws IOException
	{
		InputStream lInputStream = FileFilterTest.class.getResourceAsStream("test.tab.txt");
		OutputStream lOutputStream = new ByteArrayOutputStream();
		FileFilter.filterOut(lInputStream, 1, "ohoh", lOutputStream);
		String lString = lOutputStream.toString();
		System.out.println(lString);
		assertFalse(lString.contains("ohoh"));
	}

	@Test
	public void testFilterIn() throws IOException
	{
		InputStream lInputStream = FileFilterTest.class.getResourceAsStream("test.tab.txt");
		OutputStream lOutputStream = new ByteArrayOutputStream();
		FileFilter.filterIn(lInputStream, 1, "ohoh", lOutputStream);
		String lString = lOutputStream.toString();
		assertTrue(lString.contains("ohoh"));
		assertFalse(lString.contains("3"));
		assertFalse(lString.contains("1"));
	}

}
