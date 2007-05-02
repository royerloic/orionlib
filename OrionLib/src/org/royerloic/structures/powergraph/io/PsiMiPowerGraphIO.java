package org.royerloic.structures.powergraph.io;

/*
 * @(#)Echo02.java 1.5 99/02/09
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.royerloic.structures.graph.Node;
import org.royerloic.structures.graph.UndirectedEdge;
import org.royerloic.structures.powergraph.PowerGraph;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PsiMiPowerGraphIO extends DefaultHandler
{
	private boolean							mSpokeModel;
	private String							mText;
	private String							mInteractorId;
	private String							mInteractorName;
	private String							mRole;
	private final Map<String, String>	lIdToNameMap					= new HashMap<String, String>();
	private final Map<Node, String>		lInteractorsToRoleMap	= new HashMap<Node, String>();
	private PowerGraph<Node>		mPowerGraph;

	public PowerGraph<Node> load(final File pFile, final boolean pSpokeModel)
	{
		this.mSpokeModel = pSpokeModel;
		FileInputStream lFileInputStream;
		try
		{
			lFileInputStream = new FileInputStream(pFile);
			final PowerGraph<Node> lPowerGraph = load(lFileInputStream);

			return lPowerGraph;
		}
		catch (final FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public PowerGraph<Node> load(final InputStream pInputStream)
	{
		this.mPowerGraph = new PowerGraph<Node>();

		// Use an instance of ourselves as the SAX event handler
		final DefaultHandler handler = this;
		// Use the default (non-validating) parser
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		try
		{
			// Parse the input
			final SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(pInputStream, handler);

		}
		catch (final Throwable t)
		{
			t.printStackTrace();
		}

		return this.mPowerGraph;
	}

	// ===========================================================
	// SAX DocumentHandler methods
	// ===========================================================

	@Override
	public void startDocument() throws SAXException
	{
		// System.out.println("Start of document");
	}

	@Override
	public void startElement(final String namespaceURI, final String lName, // local name
														final String qName, // qualified name
														final Attributes attrs) throws SAXException
	{
		if (qName.equals("interactor") || qName.equals("proteinInteractor"))
		{
			this.mInteractorId = attrs.getValue("id");
			this.mInteractorName = null;
		}
		else if (qName.equals("interaction"))
			startInteraction();
		else if (qName.equals("proteinInteractorRef"))
			this.mInteractorId = attrs.getValue("ref");

	}

	@Override
	public void characters(final char buf[], final int offset, final int len) throws SAXException
	{
		this.mText = new String(buf, offset, len);
	}

	@Override
	public void endElement(final String namespaceURI, final String sName, // simple name
													final String qName // qualified name
	) throws SAXException
	{

		if (qName.equals("interactor") || qName.equals("proteinInteractor"))
		{
			this.lIdToNameMap.put(this.mInteractorId, this.mInteractorName);
			this.mPowerGraph.addNode(new Node(this.mInteractorName));
		}
		else if (qName.equals("interaction"))
			endInteraction();
		else if (qName.equals("interactorRef"))
		{
			if (this.mText != null)
				this.mInteractorId = this.mText;
			addInteractor(this.mInteractorId, this.lIdToNameMap.get(this.mInteractorId), this.mRole);
		}
		else if (qName.equals("shortLabel"))
		{
			if (this.mInteractorName == null)
				this.mInteractorName = this.mText;
		}
		else if (qName.equals("role"))
		{
			this.mRole = this.mText;
			addInteractor(this.mInteractorId, this.lIdToNameMap.get(this.mInteractorId), this.mRole);
		}

	}

	private void startInteraction()
	{
		this.lInteractorsToRoleMap.clear();
	}

	private void addInteractor(final String lInteractorId, final String pName, final String pRole)
	{
		this.lInteractorsToRoleMap.put(new Node(pName), pRole);
	}

	private void endInteraction()
	{
		if (this.lInteractorsToRoleMap.size() == 1)
		{
			final Node lNode = this.lInteractorsToRoleMap.keySet().iterator().next();
			final Set<Node> lPowerNode = new HashSet<Node>();
			lPowerNode.add(lNode);
			this.mPowerGraph.addPowerEdge(new UndirectedEdge<Set<Node>>(lPowerNode, lPowerNode));
		}
		else if (!this.mSpokeModel)
			this.mPowerGraph.addPowerEdgeDelayed(new UndirectedEdge<Set<Node>>(this.lInteractorsToRoleMap.keySet(),
					this.lInteractorsToRoleMap.keySet()));
		else if (this.mSpokeModel)
		{
			Node lBait = null;
			for (final Node lNode : this.lInteractorsToRoleMap.keySet())
				if (this.lInteractorsToRoleMap.get(lNode).equals("bait"))
				{
					lBait = lNode;
					break;
				}

			if (lBait != null)
			{
				final Set<Node> lBaitPowerNode = new HashSet<Node>();
				lBaitPowerNode.add(lBait);
				final Set<Node> lPreysPowerNode = new HashSet<Node>();
				lPreysPowerNode.addAll(this.lInteractorsToRoleMap.keySet());
				lPreysPowerNode.removeAll(lBaitPowerNode);

				this.mPowerGraph.addPowerEdgeDelayed(new UndirectedEdge<Set<Node>>(lBaitPowerNode, lPreysPowerNode));
			}
			else
			{
				final Set<Node> lPowerNode = this.lInteractorsToRoleMap.keySet();
				this.mPowerGraph.addPowerEdgeDelayed(new UndirectedEdge<Set<Node>>(lPowerNode, lPowerNode));
			}

		}

	}

	@Override
	public void endDocument() throws SAXException
	{
		this.mPowerGraph.commitDelayedEdges();
	}
}
