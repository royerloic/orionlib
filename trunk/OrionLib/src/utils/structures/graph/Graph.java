package utils.structures.graph;

import java.util.Collection;
import java.util.Set;

public interface Graph<N, E>
{
	public void addNode(N pNode);

	public void addEdge(E pEdge);

	public void addGraph(Graph<N, E> pGraph);

	public void removeNode(N pNode);

	public void removeAllNodes(Set<N> pNodeSet);

	public void removeEdge(N pFirstNode, N pSecondNode);

	public int getNumberOfNodes();

	public int getNumberOfEdges();

	public Set<N> getNodeSet();

	public Set<E> getEdgeSet();

	public Set<N> getNodeNeighbours(N pNode);

	public Set<N> getNodeNeighbours(Collection<N> pNodeCollection);

	public Set<N> getNodeNeighbours(N pNode, int pDepth);

	public boolean isEdge(N pNode1, N pNode2);

	public Set<E> getNeighbouringEdges(N pNode);

	public Graph<N, E> extractStrictSubGraph(Set<N> pNodeSet);

	public Graph<N, E> extractSubGraph(Set<N> pNodeSet);

	public Double getAverageDegree();
}
