/*
 * $Id: AlignmentScoreChecker.java,v 1.2 2005/04/18 14:06:15 ahmed Exp $
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

package utils.bioinformatics.jaligner.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import utils.bioinformatics.jaligner.Alignment;
import utils.bioinformatics.jaligner.Sequence;
import utils.bioinformatics.jaligner.SmithWatermanGotoh;
import utils.bioinformatics.jaligner.formats.Format;
import utils.bioinformatics.jaligner.formats.Pair;
import utils.bioinformatics.jaligner.matrix.Matrix;
import utils.bioinformatics.jaligner.matrix.MatrixLoader;

/**
 * Testing the scores of the alignments of the SmithWaterman algorithm
 * 
 * @author Bram Minnaert
 */

public class AlignmentScoreChecker
{

	public static void main(final String[] args)
	{

		final int numberOfTests = Integer.parseInt(args[0]);
		final int sequencesSize = Integer.parseInt(args[1]);
		final Random random = new Random();
		final Format format = new Pair();

		try
		{
			final ArrayList matrices = new ArrayList();
			for (final Iterator i = MatrixLoader.list().iterator(); i.hasNext();)
			{
				matrices.add(i.next());
			}
			final int countOfMatrices = matrices.size();

			int i = 1;
			while (i <= numberOfTests)
			{

				System.gc();

				final String s1 = RandomSequenceGenerator.generate(sequencesSize);
				final String s2 = RandomSequenceGenerator.generate(sequencesSize);

				final int o = random.nextInt(50);
				final int e = random.nextInt(10);

				if (s1.length() > 0 && s2.length() > 0 && o >= e)
				{

					final Matrix matrix = MatrixLoader.load((String) matrices.get(random.nextInt(countOfMatrices)));
					final Sequence seq1 = new Sequence(s1);
					final Sequence seq2 = new Sequence(s2);

					final Alignment alignment1 = SmithWatermanGotoh.align(seq1,
																																seq2,
																																matrix,
																																o,
																																e);

					if (!alignment1.checkScore())
					{
						System.err.println("Invalid alignment found:");
						System.err.println("Sequence 1 = " + s1);
						System.err.println("Sequence 2 = " + s2);
						System.err.println(format.format(alignment1));
						System.err.println(alignment1.getSummary());
						System.err.println("The score of the alignment above is: " + alignment1.calculateScore());
						System.exit(1);
					}

					final Alignment alignment2 = SmithWatermanGotoh.align(seq2,
																																seq1,
																																matrix,
																																o,
																																e);
					if (!alignment1.checkScore())
					{
						System.err.println("Invalid alignment found:");
						System.err.println("Sequence 1 = " + s2);
						System.err.println("Sequence 2 = " + s1);
						System.err.println(format.format(alignment2));
						System.err.println(alignment2.getSummary());
						System.err.println("The score of the alignment above is: " + alignment2.calculateScore());
						System.exit(1);
					}

					if (alignment1.getScore() != alignment2.getScore())
					{
						System.err.println("Not symmetric alignment:");

						System.err.println("Alignment #1: ");
						System.err.println("Sequence 1 = " + s1);
						System.err.println("Sequence 2 = " + s2);
						System.err.println(format.format(alignment1));
						System.err.println(alignment1.getSummary());

						System.err.println();

						System.err.println("Alignment #2: ");
						System.err.println("Sequence 1 = " + s2);
						System.err.println("Sequence 2 = " + s1);
						System.err.println(format.format(alignment2));
						System.err.println(alignment2.getSummary());

						System.exit(1);
					}
				}
				System.out.println("Processed " + i + "/" + numberOfTests);
				i++;
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
}