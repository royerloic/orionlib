/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *   
 */

package utils.optimal.script;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public final class ClinchingAnalysis
{

	private GeometryFile						lGeometryFile;
	private GeometryFile.PointList	lTopSheet, lBottomSheet;
	private SearchPointList					lSearchTopSheet, lSearchBottomSheet;

	private final double									mTol					= 0.001;
	private final double									mSearchLimit	= 7.0;

	private int											mIndexP01;
	private GeometryFile.Point			mP01;

	private int											mIndexPA1;
	private GeometryFile.Point			mPA1;

	private int											mIndexPB1;
	private GeometryFile.Point			mPB1;

	private int											mIndexPAp;
	private GeometryFile.Point			mPAp;

	private int											mIndexP02;
	private GeometryFile.Point			mP02;

	private int											mIndexPA2;
	private GeometryFile.Point			mPA2;

	private int											mIndexPB2;
	private GeometryFile.Point			mPB2;

	private int											mIndexPC2;
	private GeometryFile.Point			mPC2;

	private double									mPreB1;
	private double									mTn;
	private double									mF;
	private double									mB1;

	/**
	 * @return
	 */
	public double getTn()
	{
		return mTn;
	}

	/**
	 * @return
	 */
	public double getF()
	{
		return mF;
	}

	/**
	 * @return
	 */
	public double getB1()
	{
		return mB1;
	}

	/**
	 * @param pGeometryFileName
	 */
	public ClinchingAnalysis(final String pGeometryFileName)
	{
		try
		{
			lGeometryFile = new GeometryFile(pGeometryFileName);

			lTopSheet = (GeometryFile.PointList) lGeometryFile.getGeometryObjectAt(0);
			lBottomSheet = (GeometryFile.PointList) lGeometryFile.getGeometryObjectAt(1);

			lSearchTopSheet = new SearchPointList(lTopSheet);
			lSearchBottomSheet = new SearchPointList(lBottomSheet);

			final PointPatternP0 lPointPatternP0 = new PointPatternP0();

			final boolean lFoundP01 = (mP01 = searchPoint(lSearchTopSheet, "P01", 0, lPointPatternP0)) != null;
			mIndexP01 = (int) (mP01.getCoordinate(0)) - 1;

			final PatternExtremumX lPatternPB1 = new PatternExtremumX("maximum", "forwards");

			lSearchTopSheet.positiveDirection();
			final boolean lFoundPB1 = (mPB1 = searchPoint(lSearchTopSheet, "PB1", mIndexP01, lPatternPB1)) != null;
			mIndexPB1 = (int) mPB1.getCoordinate(0) - 1;

			final PatternExtremumX lPatternPA1 = new PatternExtremumX("minimum", "forwards");

			final boolean lFoundPA1 = (mPA1 = searchPoint(lSearchTopSheet, "PA1", mIndexPB1, lPatternPA1)) != null;

			mIndexPA1 = (int) mPA1.getCoordinate(0) - 1;

			lSearchTopSheet.negativeDirection();

			final PatternPAp lPatternPAp = new PatternPAp();

			final boolean lFoundPAp = (mPAp = searchPoint(lSearchTopSheet, "PAp", mIndexP01, lPatternPAp)) != null;

			mIndexPAp = (int) mPAp.getCoordinate(0) - 1;

			lSearchBottomSheet.negativeDirection();

			final boolean lFoundP02 = (mP02 = searchPoint(lSearchBottomSheet, "P02", 0, lPointPatternP0)) != null;

			mIndexP02 = (int) mP02.getCoordinate(0) - 1;

			final PatternExtremumX lPatternPB2 = new PatternExtremumX("maximum", "backwards");

			final boolean lFoundPB2 = (mPB2 = searchPoint(lSearchBottomSheet, "PB2", mIndexP02, lPatternPB2)) != null;

			mIndexPB2 = (int) mPB2.getCoordinate(0) - 1;

			final PatternExtremumX lPatternPA2 = new PatternExtremumX("minimum", "backwards");

			final boolean lFoundPA2 = (mPA2 = searchPoint(lSearchBottomSheet, "PA2", mIndexPB2, lPatternPA2)) != null;

			mIndexPA2 = (int) mPA2.getCoordinate(0) - 1;

			final boolean lFoundPC2 = (mPC2 = searchPoint(lSearchTopSheet, "PC2", mIndexP01, mPatternPC2)) != null;

			mIndexPC2 = (int) mPC2.getCoordinate(0) - 1;

			if (lFoundPA1)
				mTn = lPatternPAp.mDistanceMinimum;
			else
				mTn = 0;
			print("mTn = " + mTn);

			if (lFoundPA2 && lFoundPB2)
				mF = Math.abs(mPB1.getCoordinate(1) - mPA2.getCoordinate(1));
			else
				mF = 0;
			print("mF = " + mF);

			mB1 = mPreB1 - 19.5;
			print("mPreB1 = " + mPreB1);
			print("mB1 = " + mB1);
		}
		catch (final RuntimeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace(System.out);
			mTn = 0;
			mF = 0;
			mB1 = 0;
		}
	}

	/**
	 * @param pSearchPointList
	 * @param pName
	 * @param pStartIndex
	 * @param pPattern
	 * @param pPointIndex
	 * @param pPoint
	 * @return
	 */
	private GeometryFile.Point searchPoint(	final SearchPointList pSearchPointList,
																					final String pName,
																					final int pStartIndex,
																					final SearchPointList.PointPattern pPattern)
	{
		int lPointIndex;
		print("Search " + pName + " :");
		lPointIndex = pSearchPointList.doSearch(pStartIndex, pPattern);
		if (lPointIndex > 0)
		{
			print(pName + " found, lIndex" + pName + " = " + lPointIndex);
			final GeometryFile.Point pPoint = pSearchPointList.getPointList().getPointAt(lPointIndex);
			print("Step" + pName + " = " + pPoint);
			return pPoint;
		}
		else
		{
			print(pName + " not found.");
			return null;
		}
	}

	/**
	 * 
	 */
	private class PatternExtremumX implements SearchPointList.PointPattern
	{
		private double	mOptimumX;
		private boolean	mOutOfDomain	= false;
		private boolean	mMode;
		private boolean	mDirection;

		public PatternExtremumX(final String pMode, final String pDirection)
		{
			if (pMode.equals("maximum"))
			{
				mMode = true;
				mOptimumX = Double.NEGATIVE_INFINITY;
			}
			else if (pMode.equals("minimum"))
			{
				mMode = false;
				mOptimumX = Double.POSITIVE_INFINITY;
			}

			if (pDirection.equals("forwards"))
				mDirection = true;
			else if (pDirection.equals("backwards"))
				mDirection = false;
		}

		public boolean conditionOnPointBefore(final GeometryFile.Point pPoint)
		{
			if (!mDirection)
				return conditionOnPoint(pPoint);
			else
				return true;
		};

		public boolean conditionOnPointCurrent(final GeometryFile.Point pPoint)
		{
			return true;
		};

		public boolean conditionOnPointAfter(final GeometryFile.Point pPoint)
		{
			if (mDirection)
				return conditionOnPoint(pPoint);
			else
				return true;
		}

		private boolean conditionOnPoint(final GeometryFile.Point pPoint)
		{
			final double lCurrentX = pPoint.getCoordinate(1);

			if (lCurrentX > mSearchLimit)
			{
				mOutOfDomain = true;
				System.out.println("Out of domain !!");
			}

			boolean isExtremum = false;

			if (mMode)
			{
				// Maximum
				if (lCurrentX > mOptimumX)
					mOptimumX = lCurrentX;
				else
					isExtremum = true;
			}
			else // Minimum
			if (lCurrentX < mOptimumX)
				mOptimumX = lCurrentX;
			else
				isExtremum = true;

			final boolean isFound = isExtremum && !mOutOfDomain;

			return isFound;
		};
	};

	/**
	 * 
	 */
	private class PointPatternP0 implements SearchPointList.PointPattern
	{
		private boolean	mIn	= false;

		public boolean conditionOnPointBefore(final GeometryFile.Point pPoint)
		{
			return true;
		};

		public boolean conditionOnPointCurrent(final GeometryFile.Point pPoint)
		{
			final double lX = pPoint.getCoordinate(1);

			if (SearchPointList.isEqualTol(lX, 0, mTol))
			{
				mIn = true;
				return false;
			}
			else if (mIn)
				return true;
			else
				return false;
		};

		public boolean conditionOnPointAfter(final GeometryFile.Point pPoint)
		{
			return true;
		};
	};

	/**
	 * 
	 */
	private class PatternPAp implements SearchPointList.PointPattern
	{
		public double	mDistanceMinimum	= Double.MAX_VALUE;

		public boolean conditionOnPointBefore(final GeometryFile.Point pPoint)
		{
			return true;
		};

		public boolean conditionOnPointCurrent(final GeometryFile.Point pPoint)
		{
			final double mDistance = distance(pPoint, mPA1);
			if (mDistance < mDistanceMinimum)
				mDistanceMinimum = mDistance;

			final double lX = pPoint.getCoordinate(1);
			if (lX > mSearchLimit)
				return true;
			else
				return false;
		};

		public boolean conditionOnPointAfter(final GeometryFile.Point pPoint)
		{
			return true;
		};
	};

	/**
	 * 
	 */
	private final SearchPointList.PointPattern	mPatternPC2	= new SearchPointList.PointPattern()
																										{
																											public double	mYMinimum	= Double.MAX_VALUE;

																											public boolean conditionOnPointBefore(GeometryFile.Point pPoint)
																											{
																												return true;
																											};

																											public boolean conditionOnPointCurrent(GeometryFile.Point pPoint)
																											{
																												double lY = pPoint.getCoordinate(2);
																												if (SearchPointList.isGreaterTol(mYMinimum, lY, mTol))
																													mYMinimum = lY;

																												double lX = pPoint.getCoordinate(1);
																												if (SearchPointList.isGreaterTol(lX, mPB2
																														.getCoordinate(1), mTol))
																												{
																													mPreB1 = mYMinimum;
																													return true;
																												}
																												else
																													return false;
																											};

																											public boolean conditionOnPointAfter(GeometryFile.Point pPoint)
																											{
																												return true;
																											};
																										};

	/**
	 * @param string
	 */
	private void print(final String pString)
	{
		System.out.println(pString);
	}

	/**
	 * @param pPoint1
	 * @param pPoint2
	 * @return
	 */
	private double distance(final GeometryFile.Point pPoint1, final GeometryFile.Point pPoint2)
	{
		final double dx = pPoint2.getCoordinate(1) - pPoint1.getCoordinate(1);
		final double dy = pPoint2.getCoordinate(2) - pPoint1.getCoordinate(2);

		return Math.sqrt(dx * dx + dy * dy);
	}

}
