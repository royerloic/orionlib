/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package utils.xml;

import org.w3c.dom.Node;

/**
 * IDOM
 * 
 * @author MSc. Ing. Loic Royer
 */
public interface IDOM
{
	/**
	 * @param pNode
	 * @throws Exception
	 */
	void toObject(Node pNode) throws Exception;

	/**
	 * @param pDoc
	 * @return a DOM Node represeting this object.
	 */
	Node toDOM(org.w3c.dom.Document pDoc);
}