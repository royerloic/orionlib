package org.royerloic.nlp.synparsing;

import junit.framework.TestCase;

public class SentenceSplitterTest extends TestCase
{
	public void test1()
	{

		{
			final String lAbstract = "Protein glycosylation is a common and important process that can alter the stability, half-life, biological activity and receptor recognition of target molecules. We have identified a new putative mouse UDP-GalNAc:polypeptide N-acetylgalactosaminyltransferase family member, termed GalNAc-T10/ppGaNTase-T10 (gene symbol Galnt10), and determined its expression pattern in mouse CNS using in situ hybridization analysis. Results demonstrated predominant expression of Galnt10 in several distinct hypothalamic, thalamic and amygdaloid nuclei. The most abundant hybridization levels were observed in the paraventricular, ventromedial and arcuate nuclei of the hypothalamus, the anterodorsal and parafascicular nuclei of the thalamus and the central, basomedial and medial nuclei of the amygdala. Expression of Galnt10 was also detected in cerebral cortex, lateral septum, habenula and hippocampus. The localization of this putative glycosyltransferase in distinct regions within the CNS indicates the specificity for complex protein modifications and suggests that region-specific glycosylation represents an essential process in basic biological functions.";
			final TextTree<String> lTextTree = new TextTree<String>(lAbstract);
			SentenceSplitter.split(lTextTree, "sentence");

			assertTrue(lTextTree.getChildTreeList().size() == 6);
		}

	}

	public void test2()
	{

		final String lString = "blabla bla Mr. Smith ba bweiufqw fsdakjhgsdkj we";
		final TextTree<String> lTextTree = new TextTree<String>(lString);
		SentenceSplitter.split(lTextTree, "sentence");

		assertTrue(lTextTree.getChildTreeList().size() == 1);

		for (final TextTree<String> lTree : lTextTree)
			System.out.println(lTree);
	}
}
