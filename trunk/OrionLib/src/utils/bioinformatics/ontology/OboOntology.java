package utils.bioinformatics.ontology;

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

import utils.io.LineReader;
import utils.string.StringUtils;
import utils.structures.graph.DirectedEdge;
import utils.structures.lattice.HashLattice;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class OboOntology extends HashLattice<OboTerm>
{

	private final Map<Integer, OboTerm> mIdToOboTermMap = new HashMap<Integer, OboTerm>();

	@Override
	public void addNode(final OboTerm pNode)
	{
		super.addNode(pNode);
		mIdToOboTermMap.put(pNode.getId(), pNode);
	}

	public OboTerm getOboTermFromId(final Integer pId)
	{
		return mIdToOboTermMap.get(pId);
	}

	public List<OboTerm> getOboTermFromId(final Collection<Integer> pIdCollection)
	{
		final List<OboTerm> lOboTermList = new ArrayList<OboTerm>();
		for (final Integer lId : pIdCollection)
		{
			final OboTerm lOboTerm = getOboTermFromId(lId);
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
	public OboOntology(final InputStream pInputStream) throws FileNotFoundException,
																										IOException
	{
		super();
		{
			final List<List<String>> lOboMatrix = LineReader.readMatrixFromStream(pInputStream,
																																						"(\\: )|( \\! )");

			final Map<OboTerm, OboTerm> lOboTermMap = new HashMap<OboTerm, OboTerm>();
			final Set<DirectedEdge<OboTerm>> lEdgeSet = new HashSet<DirectedEdge<OboTerm>>();
			OboTerm lCurrentOboTerm = null;
			String lIdString;
			Integer lId = null;
			boolean isTerm = false;

			for (final List<String> lList : lOboMatrix)
				if (lList.get(0).equals("[Term]"))
					isTerm = true;
				else if (lList.get(0).equals("[Typedef]"))
					isTerm = false;
				else if (isTerm && lList.get(0).equals("id"))
				{
					lIdString = lList.get(1);
					// System.out.println(lIdString);
					final String[] lGroupArray = StringUtils.captures(lIdString,
																														".*?([0-9]+)");
					final String lIntegerString = lGroupArray[0];
					lId = Integer.parseInt(lIntegerString);
					lCurrentOboTerm = new OboTerm("", lId);
					lOboTermMap.put(lCurrentOboTerm, lCurrentOboTerm);
					addNode(lCurrentOboTerm);
				}
				else if (lId != null)
					if (lList.get(0).equals("name"))
					{
						final String lName = lList.get(1);
						lCurrentOboTerm.setName(lName);
					}
					else if (lList.get(0).equals("def"))
						lCurrentOboTerm.setDefinition(lList.get(1));
					else if (lList.get(0).equals("namespace"))
						lCurrentOboTerm.setNameSpace(lList.get(1));
					else if (lList.get(0).equals("is_a") || lList	.get(0)
																												.equals("relationship"))
					{
						final String lParentIdString = lList.get(1);
						final String[] lGroupArray = StringUtils.captures(lParentIdString,
																															".*?([0-9]+)");
						final String lParentIntegerString = lGroupArray[0];
						final Integer lParentId = Integer.parseInt(lParentIntegerString);
						OboTerm lParentOboTerm = new OboTerm("", lParentId);
						final OboTerm lParentOboTermTemp = lOboTermMap.get(lParentOboTerm);
						if (lParentOboTermTemp == null)
							lOboTermMap.put(lParentOboTerm, lParentOboTerm);
						else
							lParentOboTerm = lParentOboTermTemp;

						final DirectedEdge<OboTerm> lEdge = new DirectedEdge(	lParentOboTerm,
																																	lCurrentOboTerm);
						lEdgeSet.add(lEdge);
					}

			for (final DirectedEdge<OboTerm> lEdge : lEdgeSet)
			{
				lEdge.setFirstNode(lOboTermMap.get(lEdge.getFirstNode()));
				lEdge.setSecondNode(lOboTermMap.get(lEdge.getSecondNode()));
				addEdge(lEdge);
			}

		}

	}

}
