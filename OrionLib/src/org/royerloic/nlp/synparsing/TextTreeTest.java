package org.royerloic.nlp.synparsing;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.royerloic.nlp.synparsing.TextTree.AnnotationResult;

public class TextTreeTest extends TestCase
{
	public void testCreation()
	{
		String lString = "This is a test.";
		TextTree<String> lTextTree = new TextTree<String>(lString);

		assertTrue(lTextTree.getString().equals(lString));

		String lString1 = lTextTree.subSequence(5, 5 + 4).toString();
		String lString2 = lString.substring(5, 5 + 4);
		assertTrue(lString1.equals(lString2));

		assertTrue(lTextTree.length() == lString.length());

		assertTrue(lTextTree.isVoid());

		lTextTree.setAnnotation("sentence");

		assertTrue(lTextTree.getAnnotation() == "sentence");

		assertTrue(lTextTree.charAt(8) == 'a');

		assertTrue(lTextTree.annotate(0, 4, "this").isAnnotatable());

		assertTrue(lTextTree.annotate(5, 2, "verb").isAnnotatable());

		assertTrue(lTextTree.annotate(10, 4, "noun").isAnnotatable());

		assertTrue(lTextTree.annotate(5, 9, "is a test").isAnnotatable());

		assertTrue(lTextTree.annotate(8, 6, "a test").isAnnotatable());

		{
			String lStringBefore = lTextTree.getAllDescendents().toString();
			assertTrue(lTextTree.annotate(8, 6, "a test").isAnnotatable());
			String lStringAfter = lTextTree.getAllDescendents().toString();
			assertTrue(lStringBefore.equals(lStringAfter));
		}

		AnnotationResult lAnnotationResult = lTextTree.annotate(5, 4, "is a");

		assertFalse(lAnnotationResult.isAnnotatable());
		assertTrue(lAnnotationResult.getCollidingTextTree().getString().equals("a test"));

		for (TextTree<String> lTree : lTextTree)
		{
			System.out.println(lTree);
		}

		String lAbstract = "Protein glycosylation is a common and important process that can alter the stability, half-life, biological activity and receptor recognition of target molecules. We have identified a new putative mouse UDP-GalNAc:polypeptide N-acetylgalactosaminyltransferase family member, termed GalNAc-T10/ppGaNTase-T10 (gene symbol Galnt10), and determined its expression pattern in mouse CNS using in situ hybridization analysis. Results demonstrated predominant expression of Galnt10 in several distinct hypothalamic, thalamic and amygdaloid nuclei. The most abundant hybridization levels were observed in the paraventricular, ventromedial and arcuate nuclei of the hypothalamus, the anterodorsal and parafascicular nuclei of the thalamus and the central, basomedial and medial nuclei of the amygdala. Expression of Galnt10 was also detected in cerebral cortex, lateral septum, habenula and hippocampus. The localization of this putative glycosyltransferase in distinct regions within the CNS indicates the specificity for complex protein modifications and suggests that region-specific glycosylation represents an essential process in basic biological functions.";

		TextTree<String> lAbstractTextTree = new TextTree<String>(lAbstract);

		List<Integer> lIntegerList = new ArrayList<Integer>();
		int lIndex = -1;
		while ((lIndex = lAbstract.indexOf(". ", lIndex + 1)) >= 0)
		{
			lIntegerList.add(Math.min(lIndex + 2, lAbstract.length() - 1));
		}

		lAbstractTextTree.addChildrenAccordingToCutPoints(lIntegerList, "Sentence");

		for (TextTree<String> lTree : lAbstractTextTree)
		{
			System.out.println(lTree);
		}

	}
}
