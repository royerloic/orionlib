package utils.io.tabular.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;

import utils.io.tabular.TabularFile;

public class TabularFileTest
{
	
	@Test
	public void testFileIndexTest() throws IOException
	{
		File lCurrentFolder = new File(".");
		System.out.println(lCurrentFolder.getAbsolutePath());
		File lFile = new File("bin/utils/io/tabular/test/test.tab.txt");
		TabularFile lTabularFile = new TabularFile(lFile,true);
		
		lTabularFile.read();
		
		System.out.println(lTabularFile);
		
		
	}
		
}
