package utils.structures.graph;

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

	public boolean isSymetric();

	public void setConfidence(double pConfidence);

	public double getConfidence();

	public void setFirstNodeConfidence(double pConfidence);

	public void setSecondNodeConfidence(double pConfidence);

	public double getFirstNodeConfidence();

	public double getSecondNodeConfidence();

}