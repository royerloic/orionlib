/*
 * Created on 26.11.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.math;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 *  
 */
public class IntersectionInformation
{
  public boolean          mIntersects;

  public boolean          mParallel;

  public boolean          mCoincident;

  public boolean          mWithinFirstSegment;

  public boolean          mWithinSecondSegment;

  public INumericalVector mIntersectionPoint;

  @Override
	public String toString()
  {
    String lString;
    if (mIntersects)
    {
      final String lWithinFirstSegment = (mWithinFirstSegment ? "within first segment,"
          : "");
      final String lWithinSecondSegment = (mWithinSecondSegment ? "within second segment,"
          : "");

      lString = "Intersection "
                + lWithinFirstSegment + " " + lWithinSecondSegment + " at : "
                + mIntersectionPoint;
    }
    else if (mCoincident)
			lString = "Coincident Lines ";
		else
			lString = "Parallel Lines ";

    return lString;
  }
}