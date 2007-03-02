package org.royerloic.structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArrayMatrix<O> extends ArrayList<List<O>> implements Matrix<O>
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2464738472120882612L;

	public ArrayMatrix()
	{
		super();
	}
	
	public ArrayMatrix(Matrix<O> pMatrix)
	{
		super();
		for (List<O> lList : pMatrix)
		{
			add(new ArrayList<O>(lList));
		}
	}

	public ArrayMatrix(Collection<? extends List<O>> pC)
	{
		super(pC);
	}

	public ArrayMatrix(int pInitialCapacity)
	{
		super(pInitialCapacity);
	}

	public void add2Point(final O p1, final O p2)
	{
		List<O> lList = new ArrayList<O>();
		lList.add(p1);
		lList.add(p2);
		add(lList);
	}
}
