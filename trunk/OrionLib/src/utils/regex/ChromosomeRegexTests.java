// ©2006 Transinsight GmbH - www.transinsight.com - All rights reserved.
package utils.regex;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import utils.regex.ChromosomeRegexes;





/**
 */
public class ChromosomeRegexTests
{

	
	@Test
	public void testExample()
	{

		assertMatches("3p21.3",ChromosomeRegexes.cRegexCompiler.getPattern("HumanChromosomeLocation"));
		assertMatches("3p21",ChromosomeRegexes.cRegexCompiler.getPattern("HumanChromosomeLocation"));
		assertMatches("11q23",ChromosomeRegexes.cRegexCompiler.getPattern("HumanChromosomeLocation"));
		assertMatches("1p34.2",ChromosomeRegexes.cRegexCompiler.getPattern("HumanChromosomeLocation"));
		assertMatches("Xp22",ChromosomeRegexes.cRegexCompiler.getPattern("HumanChromosomeLocation"));
		assertMatches("13q31.3",ChromosomeRegexes.cRegexCompiler.getPattern("HumanChromosomeLocation"));
		assertMatches("4q22",ChromosomeRegexes.cRegexCompiler.getPattern("HumanChromosomeLocation"));
		assertMatches("3p",ChromosomeRegexes.cRegexCompiler.getPattern("HumanChromosomeLocation"));
		assertMatches("13q",ChromosomeRegexes.cRegexCompiler.getPattern("HumanChromosomeLocation"));
		
		assertNoMatches("pp",ChromosomeRegexes.cRegexCompiler.getPattern("HumanChromosomeLocation"));
		assertNoMatches("pp",ChromosomeRegexes.cRegexCompiler.getPattern("HumanChromosomeFullInterval"));

		
		
		
		assertMatch("deletion region on human chromosome 3p21.3: identification", "chromosome 3p21.3");
		assertMatch("Lung Cancer Chromosome 3p21.3 Tumor Suppressor Gene", "Chromosome 3p21.3");
		assertMatch("In fact, 3p21 is a very peculiar region", "3p21");
		
		assertMatch("within human chromosome 15q11-q13. Some cases of PWS", "chromosome 15q11-q13");
		assertMatch("chromosome 11", "chromosome 11");
		assertMatch("dfgdfg 11p13 gdfgdf", "11p13");
		//assertMatch(", llp15 ", "llp15");
		assertMatch("11q23 ", "11q23");
		assertMatch("of chromosome 1p34.2-p34.3 has been", "chromosome 1p34.2-p34.3");
		assertMatch("mapping to ch13q34 in human breast cancer", "ch13q34");
		assertMatch("human chromosome 13q34, and some of the same", "chromosome 13q34");
		//assertMatch("macaque chromosomes 17, 18, 19, 20, X and Y,", "3p21.3");
		//assertMatch("of human chromosomes 13, 18, 19, 16, X and Y, respectively", "3p21.3");
		assertMatch("placing it in Xp22-p21 interval, a hot spot", "Xp22-p21");
		assertMatch("at Chromosome 7q32. We show", "Chromosome 7q32");
		assertMatch("diagnosis of Xp11 translocation carcinoma", "Xp11");
		assertMatch("the region of 13q32.2-34 has been suggested", "13q32.2-34");
		assertMatch("for distal chromosome 13q and their parents", "chromosome 13q");
		assertMatch("spanning 13q31.3 qter and", "13q31.3");
		assertMatch("9.5-Mb interval of 13q33.3-q34 delineated by markers", "13q33.3-q34");
		assertMatch("of 18q22-qter", "18q22-qter");
		assertMatch("The region in 18q22 appears highly", "18q22");
		assertMatch("isochromosome 18q, a rare", "isochromosome 18q");
		assertMatch("map a new locus for FS on 3p. We ", "3p");
		assertMatch("a possible modifier gene on 18p.", "18p");
		assertMatch("human chromosome 3.", "chromosome 3");
		assertMatch("on chromosome 18q11.2-q12.2. A maximum two-point", "chromosome 18q11.2-q12.2");
		assertMatch("receptor 1 (DDR1; located at human chromosome 6p21.3) ", "chromosome 6p21.3");
		assertMatch("on chromosome 1p (1p+/-).", "chromosome 1p");
		assertMatch("on chromosome 11q13, a linkage region", "chromosome 11q13");
		assertMatch("translocation in the 4q22 region. As such,", "4q22");
		assertMatch("combined 1p and", "1p");

		assertFalse(ChromosomeRegexes.isChromosome("human"));
		assertFalse(ChromosomeRegexes.isChromosome("p53"));
		assertFalse(ChromosomeRegexes.isChromosome("p1"));
		assertFalse(ChromosomeRegexes.isChromosome("p3"));
		assertTrue(ChromosomeRegexes.isChromosome("2p21.1"));

		assertTrue(ChromosomeRegexes.isChromosome("p21.1-p14.2"));
		assertTrue(ChromosomeRegexes.isChromosome("p21.1-p14"));
		assertTrue(ChromosomeRegexes.isChromosome("p21.1-14"));
		assertTrue(ChromosomeRegexes.isChromosome("p1-14"));
		assertTrue(ChromosomeRegexes.isChromosome("Xp21.1-14"));
		assertTrue(ChromosomeRegexes.isChromosome("22p21.1-q14.3"));

		//assertTrue(ChromosomeRegexes.isChromosome("human chromosome X at p21.1"));

		
		//Translocations
		//assertMatch("2 translocations in prostate cancer lines, that is t(1;15) and t(4;6),", "3p21.3");
		//assertMatch("were mapped to the t(4;6)(q22;q15) region and a", "3p21.3");
		//assertMatch("the t(4;6) translocation is also", "3p21.3");
		//assertMatch("on chromosome 11q13, a linkage region", "3p21.3");
		//assertMatch("on chromosome 11q13, a linkage region", "3p21.3");
		
		// mouse chromosome:
		//assertMatch("mouse region ch8A1.1", "3p21.3");
		//assertMatch("mouse chromosome band 8A1", "3p21.3");
		
		
	}

	private void assertMatch(final CharSequence pMatch, final CharSequence pExpectedMatch)
	{
		Matcher lMatcher = ChromosomeRegexes.cGenericChromosomePattern.matcher(pMatch);
		org.junit.Assert.assertTrue("No Match found, expected: '"+pExpectedMatch+"'",lMatcher.find());
		org.junit.Assert.assertTrue("Expected: '" + pExpectedMatch + "' obtained: '" + lMatcher.group()+"'", lMatcher.group().equals(
				pExpectedMatch));
	}
	
	private void assertMatches(final CharSequence pMatch,final Pattern pPattern)
	{
		Matcher lMatcher = pPattern.matcher(pMatch);
		org.junit.Assert.assertTrue(lMatcher.matches());
	}
	
	private void assertNoMatches(final CharSequence pMatch,final Pattern pPattern)
	{
		Matcher lMatcher = pPattern.matcher(pMatch);
		org.junit.Assert.assertFalse(lMatcher.matches());
	}

}
