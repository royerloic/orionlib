package utils.structures;

import java.util.Comparator;
import java.util.Map;

/**
 * Comparator which orderes the elements accoring to the values in the give lookupMap in natural or reverse order.
 * 
 * @param <K>
 * @param <V>
 */
public class MapValueComparator<K, V> implements Comparator<K>
{
	private Map<K, V> lookupMap;
	private boolean reverseOrderOfValue2;

	/**
	 * Generates a Comparator with natural order.
	 * 
	 * @param lookupMap
	 */
	public MapValueComparator(final Map<K, V> lookupMap)
	{
		this(lookupMap, false);
	}

	/**
	 * Generates a Comparator with natural order or reverseOrder.
	 * 
	 * @param lookupMap
	 * @param reverseOrderOfValue
	 */
	public MapValueComparator(final Map<K, V> lookupMap, final boolean reverseOrderOfValue)
	{
		this.lookupMap = lookupMap;
		this.reverseOrderOfValue2 = reverseOrderOfValue;
	}

	/**
	 * see {@link Comparator
	 */
	public int compare(final K o1, final K o2)
	{
		int i = 0;
		V value1;
		V value2;
		if (reverseOrderOfValue2) {
			value1 = lookupMap.get(o2);
			value2 = lookupMap.get(o1);
		}
		else {
			value1 = lookupMap.get(o1);
			value2 = lookupMap.get(o2);
		}
		i = compareObjects(value1, value2);
		if (0 == i) {
			i = compareObjects(o1, o2);
		}
		return i;
	}

	/**
	 * Helper to compare values.
	 * 
	 * @param o1
	 * @param o2
	 * @param i
	 * @return
	 */
	private int compareObjects(final Object o1, final Object o2)
	{
		int i = 0;
		if (o1 instanceof String) {
			final String str1 = (String) o1;
			final String str2 = (String) o2;
			i = str1.compareTo(str2);
		}
		else if (o1 instanceof Number) {
			final Number number1 = (Number) o1;
			final Number number2 = (Number) o2;
			final double d1 = number1.doubleValue();
			final double d2 = number2.doubleValue();
			if (d1 == d2)
			{
				i = 0;
			}
			if (d1 > d2)
			{
				i = 1;
			}
			if (d1 < d2)
			{
				i = -1;
			}
		}
		else {
			final String str1 = o1.toString();
			final String str2 = o2.toString();
			i = str1.compareTo(str2);
		}
		return i;
	}
}
