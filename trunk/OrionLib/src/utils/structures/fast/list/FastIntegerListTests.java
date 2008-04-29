package utils.structures.fast.list;

import static org.junit.Assert.*;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import utils.structures.fast.set.FastIntegerSet;

/**
 */
public class FastIntegerListTests
{

	@Test
	public void testConstruct()
	{
		FastIntegerList lFastIntegerList = new FastIntegerList();
		
		for (int i=0; i<1000; i++)
		{
			lFastIntegerList.add(i);
		}
		
		assertTrue(lFastIntegerList.size()==1000);
		for (int i=0; i<1000; i++)
		{
		  assertTrue(lFastIntegerList.contains(i));
		}	
		
		//System.out.println(lFastIntegerList);
	}
	
	@Test
	public void testContains()
	{
		FastIntegerList lFastIntegerList = new FastIntegerList();
		for (int i=0; i<1000; i++)
		{
			lFastIntegerList.add(i);
		}
		for (int i=0; i<1000; i++)
		{
			assertTrue(lFastIntegerList.contains(i));
		}
	}
	
	@Test
	public void testIndexOf()
	{
		FastIntegerList lFastIntegerList = new FastIntegerList();
		for (int i=0; i<1000; i++)
		{
			lFastIntegerList.add(i);
		}
		for (int i=0; i<1000; i++)
		{
			assertTrue(lFastIntegerList.indexOf(i)==i);
		}
		for (int i=0; i<1000; i++)
		{
			assertTrue(lFastIntegerList.lastIndexOf(i)==i);
		}
		for (int i=0; i<1000; i++)
		{
			lFastIntegerList.add(i);
		}
		for (int i=0; i<1000; i++)
		{
			assertEquals(lFastIntegerList.lastIndexOf(i),i+1000);
		}
	}
	
	@Test
	public void testToString()
	{
		FastIntegerList lFastIntegerList = new FastIntegerList();
		
		for (int i=0; i<1000; i++)
		{
			lFastIntegerList.add(i);
		}
		
		assertTrue(lFastIntegerList.toString().contains(",999]"));
		assertFalse(lFastIntegerList.toString().contains(",0]"));
		
	}

	@Test
	public void testAddAndRemoveValue()
	{
		FastIntegerList lFastIntegerList = new FastIntegerList();
		
		for (int i=0; i<2000; i++)
		{
			lFastIntegerList.add(i);
		}
		assertTrue(lFastIntegerList.size()==2000);
		
		for (int i=0; i<2000; i++)
		{
			//System.out.println(i);
			//System.out.println(lFastIntegerList);
			lFastIntegerList.del(i);
		}
		assertTrue(lFastIntegerList.size()==0);	
	}
	
	@Test
	public void testAddAll()
	{
		FastIntegerList lFastIntegerList = new FastIntegerList();
		
		for (int i=0; i<1000; i++)
		{
			lFastIntegerList.add(i);
		}
		assertTrue(lFastIntegerList.size()==1000);
		
		lFastIntegerList.addAll(lFastIntegerList);	
		assertEquals(lFastIntegerList.size(),2000);
	}
	
	@Test
	public void testClear()
	{
		FastIntegerList lFastIntegerList = new FastIntegerList();
		
		for (int i=0; i<1000; i++)
		{
			lFastIntegerList.add(i);
		}
		
		lFastIntegerList.clear();
		assertEquals(lFastIntegerList.size(),0);		
	}
	
	
	
	

}
