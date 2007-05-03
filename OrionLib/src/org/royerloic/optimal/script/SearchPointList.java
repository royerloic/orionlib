/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *   
 */

package org.royerloic.optimal.script;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public class SearchPointList
{
	public interface PointPattern
	{
		boolean conditionOnPointBefore(GeometryFile.Point pPoint);

		boolean conditionOnPointCurrent(GeometryFile.Point pPoint);

		boolean conditionOnPointAfter(GeometryFile.Point pPoint);
	}

	private GeometryFile.PointList	mGeometryObject;

	private boolean									mPositiveDirection	= true;

	GeometryFile.PointList getPointList()
	{
		return mGeometryObject;
	}

	/**
	 * 
	 */
	public SearchPointList(final GeometryFile.PointList pGeometryObject)
	{
		super();
		mGeometryObject = pGeometryObject;
	}

	public void positiveDirection()
	{
		mPositiveDirection = true;
	}

	public void negativeDirection()
	{
		mPositiveDirection = false;
	}

	public int doSearch(final int pIndex, final PointPattern pPointPattern)
	{
		final SearchPointList.PointPattern lPointPattern = pPointPattern;

		// System.out.println(lPointPattern);

		// System.out.println("doSearch");
		int lIndex = pIndex;
		int lFoundIndex = -1;
		final int lNumberOfPoints = mGeometryObject.size();
		boolean lFound = false;

		for (int i = 0; i < lNumberOfPoints; i++)
		{
			System.out.println("searching at i= " + i);
			final int lIndexBefore = mGeometryObject.indexBefore(lIndex);
			final int lIndexAfter = mGeometryObject.indexAfter(lIndex);

			// System.out.println("lIndexBefore= "+lIndexBefore);
			// System.out.println("lIndexAfter= "+lIndexAfter);

			final GeometryFile.Point lPointBefore = mGeometryObject.getPointAt(lIndexBefore);
			final GeometryFile.Point lPointCurrent = mGeometryObject.getPointAt(lIndex);
			final GeometryFile.Point lPointAfter = mGeometryObject.getPointAt(lIndexAfter);

			// System.out.println("lPointBefore= "+lPointBefore);
			// System.out.println("lPointCurrent= "+lPointCurrent);
			// System.out.println("lPointAfter= "+lPointAfter);

			lFound = lPointPattern.conditionOnPointBefore(lPointBefore)
					&& lPointPattern.conditionOnPointCurrent(lPointCurrent)
					&& lPointPattern.conditionOnPointAfter(lPointAfter);

			// System.out.println("lFound= "+lFound);

			if (lFound)
			{
				lFoundIndex = lIndex;
				i = lNumberOfPoints;
				break;
			}

			if (mPositiveDirection)
				lIndex = mGeometryObject.indexAfter(lIndex);
			else
				lIndex = mGeometryObject.indexBefore(lIndex);
		}

		return lFoundIndex;
	}

	public static final boolean isEqualTol(final double pVal1, final double pVal2, final double pTol)
	{
		if (Math.abs(pVal1 - pVal2) < pTol)
			return true;
		else
			return false;
	}

	public static final boolean isGreaterTol(final double pVal1, final double pVal2, final double pTol)
	{
		if (pVal1 > pVal2 + pTol)
			return true;
		else
			return false;
	}

}
