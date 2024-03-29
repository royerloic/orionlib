package utils.structures.matrix.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import groovy.lang.IntRange;

import java.io.File;

import org.junit.Test;

import utils.structures.matrix.SymetricFloatMatrix;

public class SymetricFloatMatrixTest
{

	@Test
	public void test()
	{
		try
		{
			SymetricFloatMatrix lSymetricFloatMatrix = new SymetricFloatMatrix();

			lSymetricFloatMatrix.init(200);

			lSymetricFloatMatrix.set(100, 50, 0.1234f);
			lSymetricFloatMatrix.set(10, 50, 0.5678f);

			final File lFile = File.createTempFile("SymetricFloatMatrixTest", "1");

			lSymetricFloatMatrix.saveToFile(lFile);
			lSymetricFloatMatrix = null;

			SymetricFloatMatrix lSymetricFloatMatrix2 = new SymetricFloatMatrix();
			lSymetricFloatMatrix2.loadFromFile(lFile);

			assertTrue(lSymetricFloatMatrix2.get(100, 50) == 0.1234f);
			assertTrue(lSymetricFloatMatrix2.get(10, 50) == 0.5678f);

			assertTrue(lSymetricFloatMatrix2.get(10, new IntRange(50 - 10, 50 + 10))
																			.toString()
																			.contains("0.5678"));
			assertFalse(lSymetricFloatMatrix2	.get(10, new IntRange(50 - 20, 50 - 10))
																				.toString()
																				.contains("0.5678"));
			assertTrue(lSymetricFloatMatrix2.get(10, 50, 100, 200)
																			.toString()
																			.equals("[[0.0], [0.5678, 0.0], [0.0, 0.1234, 0.0], [0.0, 0.0, 0.0, 0.0]]"));

			assertTrue(lSymetricFloatMatrix2.get(new IntRange(5, 55))
																			.toString()
																			.contains("0.5678"));
			assertFalse(lSymetricFloatMatrix2	.get(new IntRange(5, 55))
																				.toString()
																				.contains("0.1234"));

			assertTrue(lSymetricFloatMatrix2.get(	new IntRange(8, 12),
																						new IntRange(48, 52))
																			.toString()
																			.contains("0.5678"));
			assertFalse(lSymetricFloatMatrix2	.get(	new IntRange(8, 12),
																							new IntRange(48, 52))
																				.toString()
																				.contains("0.1234"));

			lSymetricFloatMatrix2 = null;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

}
