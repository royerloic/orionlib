package utils.bioinformatics.genemap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import utils.math.stats.HyperGeometricEnrichement;
import utils.random.sequence.ContextPreservingSequenceRandomizer;
import utils.structures.Couple;
import utils.structures.map.HashSetMap;
import utils.structures.set.FirstInPrioritizedSetDecomposer;

public class GeneMapBuilder implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	HashMap<Integer, Element> mGeneMap = new HashMap<Integer, Element>();
	HashMap<Integer, Element> mAttributeMap = new HashMap<Integer, Element>();

	HashSetMap<Element, Element> mGene2AttributeMap = new HashSetMap<Element, Element>();
	HashSetMap<Element, Element> mAttribute2GeneMap = new HashSetMap<Element, Element>();

	public Element addGene(	Integer pGeneId,
													String pGeneName,
													String pGeneDescription)
	{
		Element lElement = new Element(pGeneId, pGeneName, pGeneDescription);
		mGeneMap.put(pGeneId, lElement);
		return lElement;
	}

	public Element addAttribute(int pAttributeId,
															String pAttributeName,
															String pAttributeDescription)
	{
		Element lElement = new Element(	pAttributeId,
																		pAttributeName,
																		pAttributeDescription);
		mAttributeMap.put(pAttributeId, lElement);
		return lElement;
	}

	public void addAnnotation(Integer pGeneId, Integer pAttributeId)
	{
		addAnnotation(pGeneId, "", pAttributeId, "");
	}

	public void addAnnotation(Integer pGeneId,
														String pGeneName,
														Integer pAttributeId,
														String pAttributeName)
	{
		Element lGene = mGeneMap.get(pGeneId);
		if (lGene == null)
		{
			lGene = addGene(pGeneId, pGeneName, "");
		}
		/***************************************************************************
		 * else { lGene.mDescription+= "|"+pGeneName; }/
		 **************************************************************************/
		Element lAttribute = mAttributeMap.get(pAttributeId);
		if (lAttribute == null)
		{
			lAttribute = addAttribute(pAttributeId, pAttributeName, "");
		}
		/***************************************************************************
		 * else { lAttribute.mDescription+= "|"+pAttributeName; }/
		 **************************************************************************/
		mGene2AttributeMap.put(lGene, lAttribute);
		mAttribute2GeneMap.put(lAttribute, lGene);
	}

	public HashMap<Element, Enrichment> computeEnrichements(int... pIntegers)

	{
		HashSet<Integer> lSet = new HashSet<Integer>();
		for (int lInt : pIntegers)
		{
			lSet.add(lInt);
		}
		return computeEnrichements(lSet);
	}

	public HashMap<Element, Enrichment> computeEnrichements(Collection<Integer> pGeneSet)
	{
		return computeEnrichements(pGeneSet, 0);
	}

	public HashMap<Element, Enrichment> computeEnrichements(Collection<Integer> pGeneSet,
																													double pMinimalCoverage)
	{
		HashMap<Element, Enrichment> lEnrichementMap = new HashMap<Element, Enrichment>();

		HashSet<Element> lSet1 = getElementSet(mGeneMap, pGeneSet);
		HashSet<Element> lAttributeSetForSet1 = getAttributesForGeneSet(lSet1);
		for (Element lAttribute : lAttributeSetForSet1)
		{
			Enrichment lEnrichment = new Enrichment();
			lEnrichementMap.put(lAttribute, lEnrichment);
		}

		double lCorrection = Math.max(lAttributeSetForSet1.size(), 1);
		final double universe = mGeneMap.size();
		final double set1 = pGeneSet.size();
		HashSet<Element> lIntersection = new HashSet<Element>((int) universe);
		for (Element lAttribute : lAttributeSetForSet1)
		{
			Set<Element> lSet2 = mAttribute2GeneMap.get(lAttribute);
			final double set2 = lSet2.size();

			lIntersection.clear();
			lIntersection.addAll(lSet1);
			lIntersection.retainAll(lSet2);
			final double inter = lIntersection.size();

			final double lCoverage = inter / set1;

			if (lCoverage > pMinimalCoverage)
			{
				final double pvalue = HyperGeometricEnrichement.hyperg(	universe,
																																set1,
																																set2,
																																inter,
																																1);
				final double lCorrectedpValue = pvalue * lCorrection;
				Enrichment lEnrichment = lEnrichementMap.get(lAttribute);

				lEnrichment.mSet1 = new ArrayList<Element>(lSet1);
				lEnrichment.mSet2 = new ArrayList<Element>(lSet2);
				lEnrichment.mIntersectionSet = new ArrayList<Element>(lIntersection);

				lEnrichment.mUniverseSize = universe;
				lEnrichment.mSet1Size = set1;
				lEnrichment.mSet2Size = set2;
				lEnrichment.mIntersectionSize = inter;

				lEnrichment.mPValue = pvalue;
				lEnrichment.mCorrectedPValue = lCorrectedpValue;
				lEnrichment.mCoverage = lCoverage;

			}
		}

		return lEnrichementMap;
	}

	private HashSet<Element> getAttributesForGeneSet(HashSet<Element> pSet1)
	{
		HashSet<Element> lAttributeSet = new HashSet<Element>();
		for (Element lElement : pSet1)
		{
			Set<Element> lAttributeSetForGene = mGene2AttributeMap.get(lElement);
			if (lAttributeSetForGene != null)
				lAttributeSet.addAll(lAttributeSetForGene);
		}
		return lAttributeSet;
	}

	private HashSet<Element> getElementSet(	HashMap<Integer, Element> pElementMap,
																					Collection<Integer> pElementSet)
	{
		HashSet<Element> lSet = new HashSet<Element>();
		for (Integer lElementId : pElementSet)
		{
			lSet.add(pElementMap.get(lElementId));
		}
		return lSet;
	}

	public Collection<GeneSet> getGeneSubSets(double pPValueThreshold,
																						int... pIntegers)

	{
		HashSet<Integer> lSet = new HashSet<Integer>();
		for (int lInt : pIntegers)
		{
			lSet.add(lInt);
		}
		return getGeneSubSets(lSet, pPValueThreshold);
	}/**/

	public Collection<GeneSet> getGeneSubSets(Collection<Integer> pGeneSet,
																						double pPValueThreshold)
	{
		HashMap<HashSet<Element>, GeneSet> lGeneSetMap = new HashMap<HashSet<Element>, GeneSet>();

		HashSet<Element> lGeneElementSet = getElementSet(mGeneMap, pGeneSet);
		HashMap<Element, Enrichment> lEnrichementMap = computeEnrichements(pGeneSet);

		for (Entry<Element, Enrichment> lEntry : lEnrichementMap.entrySet())
		{
			final Element lAttribute = lEntry.getKey();
			final double lPValue = lEntry.getValue().mCorrectedPValue;
			if (lPValue < pPValueThreshold)
			{

				HashSet<Element> lSet = new HashSet<Element>();
				lSet.addAll(mAttribute2GeneMap.get(lAttribute));
				lSet.retainAll(lGeneElementSet);

				GeneSet lGeneSet = lGeneSetMap.get(lSet);
				if (lGeneSet == null)
				{
					lGeneSet = new GeneSet(lSet);
					lGeneSetMap.put(lSet, lGeneSet);
				}
				lGeneSet.addAttributeAndPValue(lAttribute, lPValue);
			}
		}

		return lGeneSetMap.values();
	}

	public HashSet<GeneSet> getGeneSubSetsAndDecompose(	double pPValueThreshold,
																											int... pIntegers)

	{
		HashSet<Integer> lSet = new HashSet<Integer>();
		for (int lInt : pIntegers)
		{
			lSet.add(lInt);
		}
		return getGeneSubSetsAndDecompose(lSet, pPValueThreshold);
	}/**/

	public HashSet<GeneSet> getGeneSubSetsAndDecompose(	Collection<Integer> pGeneSet,
																											double pPValueThreshold)
	{
		Collection<GeneSet> lGeneSets = getGeneSubSets(pGeneSet, pPValueThreshold);

		ArrayList<GeneSet> lGeneSetList = new ArrayList<GeneSet>(lGeneSets);

		Collections.sort(lGeneSetList);

		FirstInPrioritizedSetDecomposer<Element, Couple<Element, Double>> lDecomposer = new FirstInPrioritizedSetDecomposer<Element, Couple<Element, Double>>();

		for (GeneSet lGeneSet : lGeneSetList)
			for (Couple<Element, Double> lCouple : lGeneSet.getAttributesPValuesCouples())
			{
				lDecomposer.addSet(lGeneSet.getGenes(), lCouple);
			}

		HashSet<GeneSet> lDecomposedGeneSets = new HashSet<GeneSet>();

		for (Entry<Set<Element>, Set<Couple<Element, Double>>> lEntry : lDecomposer	.getSetsAndAttributes()
																																								.entrySet())
		{
			GeneSet lGeneSet = new GeneSet(lEntry.getKey());
			for (Couple<Element, Double> lCouple : lEntry.getValue())
			{
				lGeneSet.addAttributeAndPValue(lCouple.mA, lCouple.mB);
			}
			lDecomposedGeneSets.add(lGeneSet);
		}

		return lDecomposedGeneSets;

	}

	public static final String geneSubSetsToEdgeString(HashSet<GeneSet> pGeneSets)
	{
		StringBuilder lStringBuilder = new StringBuilder();
		lStringBuilder.append("//");
		lStringBuilder.append("EDGEFORMAT\t1\t2\n");
		ArrayList<String> lSetAttributesList = new ArrayList<String>();
		for (GeneSet lGeneSet : pGeneSets)
		{
			for (Element lGene : lGeneSet.getGenes())
			{
				String lSetAttributes = lGeneSet.getAttributesPValuesCouples()
																				.toString();
				lStringBuilder.append("EDGE\t" + lGene.mId
															+ "\t"
															+ lSetAttributes
															+ "\n");
				lSetAttributesList.add(lSetAttributes);
			}
		}

		for (String lString : lSetAttributesList)
		{
			lStringBuilder.append("EDGE\t" + lString + "\tALL_ATTRIBUTES\n");
		}

		return lStringBuilder.toString();
	}

	public static GeneMapBuilder load(File pCache) throws IOException
	{
		try
		{
			FileInputStream lFileInputStream = new FileInputStream(pCache);
			GZIPInputStream lGZIPInputStream = new GZIPInputStream(lFileInputStream);
			ObjectInputStream lObjectInputStream = new ObjectInputStream(lGZIPInputStream);
			GeneMapBuilder obj = (GeneMapBuilder) lObjectInputStream.readObject();
			return obj;
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(File pCache) throws IOException
	{
		FileOutputStream lFileOutputStream = new FileOutputStream(pCache);
		GZIPOutputStream lGZIPOutputStream = new GZIPOutputStream(lFileOutputStream);
		ObjectOutputStream lObjectOutputStream = new ObjectOutputStream(lGZIPOutputStream);
		lObjectOutputStream.writeObject(this);
		lObjectOutputStream.flush();
		lObjectOutputStream.close();
	}
	
}
