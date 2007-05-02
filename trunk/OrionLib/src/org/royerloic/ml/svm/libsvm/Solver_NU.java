/*
 * Created on 04.10.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.royerloic.ml.svm.libsvm;

//
// Solver for nu-SVM classification and regression
//
// additional constraint: e^T \alpha = constant
//
final class Solver_NU extends Solver
{
	private SolutionInfo	si;

	@Override
	void Solve(	final int l1,
							final Kernel Q1,
							final double[] b1,
							final byte[] y1,
							final double[] alpha1,
							final double Cp1,
							final double Cn1,
							final double eps1,
							final SolutionInfo si1,
							final int shrinking)
	{
		this.si = si1;
		super.Solve(l1, Q1, b1, y1, alpha1, Cp1, Cn1, eps1, si1, shrinking);
	}

	@Override
	int select_working_set(final int[] working_set)
	{
		// return i,j which maximize -grad(f)^T d , under constraint
		// if alpha_i == C, d != +1
		// if alpha_i == 0, d != -1

		double Gmax1 = -INF; // max { -grad(f)_i * d | y_i = +1, d = +1 }
		int Gmax1_idx = -1;

		double Gmax2 = -INF; // max { -grad(f)_i * d | y_i = +1, d = -1 }
		int Gmax2_idx = -1;

		double Gmax3 = -INF; // max { -grad(f)_i * d | y_i = -1, d = +1 }
		int Gmax3_idx = -1;

		double Gmax4 = -INF; // max { -grad(f)_i * d | y_i = -1, d = -1 }
		int Gmax4_idx = -1;

		for (int i = 0; i < this.active_size; i++)
			if (this.y[i] == +1) // mClass == +1
			{
				if (!is_upper_bound(i))
					if (-this.G[i] > Gmax1)
					{
						Gmax1 = -this.G[i];
						Gmax1_idx = i;
					}
				if (!is_lower_bound(i))
					if (this.G[i] > Gmax2)
					{
						Gmax2 = this.G[i];
						Gmax2_idx = i;
					}
			}
			else
			// mClass == -1
			{
				if (!is_upper_bound(i))
					if (-this.G[i] > Gmax3)
					{
						Gmax3 = -this.G[i];
						Gmax3_idx = i;
					}
				if (!is_lower_bound(i))
					if (this.G[i] > Gmax4)
					{
						Gmax4 = this.G[i];
						Gmax4_idx = i;
					}
			}

		if (Math.max(Gmax1 + Gmax2, Gmax3 + Gmax4) < this.eps)
			return 1;

		if (Gmax1 + Gmax2 > Gmax3 + Gmax4)
		{
			working_set[0] = Gmax1_idx;
			working_set[1] = Gmax2_idx;
		}
		else
		{
			working_set[0] = Gmax3_idx;
			working_set[1] = Gmax4_idx;
		}
		return 0;
	}

	@Override
	void do_shrinking()
	{
		double Gmax1 = -INF; // max { -grad(f)_i * d | y_i = +1, d = +1 }
		double Gmax2 = -INF; // max { -grad(f)_i * d | y_i = +1, d = -1 }
		double Gmax3 = -INF; // max { -grad(f)_i * d | y_i = -1, d = +1 }
		double Gmax4 = -INF; // max { -grad(f)_i * d | y_i = -1, d = -1 }

		int k;
		for (k = 0; k < this.active_size; k++)
		{
			if (!is_upper_bound(k))
				if (this.y[k] == +1)
				{
					if (-this.G[k] > Gmax1)
						Gmax1 = -this.G[k];
				}
				else if (-this.G[k] > Gmax3)
					Gmax3 = -this.G[k];
			if (!is_lower_bound(k))
				if (this.y[k] == +1)
				{
					if (this.G[k] > Gmax2)
						Gmax2 = this.G[k];
				}
				else if (this.G[k] > Gmax4)
					Gmax4 = this.G[k];
		}

		final double Gm1 = -Gmax2;
		final double Gm2 = -Gmax1;
		final double Gm3 = -Gmax4;
		final double Gm4 = -Gmax3;

		for (k = 0; k < this.active_size; k++)
		{
			if (is_lower_bound(k))
			{
				if (this.y[k] == +1)
				{
					if (-this.G[k] >= Gm1)
						continue;
				}
				else if (-this.G[k] >= Gm3)
					continue;
			}
			else if (is_upper_bound(k))
			{
				if (this.y[k] == +1)
				{
					if (this.G[k] >= Gm2)
						continue;
				}
				else if (this.G[k] >= Gm4)
					continue;
			}
			else
				continue;

			--this.active_size;
			swap_index(k, this.active_size);
			--k; // look at the newcomer
		}

		// unshrink, check all variables again before final iterations

		if (this.unshrinked || (Math.max(-(Gm1 + Gm2), -(Gm3 + Gm4)) > this.eps * 10))
			return;

		this.unshrinked = true;
		reconstruct_gradient();

		for (k = this.l - 1; k >= this.active_size; k--)
		{
			if (is_lower_bound(k))
			{
				if (this.y[k] == +1)
				{
					if (-this.G[k] < Gm1)
						continue;
				}
				else if (-this.G[k] < Gm3)
					continue;
			}
			else if (is_upper_bound(k))
			{
				if (this.y[k] == +1)
				{
					if (this.G[k] < Gm2)
						continue;
				}
				else if (this.G[k] < Gm4)
					continue;
			}
			else
				continue;

			swap_index(k, this.active_size);
			this.active_size++;
			++k; // look at the newcomer
		}
	}

	@Override
	double calculate_rho()
	{
		int nr_free1 = 0, nr_free2 = 0;
		double ub1 = INF, ub2 = INF;
		double lb1 = -INF, lb2 = -INF;
		double sum_free1 = 0, sum_free2 = 0;

		for (int i = 0; i < this.active_size; i++)
			if (this.y[i] == +1)
			{
				if (is_lower_bound(i))
					ub1 = Math.min(ub1, this.G[i]);
				else if (is_upper_bound(i))
					lb1 = Math.max(lb1, this.G[i]);
				else
				{
					++nr_free1;
					sum_free1 += this.G[i];
				}
			}
			else if (is_lower_bound(i))
				ub2 = Math.min(ub2, this.G[i]);
			else if (is_upper_bound(i))
				lb2 = Math.max(lb2, this.G[i]);
			else
			{
				++nr_free2;
				sum_free2 += this.G[i];
			}

		double r1, r2;
		if (nr_free1 > 0)
			r1 = sum_free1 / nr_free1;
		else
			r1 = (ub1 + lb1) / 2;

		if (nr_free2 > 0)
			r2 = sum_free2 / nr_free2;
		else
			r2 = (ub2 + lb2) / 2;

		this.si.r = (r1 + r2) / 2;
		return (r1 - r2) / 2;
	}
}