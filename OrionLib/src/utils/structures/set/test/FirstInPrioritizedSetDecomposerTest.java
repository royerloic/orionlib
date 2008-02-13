package utils.structures.set.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import utils.structures.set.FirstInPrioritizedSetDecomposer;

public class FirstInPrioritizedSetDecomposerTest
{

	@Test
	public void testFirstInPrioritizedSetDecomposer() throws IOException
	{
		FirstInPrioritizedSetDecomposer<String, String> lDecomposer = new FirstInPrioritizedSetDecomposer<String, String>();

		lDecomposer.addSet("att1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
		lDecomposer.addSet("att2", "0", "1", "2", "3", "4");
		lDecomposer.addSet("att3", "5", "6", "7", "8", "9");
		lDecomposer.addSet("att4", "5", "6", "7", "8", "9");
		assertTrue(lDecomposer.getSetsAndAttributes().size() == 3);

		lDecomposer.addSet("att5", "4", "5");
		assertTrue(lDecomposer.getSetsAndAttributes().size() == 5);

		lDecomposer.addSet("att6", "3", "4", "5", "6");
		assertTrue(lDecomposer.getSetsAndAttributes().size() == 5 + 2);

		lDecomposer.addSet("att7", "13", "24", "35", "46");
		assertTrue(lDecomposer.getSetsAndAttributes().size() == 5 + 2 + 1);

		System.out.println(lDecomposer.getSetsAndAttributes());
	}

}
