/**
 * Copyrights Transinsight GmbH 2007
 * Author: waechter
 */
package utils.structures;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.TreeSet;

import org.junit.Test;

import utils.structures.MapValueComparator;

/**
 * TODO describe me
 */
public class MapValueComparatorTest
{

	/**
	 * Test method for {@link utils.structures.MapValueComparator#compare(java.lang.Object, java.lang.Object)}.
	 */
	@Test
	public void testCompareStringInteger()
	{
		final HashMap<String, Integer> map1 = new HashMap<String, Integer>();
		map1.put("a", 4);
		map1.put("b", 1);
		map1.put("c", 2);
		map1.put("d", 4);
		map1.put("3", 2);

		final MapValueComparator<String, Integer> comp1 = new MapValueComparator<String, Integer>(map1);

		final TreeSet<String> set1 = new TreeSet<String>(comp1);
		set1.add("a");
		set1.add("b");
		set1.add("c");
		set1.add("d");
		set1.add("3");
		String resultString = "";
		for (final String string : set1) {
			resultString += string;
		}
		assertEquals("b3cad", resultString);
	}

	/**
	 * Test method for {@link utils.structures.MapValueComparator#compare(java.lang.Object, java.lang.Object)}.
	 */
	@Test
	public void testCompareStringString()
	{

		final HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("a", "f");
		map2.put("b", "a");
		map2.put("c", "c");
		map2.put("d", "b");
		map2.put("f", "b");

		final MapValueComparator<String, String> comp2 = new MapValueComparator<String, String>(map2);

		final TreeSet<String> set2 = new TreeSet<String>(comp2);
		set2.add("a");
		set2.add("b");
		set2.add("c");
		set2.add("d");
		set2.add("f");
		String resultString = "";
		for (final String string : set2) {
			resultString += string;
		}
		assertEquals("bdfca", resultString);
	}

}
