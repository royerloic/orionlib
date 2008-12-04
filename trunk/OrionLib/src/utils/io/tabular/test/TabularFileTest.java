package utils.io.tabular.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import utils.io.StreamToFile;
import utils.io.tabular.TabularFile;

public class TabularFileTest
{

	@Test
	public void testFileIndexTest() throws IOException
	{
		final InputStream lInputStream = TabularFileTest.class.getResourceAsStream("test.tab.txt");
		final File lTempFile = File.createTempFile(	"TabularFileTest",
																								"testFileIndexTest");
		StreamToFile.streamToFile(lInputStream, lTempFile);
		final TabularFile lTabularFile = new TabularFile(lTempFile, true);
		lTabularFile.read();
		System.out.println(lTabularFile);
	}

}
