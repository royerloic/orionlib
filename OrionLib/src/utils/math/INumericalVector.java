/*
 * Created on 08.11.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.math;

import utils.java.IObject;
import utils.math.stdimpl.NumericalVector;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 *  
 */
public interface INumericalVector extends IObject, IVectorArray
{
  /**
   * @return
   */
  public abstract int getDimension();

  /**
   * @param pIndex
   * @param pValue
   */
  public abstract void set(final int pIndex, final double pValue);

  /**
   * @param pValue
   */
  public abstract void addCoordinate(final double pValue);

  /**
   * @see utils.math.IVectorArray#get(int)
   */
  public abstract double get(final int pIndex);

  public abstract void toZero();

  /**
   * @return
   */
  public abstract double euclideanNorm();

  /**
   * @param pVect
   * @return
   */
  public abstract double euclideanDistanceTo(final INumericalVector pVect);

  public abstract void normalizeEquals();

  /**
   * @param pVect
   * @return
   */
  public abstract NumericalVector plus(final INumericalVector pVect);

  /**
   * @param pVect
   * @return
   */
  public abstract NumericalVector plusEquals(final INumericalVector pVect);

 
  /**
   * @param pVect
   * @return
   */
  public abstract NumericalVector minus(final INumericalVector pVect);

  /**
   * @param pVect
   * @return
   */
  public abstract NumericalVector minusEquals(final INumericalVector pVect);

  /**
   * @param pScal
   * @return
   */
  public abstract NumericalVector times(final double pScal);

  /**
   * @param pScal
   * @return
   */
  public abstract NumericalVector timesEquals(final double pScal);

  /**
   * @param pVect
   * @return
   */
  public abstract double times(final INumericalVector pVect);

  public abstract double angleWith(final INumericalVector pVect);
  
}