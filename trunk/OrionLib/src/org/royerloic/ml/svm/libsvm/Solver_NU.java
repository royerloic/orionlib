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

	void Solve(	int l1,
							Kernel Q1,
							double[] b1,
							byte[] y1,
							double[] alpha1,
							double Cp1,
							double Cn1,
							double eps1,
							SolutionInfo si1,
							int shrinking)
	{
		this.si = si1;
		super.Solve(l1, Q1, b1, y1, alpha1, Cp1, Cn1, eps1, si1, shrinking);
	}

	int select_working_set(int[] working_set)
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

		for (int i = 0; i < active_size; i++)
		{
			if (y[i] == +1) // mClass == +1
			{
				if (!is_upper_bound(i)) // d = +1
				{
					if (-G[i] > Gmax1)
					{
						Gmax1 = -G[i];
						Gmax1_idx = i;
					}
				}
				if (!is_lower_bound(i)) // d = -1
				{
					if (G[i] > Gmax2)
					{
						Gmax2 = G[i];
						Gmax2_idx = i;
					}
				}
			}
			else
			// mClass == -1
			{
				if (!is_upper_bound(i)) // d = +1
				{
					if (-G[i] > Gmax3)
					{
						Gmax3 = -G[i];
						Gmax3_idx = i;
					}
				}
				if (!is_lower_bound(i)) // d = -1
				{
					if (G[i] > Gmax4)
					{
						Gmax4 = G[i];
						Gmax4_idx = i;
					}
				}
			}
		}

		if (Math.max(Gmax1 + Gmax2, Gmax3 + Gmax4) < eps)
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

	void do_shrinking()
	{
		double Gmax1 = -INF; // max { -grad(f)_i * d | y_i = +1, d = +1 }
		double Gmax2 = -INF; // max { -grad(f)_i * d | y_i = +1, d = -1 }
		double Gmax3 = -INF; // max { -grad(f)_i * d | y_i = -1, d = +1 }
		double Gmax4 = -INF; // max { -grad(f)_i * d | y_i = -1, d = -1 }

		int k;
		for (k = 0; k < active_size; k++)
		{
			if (!is_upper_bound(k))
			{
				if (y[k] == +1)
				{
					if (-G[k] > Gmax1)
						Gmax1 = -G[k];
				}
				else if (-G[k] > Gmax3)
					Gmax3 = -G[k];
			}
			if (!is_lower_bound(k))
			{
				if (y[k] == +1)
				{
					if (G[k] > Gmax2)
						Gmax2 = G[k];
				}
				else if (G[k] > Gmax4)
					Gmax4 = G[k];
			}
		}

		double Gm1 = -Gmax2;
		double Gm2 = -Gmax1;
		double Gm3 = -Gmax4;
		double Gm4 = -Gmax3;

		for (k = 0; k < active_size; k++)
		{
			if (is_lower_bound(k))
			{
				if (y[k] == +1)
				{
					if (-G[k] >= Gm1)
						continue;
				}
				else if (-G[k] >= Gm3)
					continue;
			}
			else if (is_upper_bound(k))
			{
				if (y[k] == +1)
				{
					if (G[k] >= Gm2)
						continue;
				}
				else if (G[k] >= Gm4)
					continue;
			}
			else
				continue;

			--active_size;
			swap_index(k, active_size);
			--k; // look at the newcomer
		}

		// unshrink, check all variables again before final iterations

		if (unshrinked || Math.max(-(Gm1 + Gm2), -(Gm3 + Gm4)) > eps * 10)
			return;

		unshrinked = true;
		reconstruct_gradient();

		for (k = l - 1; k >= active_size; k--)
		{
			if (is_lower_bound(k))
			{
				if (y[k] == +1)
				{
					if (-G[k] < Gm1)
						continue;
				}
				else if (-G[k] < Gm3)
					continue;
			}
			else if (is_upper_bound(k))
			{
				if (y[k] == +1)
				{
					if (G[k] < Gm2)
						continue;
				}
				else if (G[k] < Gm4)
					continue;
			}
			else
				continue;

			swap_index(k, active_size);
			active_size++;
			++k; // look at the newcomer
		}
	}

	double calculate_rho()
	{
		int nr_free1 = 0, nr_free2 = 0;
		double ub1 = INF, ub2 = INF;
		double lb1 = -INF, lb2 = -INF;
		double sum_free1 = 0, sum_free2 = 0;

		for (int i = 0; i < active_size; i++)
		{
			if (y[i] == +1)
			{
				if (is_lower_bound(i))
					ub1 = Math.min(ub1, G[i]);
				else if (is_upper_bound(i))
					lb1 = Math.max(lb1, G[i]);
				else
				{
					++nr_free1;
					sum_free1 += G[i];
				}
			}
			else
			{
				if (is_lower_bound(i))
					ub2 = Math.min(ub2, G[i]);
				else if (is_upper_bound(i))
					lb2 = Math.max(lb2, G[i]);
				else
				{
					++nr_free2;
					sum_free2 += G[i];
				}
			}
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

		si.r = (r1 + r2) / 2;
		return (r1 - r2) / 2;
	}
}