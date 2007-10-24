package utils.bioinformatics.proteome;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

import utils.bioinformatics.genome.Gene;
import utils.bioinformatics.genome.GeneSet;
import utils.bioinformatics.genome.Genome;
import utils.io.MatrixFile;

public class InterProReader
{

	private static final Pattern lSplitTabPattern = Pattern.compile("\t");
	private static final Pattern lSplitSemicolonPattern = Pattern.compile("\\;");

	public static void addDomainsTo(Proteome pProteome, File pInterProScanFile)	throws FileNotFoundException,
																																							IOException
	{
		addDomainsTo(pProteome, new FileInputStream(pInterProScanFile));
	}

	public static void addDomainsTo(Proteome pProteome, InputStream pInputStream) throws IOException
	{

		ProteinSet lProteinSet = pProteome.getProteinSet();
		for (String lLine : MatrixFile.getLines(pInputStream))
			if (!lLine.isEmpty())
			{
				lLine = lLine.trim();
				if (lLine.startsWith("#") || lLine.startsWith("//"))
				{
					// ignore comments.
				}
				else
				{
					final String[] lTokenArray = lSplitTabPattern.split(lLine, -1);
					final String lSwissProtName = lTokenArray[0];
					final String lId = lTokenArray[3];
					final String lInterProId = lTokenArray[1];
					final String lDescription = lTokenArray[2];
					final String lSource = lId.split("[0-9]+", 0)[0];

					if (!lInterProId.equals("NULL"))
					{// if not in interpro it can't be very interesting, at least not for
						// us...

						try
						{
							final int lStart = Integer.parseInt(lTokenArray[4]) - 1;
							final int lEnd = Integer.parseInt(lTokenArray[5]) - 1 + 1;

							
							final Domain lDomain = new Domain(lId,
																								lInterProId,
																								lDescription,
																								lSource,
																								lStart,
																								lEnd,
																								0);

							final Protein lProtein = lProteinSet.getProteinById(lSwissProtName);
							if (lProtein != null)
							{
								lProtein.addDomain(lDomain);
							}
							else
							{
								System.err.println("Missing protein: " + lSwissProtName
																		+ " for domain: "
																		+ lId);
							}
						}
						catch (IndexOutOfBoundsException e)
						{
							System.err.println(e.getMessage());
						}
					}
				}
			}

	}

}
