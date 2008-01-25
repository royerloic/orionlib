/*
 * Created on 19.10.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package utils.math;

/**
 * @author royer
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public interface IVectorArray
{
	/**
	 * @return
	 */
	public int getDimension();

	/**
	 * @param pIndex
	 * @param pValue
	 */
	public void set(final int pIndex, final double pValue);

	/**
	 * @param pIndex
	 * @return
	 */
	public double get(final int pIndex);

}