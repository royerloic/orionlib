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

import utils.math.statistics.HyperGeometricEnrichement;
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

	public Element addGene(	final Integer pGeneId,
													final String pGeneName,
													final String pGeneDescription)
	{
		final Element lElement = new Element(pGeneId, pGeneName, pGeneDescription);
		mGeneMap.put(pGeneId, lElement);
		return lElement;
	}

	public Element addAttribute(final int pAttributeId,
															final String pAttributeName,
															final String pAttributeDescription)
	{
		final Element lElement = new Element(	pAttributeId,
																					pAttributeName,
																					pAttributeDescription);
		mAttributeMap.put(pAttributeId, lElement);
		return lElement;
	}

	public void addAnnotation(final Integer pGeneId, final Integer pAttributeId)
	{
		addAnnotation(pGeneId, "", pAttributeId, "");
	}

	public void addAnnotation(final Integer pGeneId,
														final String pGeneName,
														final Integer pAttributeId,
														final String pAttributeName)
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

	public HashMap<Element, Enrichment> computeEnrichements(final int... pIntegers)

	{
		final HashSet<Integer> lSet = new HashSet<Integer>();
		for (final int lInt : pIntegers)
		{
			lSet.add(lInt);
		}
		return computeEnrichements(lSet);
	}

	public HashMap<Element, Enrichment> computeEnrichements(final Collection<Integer> pGeneSet)
	{
		return computeEnrichements(pGeneSet, 0);
	}

	public HashMap<Element, Enrichment> computeEnrichements(final Collection<Integer> pGeneSet,
																													final double pMinimalCoverage)
	{
		final HashMap<Element, Enrichment> lEnrichementMap = new HashMap<Element, Enrichment>();

		final HashSet<Element> lSet1 = getElementSet(mGeneMap, pGeneSet);
		final HashSet<Element> lAttributeSetForSet1 = getAttributesForGeneSet(lSet1);
		for (final Element lAttribute : lAttributeSetForSet1)
		{
			final Enrichment lEnrichment = new Enrichment();
			lEnrichementMap.put(lAttribute, lEnrichment);
		}

		final double lCorrection = Math.max(lAttributeSetForSet1.size(), 1);
		final double universe = mGeneMap.size();
		final double set1 = pGeneSet.size();
		final HashSet<Element> lIntersection = new HashSet<Element>((int) universe);
		for (final Element lAttribute : lAttributeSetForSet1)
		{
			final Set<Element> lSet2 = mAttribute2GeneMap.get(lAttribute);
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
				final Enrichment lEnrichment = lEnrichementMap.get(lAttribute);

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

	private HashSet<Element> getAttributesForGeneSet(final HashSet<Element> pSet1)
	{
		final HashSet<Element> lAttributeSet = new HashSet<Element>();
		for (final Element lElement : pSet1)
		{
			final Set<Element> lAttributeSetForGene = mGene2AttributeMap.get(lElement);
			if (lAttributeSetForGene != null)
			{
				lAttributeSet.addAll(lAttributeSetForGene);
			}
		}
		return lAttributeSet;
	}

	private HashSet<Element> getElementSet(	final HashMap<Integer, Element> pElementMap,
																					final Collection<Integer> pElementSet)
	{
		final HashSet<Element> lSet = new HashSet<Element>();
		for (final Integer lElementId : pElementSet)
		{
			lSet.add(pElementMap.get(lElementId));
		}
		return lSet;
	}

	public Collection<GeneSet> getGeneSubSets(final double pPValueThreshold,
																						final int... pIntegers)

	{
		final HashSet<Integer> lSet = new HashSet<Integer>();
		for (final int lInt : pIntegers)
		{
			lSet.add(lInt);
		}
		return getGeneSubSets(lSet, pPValueThreshold);
	}/**/

	public Collection<GeneSet> getGeneSubSets(final Collection<Integer> pGeneSet,
																						final double pPValueThreshold)
	{
		final HashMap<HashSet<Element>, GeneSet> lGeneSetMap = new HashMap<HashSet<Element>, GeneSet>();

		final HashSet<Element> lGeneElementSet = getElementSet(mGeneMap, pGeneSet);
		final HashMap<Element, Enrichment> lEnrichementMap = computeEnrichements(pGeneSet);

		for (final Entry<Element, Enrichment> lEntry : lEnrichementMap.entrySet())
		{
			final Element lAttribute = lEntry.getKey();
			final double lPValue = lEntry.getValue().mCorrectedPValue;
			if (lPValue < pPValueThreshold)
			{

				final HashSet<Element> lSet = new HashSet<Element>();
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

	public HashSet<GeneSet> getGeneSubSetsAndDecompose(	final double pPValueThreshold,
																											final int... pIntegers)

	{
		final HashSet<Integer> lSet = new HashSet<Integer>();
		for (final int lInt : pIntegers)
		{
			lSet.add(lInt);
		}
		return getGeneSubSetsAndDecompose(lSet, pPValueThreshold);
	}/**/

	public HashSet<GeneSet> getGeneSubSetsAndDecompose(	final Collection<Integer> pGeneSet,
																											final double pPValueThreshold)
	{
		final Collection<GeneSet> lGeneSets = getGeneSubSets(	pGeneSet,
																													pPValueThreshold);

		final ArrayList<GeneSet> lGeneSetList = new ArrayList<GeneSet>(lGeneSets);

		Collections.sort(lGeneSetList);

		final FirstInPrioritizedSetDecomposer<Element, Couple<Element, Double>> lDecomposer = new FirstInPrioritizedSetDecomposer<Element, Couple<Element, Double>>();

		for (final GeneSet lGeneSet : lGeneSetList)
		{
			for (final Couple<Element, Double> lCouple : lGeneSet.getAttributesPValuesCouples())
			{
				lDecomposer.addSet(lGeneSet.getGenes(), lCouple);
			}
		}

		final HashSet<GeneSet> lDecomposedGeneSets = new HashSet<GeneSet>();

		for (final Entry<Set<Element>, Set<Couple<Element, Double>>> lEntry : lDecomposer	.getSetsAndAttributes()
																																											.entrySet())
		{
			final GeneSet lGeneSet = new GeneSet(lEntry.getKey());
			for (final Couple<Element, Double> lCouple : lEntry.getValue())
			{
				lGeneSet.addAttributeAndPValue(lCouple.mA, lCouple.mB);
			}
			lDecomposedGeneSets.add(lGeneSet);
		}

		return lDecomposedGeneSets;

	}

	public static final String geneSubSetsToEdgeString(final HashSet<GeneSet> pGeneSets)
	{
		final StringBuilder lStringBuilder = new StringBuilder();
		lStringBuilder.append("//");
		lStringBuilder.append("EDGEFORMAT\t1\t2\n");
		final ArrayList<String> lSetAttributesList = new ArrayList<String>();
		for (final GeneSet lGeneSet : pGeneSets)
		{
			for (final Element lGene : lGeneSet.getGenes())
			{
				final String lSetAttributes = lGeneSet.getAttributesPValuesCouples()
																							.toString();
				lStringBuilder.append("EDGE\t" + lGene.mId
															+ "\t"
															+ lSetAttributes
															+ "\n");
				lSetAttributesList.add(lSetAttributes);
			}
		}

		for (final String lString : lSetAttributesList)
		{
			lStringBuilder.append("EDGE\t" + lString + "\tALL_ATTRIBUTES\n");
		}

		return lStringBuilder.toString();
	}

	public static GeneMapBuilder load(final File pCache) throws IOException
	{
		try
		{
			final FileInputStream lFileInputStream = new FileInputStream(pCache);
			final GZIPInputStream lGZIPInputStream = new GZIPInputStream(lFileInputStream);
			final ObjectInputStream lObjectInputStream = new ObjectInputStream(lGZIPInputStream);
			final GeneMapBuilder obj = (GeneMapBuilder) lObjectInputStream.readObject();
			return obj;
		}
		catch (final ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void save(final File pCache) throws IOException
	{
		final FileOutputStream lFileOutputStream = new FileOutputStream(pCache);
		final GZIPOutputStream lGZIPOutputStream = new GZIPOutputStream(lFileOutputStream);
		final ObjectOutputStream lObjectOutputStream = new ObjectOutputStream(lGZIPOutputStream);
		lObjectOutputStream.writeObject(this);
		lObjectOutputStream.flush();
		lObjectOutputStream.close();
	}

}
