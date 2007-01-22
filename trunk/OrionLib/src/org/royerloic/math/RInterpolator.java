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
			mVector = pVector;
			mValue = pValue;
		}
		public INumericalVector mVector;
		public double mValue;
		public INumericalVector mGradient;
	};

	/** List of Points */
	private Vector mPointList;

	public void setPointList(final Vector pPointList)
	{
		mPointList = pPointList;
	}

	private final int getNumberOfPoints()
	{
		return mPointList.size();
	}

	private final Point getPoint(final int pIndex)
	{
		return ((Point) mPointList.elementAt(pIndex));
	}

	private final INumericalVector getVector(final int pIndex)
	{
		return ((Point) mPointList.elementAt(pIndex)).mVector;
	}

	private final double getValue(final int pIndex)
	{
		return ((Point) mPointList.elementAt(pIndex)).mValue;
	}

	private final INumericalVector getGradient(final int pIndex)
	{
		return ((Point) mPointList.elementAt(pIndex)).mGradient;
	}

	private final void setVector(final int pIndex, final INumericalVector pVector)
	{
		((Point) mPointList.elementAt(pIndex)).mVector = pVector;
	}

	private final void setValue(final int pIndex, final double pValue)
	{
		((Point) mPointList.elementAt(pIndex)).mValue = pValue;
	}

	private final void setGradient(final int pIndex, final INumericalVector pGradient)
	{
		((Point) mPointList.elementAt(pIndex)).mGradient = pGradient;
	}

	/**
	 * Constructs an RInterpolator given a function <code>pInterpolatedFunction</code> 
	 * implementing the IScalarFunction interface.
	 * @param pInterpolatedFunction function to interpolate.
	 */
	public RInterpolator(final int pInputDimension)
	{
		mPointList = new Vector();
		mInputDimension = pInputDimension;
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
		Point lPoint = new Point(pVector, pValue);
		mPointList.addElement(lPoint);
	}

	/**
	 * Adds a couple (vector,value) to the interpolator.
	 * @param pVector vector.
	 * @param pValue value.
	 */
	public final void addPoint(final INumericalVector pVector, final double pValue)
	{
		Point lPoint = new Point(pVector, pValue);
		mPointList.addElement(lPoint);
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
		{
			setGradient(i, computeGradientOf(i));
		}
	}

	/**
	 * Computes the gradient of the point of index <code>pIndex</code>.
	 * @param pIndex index of point from which we want to compute the gradient.
	 * @return gradient vector.
	 */
	private final INumericalVector computeGradientOf(final int pIndex)
	{
		INumericalVector lVector = getVector(pIndex);
		INumericalVector lGradientVector = new NumericalVector(lVector.getDimension());

		double lLowestDistance = lVector.euclideanDistanceTo(getClosestPointExcludingMyself(lVector).mVector);

		double lSumCoeficients = 0;
		for (int i = 0; i < getNumberOfPoints(); i++)
		{
			INumericalVector lCurrentVector = getVector(i);
			double lDistance = lCurrentVector.euclideanDistanceTo(lVector);
			if (lDistance != 0)
			{
				double lNormalizedDistance = lDistance / lLowestDistance;
				if (lNormalizedDistance < cCUT_OFF_DISTANCE)
				{
					double lCoeficient = Math.pow(cBASE, 1 - lNormalizedDistance);
					lSumCoeficients += lCoeficient;
					double lAlpha =
						(lCoeficient * (getValue(i) - getValue(pIndex)))
							/ (lDistance * lDistance);
					INumericalVector lGradientContribution = lCurrentVector.minus(lVector);
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
			double lDistance = pVector.euclideanDistanceTo(getVector(i));
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
			double lDistance = pVector.euclideanDistanceTo(getVector(i));
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
		{
			return 0;
		}

		double lLowestDistance = getDistanceToClosestPoint(pVector);
		Point lClosestPoint = getClosestPoint(pVector);

		if (lLowestDistance == 0)
		{
			return lClosestPoint.mValue;
		}
		else
		{
			double lValue = 0;

			double lSumCoeficients = 0;

			for (int i = 0; i < getNumberOfPoints(); i++)
			{
				INumericalVector lCurrentVector = getVector(i);
				double lDistance = lCurrentVector.euclideanDistanceTo(pVector);
				double lNormalizedDistance = lDistance / lLowestDistance;

				if (lNormalizedDistance < cCUT_OFF_DISTANCE)
				{
					double lCoeficient = Math.pow(cBASE, 1 - lNormalizedDistance);
					lSumCoeficients += lCoeficient;
					INumericalVector lPointVector = pVector.minus(lCurrentVector);
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
		return mInputDimension;
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
	public Object clone() throws CloneNotSupportedException
	{
		RInterpolator lClonedInterpolator = new RInterpolator(mInputDimension);
		Vector lClonedPointList = (Vector) mPointList.clone();
		lClonedInterpolator.setPointList(lClonedPointList);
		lClonedInterpolator.update();
		return lClonedInterpolator;
	}

	/** 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object pInterpolator)
	{
		return super.equals(pInterpolator);
	}

	/** 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		// TODO Auto-generated method stub
		return super.toString();
	}

	/** 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		// TODO Auto-generated method stub
		return super.hashCode();
	}

}
