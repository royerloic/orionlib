package utils.robotics.automatism;

/*
 * start:
 previous_error = error or 0 if undefined
 error = setpoint - actual_position
 P = Kp * error
 I = I + Ki * error * dt
 D = Kd * (error - previous_error) / dt
 output = P + I + D
 wait(dt)
 goto start
 */
public class PIDController
{

	double mKP;
	double mKI = 0;
	double mKD;
	double mIDampening = 0.9;

	double mPreviousTime = Double.NEGATIVE_INFINITY;
	double mPreviousError = 0;
	double mI = 0;

	public PIDController(final double p, final double i, final double d)
	{
		super();
		mKP = p;
		mKI = i;
		mKD = d;
	}

	public double control(final double pError)
	{
		final double lDT = mPreviousTime == Double.NEGATIVE_INFINITY ? 0
																																: getTime() - mPreviousTime;
		mPreviousTime = getTime();

		final double lP = mKP * pError;

		final double lI = mI + mKI * pError * lDT;
		mI = lI * mIDampening;

		final double lD = mKD * (pError - mPreviousError) / lDT;
		mPreviousError = pError;
		if (Double.isNaN(lD))
		{
			return boundOutput(lP + lI);
		}
		else
		{
			return boundOutput(lP + lI + lD);
		}

	}

	private double getTime()
	{
		return System.nanoTime() / 1000000000;
	}

	private final double boundOutput(final double pD)
	{
		if (pD > 1)
		{
			return 1;
		}
		else if (pD < -1)
		{
			return -1;
		}
		return pD;
	}

}
