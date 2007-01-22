/*
 * Created on 19.10.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package org.royerloic.math;

/**
 * @author royer
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public interface IMatrixArray
{
  /**
   * Access the internal two-dimensional array.
   * 
   * @return Pointer to the two-dimensional array of matrix elements.
   */
  public abstract double[][] getArray();

  /**
   * Copy the internal two-dimensional array.
   * 
   * @return Two-dimensional array copy of matrix elements.
   */
  public abstract double[][] getArrayCopy();

  /**
   * Make a one-dimensional column packed copy of the internal array.
   * 
   * @return MatrixAlgebraic elements packed in a one-dimensional array by
   *         columns.
   */
  public abstract double[] getColumnPackedCopy();

  /**
   * Make a one-dimensional row packed copy of the internal array.
   * 
   * @return MatrixAlgebraic elements packed in a one-dimensional array by rows.
   */
  public abstract double[] getRowPackedCopy();

  /**
   * Get row dimension.
   * 
   * @return m, the number of rows.
   */
  public abstract int getRowDimension();

  /**
   * Get column dimension.
   * 
   * @return n, the number of columns.
   */
  public abstract int getColumnDimension();

  /**
   * Get a single element.
   * 
   * @param i
   *          Row index.
   * @param j
   *          Column index.
   * @return A(i,j)
   * @exception ArrayIndexOutOfBoundsException
   */
  public abstract double get(int i, int j);

  /**
   * Set a single element.
   * 
   * @param i
   *          Row index.
   * @param j
   *          Column index.
   * @param s
   *          A(i,j).
   * @exception ArrayIndexOutOfBoundsException
   */
  public abstract void set(int i, int j, double s);
}