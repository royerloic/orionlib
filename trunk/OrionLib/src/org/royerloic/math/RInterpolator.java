/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *   
 */

package org.royerloic.math;

import java.util.Vector;

import org.royerloic.math.stdimpl.NumericalVector;

/**
 * @author MSc. Ing. Loic Royer
 *
 */
public class RInterpolator implements IScalarFunction
{
	private static final double cBASE = 8;
	private static final double cCUT_OFF_DISTANCE = 4;

	private int mInputDimension;

	/**
	 * Class Step is used to package the information
	 * about one point in the interpolation.
	 */
	class Point
	{
		public Point(final INumericalVector pVector, final double pValue)
		{
			this.mVector = pVector;
			this.mValue = pValue;
		}
		public INumericalVector mVector;
		public double mValue;
		public INumericalVector mGradient;
	};

	/** List of Points */
	private Vector mPointList;

	public void setPointList(final Vector pPointList)
	{
		this.mPointList = pPointList;
	}

	private final int getNumberOfPoints()
	{
		return this.mPointList.size();
	}

	private final Point getPoint(final int pIndex)
	{
		return ((Point) this.mPointList.elementAt(pIndex));
	}

	private final INumericalVector getVector(final int pIndex)
	{
		return ((Point) this.mPointList.elementAt(pIndex)).mVector;
	}

	private final double getValue(final int pIndex)
	{
		return ((Point) this.mPointList.elementAt(pIndex)).mValue;
	}

	private final INumericalVector getGradient(final int pIndex)
	{
		return ((Point) this.mPointList.elementAt(pIndex)).mGradient;
	}

	private final void setGradient(final int pIndex, final INumericalVector pGradient)
	{
		((Point) this.mPointList.elementAt(pIndex)).mGradient = pGradient;
	}

	/**
	 * Constructs an RInterpolator given a function <code>pInterpolatedFunction</code> 
	 * implementing the IScalarFunction interface.
	 * @param pInterpolatedFunction function to interpolate.
	 */
	public RInterpolator(final int pInputDimension)
	{
		this.mPointList = new Vector();
		this.mInputDimension = pInputDimension;
	}

	/**
	 * Adds a couple (vector,value) to the interpolator, without performing an update.
	 * @param pVector vector.
	 * @param pValue value.
	 */
	public final void addPointWithoutUpdate(
		final INumericalVector pVector,
		final double pValue)
	{
		final Point lPoint = new Point(pVector, pValue);
		this.mPointList.addElement(lPoint);
	}

	/**
	 * Adds a couple (vector,value) to the interpolator.
	 * @param pVector vector.
	 * @param pValue value.
	 */
	public final void addPoint(final INumericalVector pVector, final double pValue)
	{
		final Point lPoint = new Point(pVector, pValue);
		this.mPointList.addElement(lPoint);
		update();
	}

	/**
	 * Updates this interpolator.
	 * Typically after adding a point.
	 */
	public final void update()
	{
		updateGradients();
	}

	/**
	 * Updates the gradient of each point.
	 */
	private final void updateGradients()
	{
		for (int i = 0; i < getNumberOfPoints(); i++)
			setGradient(i, computeGradientOf(i));
	}

	/**
	 * Computes the gradient of the point of index <code>pIndex</code>.
	 * @param pIndex index of point from which we want to compute the gradient.
	 * @return gradient vector.
	 */
	private final INumericalVector computeGradientOf(final int pIndex)
	{
		final INumericalVector lVector = getVector(pIndex);
		final INumericalVector lGradientVector = new NumericalVector(lVector.getDimension());

		final double lLowestDistance = lVector.euclideanDistanceTo(getClosestPointExcludingMyself(lVector).mVector);

		double lSumCoeficients = 0;
		for (int i = 0; i < getNumberOfPoints(); i++)
		{
			final INumericalVector lCurrentVector = getVector(i);
			final double lDistance = lCurrentVector.euclideanDistanceTo(lVector);
			if (lDistance != 0)
			{
				final double lNormalizedDistance = lDistance / lLowestDistance;
				if (lNormalizedDistance < cCUT_OFF_DISTANCE)
				{
					final double lCoeficient = Math.pow(cBASE, 1 - lNormalizedDistance);
					lSumCoeficients += lCoeficient;
					final double lAlpha =
						(lCoeficient * (getValue(i) - getValue(pIndex)))
							/ (lDistance * lDistance);
					final INumericalVector lGradientContribution = lCurrentVector.minus(lVector);
					lGradientContribution.timesEquals(lAlpha);

					lGradientVector.plusEquals(lGradientContribution);
				}
			}
		}

		lGradientVector.timesEquals(1.0 / lSumCoeficients);

		return lGradientVector;
	}

