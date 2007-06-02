/*
 * Created on 04.10.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package utils.ml.svm.libsvm;

//
// Kernel evaluation
//
// the static method k_function is for doing single kernel evaluation
// the constructor of Kernel prepares to calculate the
// mNumberOfSupportVectors*mNumberOfSupportVectors kernel matrix
// the member function get_Q is for getting one column from the Q Matrix
//
abstract class Kernel
{
	private Node[][]				x;

	private final double[]	x_square;

	// Parameter
	private final int				kernel_type;

	private final double		degree;

	private final double		gamma;

	private final double		coef0;

	abstract float[] get_Q(int column, int len);

	void swap_index(final int i, final int j)
	{
		do
		{
			final Node[] _ = x[i];
			x[i] = x[j];
			x[j] = _;
		}
		while (false);
		if (x_square != null)
			do
			{
				final double _ = x_square[i];
				x_square[i] = x_square[j];
				x_square[j] = _;
			}
			while (false);
	}

	private static double tanh(final double x)
	{
		final double e = Math.exp(x);
		return 1.0 - 2.0 / (e * e + 1);
	}

	double kernel_function(final int i, final int j)
	{
		switch (kernel_type)
		{
			case Parameter.LINEAR:
				return dot(x[i], x[j]);
			case Parameter.POLY:
				return Math.pow(gamma * dot(x[i], x[j]) + coef0, degree);
			case Parameter.RBF:
				return Math.exp(-gamma * (x_square[i] + x_square[j] - 2 * dot(x[i], x[j])));
			case Parameter.SIGMOID:
				return tanh(gamma * dot(x[i], x[j]) + coef0);
			default:
				return 0; // java
		}
	}

	Kernel(final int l, final Node[][] x_, final Parameter param)
	{
		kernel_type = param.kernel_type;
		degree = param.degree;
		gamma = param.gamma;
		coef0 = param.coef0;

		x = x_.clone();

		if (kernel_type == Parameter.RBF)
		{
			x_square = new double[l];
			for (int i = 0; i < l; i++)
				x_square[i] = dot(x[i], x[i]);
		}
		else
			x_square = null;
	}

	static double dot(final Node[] x, final Node[] y)
	{
		double sum = 0;
		final int xlen = x.length;
		final int ylen = y.length;
		int i = 0;
		int j = 0;
		while ((i < xlen) && (j < ylen))
			if (x[i].mIndex == y[j].mIndex)
				sum += x[i++].mValue * y[j++].mValue;
			else if (x[i].mIndex > y[j].mIndex)
				++j;
			else
				++i;
		return sum;
	}

	static double k_function(final Node[] x, final Node[] y, final Parameter param)
	{
		switch (param.kernel_type)
		{
			case Parameter.LINEAR:
				return dot(x, y);
			case Parameter.POLY:
				return Math.pow(param.gamma * dot(x, y) + param.coef0, param.degree);
			case Parameter.RBF:
			{
				double sum = 0;
				final int xlen = x.length;
				final int ylen = y.length;
				int i = 0;
				int j = 0;
				while ((i < xlen) && (j < ylen))
					if (x[i].mIndex == y[j].mIndex)
					{
						final double d = x[i++].mValue - y[j++].mValue;
						sum += d * d;
					}
					else if (x[i].mIndex > y[j].mIndex)
					{
						sum += y[j].mValue * y[j].mValue;
						++j;
					}
					else
					{
						sum += x[i].mValue * x[i].mValue;
						++i;
					}

				while (i < xlen)
				{
					sum += x[i].mValue * x[i].mValue;
					++i;
				}

				while (j < ylen)
				{
					sum += y[j].mValue * y[j].mValue;
					++j;
				}

				return Math.exp(-param.gamma * sum);
			}
			case Parameter.SIGMOID:
				return tanh(param.gamma * dot(x, y) + param.coef0);
			default:
				return 0; // java
		}
	}
}