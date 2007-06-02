package utils.nlp.regex;

import junit.framework.TestCase;

public class ChromosomeRegexesTest extends TestCase
{

	public void testHuman()
	{
		assertFalse(ChromosomeRegexes.isHumanChromosome("human"));
		assertFalse(ChromosomeRegexes.isHumanChromosome("p53"));
		assertTrue(ChromosomeRegexes.isHumanChromosome("2p21.1"));

		assertTrue(ChromosomeRegexes.isHumanChromosome("p21.1-p14.2"));
		assertTrue(ChromosomeRegexes.isHumanChromosome("p21.1-p14"));
		assertTrue(ChromosomeRegexes.isHumanChromosome("p21.1-14"));
		assertTrue(ChromosomeRegexes.isHumanChromosome("p1-14"));
		assertTrue(ChromosomeRegexes.isHumanChromosome("Xp21.1-14"));
		assertTrue(ChromosomeRegexes.isHumanChromosome("22p21.1-q14.3"));

		assertTrue(ChromosomeRegexes.isHumanChromosome("human chromosome X at p21.1"));
	}

}
