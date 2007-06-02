/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package utils.math.stdimpl;

/**
 * @author MSc. Ing. Loic Royer
 *  
 */
public class MathFunctions
{

  /**
   *  
   */
  private MathFunctions()
  {
    super();
    // TODO Auto-generated constructor stub
  }

  public static final double sigmoid(final double px)
  {
    return 1 / (1 + Math.exp(-px));
  }

  public static final double sig(final double px)
  {
    return 1 / (1 + Math.exp(4 - px));
  }

  /** sqrt(a^2 + b^2) without under/overflow. * */

  public static double hypot(final double a, final double b)
  {
    double r;
    if (Math.abs(a) > Math.abs(b))
    {
      r = b / a;
      r = Math.abs(a) * Math.sqrt(1 + r * r);
    }
    else if (b != 0)
    {
      r = a / b;
      r = Math.abs(b) * Math.sqrt(1 + r * r);
    }
		else
			r = 0.0;
    return r;
  }

  public static double round(final double pValue, final double pDelta)
  {
    final double lResult = pValue - Math.IEEEremainder(pValue, pDelta);

    return lResult;
  }

}