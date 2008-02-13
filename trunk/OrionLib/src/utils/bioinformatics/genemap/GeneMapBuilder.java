package utils.bioinformatics.genemap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import utils.math.stats.HyperGeometricEnrichement;
import utils.structures.Couple;
import utils.structures.map.HashSetMap;
import utils.structures.set.FirstInPrioritizedSetDecomposer;

public class GeneMapBuilder
{

	HashMap<Integer, Element> mGeneMap = new HashMap<Integer, Element>();
	HashMap<Integer, Element> mAttributeMap = new HashMap<Integer, Element>();

	HashSetMap<Element, Element> mGene2AttributeMap = new HashSetMap<Element, Element>();
	HashSetMap<Element, Element> mAttribute2GeneMap = new HashSetMap<Element, Element>();

	public void addGene(int pGeneId, String pGeneName, String pGeneDescription)
	{
		mGeneMap.put(pGeneId, new Element(pGeneId, pGeneName, pGeneDescription));
	}

	public void addAttribute(	int pAttributeId,
														String pAttributeName,
														String pAttributeDescription)
	{
		mAttributeMap.put(pAttributeId, new Element(pAttributeId,
																								pAttributeName,
																								pAttributeDescription));
	}

	public void addAnnotation(int pGeneId, int pAttributeId)
	{
		final Element lGene = mGeneMap.get(pGeneId);
		final Element lAttribute = mAttributeMap.get(pAttributeId);
		mGene2AttributeMap.put(lGene, lAttribute);
		mAttribute2GeneMap.put(lAttribute, lGene);
	}

	public HashMap<Element, Double> computeEnrichements(int... pIntegers)

	{
		HashSet<Integer> lSet = new HashSet<Integer>();
		for (int lInt : pIntegers)
		{
			lSet.add(lInt);
		}
		return computeEnrichements(lSet);
	}

	public HashMap<Element, Double> computeEnrichements(Collection<Integer> pGeneSet)
	{
		HashMap<Element, Double> lEnrichementMap = new HashMap<Element, Double>();

		HashSet<Element> lSet1 = getElementSet(mGeneMap, pGeneSet);
		HashSet<Element> lAttributeSetForSet1 = getAttributesForGeneSet(lSet1);
		for (Element lAttribute : lAttributeSetForSet1)
		{
			lEnrichementMap.put(lAttribute, 1.0);
		}

		double lCorrection = Math.max(lAttributeSetForSet1.size(), 1);
		final double universe = mGeneMap.size();
		final double set1 = pGeneSet.size();
		HashSet<Element> lIntersection = new HashSet<Element>();
		for (Element lAttribute : lAttributeSetForSet1)
		{
			Set<Element> lSet2 = mAttribute2GeneMap.get(lAttribute);
			final double set2 = lSet2.size();

			lIntersection.clear();
			lIntersection.addAll(lSet1);
			lIntersection.retainAll(lSet2);
			final double inter = lIntersection.size();

			final double pvalue = HyperGeometricEnrichement.hyperg(	universe,
																															set1,
																															set2,
																															inter,
																															1);
			final double lCorrectedpValue = pvalue * lCorrection;
			lEnrichementMap.put(lAttribute, lCorrectedpValue);
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
		HashMap<Element, Double> lEnrichementMap = computeEnrichements(pGeneSet);

		for (Entry<Element, Double> lEntry : lEnrichementMap.entrySet())
		{
			final Element lAttribute = lEntry.getKey();
			final double lPValue = lEntry.getValue();
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
}
