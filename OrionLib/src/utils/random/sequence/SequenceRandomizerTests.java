package utils.random.sequence;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import utils.io.LineReader;
import utils.io.LineWriter;
import utils.random.sequence.ContextPreservingSequenceRandomizer.Context;

/**
 */
public class SequenceRandomizerTests
{
	static Random mRandom = new Random();
	
	@Test
	public void testOneGramInvariantRandomization()
	{
		final char[] array = "abcdefgh".toCharArray();
		for (int i = 0; i < 1000; i++)
		{
			final char[] rndarray = SequenceRandomization.oneGramInvariantRandomization(mRandom, array);
			assertFalse(Arrays.equals(array, rndarray));
		}
	}

	@Test
	public void testArrayReverse()
	{
		final char[] array1 = "____1234_____".toCharArray();
		SequenceRandomization.reverse(array1, 4, 4 + 4);
		System.out.println(new String(array1));
	}

	@Test
	public void testRotate()
	{
		final char[] array0 = "ab".toCharArray();
		final char[] rotatedarray0 = SequenceRandomization.rotate(array0, 1);
		System.out.println(new String(rotatedarray0));
		final char[] targetarray0 = "ba".toCharArray();
		assertTrue(Arrays.equals(rotatedarray0, targetarray0));

		final char[] array1 = "abcdefgh".toCharArray();
		final char[] rotatedarray1 = SequenceRandomization.rotate(array1, 3);
		System.out.println(new String(rotatedarray1));
		final char[] targetarray1 = "fghabcde".toCharArray();
		assertTrue(Arrays.equals(rotatedarray1, targetarray1));
	}

	@Test
	public void testPairInvariantRandomization()
	{
		final char[] array1 = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		final char[] rndarray1 = SequenceRandomization.pairInvariantRandomization(mRandom, array1);
		// assertTrue(Arrays.equals(array1,rndarray1));

		final char[] array2 = "uvwab120584-->103956baxyz".toCharArray();
		final char[] rndarray2 = SequenceRandomization.pairInvariantRandomization(mRandom, array2);
		System.out.println(new String(rndarray2));
		assertFalse(Arrays.equals(array2, rndarray2));

		final char[] array3 = "MGGKWSKSSVIGWPAVRERMRRAEPAADGVGAASRDLEKHGAITSSNTAANNAACAWLEAQEEEKVGFPVTPQVPLRPMTYKAAVDLSHFLKEKGGLEGLIHSQRRQDILDLWIYHTQGYFPDWQNYTPGPGIRYPLTFGWCYKLVPVEPDKVEEANKGENTSLLHPVSLHGMDDPEREVLEWRFDSRLAFHHVARELHPEYFKNC".toCharArray();
		char[] rndarray = array3;
		System.out.println(new String(rndarray));
		for (int i = 0; i < 1000; i++)
		{
			rndarray = SequenceRandomization.pairInvariantRandomization(mRandom, rndarray);
			// System.out.println(new String(rndarray));
		}
		System.out.println(new String(rndarray));
		assertFalse(Arrays.equals(array3, rndarray));
	}

	@Test
	public void testContextInvariantRandomization()
	{
		final char[] array = "AB1CDAB2CDAB3CD".toCharArray();
		ContextPreservingSequenceRandomizer rand = new ContextPreservingSequenceRandomizer(2);
		for (int i = 0; i < 1000; i++)
		{
			final char[] rndarray = SequenceRandomization.oneGramInvariantRandomization(mRandom, array);
			rand.addSequenceToStatistics(rndarray);
		}
		rand.finalizeStatistics();

		rand = new ContextPreservingSequenceRandomizer(2);
		rand.addSequenceToStatistics(array);
		rand.finalizeStatistics();

		final Context lContext = new Context();
		lContext.left = new char[]
		{ 'A', 'B' };
		lContext.right = new char[]
		{ 'C', 'D' };

		System.out.print("Random Char:");
		for (int i = 0; i < 100; i++)
		{
			final char lChar = rand.mContext2DistributionMap.get(lContext)
																											.getRandomChar();
			assertTrue(lChar == '1' || lChar == '2' || lChar == '3');
			System.out.print(lChar);
		}
		System.out.println("");

		final char[] testarray = "AB.CDAB.CDAB.CDAB.CDAB.CDUWXYZ".toCharArray();

		final char[] rndtestarray = rand.randomize(testarray);

		System.out.println(rndtestarray);

		System.out.println("");

	}

