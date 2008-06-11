package utils.structures;

import java.util.ArrayList;
import java.util.Iterator;

public class Pair<O> extends Couple<O, O> implements Iterable<O>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7087025231228481462L;

	public Pair(final O pA, final O pB)
	{
		super(pA, pB);
	}

	public Iterator<O> iterator()
	{
		final ArrayList<O> lList = new ArrayList<O>();
		lList.add(mA);
		lList.add(mB);
		return lList.iterator();
	}
}
