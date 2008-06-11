package utils.structures.fast.graph;

import java.io.Serializable;

public class Edge<N> implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1;

	private N mFirstNode;

	private N mSecondNode;

	public Edge(final N pFirstNode, final N pSecondNode)
	{
		super();
		this.mFirstNode = pFirstNode;
		this.mSecondNode = pSecondNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.structures.graph.Edge#setFirstNode(N)
	 */
	public void setFirstNode(final N pFirstNode)
	{
		this.mFirstNode = pFirstNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.structures.graph.Edge#setSecondNode(N)
	 */
	public void setSecondNode(final N pSecondNode)
	{
		this.mSecondNode = pSecondNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.structures.graph.Edge#getFirstNode()
	 */
	public N getFirstNode()
	{
		return this.mFirstNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.royerloic.structures.graph.Edge#getSecondNode()
	 */
	public N getSecondNode()
	{
		return this.mSecondNode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object pObject)
	{
		if (pObject == this)
			return true;
		else
		{
			final Edge<N> lEdge = (Edge<N>) pObject;
			final boolean lEquals = ((this.mFirstNode.equals(lEdge.getFirstNode())) && (this.mSecondNode.equals(lEdge.getSecondNode()))) || ((this.mFirstNode.equals(lEdge.getSecondNode())) && (this.mSecondNode.equals(lEdge.getFirstNode())));
			return lEquals;
		}
	}

	@Override
	public int hashCode()
	{
		return this.mFirstNode.hashCode() ^ this.mSecondNode.hashCode();
	}

	@Override
	public String toString()
	{
		return "(" + this.mFirstNode + "," + this.mSecondNode + ")";
	}

	public boolean symetricTo(final Edge<N> pEdge)
	{
		return (this.mFirstNode.equals(pEdge.getSecondNode())) && (this.mSecondNode.equals(pEdge.getFirstNode()));
	}

	public boolean contains(final N pNode)
	{
		return this.mFirstNode.equals(pNode) || this.mSecondNode.equals(pNode);
	}

}
