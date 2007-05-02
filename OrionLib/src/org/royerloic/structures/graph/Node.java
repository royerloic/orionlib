package org.royerloic.structures.graph;

public class Node
{
	private String	mName;
	private int			mHashCode;

	public Node(final String pName)
	{
		super();
		this.mName = pName;
		this.mHashCode = this.mName.hashCode();
	}

	@Override
	public String toString()
	{
		return this.mName;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;

		return this.mName.equals(((Node) obj).mName);
	}

	@Override
	public int hashCode()
	{
		return this.mHashCode;
	}

	public String getName()
	{
		return this.mName;
	}

}
