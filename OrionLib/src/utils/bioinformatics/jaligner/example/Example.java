/*
 * $Id: Example.java,v 1.3 2005/04/03 19:38:21 ahmed Exp $
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package utils.bioinformatics.jaligner.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import utils.bioinformatics.jaligner.Alignment;
import utils.bioinformatics.jaligner.Sequence;
import utils.bioinformatics.jaligner.SmithWatermanGotoh;
import utils.bioinformatics.jaligner.formats.Pair;
import utils.bioinformatics.jaligner.matrix.MatrixLoader;
import utils.bioinformatics.jaligner.util.SequenceParser;

/**
 * Example of using JAligner API to align P53 human aganist P53 mouse using
 * Smith-Waterman-Gotoh algorithm.
 * 
 * @author Ahmed Moustafa (ahmed@users.sf.net)
 */

public class Example
{

	/**
	 * 
	 */
	private static final String SAMPLE_SEQUENCE_P35_HUMAN = "utils/bioinformatics/jaligner/example/sequences/p53_human.fasta";

	/**
	 * 
	 */
	private static final String SAMPLE_SEQUENCE_P35_MOUSE = "utils/bioinformatics/jaligner/example/sequences/p53_mouse.fasta";

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(Example.class.getName());

	@Test
	public void test()
	{
		try
		{
			logger.info("Running example...");

			final Sequence s1 = SequenceParser.parse(loadP53Human());
			final Sequence s2 = SequenceParser.parse(loadP53Mouse());

			final Alignment alignment = SmithWatermanGotoh.align(	s1,
																														s2,
																														MatrixLoader.load("BLOSUM62"),
																														10f,
																														0.5f);

			System.out.println(alignment.getSummary());
			System.out.println(new Pair().format(alignment));

			logger.info("Finished running example");
		}
		catch (final Exception e)
		{
			logger.log(Level.SEVERE, "Failed running example: " + e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param path
	 *          location of the sequence
	 * @return sequence string
	 * @throws IOException
	 */
	private static String loadSampleSequence(final String path) throws IOException
	{
		final InputStream inputStream = Example.class	.getClassLoader()
																									.getResourceAsStream(path);
		final StringBuffer buffer = new StringBuffer();
		int ch;
		while ((ch = inputStream.read()) != -1)
		{
			buffer.append((char) ch);
		}
		return buffer.toString();
	}

	/**
	 * 
	 * @return sequence string
	 * @throws IOException
	 */
	public static String loadP53Human() throws IOException
	{
		return loadSampleSequence(SAMPLE_SEQUENCE_P35_HUMAN);
	}

	/**
	 * 
	 * @return sequence string
	 * @throws IOException
	 */
	public static String loadP53Mouse() throws IOException
	{
		return loadSampleSequence(SAMPLE_SEQUENCE_P35_MOUSE);
	}
}