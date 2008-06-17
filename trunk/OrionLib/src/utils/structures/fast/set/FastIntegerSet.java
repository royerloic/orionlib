package utils.structures.fast.set;

import java.util.Set;

public interface FastIntegerSet extends Set<Integer>
{
	public boolean add(final int o);
	
	public void addAll(final int[] o);

	public boolean remove(final int o);
		
	public void removeAll(final int[] o);
	
	public void toggle(final int o);
	
	public void toggleAll(final int[] o);

	public boolean contains(final int o);

	public boolean containsAll(final int... pArray);

	public boolean equals(final int... intarray);

	public Integer getMin(final int pMin);

	public Integer getMax(final int pMax);

	public void clear();

	public void wipe();
	
}
