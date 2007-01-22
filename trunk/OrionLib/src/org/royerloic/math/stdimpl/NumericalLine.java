/*
 * Created on 25.11.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.math.stdimpl;

import org.royerloic.math.ILine;
import org.royerloic.math.INumericalVector;
import org.royerloic.math.IntersectionInformation;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 *  
 */
public class NumericalLine implements ILine
{
  INumericalVector mFirstPoint;

  INumericalVector mSecondPoint;

  /**
   *  
   */
  public NumericalLine()
  {
    super();
    mFirstPoint = new NumericalVector();
    mSecondPoint = new NumericalVector();
  }

  /**
   *  
   */
  public NumericalLine(final INumericalVector lVector)
  {
    super();
    (mFirstPoint = (INumericalVector) lVector.clone()).toZero();
    mSecondPoint = lVector;
  }

  /**
   * @see org.royerloic.math.ILine#getFirstPoint()
   */
  public INumericalVector getFirstPoint()
  {
    return mFirstPoint;
  }

  /**
   * @see org.royerloic.math.ILine#getSecondPoint()
   */
  public INumericalVector getSecondPoint()
  {
    return mSecondPoint;
  }

  /**
   * @see org.royerloic.math.ILine#setFirstPoint(org.royerloic.math.INumericalVector)
   */
  public void setFirstPoint(final INumericalVector pPoint)
  {
    mFirstPoint = pPoint;
  }

  /**
   * @see org.royerloic.math.ILine#getSecondPoint(org.royerloic.math.INumericalVector)
   */
  public void setSecondPoint(final INumericalVector pPoint)
  {
    mSecondPoint = pPoint;
  }

  public IntersectionInformation intersection(final ILine pLine)
  {
    IntersectionInformation lIntersectionInformation = new IntersectionInformation();
    if (mFirstPoint.getDimension() == 2)
    {
      final double x1 = getFirstPoint().get(0);
      final double y1 = getFirstPoint().get(1);
      final double x2 = getSecondPoint().get(0);
      final double y2 = getSecondPoint().get(1);

      final double x3 = pLine.getFirstPoint().get(0);
      final double y3 = pLine.getFirstPoint().get(1);
      final double x4 = pLine.getSecondPoint().get(0);
      final double y4 = pLine.getSecondPoint().get(1);

      final double det = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
      final double da = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
      final double db = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3);

      if (Math.abs(det) < 0.000001)
      {
        lIntersectionInformation.mParallel = true;
        lIntersectionInformation.mCoincident = (da == 0) && (db == 0);
      }
      else
      {
        lIntersectionInformation.mIntersects = true;
        lIntersectionInformation.mParallel = false;
        lIntersectionInformation.mCoincident = false;

        final double ua = da / det;
        final double ub = db / det;

        lIntersectionInformation.mWithinFirstSegment = ((ua >= 0) && (ua <= 1));
        lIntersectionInformation.mWithinSecondSegment = ((ub >= 0) && (ub <= 1));

        final double x = x1 + ua * (x2 - x1);
        final double y = y1 + ua * (y2 - y1);

        double[] lArray = new double[] { x, y };
        lIntersectionInformation.mIntersectionPoint = new NumericalVector(lArray);
      }

    }

    return lIntersectionInformation;
  };

  /**
   * @see org.royerloic.math.ILine#angleWith(org.royerloic.math.INumericalVector)
   */
  public double angleWith(INumericalVector pVect)
  {
    INumericalVector lVector = (INumericalVector) mFirstPoint.clone();
    lVector.minusEquals(mSecondPoint);
    double lAngle = lVector.angleWith(pVect);

    return lAngle;
  }

  /**
   * @see org.royerloic.math.ILine#euclideanDistanceTo(org.royerloic.math.INumericalVector)
   */
  public double euclideanDistanceTo(INumericalVector pVect)
  {

    return -1;
  }

  /**
   * @see org.royerloic.math.ILine#minus(org.royerloic.math.INumericalVector)
   */
  public INumericalVector minus(INumericalVector pVect)
  {
    mFirstPoint = mFirstPoint.minus(pVect);
    mSecondPoint = mSecondPoint.minus(pVect);
    return null;
  }

  /**
   * @see org.royerloic.math.ILine#minusEquals(org.royerloic.math.INumericalVector)
   */
  public INumericalVector minusEquals(INumericalVector pVect)
  {
    mFirstPoint.minusEquals(pVect);
    mSecondPoint.minusEquals(pVect);
    return null;
  }

  /**
   * @see org.royerloic.math.ILine#plus(org.royerloic.math.INumericalVector)
   */
  public INumericalVector plus(INumericalVector pVect)
  {
    mFirstPoint = mFirstPoint.plus(pVect);
    mSecondPoint = mSecondPoint.plus(pVect);
    return null;
  }

  /**
   * @see org.royerloic.math.ILine#plusEquals(org.royerloic.math.INumericalVector)
   */
  public INumericalVector plusEquals(INumericalVector pVect)
  {
    mFirstPoint.plusEquals(pVect);
    mSecondPoint.plusEquals(pVect);
    return null;
  }

  /**
   * @see org.royerloic.math.ILine#times(double)
   */
  public INumericalVector times(double pScal)
  {
    mFirstPoint = mFirstPoint.times(pScal);
    mSecondPoint = mSecondPoint.times(pScal);
    return null;
  }

  /**
   * @see org.royerloic.math.ILine#timesEquals(double)
   */
  public NumericalVector timesEquals(double pScal)
  {
    mFirstPoint = mFirstPoint.timesEquals(pScal);
    mSecondPoint = mSecondPoint.timesEquals(pScal);
    return null;
  }

  /**
   * @see org.royerloic.math.ILine#getNormalSupportVector()
   */
  public INumericalVector getNormalSupportVector()
  {
    INumericalVector lVector = (INumericalVector) mFirstPoint.clone();
    lVector.minusEquals(mSecondPoint);
    lVector.normalizeEquals();
    return lVector;
  }

  public String toString()
  {
    return "{" + mFirstPoint + ", " + mSecondPoint + "}";
  }

}