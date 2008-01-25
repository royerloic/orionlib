/*
 * Created on 25.11.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.math;

import utils.math.stdimpl.NumericalVector;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public interface ILine
{
	/**
	 * @return
	 */
	public INumericalVector getFirstPoint();

	/**
	 * @return
	 */
	public INumericalVector getSecondPoint();

	/**
	 * @param pPoint
	 */
	public void setFirstPoint(final INumericalVector pPoint);

	/**
	 * @param pPoint
	 */
	public void setSecondPoint(final INumericalVector pPoint);

	/**
	 * @param pLine
	 * @return
	 */
	public IntersectionInformation intersection(final ILine pLine);

	/**
	 * @param pVect
	 * @return
	 */
	public abstract double angleWith(final INumericalVector pVect);

	/**
	 * @param pVect
	 * @return
	 */
	public abstract double euclideanDistanceTo(final INumericalVector pVect);

	/**
	 * @param pVect
	 * @return
	 */
	public abstract INumericalVector minus(final INumericalVector pVect);

	/**
	 * @param pVect
	 * @return
	 */
	public abstract INumericalVector minusEquals(final INumericalVector pVect);

	/**
	 * @param pVect
	 * @return
	 */
	public abstract INumericalVector plus(final INumericalVector pVect);

	/**
	 * @param pVect
	 * @return
	 */
	public abstract INumericalVector plusEquals(final INumericalVector pVect);

	/**
	 * @param pScal
	 * @return
	 */
	public abstract INumericalVector times(final double pScal);

	/**
	 * @param pScal
	 * @return
	 */
	public abstract NumericalVector timesEquals(final double pScal);

	/**
	 * @return
	 */
	public abstract INumericalVector getNormalSupportVector();
}