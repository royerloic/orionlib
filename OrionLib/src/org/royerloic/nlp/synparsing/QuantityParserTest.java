package org.royerloic.nlp.synparsing;

import junit.framework.TestCase;

public class QuantityParserTest extends TestCase
{
	public void test1()
	{

		{
			final String lAbstract = "Background and purpose:Obestatin, encoded by the ghrelin gene may inhibit gastrointestinal (GI) motility. This activity was re-investigated.Experimental approach:Rat GI motility was studied in vitro (jejunum contractility and cholinergically-mediated contractions of forestomach evoked by electrical field stimulation; EFS) and in vivo (gastric emptying and intestinal myoelectrical activity). Ghrelin receptor function was studied using a GTPgammaS assay and transfected cells.Key results:Contractions of the jejunum or forestomach were unaffected by obestatin 100 nM or 0.01-1000 nM, respectively (P>0.05 each; n=4-18). Obestatin (0.1-1 nM) reduced the ability of ghrelin 1 muM to facilitate EFS-evoked contractions of the stomach (increases were 42.7+/-7.8% and 21.2+/-5.0 % in the absence and presence of obestatin 1 nM; P<0.05; n=12); higher concentrations (10-1000 nM) tended to reduce the response to ghrelin but changes were not statistically significant. Similar concentrations of obestatin did not significantly reduce a facilitation of contractions caused by the 5-HT(4) receptor agonist prucalopride, although an inhibitory trend occurred at the higher concentrations (increases were 69.3+/-14.0% and 42.6+/-8.7% in the absence and presence of 1000 nM obestatin; n=10). Obestatin (up to 10 muM) did not modulate recombinant ghrelin receptor function. Ghrelin increased gastric emptying and reduced MMC cycle time; obestatin (1000 and 30,000 pmol kg(-1) min(-1)) had no effects. Obestatin (2500 pmol kg(-1) min(-1), starting 10 min before ghrelin) did not prevent the ability of ghrelin (500 pmol kg(-1) min(-1)) to shorten MMC cycle time.Conclusions and implications:Obestatin has little ability to modulate rat GI motility.British Journal of Pharmacology advance online publication, 27 November 2006; doi:10.1038/sj.bjp.0706969.";
			final TextTree<String> lTextTree = new TextTree<String>(lAbstract);
			QuantityParser.markAll(lTextTree, "quantity");

			System.out.println(lTextTree.toTreeString());

		}
	}

}
