package org.royerloic.structures;

import java.util.ArrayList;
import java.util.List;

public class ArrayMatrix<O> extends ArrayList<List<O>> implements Matrix<O>
{
	public void add2Point(final O p1, final O p2)
	{
		List<O> lList = new ArrayList<O>();
		lList.add(p1);
		lList.add(p2);
		add(lList);
	}
}
