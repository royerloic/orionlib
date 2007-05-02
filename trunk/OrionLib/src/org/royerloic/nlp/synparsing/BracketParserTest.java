package org.royerloic.nlp.synparsing;

import junit.framework.TestCase;

public class BracketParserTest extends TestCase
{
	public void test1()
	{

		{
			final String lAbstract = "Protein glycosylation is a common and important process that can alter the stability, half-life, biological activity and receptor recognition of target molecules. We have identified a new putative mouse UDP-GalNAc:polypeptide N-acetylgalactosaminyltransferase family member, termed GalNAc-T10/ppGaNTase-T10 (gene symbol Galnt10), and determined its expression pattern in mouse CNS using in situ hybridization analysis. Results demonstrated predominant expression of Galnt10 in several distinct hypothalamic, thalamic and amygdaloid nuclei. The most abundant hybridization levels were observed in the paraventricular, ventromedial and arcuate nuclei of the hypothalamus, the anterodorsal and parafascicular nuclei of the thalamus and the central, basomedial and medial nuclei of the amygdala. Expression of Galnt10 was also detected in cerebral cortex, lateral septum, habenula and hippocampus. The localization of this putative glycosyltransferase in distinct regions within the CNS indicates the specificity for complex protein modifications and suggests that region-specific glycosylation represents an essential process in basic biological functions.";
			final TextTree<String> lTextTree = new TextTree<String>(lAbstract);
			BracketParser.markAll(lTextTree, "bracket");

			assertTrue(lTextTree.getChildTreeList().size() == 3);
			assertTrue(lTextTree.getChildTreeList().get(1).getString().equals("(gene symbol Galnt10)"));

		}
	}

	public void testNesting()
	{

		final String lAbstract = "( 5' bla(TEM), bla[SHV], bla(CARB{19 - 0}), bla(CTX-\"Mdiuhdkjhd\"), bla(IMP), bla(VIM), bla(CphA/IMIS), bla(OXA-A), bla(OXA-B), bla(OXA-C))";
		final TextTree<String> lTextTree = new TextTree<String>(lAbstract);
		BracketParser.markAll(lTextTree, "bracket");
		assertTrue(lTextTree.getChildTreeList().size() == 1);
		assertTrue(lTextTree.getChildTreeList().get(0).getChildTreeList().size() == 21);

		System.out.println(lTextTree.getAllDescendents().toString());
		final String lString = "['( 5' bla(TEM), bla[SHV], bla(CARB{19 - 0}), bla(CTX-\"Mdiuhdkjhd\"), bla(IMP), bla(VIM), bla(CphA/IMIS), bla(OXA-A), bla(OXA-B), bla(OXA-C))':bracket, '( 5' bla':null, '(TEM)':bracket, ', bla':null, '[SHV]':bracket, ', bla':null, '(CARB{19 - 0})':bracket, ', bla':null, '(CTX-\"Mdiuhdkjhd\")':bracket, ', bla':null, '(IMP)':bracket, ', bla':null, '(VIM)':bracket, ', bla':null, '(CphA/IMIS)':bracket, ', bla':null, '(OXA-A)':bracket, ', bla':null, '(OXA-B)':bracket, ', bla':null, '(OXA-C)':bracket, ')':null, '(CARB':null, '{19 - 0}':bracket, ')':null, '(CTX-':null, '\"Mdiuhdkjhd\"':bracket, ')':null]";
		assertTrue(lTextTree.getAllDescendents().toString().equals(lString));

		System.out.println(lTextTree.toTreeString());

	}

}
