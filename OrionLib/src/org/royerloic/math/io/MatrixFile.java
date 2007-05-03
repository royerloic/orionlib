/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package org.royerloic.math.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import org.royerloic.math.stdimpl.NumericalVector;

/**
 * @author MSc. Ing. Loic Royer
 *  
 */
public class MatrixFile
{

  private Vector mMVectorList;

  private void allocate()
  {
    mMVectorList = new Vector();
  }

  private final void addVector(final NumericalVector pMVector)
  {
    mMVectorList.add(pMVector);
  }

  public final NumericalVector getVector(final int pIndex)
  {
    return ((NumericalVector) mMVectorList.elementAt(pIndex));
  }

  public final int size()
  {
    return mMVectorList.size();
  }

  public MatrixFile(final File pFile)
  {
    allocate();

    try
    {

      final File lInputFile = pFile;

      FileReader lFileReader;
      BufferedReader lBufferedReader;
      try
      {
        lFileReader = new FileReader(lInputFile);
        lBufferedReader = new BufferedReader(lFileReader);
      }
      catch (final FileNotFoundException e)
      {
        System.out.println("File: " + lInputFile + " not found.");
        throw e;
      }

      try
      {
        String lLineString;
        while ((lLineString = lBufferedReader.readLine()) != null)
        {
          final NumericalVector lMVector = new NumericalVector();
          final boolean lCorrectMVector = lMVector.parse(lLineString);
          addVector(lMVector);
        }

      }
      catch (final IOException e2)
      {
        System.out.println("Error while reading: " + e2.getCause());
      }
      finally
      {
        lFileReader.close();
      }
    }
    catch (final Exception any)
    {
      any.printStackTrace(System.out);

    }
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
	public String toString()
  {
    String lString = "";
    for (int i = 0; i < size(); i++)
			lString += getVector(i) + "\n";
    return lString;
  }
}