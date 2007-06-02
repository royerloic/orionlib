/*
 * Created on 04.10.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package utils.ml.svm.libsvm;

class SVR_Q extends Kernel
{
	private final int			l;

	private final Cache		cache;

	private final byte[]	sign;

	private final int[]		index;

	private int						next_buffer;

	private float[][]			buffer;

	SVR_Q(final Problem prob, final Parameter param)
	{
		super(prob.mNumberOfVectors, prob.mVectorsTable, param);
		l = prob.mNumberOfVectors;
		cache = new Cache(l, (int) (param.cache_size * (1 << 20)));
		sign = new byte[2 * l];
		index = new int[2 * l];
		for (int k = 0; k < l; k++)
		{
			sign[k] = 1;
			sign[k + l] = -1;
			index[k] = k;
			index[k + l] = k;
		}
		buffer = new float[2][2 * l];
		next_buffer = 0;
	}

	@Override
	void swap_index(final int i, final int j)
	{
		do
		{
			final byte _ = sign[i];
			sign[i] = sign[j];
			sign[j] = _;
		}
		while (false);
		do
		{
			final int _ = index[i];
			index[i] = index[j];
			index[j] = _;
		}
		while (false);
	}

	@Override
	float[] get_Q(final int i, final int len)
	{
		final float[][] data = new float[1][];
		final int real_i = index[i];
		if (cache.get_data(real_i, data, l) < l)
			for (int j = 0; j < l; j++)
				data[0][j] = (float) kernel_function(real_i, j);

		// reorder and copy
		final float buf[] = buffer[next_buffer];
		next_buffer = 1 - next_buffer;
		final byte si = sign[i];
		for (int j = 0; j < len; j++)
			buf[j] = si * sign[j] * data[0][index[j]];
		return buf;
	}
}