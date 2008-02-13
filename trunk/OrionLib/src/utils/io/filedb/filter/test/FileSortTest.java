package utils.io.filedb.sort.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import utils.io.filedb.sort.FileSort;

public class FileSortTest
{

	@Test
	public void testSortInt() throws IOException
	{
		InputStream lInputStream = FileSortTest.class.getResourceAsStream("test.tab.txt");
		OutputStream lOutputStream = new ByteArrayOutputStream();
		FileSort.sort(lInputStream, 0, "Double", true, lOutputStream);
		String lString = lOutputStream.toString();
		// System.out.println(lString);
		assertEquals(lString, "2	ab	3.2\n4	aa	1.3E-10\n10	a	2.1\n30	ac	1.0006\n");
	}

	@Test
	public void testSortDouble() throws IOException
	{
		InputStream lInputStream = FileSortTest.class.getResourceAsStream("test.tab.txt");
		OutputStream lOutputStream = new ByteArrayOutputStream();
		FileSort.sort(lInputStream, 2, "Double", true, lOutputStream);
		String lString = lOutputStream.toString();
		// System.out.println(lString);
		assertEquals(lString, "4	aa	1.3E-10\n30	ac	1.0006\n10	a	2.1\n2	ab	3.2\n");
	}

	@Test
	public void testSortString() throws IOException
	{
		InputStream lInputStream = FileSortTest.class.getResourceAsStream("test.tab.txt");
		OutputStream lOutputStream = new ByteArrayOutputStream();
		FileSort.sort(lInputStream, 1, "String", true, lOutputStream);
		String lString = lOutputStream.toString();
		// System.out.println(lString);
		assertEquals(lString, "10	a	2.1\n4	aa	1.3E-10\n2	ab	3.2\n30	ac	1.0006\n");
	}

	@Test
	public void testSortDescendingString() throws IOException
	{
		InputStream lInputStream = FileSortTest.class.getResourceAsStream("test.tab.txt");
		OutputStream lOutputStream = new ByteArrayOutputStream();
		FileSort.sort(lInputStream, 1, "String", false, lOutputStream);
		String lString = lOutputStream.toString();
		// System.out.println(lString);
		assertEquals(lString, "30	ac	1.0006\n2	ab	3.2\n4	aa	1.3E-10\n10	a	2.1\n");
	}

}
