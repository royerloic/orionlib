/*
 * $Id: AlignCommandLine.java,v 1.7 2005/04/18 05:37:52 ahmed Exp $
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

package utils.bioinformatics.jaligner.ui;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.bioinformatics.jaligner.Alignment;
import utils.bioinformatics.jaligner.Sequence;
import utils.bioinformatics.jaligner.SmithWatermanGotoh;
import utils.bioinformatics.jaligner.formats.Pair;
import utils.bioinformatics.jaligner.matrix.Matrix;
import utils.bioinformatics.jaligner.matrix.MatrixLoader;
import utils.bioinformatics.jaligner.util.Commons;
import utils.bioinformatics.jaligner.util.SequenceParser;

/**
 * Command line interface for JAligner.
 * 
 * @author Ahmed Moustafa (ahmed@users.sf.net)
 */
public class AlignCommandLine
{

	private static final Logger logger = Logger.getLogger(AlignCommandLine.class.getName());

	/**
	 * @param args
	 *          The command line arguments
	 */
	public static void main(final String[] args)
	{
		logger.info(Commons.getJAlignerInfo());

		if (args.length == 0)
		{
			new AlignWindow().setVisible(true);
		}
		else
		{
			if (args.length == 5)
			{
				try
				{

					final String f1 = args[0]; // file name of sequence #1
					final String f2 = args[1]; // file name of sequence #1
					final String m = args[2]; // scoring matrix id or file name
					// user-defined
					// scoring matrix
					final float o = Float.parseFloat(args[3]); // open gap penalty
					final float e = Float.parseFloat(args[4]); // extend gap penalty

					final Sequence s1 = SequenceParser.parse(new File(f1));
					final Sequence s2 = SequenceParser.parse(new File(f2));
					final Matrix matrix = MatrixLoader.load(m);
					final Alignment alignment = SmithWatermanGotoh.align(	s1,
																																s2,
																																matrix,
																																o,
																																e);
					System.out.println(alignment.getSummary());
					System.out.println(new Pair().format(alignment));
				}
				catch (final Exception e)
				{
					logger.log(	Level.SEVERE,
											"Failed processing the command line: " + e.getMessage(),
											e);
					System.exit(1);
				}
			}
			else
			{
				logger.severe("Invalid number of arguments: " + args.length);
				printUsage();
				System.exit(1);
			}
		}

	}

	/**
	 * Prints the syntax for using JAligner
	 */
	private static void printUsage()
	{
		final StringBuffer buffer = new StringBuffer();
		buffer.append("\n");
		buffer.append("Usage:\n");
		buffer.append("------\n");
		buffer.append("[1] java -jar jaligner.jar <s1> <s2> <matrix> <open> <extend>\n");
		buffer.append("[2] java -jar jaligner.jar\n");
		buffer.append("\n");
		logger.info(buffer.toString());
	}
}