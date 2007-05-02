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
		return this.mTn;
	}

	/**
	 * @return
	 */
	public double getF()
	{
		return this.mF;
	}

	/**
	 * @return
	 */
	public double getB1()
	{
		return this.mB1;
	}

	/**
	 * @param pGeometryFileName
	 */
	public ClinchingAnalysis(final String pGeometryFileName)
	{
		try
		{
			this.lGeometryFile = new GeometryFile(pGeometryFileName);

			this.lTopSheet = (GeometryFile.PointList) this.lGeometryFile.getGeometryObjectAt(0);
			this.lBottomSheet = (GeometryFile.PointList) this.lGeometryFile.getGeometryObjectAt(1);

			this.lSearchTopSheet = new SearchPointList(this.lTopSheet);
			this.lSearchBottomSheet = new SearchPointList(this.lBottomSheet);

			final PointPatternP0 lPointPatternP0 = new PointPatternP0();

			boolean lFoundP01 = (this.mP01 = searchPoint(this.lSearchTopSheet, "P01", 0, lPointPatternP0)) != null;
			this.mIndexP01 = (int) (this.mP01.getCoordinate(0)) - 1;

			final PatternExtremumX lPatternPB1 = new PatternExtremumX("maximum", "forwards");

			this.lSearchTopSheet.positiveDirection();
			final boolean lFoundPB1 = (this.mPB1 = searchPoint(this.lSearchTopSheet, "PB1", this.mIndexP01, lPatternPB1)) != null;
			this.mIndexPB1 = (int) this.mPB1.getCoordinate(0) - 1;

			final PatternExtremumX lPatternPA1 = new PatternExtremumX("minimum", "forwards");

			final boolean lFoundPA1 = (this.mPA1 = searchPoint(this.lSearchTopSheet, "PA1", this.mIndexPB1, lPatternPA1)) != null;

			this.mIndexPA1 = (int) this.mPA1.getCoordinate(0) - 1;

			this.lSearchTopSheet.negativeDirection();

			final PatternPAp lPatternPAp = new PatternPAp();

			final boolean lFoundPAp = (this.mPAp = searchPoint(this.lSearchTopSheet, "PAp", this.mIndexP01, lPatternPAp)) != null;

			this.mIndexPAp = (int) this.mPAp.getCoordinate(0) - 1;

			this.lSearchBottomSheet.negativeDirection();

			boolean lFoundP02 = (this.mP02 = searchPoint(this.lSearchBottomSheet, "P02", 0, lPointPatternP0)) != null;

			this.mIndexP02 = (int) this.mP02.getCoordinate(0) - 1;

			final PatternExtremumX lPatternPB2 = new PatternExtremumX("maximum", "backwards");

			final boolean lFoundPB2 = (this.mPB2 = searchPoint(this.lSearchBottomSheet, "PB2", this.mIndexP02, lPatternPB2)) != null;

			this.mIndexPB2 = (int) this.mPB2.getCoordinate(0) - 1;

			final PatternExtremumX lPatternPA2 = new PatternExtremumX("minimum", "backwards");

			final boolean lFoundPA2 = (this.mPA2 = searchPoint(this.lSearchBottomSheet, "PA2", this.mIndexPB2, lPatternPA2)) != null;

			this.mIndexPA2 = (int) this.mPA2.getCoordinate(0) - 1;

			final boolean lFoundPC2 = (this.mPC2 = searchPoint(this.lSearchTopSheet, "PC2", this.mIndexP01, this.mPatternPC2)) != null;

			this.mIndexPC2 = (int) this.mPC2.getCoordinate(0) - 1;

			if (lFoundPA1)
				this.mTn = lPatternPAp.mDistanceMinimum;
			else
				this.mTn = 0;
			print("mTn = " + this.mTn);

			if (lFoundPA2 && lFoundPB2)
				this.mF = Math.abs(this.mPB1.getCoordinate(1) - this.mPA2.getCoordinate(1));
			else
				this.mF = 0;
			print("mF = " + this.mF);

			this.mB1 = this.mPreB1 - 19.5;
			print("mPreB1 = " + this.mPreB1);
			print("mB1 = " + this.mB1);
		}
		catch (final RuntimeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace(System.out);
			this.mTn = 0;
			this.mF = 0;
			this.mB1 = 0;
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
				this.mMode = true;
				this.mOptimumX = Double.NEGATIVE_INFINITY;
			}
			else if (pMode.equals("minimum"))
			{
				this.mMode = false;
				this.mOptimumX = Double.POSITIVE_INFINITY;
			}

			if (pDirection.equals("forwards"))
				this.mDirection = true;
			else if (pDirection.equals("backwards"))
				this.mDirection = false;
		}

		public boolean conditionOnPointBefore(final GeometryFile.Point pPoint)
		{
			if (!this.mDirection)
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
			if (this.mDirection)
				return conditionOnPoint(pPoint);
			else
				return true;
		}

		private boolean conditionOnPoint(final GeometryFile.Point pPoint)
		{
			final double lCurrentX = pPoint.getCoordinate(1);

			if (lCurrentX > ClinchingAnalysis.this.mSearchLimit)
			{
				this.mOutOfDomain = true;
				System.out.println("Out of domain !!");
			}

			boolean isExtremum = false;

			if (this.mMode)
			{
				// Maximum
				if (lCurrentX > this.mOptimumX)
					this.mOptimumX = lCurrentX;
				else
					isExtremum = true;
			}
			else // Minimum
			if (lCurrentX < this.mOptimumX)
				this.mOptimumX = lCurrentX;
			else
				isExtremum = true;

			final boolean isFound = isExtremum && !this.mOutOfDomain;

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

			if (SearchPointList.isEqualTol(lX, 0, ClinchingAnalysis.this.mTol))
			{
				this.mIn = true;
				return false;
			}
			else if (this.mIn)
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
			final double mDistance = distance(pPoint, ClinchingAnalysis.this.mPA1);
			if (mDistance < this.mDistanceMinimum)
				this.mDistanceMinimum = mDistance;

			final double lX = pPoint.getCoordinate(1);
			if (lX > ClinchingAnalysis.this.mSearchLimit)
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
																												if (SearchPointList.isGreaterTol(this.mYMinimum, lY, ClinchingAnalysis.this.mTol))
																													this.mYMinimum = lY;

																												double lX = pPoint.getCoordinate(1);
																												if (SearchPointList.isGreaterTol(lX, ClinchingAnalysis.this.mPB2
																														.getCoordinate(1), ClinchingAnalysis.this.mTol))
																												{
																													ClinchingAnalysis.this.mPreB1 = this.mYMinimum;
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
