package org.royerloic.bsh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.royerloic.io.MatrixFile;
import org.royerloic.structures.ArrayMatrix;
import org.royerloic.structures.Matrix;

import bsh.CallStack;
import bsh.Interpreter;

public class readallfiles
{

	public static Matrix<String> invoke(Interpreter env,
																			CallStack callstack,
																			String pFolderName,
																			String pFileNameRegex,
																			boolean pHasHeader) throws FileNotFoundException, IOException
	{
		Matrix<String> lAgregatedMatrix = new ArrayMatrix<String>();
		invoke(env,callstack,lAgregatedMatrix,pFolderName,pFileNameRegex,pHasHeader);

		return lAgregatedMatrix;
	}

	public static Matrix<String> invoke(Interpreter env,
																			CallStack callstack,
																			Matrix<String> pMatrix,
																			String pFolderName,
																			String pFileNameRegex,
																			boolean pHasHeader) throws FileNotFoundException, IOException
	{
		Matrix<String> lAgregatedMatrix = pMatrix;
		File lFolder = new File(pFolderName);
		for (File lFile : lFolder.listFiles())
			if (lFile.getName().matches(pFileNameRegex))
			{
				Matrix<String> lMatrix = MatrixFile.readMatrixFromFile(lFile, pHasHeader);
				lAgregatedMatrix.addAll(lMatrix);
			}

		return lAgregatedMatrix;
	}

}
