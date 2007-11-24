package utils.io.fileindex;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;

public class FileIndexTest
{
	
	@Test
	public void testFileIndexTest() throws IOException
	{
		File lCurrentFolder = new File(".");
		System.out.println(lCurrentFolder.getAbsolutePath());
		File lFile = new File("bin/utils/io/fileindex/test.tab.txt");
		FileIndex lFileIndex = new FileIndex(lFile,"\\t");
		
		lFileIndex.buildIndex(1);
		
		lFileIndex.open();
		assertEquals(lFileIndex.getLine("bla")[0],"1");
		assertEquals(lFileIndex.getLine("blu")[0],"2");
		assertEquals(lFileIndex.getLine("blublu")[0],"3");
		assertEquals(lFileIndex.getLine("ohoh")[0],"4");

		lFileIndex.close();
	}
		
	@Test
	public void testFileIndexTestWithCache() throws IOException
	{
		File lCurrentFolder = new File(".");
		System.out.println(lCurrentFolder.getAbsolutePath());
		File lFile = new File("bin/utils/io/fileindex/test.tab.txt");
		FileIndex lFileIndex = new FileIndex(lFile,"\\t");
		
		lFileIndex.buildIndex(1);
		
		lFileIndex.open();
		assertEquals(lFileIndex.getLineAndCache("bla")[0],"1");
		assertEquals(lFileIndex.getLineAndCache("blu")[0],"2");
		assertEquals(lFileIndex.getLineAndCache("blublu")[0],"3");
		assertEquals(lFileIndex.getLineAndCache("ohoh")[0],"4");

		lFileIndex.close();
	}

}