	@Test
	public void testMainTxt()
	{
		try
		{
			final File tempfilein = File.createTempFile("in.txt", "1");
			final File tempfileout = File.createTempFile("out.txt", "1");

			final Writer writer = LineWriter.getWriter(tempfilein);

			final String line1 = "MGGKWSKSSVIGWPAVRERMRRAEPAADGVGAASRDLEKHGAITSSNTAANNAACAWLEAQEEEKVGFPVTPQVPLRPMTYKAAVDLSHFLKEKGGLEGLIHSQRRQDILDLWIYHTQGYFPDWQNYTPGPGIRYPLTFGWCYKLVPVEPDKVEEANKGENTSLLHPVSLHGMDDPEREVLEWRFDSRLAFHHVARELHPEYFKNC";
			final String line2 = "MGASGSKKRSEPSRGLRERLLQTPGEASGGHWDKLGGEYLQSQEGSGRGQKSPSCEGRRYQQGDFMNTPWRAPAEGEKGSYKQQNMDDVDSDDDDLVGVPVTPRVPLREMTYRLARDMSHLIKEKGGLEGLYYSDRRRRVLDIYLEKEEGIIGDWQNYTHGPGVRYPKFFGWLWKLVPVDVPQEGDDSETHCLVHPAQTSRFDDPHGETLVWRFDPTLAFSYEAFIRYPEEFGYKSGLPEDEWKARLKARGIPFS";

			writer.append(line1 + "\n");
			writer.append(line2 + "\n");

			writer.close();

			SequenceRandomizer.main("filein=" + tempfilein.toString()
															+ " fileout="
															+ tempfileout.toString());

			for (final String line : LineReader.getLines(tempfileout))
			{
				System.out.println(line);
			}

		}
		catch (final IOException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testMainFasta()
	{
		try
		{
			final File tempfilein = File.createTempFile("in.txt", "1");
			final File tempfileout = File.createTempFile("out.txt", "1");

			final Writer writer = LineWriter.getWriter(tempfilein);

			final String head1 = ">Bla1";
			final String seq1 = "MGGKWSKSSVIGWPAVRERMRRAEPAADGVGAASRDLEKHGAITSSNTAANNAACAWLEAQEEEKVGFPVTPQ\nVPLRPMTYKAAVDLSHFLKEKGGLEGLIHSQRRQDILDLWIYHTQGYFPDWQNYTPGPGIRYPLTFGWCYKLVP\nVEPDKVEEANKGENTSLLHPVSLHGMDDPEREVLEWRFDSRLAFHHVARELHPEYFKNC";
			final String head2 = ">Bla2";
			final String seg2 = "MGASGSKKRSEPSRGLRERLLQTPGEASGGHWDKLGGEYLQSQEGSGRGQKSPSCEGRRYQQGDFMNTPWRAP\nAEGEKGSYKQQNMDDVDSDDDDLVGVPVTPRVPLREMTYRLARDMSHLIKEKGGLEGLYYSDRRRRVLDIYLEK\nEEGIIGDWQNYTHGPGVRYPKFFGWLWKLVPVDVPQEGDDSETHCLVHPAQTSRFDDPHGETLVWRFDPTLA\nFSYEAFIRYPEEFGYKSGLPEDEWKARLKARGIPFS";

			writer.append(head1 + "\n");
			writer.append(seq1 + "\n");
			writer.append(head2 + "\n");
			writer.append(seg2 + "\n");
			writer.append("\n");

			writer.close();

			SequenceRandomizer.main("filein=" + tempfilein.toString()
															+ " fileout="
															+ tempfileout.toString()
															+ " format=fasta");

			for (final String line : LineReader.getLines(tempfileout))
			{
				System.out.println(line);
			}

		}
		catch (final IOException e)
		{
			e.printStackTrace();
			fail();
		}
	}

}
