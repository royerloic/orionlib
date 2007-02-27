package org.royerloic.structures.graph;

public class DirectedEdge<N> implements Edge<N>
{
	private N				mFirstNode;

	private N				mSecondNode;

	private String	mName;

	public DirectedEdge(N pFirstNode, N pSecondNode, String pName)
	{
		super();
		mFirstNode = pFirstNode;
		mSecondNode = pSecondNode;
		mName = pName;
	}

	public DirectedEdge(N pFirstNode, N pSecondNode)
	{
		this(pFirstNode, pSecondNode, pFirstNode.toString() + "-" + pSecondNode.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.structures.graph.Edge#setFirstNode(N)
	 */
	public void setFirstNode(N pFirstNode)
	{
		mFirstNode = pFirstNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.structures.graph.Edge#setSecondNode(N)
	 */
	public void setSecondNode(N pSecondNode)
	{
		mSecondNode = pSecondNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.structures.graph.Edge#getFirstNode()
	 */
	public N getFirstNode()
	{
		return mFirstNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.structures.graph.Edge#getSecondNode()
	 */
	public N getSecondNode()
	{
		return mSecondNode;
	}

	public boolean equals(Object pObject)
	{
		if (pObject == this)
		{
			return true;
		}
		else
		{
			Edge<N> lEdge = (Edge<N>) pObject;
			boolean lEquals = (mFirstNode.equals(lEdge.getFirstNode()))
					&& (mSecondNode.equals(lEdge.getSecondNode()));
			return lEquals;
		}
	}

	@Override
	public int hashCode()
	{
		return mFirstNode.hashCode() + mSecondNode.hashCode();
	}

	@Override
	public String toString()
	{
		return mName + "(" + mFirstNode + "," + mSecondNode + ")";
	}

	public boolean symetricTo(Edge<N> pEdge)
	{
		return (mFirstNode.equals(pEdge.getSecondNode())) && (mSecondNode.equals(pEdge.getFirstNode()));
	}

	public boolean contains(N pNode)
	{
		return mFirstNode.equals(pNode) || mSecondNode.equals(pNode);
	}

	public Edge<N> createSymetricEdge()
	{
		return new DirectedEdge(mSecondNode, mFirstNode, mName);
	}

	public String getName()
	{
		return mName;
	}

	public boolean isSymetric()
	{
		return false;
	}

}
