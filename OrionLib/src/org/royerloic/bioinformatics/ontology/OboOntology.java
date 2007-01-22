package org.royerloic.bioinformatics.ontology;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.royerloic.io.MatrixFile;
import org.royerloic.string.StringUtils;
import org.royerloic.structures.graph.DirectedEdge;
import org.royerloic.structures.lattice.HashLattice;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class OboOntology extends HashLattice<OboTerm>
{

	private Map<Integer, OboTerm>	mIdToOboTermMap	= new HashMap<Integer, OboTerm>();

	@Override
	public void addNode(OboTerm pNode)
	{
		super.addNode(pNode);
		mIdToOboTermMap.put(pNode.getId(), pNode);
	}

	public OboTerm getOboTermFromId(Integer pId)
	{
		return mIdToOboTermMap.get(pId);
	}

	public List<OboTerm> getOboTermFromId(Collection<Integer> pIdCollection)
	{
		List<OboTerm> lOboTermList = new ArrayList<OboTerm>();
		for (Integer lId : pIdCollection)
		{
			OboTerm lOboTerm = getOboTermFromId(lId);
			if (lOboTerm != null)
				lOboTermList.add(lOboTerm);

		}
		return lOboTermList;
	}

	/**
	 * @param pOboFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public OboOntology(InputStream pInputStream) throws FileNotFoundException, IOException
	{
		super();
		{
			List<List<String>> lOboMatrix = MatrixFile.readMatrixFromStream(pInputStream, "(\\: )|( \\! )");

			Map<OboTerm, OboTerm> lOboTermMap = new HashMap<OboTerm, OboTerm>();
			Set<DirectedEdge<OboTerm>> lEdgeSet = new HashSet<DirectedEdge<OboTerm>>();
			OboTerm lCurrentOboTerm = null;
			String lIdString;
			Integer lId = null;
			boolean isTerm = false;

			for (List<String> lList : lOboMatrix)
			{
				if (lList.get(0).equals("[Term]"))
				{
					isTerm = true;
				}
				else if (lList.get(0).equals("[Typedef]"))
				{
					isTerm = false;
				}
				else if (isTerm && lList.get(0).equals("id"))
				{
					lIdString = lList.get(1);
					// System.out.println(lIdString);
					String[] lGroupArray = StringUtils.captures(lIdString, ".*?([0-9]+)");
					String lIntegerString = lGroupArray[0];
					lId = Integer.parseInt(lIntegerString);
					lCurrentOboTerm = new OboTerm(lId);
					lOboTermMap.put(lCurrentOboTerm, lCurrentOboTerm);
					addNode(lCurrentOboTerm);
				}
				else if (lId != null)
				{
					if (lList.get(0).equals("name"))
					{
						String lName = lList.get(1);
						lCurrentOboTerm.setName(lName);
					}
					else if (lList.get(0).equals("def"))
					{
						lCurrentOboTerm.setDefinition(lList.get(1));
					}
					else if (lList.get(0).equals("namespace"))
					{
						lCurrentOboTerm.setNameSpace(lList.get(1));
					}
					else if (lList.get(0).equals("is_a") || lList.get(0).equals("relationship"))
					{
						String lParentIdString = lList.get(1);
						String[] lGroupArray = StringUtils.captures(lParentIdString, ".*?([0-9]+)");
						String lParentIntegerString = lGroupArray[0];
						Integer lParentId = Integer.parseInt(lParentIntegerString);
						OboTerm lParentOboTerm = new OboTerm(lParentId);
						OboTerm lParentOboTermTemp = lOboTermMap.get(lParentOboTerm);
						if (lParentOboTermTemp == null)
						{
							lOboTermMap.put(lParentOboTerm, lParentOboTerm);
						}
						else
						{
							lParentOboTerm = lParentOboTermTemp;
						}

						DirectedEdge<OboTerm> lEdge = new DirectedEdge(lParentOboTerm, lCurrentOboTerm);
						lEdgeSet.add(lEdge);
					}
				}
			}

			for (DirectedEdge<OboTerm> lEdge : lEdgeSet)
			{
				lEdge.setFirstNode(lOboTermMap.get(lEdge.getFirstNode()));
				lEdge.setSecondNode(lOboTermMap.get(lEdge.getSecondNode()));
				addEdge(lEdge);
			}

		}

	}

}
