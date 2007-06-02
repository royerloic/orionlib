/*
 * Created on 04.10.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package utils.ml.svm.libsvm;

class ONE_CLASS_Q extends Kernel
{
	private final Cache	cache;

	ONE_CLASS_Q(final Problem prob, final Parameter param)
	{
		super(prob.mNumberOfVectors, prob.mVectorsTable, param);
		cache = new Cache(prob.mNumberOfVectors, (int) (param.cache_size * (1 << 20)));
	}

	@Override
	float[] get_Q(final int i, final int len)
	{
		final float[][] data = new float[1][];
		int start;
		if ((start = cache.get_data(i, data, len)) < len)
			for (int j = start; j < len; j++)
				data[0][j] = (float) kernel_function(i, j);
		return data[0];
	}

	@Override
	void swap_index(final int i, final int j)
	{
		cache.swap_index(i, j);
		super.swap_index(i, j);
	}
}