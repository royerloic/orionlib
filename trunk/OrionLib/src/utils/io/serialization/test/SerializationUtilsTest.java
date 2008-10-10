package utils.io.serialization.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;

import org.junit.Test;

import utils.io.serialization.SerializationUtils;
import utils.network.groovyserver.GroovyClient;
import utils.network.groovyserver.GroovyServer;

public class SerializationUtilsTest
{

	@Test
	public void test() 
	{
		try
		{
			File lFile = File.createTempFile("SerializationUtilsTest", "array");
			
			final int[] array = new int[1000];
			for (int i = 0; i < array.length; i++)
			{
				array[i]=i;
			}
			
			SerializationUtils.write(array,lFile);
			
			final int[] read = (int[]) SerializationUtils.read(lFile);
			
			for (int i = 0; i < array.length; i++)
			{
				assertTrue(array[i]==read[i]);
			}
			
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testGzipped() 
	{
		try
		{
			File lFile = File.createTempFile("SerializationUtilsTest", "array");
			
			final int[] array = new int[1000];
			for (int i = 0; i < array.length; i++)
			{
				array[i]=i;
			}
			
			SerializationUtils.writegzipped(array,lFile);
			
			final int[] read = (int[]) SerializationUtils.readgzipped(lFile);
			
			for (int i = 0; i < array.length; i++)
			{
				assertTrue(array[i]==read[i]);
			}
			
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail();
		}
	}

}
