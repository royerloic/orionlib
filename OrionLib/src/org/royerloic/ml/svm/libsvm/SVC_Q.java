/*
 * Created on 04.10.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.royerloic.ml.svm.libsvm;

//
// Q matrices for various formulations
//
class SVC_Q extends Kernel
{
	private final byte[]	y;

	private final Cache		cache;

	SVC_Q(Problem prob, Parameter param, byte[] y_)
	{
		super(prob.mNumberOfVectors, prob.mVectorsTable, param);
		y = (byte[]) y_.clone();
		cache = new Cache(prob.mNumberOfVectors, (int) (param.cache_size * (1 << 20)));
	}

	float[] get_Q(int i, int len)
	{
		float[][] data = new float[1][];
		int start;
		if ((start = cache.get_data(i, data, len)) < len)
		{
			for (int j = start; j < len; j++)
				data[0][j] = (float) (y[i] * y[j] * kernel_function(i, j));
		}
		return data[0];
	}

	void swap_index(int i, int j)
	{
		cache.swap_index(i, j);
		super.swap_index(i, j);
		do
		{
			byte _ = y[i];
			y[i] = y[j];
			y[j] = _;
		}
		while (false);
	}
}
