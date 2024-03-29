package utils.structures.graph;

import java.io.Serializable;

public class Node implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String mName;
	private final int mHashCode;

	public Node(final String pName)
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
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}

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
