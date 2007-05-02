package org.royerloic.bsh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.royerloic.io.MatrixFile;
import org.royerloic.structures.Matrix;

import bsh.CallStack;
import bsh.Interpreter;

public class readfile
{
	
	public static Matrix<String> invoke(final Interpreter env, final CallStack callstack, final String pFileName) throws FileNotFoundException, IOException
	{
		final File lFile = new File(pFileName);
		final Matrix<String> lMatrix = MatrixFile.readMatrixFromFile(lFile);
		return lMatrix;		
	}
	
	public static Matrix<String> invoke(final Interpreter env, final CallStack callstack, final String pFileName, final boolean pHasHeader) throws FileNotFoundException, IOException
	{
		final File lFile = new File(pFileName);
		final Matrix<String> lMatrix = MatrixFile.readMatrixFromFile(lFile,pHasHeader);
		return lMatrix;		
	}
	
	public static Matrix<String> invoke(final Interpreter env, final CallStack callstack, final Matrix<String> pMatrix, final String pFileName, final boolean pHasHeader) throws FileNotFoundException, IOException
	{
		final File lFile = new File(pFileName);
		final Matrix<String> lMatrix = MatrixFile.readMatrixFromFile(lFile,pHasHeader);
		pMatrix.addAll(lMatrix);
		return lMatrix;		
	}
	
	public static Matrix<String> invoke(final Interpreter env, final CallStack callstack, final String pFileName, final String pSeparator ) throws FileNotFoundException, IOException
	{
		final File lFile = new File(pFileName);
		final Matrix<String> lMatrix = MatrixFile.readMatrixFromFile(lFile,false,pSeparator);
		return lMatrix;		
	}
	
	

}
