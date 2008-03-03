package utils.structures.matrix.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import groovy.lang.IntRange;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import utils.structures.matrix.SymetricFloatMatrix;

public class SymetricFloatMatrixTest
{

	@Test
	public void test() throws IOException
	{
		SymetricFloatMatrix lSymetricFloatMatrix = new SymetricFloatMatrix();

		lSymetricFloatMatrix.init(200);

		lSymetricFloatMatrix.set(100, 50, 0.1234f);
		lSymetricFloatMatrix.set(10, 50, 0.5678f);

		File lFile = File.createTempFile("SymetricFloatMatrixTest", "1");

		lSymetricFloatMatrix.saveToFile(lFile);
		lSymetricFloatMatrix = null;

		SymetricFloatMatrix lSymetricFloatMatrix2 = new SymetricFloatMatrix();
		lSymetricFloatMatrix2.loadFromFile(lFile);

		assertTrue(lSymetricFloatMatrix2.get(100, 50) == 0.1234f);
		assertTrue(lSymetricFloatMatrix2.get(10, 50) == 0.5678f);
		assertTrue(lSymetricFloatMatrix2.get(10, new IntRange(50-10,50+10)).contains(0.5678f));
		assertFalse(lSymetricFloatMatrix2.get(10, new IntRange(50-20,50-10)).contains(0.5678f));
		assertTrue(lSymetricFloatMatrix2.get(10,50,100,200).toString().equals("[[0.0], [0.5678, 0.0], [0.0, 0.1234, 0.0], [0.0, 0.0, 0.0, 0.0]]"));
		
		lSymetricFloatMatrix2 = null;
	}

}
