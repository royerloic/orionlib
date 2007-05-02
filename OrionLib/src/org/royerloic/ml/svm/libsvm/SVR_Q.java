/*
 * Created on 04.10.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.royerloic.ml.svm.libsvm;

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
		this.l = prob.mNumberOfVectors;
		this.cache = new Cache(this.l, (int) (param.cache_size * (1 << 20)));
		this.sign = new byte[2 * this.l];
		this.index = new int[2 * this.l];
		for (int k = 0; k < this.l; k++)
		{
			this.sign[k] = 1;
			this.sign[k + this.l] = -1;
			this.index[k] = k;
			this.index[k + this.l] = k;
		}
		this.buffer = new float[2][2 * this.l];
		this.next_buffer = 0;
	}

	@Override
	void swap_index(final int i, final int j)
	{
		do
		{
			final byte _ = this.sign[i];
			this.sign[i] = this.sign[j];
			this.sign[j] = _;
		}
		while (false);
		do
		{
			final int _ = this.index[i];
			this.index[i] = this.index[j];
			this.index[j] = _;
		}
		while (false);
	}

	@Override
	float[] get_Q(final int i, final int len)
	{
		final float[][] data = new float[1][];
		final int real_i = this.index[i];
		if (this.cache.get_data(real_i, data, this.l) < this.l)
			for (int j = 0; j < this.l; j++)
				data[0][j] = (float) kernel_function(real_i, j);

		// reorder and copy
		final float buf[] = this.buffer[this.next_buffer];
		this.next_buffer = 1 - this.next_buffer;
		final byte si = this.sign[i];
		for (int j = 0; j < len; j++)
			buf[j] = si * this.sign[j] * data[0][this.index[j]];
		return buf;
	}
}