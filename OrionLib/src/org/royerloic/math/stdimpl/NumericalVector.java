package org.royerloic.math.stdimpl;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.royerloic.math.INumericalVector;
import org.royerloic.math.IVectorArray;

/**
 * @author MSc. Ing. Loic Royer
 *  
 */

public class NumericalVector extends Object implements IVectorArray,
    INumericalVector
{

  /**
   * Comment for <code>mCoordinatesList</code>
   */
  private ArrayList mCoordinatesList;

  /**
   * @param pDimension
   */
  void allocate(final int pDimension)
  {
    if (pDimension == 0)
			mCoordinatesList = new ArrayList();
		else
    {
      mCoordinatesList = new ArrayList(pDimension);
      for (int i = 0; i < pDimension; i++)
				addCoordinate(0.0);
    }
  }

  /**
   *  
   */
  public NumericalVector()
  {
    allocate(0);
  }

  /**
   * @param pDimension
   */
  public NumericalVector(final int pDimension)
  {
    allocate(pDimension);
  }

  /**
   * @param pArray
   */
  public NumericalVector(final double[] pArray)
  {
    allocate(pArray.length);
    for (int i = 0; i < getDimension(); i++)
			set(i, pArray[i]);
  }

  /**
   * @return
   */
  public final int getDimension()
  {
    return mCoordinatesList.size();
  }

  /**
   * @param pIndex
   * @param pValue
   */
  public final void set(final int pIndex, final double pValue)
  {
    mCoordinatesList.set(pIndex, new Double(pValue));
  }

  /**
   * @param pValue
   */
  public final void addCoordinate(final double pValue)
  {
    mCoordinatesList.add(new Double(pValue));
  }

  /**
   * @see org.royerloic.math.IVectorArray#get(int)
   */
  public final double get(final int pIndex)
  {
    final double lCoordinate;
    if (pIndex > (getDimension() - 1))
			lCoordinate = 0;
		else
			lCoordinate = ((Double) mCoordinatesList.get(pIndex)).doubleValue();
    return lCoordinate;
  }

  /**
   * @see org.royerloic.math.INumericalVector#toZero()
   */
  public void toZero()
  {
    for (int i = 0; i < this.getDimension(); i++)
			this.set(i, 0);
  }

  /**
   * @return
   */
  public final double euclideanNorm()
  {
    double lNorm = 0;
    for (int i = 0; i < this.getDimension(); i++)
    {
      final double lCoordinate = this.get(i);
      lNorm += lCoordinate * lCoordinate;
    }
    lNorm = Math.sqrt(lNorm);
    return lNorm;
  }

  /**
   * @param pVect
   * @return
   */
  public final double euclideanDistanceTo(final INumericalVector pVect)
  {
    final int lMaxDimension = Math.max(pVect.getDimension(), this
        .getDimension());

    double lSum = 0;
    for (int i = 0; i < lMaxDimension; i++)
    {
      final double lDiff = this.get(i) - pVect.get(i);
      lSum += lDiff * lDiff;
    }
    lSum = Math.sqrt(lSum);

    return lSum;
  }

  /**
   * @see org.royerloic.math.INumericalVector#normalizeEquals()
   */
  public void normalizeEquals()
  {
    final double lNorm = this.euclideanNorm();
    if (lNorm != 0)
			timesEquals(1 / lNorm);
  }

  /**
   * @param pVect
   * @return
   */
  public final NumericalVector plus(final INumericalVector pVect)
  {
    final int lMaxDimension = Math.max(pVect.getDimension(), this
        .getDimension());

    final NumericalVector lSum = new NumericalVector(lMaxDimension);
    for (int i = 0; i < pVect.getDimension(); i++)
			lSum.set(i, pVect.get(i) + this.get(i));
    return lSum;
  }

  /**
   * @param pVect
   * @return
   */
  public final NumericalVector plusEquals(final INumericalVector pVect)
  {
    final int lMaxDimension = Math.max(pVect.getDimension(), this
        .getDimension());

    for (int i = 0; i < lMaxDimension; i++)
			this.set(i, pVect.get(i) + this.get(i));

    return this;
  }

  /**
   * @param pVect
   * @return
   */
  public final NumericalVector minus(final INumericalVector pVect)
  {
    return this.plus(pVect.times(-1));
  }

  /**
   * @param pVect
   * @return
   */
  public final NumericalVector minusEquals(final INumericalVector pVect)
  {
    return this.plusEquals(pVect.times(-1));
  }

  /**
   * @param pScal
   * @return
   */
  public final NumericalVector times(final double pScal)
  {
    final int lSize = this.getDimension();
    final NumericalVector lResult = new NumericalVector(lSize);

    for (int i = 0; i < lSize; i++)
			lResult.set(i, pScal * this.get(i));

    return lResult;
  }

  /**
   * @param pScal
   * @return
   */
  public final NumericalVector timesEquals(final double pScal)
  {
    for (int i = 0; i < this.getDimension(); i++)
			this.set(i, pScal * this.get(i));

    return this;
  }

  /**
   * @param pVect
   * @return
   */
  public final double times(final INumericalVector pVect)
  {
    double lResult = 0;
    for (int i = 0; i < pVect.getDimension(); i++)
			lResult += this.get(i) * pVect.get(i);
    return lResult;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
	public final boolean equals(final Object pObject)
  {
    boolean lResult = false;

    if (pObject instanceof NumericalVector)
    {
      final NumericalVector lVector = (NumericalVector) pObject;
      final int lNumberOfCoordinates = getDimension();
      final int lNumberOfCoordinatesOther = lVector.getDimension();

      if (lNumberOfCoordinates == lNumberOfCoordinatesOther)
      {
        lResult = true;

        for (int i = 0; (i < lNumberOfCoordinates) && lResult; i++)
					lResult = lResult && (get(i) == lVector.get(i));
      }
    }

    return lResult;
  }

  /**
   * @see java.lang.Object#clone()
   */
  @Override
	public final Object clone()
  {
    final NumericalVector lVector = new NumericalVector(this.getDimension());
    for (int i = 0; i < this.getDimension(); i++)
			lVector.set(i, this.get(i));
    return lVector;
  }

  public final void copyTo(final INumericalVector pVector)
  {
    for (int i = 0; i < this.getDimension(); i++)
			this.set(i, pVector.get(i));
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
	public int hashCode()
  {
    return mCoordinatesList.hashCode();
  }

  /**
   * @param pString
   * @return
   */
  public boolean parse(final String pString)
  {
    allocate(0);
    boolean lCorrect = true;
	
    final StringTokenizer lStringTokenizer = new StringTokenizer(pString, " \t(),");
    while (lStringTokenizer.hasMoreTokens())
			try
      {
		final String lString = lStringTokenizer.nextToken();
        final double lValue = stringToDouble(lString);
        addCoordinate(lValue);
      }
      catch (final Throwable e)
      {
        lCorrect = false;
      }

    return lCorrect;
  }

  /**
   * @see org.royerloic.math.INumericalVector#angleWith(org.royerloic.math.stdimpl.NumericalVector)
   */
  public double angleWith(final INumericalVector pVect)
  {
    final double lDotProduct = this.times(pVect);
    final double lNormThis = this.euclideanNorm();
    final double lNormParam = pVect.euclideanNorm();
    final double lAngle = Math.acos(lDotProduct / (lNormThis * lNormParam));
    return lAngle;
  }

  /**
   * @see org.royerloic.java.IObject#copyFrom(java.lang.Object)
   */
  public void copyFrom(final Object pObject)
  {
    // TODO Auto-generated method stub

  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
	public final String toString()
  {
    String lString = "";
    if (getDimension() != 0)
    {
      lString = "(" + get(0);

      for (int i = 1; i < getDimension(); i++)
				lString = lString + ",\t" + get(i);

      lString = lString + ")";
    }
		else
			lString = "Empty";
    return lString;
  }

  /**
   * @param pString
   * @return
   */
  private final double stringToDouble(final String pString)
  {
    return Double.parseDouble(pString);
  }

  public boolean StringToObject(final String pString)
  {
    return parse(pString);
  }

}

