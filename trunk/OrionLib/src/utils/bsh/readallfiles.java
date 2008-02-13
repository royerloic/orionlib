package utils.bsh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import utils.io.MatrixFile;
import utils.structures.matrix.ArrayMatrix;
import utils.structures.matrix.Matrix;
import bsh.CallStack;
import bsh.Interpreter;

public class readallfiles
{

	public static Matrix<String> invoke(final Interpreter env,
																			final CallStack callstack,
																			final String pFolderName,
																			final String pFileNameRegex,
																			final boolean pHasHeader)	throws FileNotFoundException,
																																IOException
	{
		final Matrix<String> lAgregatedMatrix = new ArrayMatrix<String>();
		invoke(	env,
						callstack,
						lAgregatedMatrix,
						pFolderName,
						pFileNameRegex,
						pHasHeader);

		return lAgregatedMatrix;
	}

	public static Matrix<String> invoke(final Interpreter env,
																			final CallStack callstack,
																			final Matrix<String> pMatrix,
																			final String pFolderName,
																			final String pFileNameRegex,
																			final boolean pHasHeader)	throws FileNotFoundException,
																																IOException
	{
		final Matrix<String> lAgregatedMatrix = pMatrix;
		final File lFolder = new File(pFolderName);
		for (final File lFile : lFolder.listFiles())
			if (lFile.getName().matches(pFileNameRegex))
			{
				final Matrix<String> lMatrix = MatrixFile.readMatrixFromFile(	lFile,
																																			pHasHeader);
				lAgregatedMatrix.addAll(lMatrix);
			}

		return lAgregatedMatrix;
	}

}
