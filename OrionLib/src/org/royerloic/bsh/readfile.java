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
	
	public static Matrix invoke(Interpreter env, CallStack callstack, String pFileName) throws FileNotFoundException, IOException
	{
		File lFile = new File(pFileName);
		Matrix lMatrix = MatrixFile.readMatrixFromFile(lFile);
		return lMatrix;		
	}
	
	public static Matrix invoke(Interpreter env, CallStack callstack, String pFileName, boolean pHasHeader) throws FileNotFoundException, IOException
	{
		File lFile = new File(pFileName);
		Matrix lMatrix = MatrixFile.readMatrixFromFile(lFile,pHasHeader);
		return lMatrix;		
	}
	
	public static Matrix invoke(Interpreter env, CallStack callstack, String pFileName, String pSeparator ) throws FileNotFoundException, IOException
	{
		File lFile = new File(pFileName);
		Matrix lMatrix = MatrixFile.readMatrixFromFile(lFile,false,pSeparator);
		return lMatrix;		
	}

}
