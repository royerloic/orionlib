package org.royerloic.structures.graph;

public class Node
{
	private String	mName;
	private int			mHashCode;

	public Node(String pName)
	{
		super();
		mName = pName;
		mHashCode = mName.hashCode();
	}

	@Override
	public String toString()
	{
		return mName;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		return mName.equals(((Node) obj).mName);
	}

	@Override
	public int hashCode()
	{
		return mHashCode;
	}

	public String getName()
	{
		return mName;
	}

}
