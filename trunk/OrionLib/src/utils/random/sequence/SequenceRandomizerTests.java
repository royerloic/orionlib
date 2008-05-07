package utils.random.sequence;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
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

	@Test
	public void testOneGramInvariantRandomization()
	{
		char[] array = "abcdefgh".toCharArray();
		for (int i = 0; i < 1000; i++)
		{
			char[] rndarray = SequenceRandomizer.oneGramInvariantRandomization(array);
			assertFalse(Arrays.equals(array, rndarray));
		}
	}

	@Test
	public void testArrayReverse()
	{
		char[] array1 = "____1234_____".toCharArray();
		SequenceRandomizer.reverse(array1, 4, 4 + 4);
		System.out.println(new String(array1));
	}

	@Test
	public void testRotate()
	{
		char[] array0 = "ab".toCharArray();
		char[] rotatedarray0 = SequenceRandomizer.rotate(array0, 1);
		System.out.println(new String(rotatedarray0));
		char[] targetarray0 = "ba".toCharArray();
		assertTrue(Arrays.equals(rotatedarray0, targetarray0));

		char[] array1 = "abcdefgh".toCharArray();
		char[] rotatedarray1 = SequenceRandomizer.rotate(array1, 3);
		System.out.println(new String(rotatedarray1));
		char[] targetarray1 = "fghabcde".toCharArray();
		assertTrue(Arrays.equals(rotatedarray1, targetarray1));
	}

	@Test
	public void testPairInvariantRandomization()
	{
		char[] array1 = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		char[] rndarray1 = SequenceRandomizer.pairInvariantRandomization(array1);
		// assertTrue(Arrays.equals(array1,rndarray1));

		char[] array2 = "uvwab120584-->103956baxyz".toCharArray();
		char[] rndarray2 = SequenceRandomizer.pairInvariantRandomization(array2);
		System.out.println(new String(rndarray2));
		assertFalse(Arrays.equals(array2, rndarray2));

		char[] array3 = "MGGKWSKSSVIGWPAVRERMRRAEPAADGVGAASRDLEKHGAITSSNTAANNAACAWLEAQEEEKVGFPVTPQVPLRPMTYKAAVDLSHFLKEKGGLEGLIHSQRRQDILDLWIYHTQGYFPDWQNYTPGPGIRYPLTFGWCYKLVPVEPDKVEEANKGENTSLLHPVSLHGMDDPEREVLEWRFDSRLAFHHVARELHPEYFKNC".toCharArray();
		char[] rndarray = array3;
		System.out.println(new String(rndarray));
		for (int i = 0; i < 1000; i++)
		{
			rndarray = SequenceRandomizer.pairInvariantRandomization(rndarray);
			// System.out.println(new String(rndarray));
		}
		System.out.println(new String(rndarray));
		assertFalse(Arrays.equals(array3, rndarray));
	}

	@Test
	public void testContextInvariantRandomization()
	{
		char[] array = "AB1CDAB2CDAB3CD".toCharArray();
		ContextPreservingSequenceRandomizer rand = new ContextPreservingSequenceRandomizer(2);
		for (int i = 0; i < 1000; i++)
		{
			char[] rndarray = SequenceRandomizer.oneGramInvariantRandomization(array);
			rand.addSequenceToStatistics(rndarray);
		}
		rand.finalizeStatistics();

		rand = new ContextPreservingSequenceRandomizer(2);
		rand.addSequenceToStatistics(array);
		rand.finalizeStatistics();

		Context lContext = new Context();
		lContext.left = new char[]
		{ 'A', 'B' };
		lContext.right = new char[]
		{ 'C', 'D' };

		
		System.out.print("Random Char:");
		for(int i=0; i<100; i++)
		{
			char lChar = rand.mContext2DistributionMap.get(lContext).getRandomChar();
			assertTrue(lChar=='1' ||lChar=='2'||lChar=='3');
			System.out.print(lChar);
		}
		System.out.println("");
		
		
		char[] testarray = "AB.CDAB.CDAB.CDAB.CDAB.CDUWXYZ".toCharArray();
		
		char[] rndtestarray = rand.randomize(testarray);
		
		System.out.println(rndtestarray);		
		
		System.out.println("");
		
	}

	@Test
	public void testMainTxt()
	{
		try
		{
			File tempfilein = File.createTempFile("in.txt", "1");
			File tempfileout = File.createTempFile("out.txt", "1");

			Writer writer = LineWriter.getWriter(tempfilein);

			String line1 = "MGGKWSKSSVIGWPAVRERMRRAEPAADGVGAASRDLEKHGAITSSNTAANNAACAWLEAQEEEKVGFPVTPQVPLRPMTYKAAVDLSHFLKEKGGLEGLIHSQRRQDILDLWIYHTQGYFPDWQNYTPGPGIRYPLTFGWCYKLVPVEPDKVEEANKGENTSLLHPVSLHGMDDPEREVLEWRFDSRLAFHHVARELHPEYFKNC";
			String line2 = "MGASGSKKRSEPSRGLRERLLQTPGEASGGHWDKLGGEYLQSQEGSGRGQKSPSCEGRRYQQGDFMNTPWRAPAEGEKGSYKQQNMDDVDSDDDDLVGVPVTPRVPLREMTYRLARDMSHLIKEKGGLEGLYYSDRRRRVLDIYLEKEEGIIGDWQNYTHGPGVRYPKFFGWLWKLVPVDVPQEGDDSETHCLVHPAQTSRFDDPHGETLVWRFDPTLAFSYEAFIRYPEEFGYKSGLPEDEWKARLKARGIPFS";

			writer.append(line1 + "\n");
			writer.append(line2 + "\n");

			writer.close();

			SequenceRandomizer.main("filein=" + tempfilein.toString()
															+ " fileout="
															+ tempfileout.toString());

			for (String line : LineReader.getLines(tempfileout))
			{
				System.out.println(line);
			}

		}
		catch (IOException e)
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
			File tempfilein = File.createTempFile("in.txt", "1");
			File tempfileout = File.createTempFile("out.txt", "1");

			Writer writer = LineWriter.getWriter(tempfilein);

			String head1 = ">Bla1";
			String seq1 = "MGGKWSKSSVIGWPAVRERMRRAEPAADGVGAASRDLEKHGAITSSNTAANNAACAWLEAQEEEKVGFPVTPQ\nVPLRPMTYKAAVDLSHFLKEKGGLEGLIHSQRRQDILDLWIYHTQGYFPDWQNYTPGPGIRYPLTFGWCYKLVP\nVEPDKVEEANKGENTSLLHPVSLHGMDDPEREVLEWRFDSRLAFHHVARELHPEYFKNC";
			String head2 = ">Bla2";
			String seg2 = "MGASGSKKRSEPSRGLRERLLQTPGEASGGHWDKLGGEYLQSQEGSGRGQKSPSCEGRRYQQGDFMNTPWRAP\nAEGEKGSYKQQNMDDVDSDDDDLVGVPVTPRVPLREMTYRLARDMSHLIKEKGGLEGLYYSDRRRRVLDIYLEK\nEEGIIGDWQNYTHGPGVRYPKFFGWLWKLVPVDVPQEGDDSETHCLVHPAQTSRFDDPHGETLVWRFDPTLA\nFSYEAFIRYPEEFGYKSGLPEDEWKARLKARGIPFS";

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

			for (String line : LineReader.getLines(tempfileout))
			{
				System.out.println(line);
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail();
		}
	}

}