	/**
	 * @param pVector
	 * @return
	 */
	private final double getDistanceToClosestPoint(final INumericalVector pVector)
	{
		return pVector.euclideanDistanceTo(getClosestPoint(pVector).mVector);
	}

	/**
	 * @param pVector
	 * @return
	 */
	private final Point getClosestPoint(final INumericalVector pVector)
	{
		Point lClosestPoint = getPoint(0);

		double lLowestDistance = Double.POSITIVE_INFINITY;
		for (int i = 0; i < getNumberOfPoints(); i++)
		{
			final double lDistance = pVector.euclideanDistanceTo(getVector(i));
			if (lDistance < lLowestDistance)
			{
				lLowestDistance = lDistance;
				lClosestPoint = getPoint(i);
			}
		}

		return lClosestPoint;
	}

	/**
	 * @param pVector
	 * @return
	 */
	private final Point getClosestPointExcludingMyself(final INumericalVector pVector)
	{
		Point lClosestPoint = getPoint(0);

		double lLowestDistance = Double.POSITIVE_INFINITY;
		for (int i = 0; i < getNumberOfPoints(); i++)
		{
			final double lDistance = pVector.euclideanDistanceTo(getVector(i));
			if ((lDistance < lLowestDistance) && (lDistance != 0))
			{
				lLowestDistance = lDistance;
				lClosestPoint = getPoint(i);
			}
		}

		return lClosestPoint;
	}

	/** (non-Javadoc)
	 * @see de.fhg.iwu.utils.math.IScalarFunction#evaluate(de.fhg.iwu.utils.math.MVector)
	 */
	public double evaluate(final INumericalVector pVector)
	{
		if (getNumberOfPoints() == 0)
			return 0;

		final double lLowestDistance = getDistanceToClosestPoint(pVector);
		final Point lClosestPoint = getClosestPoint(pVector);

		if (lLowestDistance == 0)
			return lClosestPoint.mValue;
		else
		{
			double lValue = 0;

			double lSumCoeficients = 0;

			for (int i = 0; i < getNumberOfPoints(); i++)
			{
				final INumericalVector lCurrentVector = getVector(i);
				final double lDistance = lCurrentVector.euclideanDistanceTo(pVector);
				final double lNormalizedDistance = lDistance / lLowestDistance;

				if (lNormalizedDistance < cCUT_OFF_DISTANCE)
				{
					final double lCoeficient = Math.pow(cBASE, 1 - lNormalizedDistance);
					lSumCoeficients += lCoeficient;
					final INumericalVector lPointVector = pVector.minus(lCurrentVector);
					double lValueContribution =	getValue(i) + lPointVector.times(getGradient(i));
					lValueContribution *= lCoeficient;
					lValue += lValueContribution;
				}

			}

			lValue = lValue / lSumCoeficients;
			return lValue;
		}

	}

	/**
	 * @see de.fhg.iwu.utils.math.IScalarFunction#computePoints(int)
	 */
	public double[][] computePoints(final int pResolution)
	{
	  throw new RuntimeException(""); 
	  
	}

	/**
	 * @see de.fhg.iwu.utils.math.IFunction#getInputDimension()
	 */
	public int getInputDimension()
	{
		return this.mInputDimension;
	}

	/**
	 * @see de.fhg.iwu.utils.math.IFunction#getOutputDimension()
	 */
	public int getOutputDimension()
	{
		return 1;
	}

	/**
	 * @see de.fhg.iwu.utils.math.IFunction#getInputMin(int)
	 */
	public double getInputMin(final int pIndex)
	{
		return 0;
	}

	/**
	 * @see de.fhg.iwu.utils.math.IFunction#getInputMax(int)
	 */
	public double getInputMax(final int pIndex)
	{
		return 1;
	}

	/**
	 * @see de.fhg.iwu.utils.math.IFunction#getInputDelta(int)
	 */
	public double getInputDelta(final int pIndex)
	{
		return 0.00001;
	}

	/**
	 * @see de.fhg.iwu.utils.math.IFunction#normalizeInputVector(de.fhg.iwu.utils.math.MVector)
	 */
	public void normalizeInputVector(final INumericalVector pVector)
	{
		
	}

	/** 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		final RInterpolator lClonedInterpolator = new RInterpolator(this.mInputDimension);
		final Vector lClonedPointList = (Vector) this.mPointList.clone();
		lClonedInterpolator.setPointList(lClonedPointList);
		lClonedInterpolator.update();
		return lClonedInterpolator;
	}

	/** 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object pInterpolator)
	{
		return super.equals(pInterpolator);
	}

	/** 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return super.toString();
	}

	/** 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		// TODO Auto-generated method stub
		return super.hashCode();
	}

}
