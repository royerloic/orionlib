package org.royerloic.structures.graph;

public interface Edge<N>
{

	public abstract void setFirstNode(N pFirstNode);

	public abstract void setSecondNode(N pSecondNode);

	public abstract N getFirstNode();

	public abstract N getSecondNode();

	public abstract boolean symetricTo(Edge<N> pEdge);

	public abstract boolean contains(N pNode);

	public abstract Edge createSymetricEdge();

	public abstract String getName();

	public abstract Edge<N> newInstance(N pFirstNode, N pSecondNode);

}