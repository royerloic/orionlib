package utils.structures.graph;

public interface RawGraphInterface<ElementLabel>
{

	void setNodeArrays(ElementLabel[] pNodeLabelArray);

	void setEdgeArrays(	int[] pFirstStartNodeArray,
											int[] pSecondNodeArray,
											ElementLabel[] pEdgeLabelArray);

	int getNumberOfNodes();

	int[] getFirstNodeArray();

	int[] getSecondNodeArray();

	ElementLabel[] getElementLabelArray();

}
