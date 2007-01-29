package org.royerloic.structures.graph.io.psimi;

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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.royerloic.bioinformatics.ids.GoIdConversion;
import org.royerloic.bioinformatics.ids.InterproIdConversion;
import org.royerloic.structures.graph.UndirectedEdge;
import org.royerloic.structures.graph.io.EdgIO;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PsiMiIO
{

	private static final class PsiMiHandler extends DefaultHandler
	{
		public PsiMiGraph									mGraph;

		private boolean										mSpokeModel;
		private boolean										mIsInteractorDefinition;
		private String										mText;
		private String										mInteractorId;
		private PsiMiNode									mInteractorNode;
		private String										mRole;
		private Map<PsiMiNode, String>		lInteractorsToRoleMap	= new HashMap<PsiMiNode, String>();
		private Map<PsiMiNode, PsiMiNode>	lPsiMiNodeMap					= new HashMap<PsiMiNode, PsiMiNode>();

		public PsiMiHandler(boolean pSpokeModel)
		{
			super();
			mGraph = new PsiMiGraph();
			mSpokeModel = pSpokeModel;
		}

		public void startDocument() throws SAXException
		{
			mIsInteractorDefinition = true;
		}

		public void endDocument() throws SAXException
		{
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@SuppressWarnings("unused")
		public void startElement(String namespaceURI, String lName, // local name
															String qName, // qualified name
															Attributes attrs) throws SAXException
		{
			if (qName.equals("proteinInteractor"))
			{
				mInteractorId = attrs.getValue("id");
				if (mInteractorId != null)
				{
					mInteractorNode = new PsiMiNode(mInteractorId);
					lPsiMiNodeMap.put(mInteractorNode, mInteractorNode);
				}
			}
			else if (qName.equals("proteinInteractorRef"))
			{
				mInteractorId = attrs.getValue("ref");
				if (mInteractorId != null)
				{
					mInteractorNode = new PsiMiNode(mInteractorId);
					mInteractorNode = lPsiMiNodeMap.get(mInteractorNode);
				}
			}
			else if (mIsInteractorDefinition && qName.equals("secondaryRef"))
			{
				String db = attrs.getValue("db");
				String id = attrs.getValue("id");
				if (mInteractorNode != null)
				{
					if (db.equalsIgnoreCase("go"))
						mInteractorNode.addGoId(GoIdConversion.getIdFromString(id));
					if (db.equalsIgnoreCase("interpro"))
						mInteractorNode.addInterproId(InterproIdConversion.getIdFromString(id));
				}
			}
			else if (qName.equals("interaction"))
			{
				startInteraction();
			}
		}

		public void characters(char buf[], int offset, int len) throws SAXException
		{
			mText = new String(buf, offset, len);
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
		 *      java.lang.String, java.lang.String)
		 */
		@SuppressWarnings("unused")
		public void endElement(String namespaceURI, String sName, // simple name
														String qName // qualified name
		) throws SAXException
		{

			if (qName.equals("proteinInteractor") || qName.equals("proteinInteractorRef"))
			{
				mGraph.addNode(mInteractorNode);
			}
			else if (qName.equals("interaction"))
			{
				endInteraction();
			}
			else if (qName.equals("role"))
			{
				mRole = mText;
				addInteractor(mInteractorNode, mRole);
			}
			else if (qName.equals("interactorList"))
			{
				mIsInteractorDefinition = false;
			}

		}

		private void startInteraction()
		{
			lInteractorsToRoleMap.clear();
		}

		private void addInteractor(PsiMiNode pInteractorNode, String pRole)
		{
			lInteractorsToRoleMap.put(pInteractorNode, pRole);
		}

		private void endInteraction()
		{
			if (lInteractorsToRoleMap.size() == 1)
			{
				PsiMiNode lNode = lInteractorsToRoleMap.keySet().iterator().next();
				mGraph.addEdge(new UndirectedEdge<PsiMiNode>(lNode, lNode));
			}
			else if (!mSpokeModel)
			{
				for (PsiMiNode lNode1 : lInteractorsToRoleMap.keySet())
					for (PsiMiNode lNode2 : lInteractorsToRoleMap.keySet())
						if (!lNode1.equals(lNode2))
						{
							mGraph.addEdge(new UndirectedEdge<PsiMiNode>(lNode1, lNode2));
						}
			}
			else if (mSpokeModel)
				for (PsiMiNode lNode1 : lInteractorsToRoleMap.keySet())
					for (PsiMiNode lNode2 : lInteractorsToRoleMap.keySet())
						if (!lNode1.equals(lNode2))
						{
							String lRole1 = lInteractorsToRoleMap.get(lNode1);
							String lRole2 = lInteractorsToRoleMap.get(lNode2);
							boolean lIsInteraction = (lRole1.equals("neutral") && lRole2.equals("neutral"))
									|| (lRole1.equals("bait") && lRole2.equals("prey")) || lRole1.equals("unspecified")
									|| lRole2.equals("unspecified");

							if (!(lRole1.equals("bait") || lRole1.equals("prey") || lRole1.equals("neutral") || lRole1
									.equals("unspecified")))
								System.out.println("Something strange here:" + lRole1);

							if (lIsInteraction)
								mGraph.addEdge(new UndirectedEdge<PsiMiNode>(lNode1, lNode2));

						}

		}
	}

	public static final PsiMiGraph load(File pFile, boolean pSpokeModel)
	{
		FileInputStream lFileInputStream;
		try
		{
			lFileInputStream = new FileInputStream(pFile);
			PsiMiGraph lGraph = load(lFileInputStream, pSpokeModel);

			try
			{
				EdgIO.save(lGraph, new File("dump.psimi.edg"));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return lGraph;
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static final PsiMiGraph load(InputStream pInputStream, boolean pSpokeModel)
	{
		PsiMiHandler lPsiMiHandler = new PsiMiHandler(pSpokeModel);
		DefaultHandler handler = lPsiMiHandler;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try
		{
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(pInputStream, handler);

		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}

		return lPsiMiHandler.mGraph;
	}

}
