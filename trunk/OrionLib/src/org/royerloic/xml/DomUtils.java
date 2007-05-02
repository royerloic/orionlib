/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package org.royerloic.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * DomManip is a utility class used to manipulate DOM trees.
 * 
 * @author MSc. Ing. Loic Royer
 */
public final class DomUtils
{

	/**
	 * Hides the default Constructor.
	 */
	private DomUtils()
	{
	};

	/**
	 * Returns a sub node of <code>pNode</code> given its name
	 * <code>pName</code>.
	 * 
	 * @param pNode
	 * @param pName
	 * @return child DOM Mode by its name.
	 */
	public static final Node getSubNodeByName(final Node pNode, final String pName)
	{
		Node lNProjectPart = null;
		for (lNProjectPart = pNode.getFirstChild(); lNProjectPart != null; lNProjectPart = lNProjectPart
				.getNextSibling())
			if (lNProjectPart.getNodeName().equalsIgnoreCase(pName))
				break;

		return lNProjectPart;
	}

	/**
	 * Returns a <code>Vector</code>
	 * 
	 * @param pNode
	 * @param pName
	 * @return list of DOM Nodes
	 */
	public static final List<Node> getAllSubNodeByName(final Node pNode, final String pName)
	{
		final List<Node> lSubNodeList = new ArrayList<Node>();

		Node lNProjectPart = null;
		for (lNProjectPart = pNode.getFirstChild(); lNProjectPart != null; lNProjectPart = lNProjectPart
				.getNextSibling())
			if (lNProjectPart.getNodeName().equalsIgnoreCase(pName))
				lSubNodeList.add(lNProjectPart);

		return lSubNodeList;
	}

	/**
	 * Returns a <code>Vector</code>
	 * 
	 * @param pNode
	 * @param pName
	 * @return list of DOM Nodes
	 */
	public static final List<Node> getAllSubNodeByNameRecursive(final Node pNode, final String pName)
	{
		final List<Node> lSubNodeList = new ArrayList<Node>();

		Node lNode = null;
		for (lNode = pNode.getFirstChild(); lNode != null; lNode = lNode.getNextSibling())
		{
			if (lNode.getNodeName().equalsIgnoreCase(pName))
				lSubNodeList.add(lNode);
			lSubNodeList.addAll(getAllSubNodeByNameRecursive(lNode, pName));
		}

		return lSubNodeList;
	}

	/**
	 * @param pNode
	 * @param pName
	 * @return value associated to attribute name.
	 * @throws Exception
	 */
	public static final String getAttributeValueByName(final Node pNode, final String pName) throws Exception
	{
		final NamedNodeMap lMap = pNode.getAttributes();
		String lResult = null;
		if (lMap != null)
		{
			final int len = lMap.getLength();
			for (int i = 0; i < len; i++)
				if (((Attr) lMap.item(i)).getNodeName().equalsIgnoreCase(pName))
				{
					lResult = ((Attr) lMap.item(i)).getValue();
					break;
				}
		}
		if (lResult == null)
			throw new Exception("Could not find attribute: '" + pName + "'");
		return lResult;
	}

	/**
	 * @param pMainNode
	 * @param pSubNode
	 */
	public static final void addSubNode(final Node pMainNode, final Node pSubNode)
	{
		pMainNode.appendChild(pSubNode);
	}

	/**
	 * @param pDoc
	 * @param pMainNode
	 */
	public static final void addCR(final Document pDoc, final Node pMainNode)
	{
		pMainNode.appendChild(pDoc.createTextNode("\n"));
	}

	/**
	 * @param pDoc
	 * @param pName
	 * @return Node created by its name.
	 */
	public static final Node createNodeByName(final Document pDoc, final String pName)
	{
		final Node lChildNode = pDoc.createElement(pName);
		return lChildNode;
	}

	/**
	 * @param pDoc
	 * @param pNode
	 * @param pName
	 * @param pValue
	 */
	public static final void addAttributeValueByName(	final Document pDoc,
																										final Node pNode,
																										final String pName,
																										final String pValue)
	{
		((Element) pNode).setAttribute(pName, pValue);
	}

	/**
	 * @param pDoc
	 * @param pFileName
	 */
	public static final void writeDOMtoXML(final Document pDoc, final String pFileName)
	{
		final File lXmlFile = new File(pFileName);

		try
		{
			// Use a Transformer for output
			final TransformerFactory lTransformerFactory = TransformerFactory.newInstance();
			final Transformer lTransformer = lTransformerFactory.newTransformer();

			FileOutputStream lFileOutputStream;
			try
			{
				final DOMSource lDOMSource = new DOMSource(pDoc);
				lFileOutputStream = new FileOutputStream(lXmlFile);
				final StreamResult lStreamResult = new StreamResult(lFileOutputStream);
				lTransformer.transform(lDOMSource, lStreamResult);
				lFileOutputStream.flush();
				lFileOutputStream.close();
			}
			catch (final FileNotFoundException e)
			{
				e.printStackTrace(System.out);
			}
			catch (final IOException e)
			{
				e.printStackTrace(System.out);
			}

		}
		catch (final TransformerConfigurationException tce)
		{
			// Error generated by the parser
			System.out.println("\n** Transformer Factory error");
			System.out.println("   " + tce.getMessage());

			// Use the contained exception, if any
			Throwable lException = tce;
			if (tce.getException() != null)
				lException = tce.getException();
			lException.printStackTrace(System.out);

		}
		catch (final TransformerException te)
		{
			// Error generated by the parser
			System.out.println("\n** Transformation error");
			System.out.println("   " + te.getMessage());

			// Use the contained exception, if any
			Throwable lException = te;
			if (te.getException() != null)
				lException = te.getException();
			lException.printStackTrace(System.out);

		}

	}

}