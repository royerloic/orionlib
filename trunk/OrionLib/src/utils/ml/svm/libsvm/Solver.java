/*
 * Created on 04.10.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package utils.ml.svm.libsvm;

// Generalized SMO+SVMlight algorithm
// Solves:
//
// min 0.5(\alpha^T Q \alpha) + b^T \alpha
//
// mClass^T \alpha = \delta
// y_i = +1 or -1
// 0 <= alpha_i <= Cp for y_i = 1
// 0 <= alpha_i <= Cn for y_i = -1
//
// Given:
//
// Q, b, mClass, Cp, Cn, and an initial feasible point \alpha
// mNumberOfSupportVectors is the size of vectors and matrices
// eps is the stopping criterion
//
// solution will be put in \alpha, objective mValue will be put in obj
//
class Solver
{
	int									active_size;

	byte[]							y;

	// gradient of objective function
	double[]						G;

	static final byte		LOWER_BOUND	= 0;

	static final byte		UPPER_BOUND	= 1;

	static final byte		FREE				= 2;

	// LOWER_BOUND UPPER_BOUND FREE
	byte[]							alpha_status;

	double[]						alpha;

	Kernel							Q;

	double							eps;

	double							Cp, Cn;

	double[]						b;

	int[]								active_set;

	// gradient, if we treat free variables as 0
	double[]						G_bar;

	int									l;

	// XXX
	boolean							unshrinked;

	static final double	INF					= java.lang.Double.POSITIVE_INFINITY;

	double get_C(final int i)
	{
		return (y[i] > 0) ? Cp : Cn;
	}

	void update_alpha_status(final int i)
	{
		if (alpha[i] >= get_C(i))
			alpha_status[i] = UPPER_BOUND;
		else if (alpha[i] <= 0)
			alpha_status[i] = LOWER_BOUND;
		else
			alpha_status[i] = FREE;
	}

	boolean is_upper_bound(final int i)
	{
		return alpha_status[i] == UPPER_BOUND;
	}

	boolean is_lower_bound(final int i)
	{
		return alpha_status[i] == LOWER_BOUND;
	}

	boolean is_free(final int i)
	{
		return alpha_status[i] == FREE;
	}

	// java: information about solution except alpha,
	// because we cannot return multiple values otherwise...
	static class SolutionInfo
	{
		double	obj;

		double	rho;

		double	upper_bound_p;

		double	upper_bound_n;

		double	r;							// for Solver_NU
	}

	void swap_index(final int i, final int j)
	{
		Q.swap_index(i, j);
		do
		{
			final byte _ = y[i];
			y[i] = y[j];
			y[j] = _;
		}
		while (false);
		do
		{
			final double _ = G[i];
			G[i] = G[j];
			G[j] = _;
		}
		while (false);
		do
		{
			final byte _ = alpha_status[i];
			alpha_status[i] = alpha_status[j];
			alpha_status[j] = _;
		}
		while (false);
		do
		{
			final double _ = alpha[i];
			alpha[i] = alpha[j];
			alpha[j] = _;
		}
		while (false);
		do
		{
			final double _ = b[i];
			b[i] = b[j];
			b[j] = _;
		}
		while (false);
		do
		{
			final int _ = active_set[i];
			active_set[i] = active_set[j];
			active_set[j] = _;
		}
		while (false);
		do
		{
			final double _ = G_bar[i];
			G_bar[i] = G_bar[j];
			G_bar[j] = _;
		}
		while (false);
	}

	void reconstruct_gradient()
	{
		// reconstruct inactive elements of G from G_bar and free variables

		if (active_size == l)
			return;

		int i;
		for (i = active_size; i < l; i++)
			G[i] = G_bar[i] + b[i];

		for (i = 0; i < active_size; i++)
			if (is_free(i))
			{
				final float[] Q_i = Q.get_Q(i, l);
				final double alpha_i = alpha[i];
				for (int j = active_size; j < l; j++)
					G[j] += alpha_i * Q_i[j];
			}
	}

	void Solve(	final int l1,
							final Kernel Q1,
							final double[] b_,
							final byte[] y_,
							final double[] alpha_,
							final double Cp1,
							final double Cn1,
							final double eps1,
							final SolutionInfo si,
							final int shrinking)
	{
		l = l1;
		Q = Q1;
		b = b_.clone();
		y = y_.clone();
		alpha = alpha_.clone();
		Cp = Cp1;
		Cn = Cn1;
		eps = eps1;
		unshrinked = false;

		// initialize alpha_status
		{
			alpha_status = new byte[l1];
			for (int i = 0; i < l1; i++)
				update_alpha_status(i);
		}

		// initialize active set (for shrinking)
		{
			active_set = new int[l1];
			for (int i = 0; i < l1; i++)
				active_set[i] = i;
			active_size = l1;
		}

		// initialize gradient
		{
			G = new double[l1];
			G_bar = new double[l1];
			int i;
			for (i = 0; i < l1; i++)
			{
				G[i] = b[i];
				G_bar[i] = 0;
			}
			for (i = 0; i < l1; i++)
				if (!is_lower_bound(i))
				{
					final float[] Q_i = Q1.get_Q(i, l1);
					final double alpha_i = alpha[i];
					int j;
					for (j = 0; j < l1; j++)
						G[j] += alpha_i * Q_i[j];
					if (is_upper_bound(i))
						for (j = 0; j < l1; j++)
							G_bar[j] += get_C(i) * Q_i[j];
				}
		}

		// optimization step

		int iter = 0;
		int counter = Math.min(l1, 1000) + 1;
		final int[] working_set = new int[2];

		while (true)
		{
			// show progress and do shrinking

			if (--counter == 0)
			{
				counter = Math.min(l1, 1000);
				if (shrinking != 0)
					do_shrinking();
				// System.err.print(".");
			}

			if (select_working_set(working_set) != 0)
			{
				// reconstruct the whole gradient
				reconstruct_gradient();
				// reset active set size and check
				active_size = l1;
				// System.err.print("*");
				if (select_working_set(working_set) != 0)
					break;
				else
					counter = 1; // do shrinking next iteration
			}

			final int i = working_set[0];
			final int j = working_set[1];

			++iter;

			// update alpha[i] and alpha[j], handle bounds carefully

			float[] Q_i = Q1.get_Q(i, active_size);
			float[] Q_j = Q1.get_Q(j, active_size);

			final double C_i = get_C(i);
			final double C_j = get_C(j);

			final double old_alpha_i = alpha[i];
			final double old_alpha_j = alpha[j];

			if (y[i] != y[j])
			{
				final double delta = (-G[i] - G[j]) / Math.max(Q_i[i] + Q_j[j] + 2 * Q_i[j], 0);
				double diff = alpha[i] - alpha[j];
				alpha[i] += delta;
				alpha[j] += delta;

				if (diff > 0)
				{
					if (alpha[j] < 0)
					{
						alpha[j] = 0;
						alpha[i] = diff;
					}
				}
				else if (alpha[i] < 0)
				{
					alpha[i] = 0;
					alpha[j] = -diff;
				}
				if (diff > C_i - C_j)
				{
					if (alpha[i] > C_i)
					{
						alpha[i] = C_i;
						alpha[j] = C_i - diff;
					}
				}
				else if (alpha[j] > C_j)
				{
					alpha[j] = C_j;
					alpha[i] = C_j + diff;
				}
			}
			else
			{
				final double delta = (G[i] - G[j]) / Math.max(Q_i[i] + Q_j[j] - 2 * Q_i[j], 0);
				final double sum = alpha[i] + alpha[j];
				alpha[i] -= delta;
				alpha[j] += delta;
				if (sum > C_i)
				{
					if (alpha[i] > C_i)
					{
						alpha[i] = C_i;
						alpha[j] = sum - C_i;
					}
				}
				else if (alpha[j] < 0)
				{
					alpha[j] = 0;
					alpha[i] = sum;
				}
				if (sum > C_j)
				{
					if (alpha[j] > C_j)
					{
						alpha[j] = C_j;
						alpha[i] = sum - C_j;
					}
				}
				else if (alpha[i] < 0)
				{
					alpha[i] = 0;
					alpha[j] = sum;
				}
			}

			// update G

			final double delta_alpha_i = alpha[i] - old_alpha_i;
			final double delta_alpha_j = alpha[j] - old_alpha_j;

			for (int k = 0; k < active_size; k++)
				G[k] += Q_i[k] * delta_alpha_i + Q_j[k] * delta_alpha_j;

			// update alpha_status and G_bar

			{
				final boolean ui = is_upper_bound(i);
				final boolean uj = is_upper_bound(j);
				update_alpha_status(i);
				update_alpha_status(j);
				int k;
				if (ui != is_upper_bound(i))
				{
					Q_i = Q1.get_Q(i, l1);
					if (ui)
						for (k = 0; k < l1; k++)
							G_bar[k] -= C_i * Q_i[k];
					else
						for (k = 0; k < l1; k++)
							G_bar[k] += C_i * Q_i[k];
				}

				if (uj != is_upper_bound(j))
				{
					Q_j = Q1.get_Q(j, l1);
					if (uj)
						for (k = 0; k < l1; k++)
							G_bar[k] -= C_j * Q_j[k];
					else
						for (k = 0; k < l1; k++)
							G_bar[k] += C_j * Q_j[k];
				}
			}

		}

		// calculate mRho

		si.rho = calculate_rho();

		// calculate objective mValue
		{
			double v = 0;
			int i;
			for (i = 0; i < l1; i++)
				v += alpha[i] * (G[i] + b[i]);

			si.obj = v / 2;
		}

		// put back the solution
		{
			for (int i = 0; i < l1; i++)
				alpha_[active_set[i]] = alpha[i];
		}

		si.upper_bound_p = Cp1;
		si.upper_bound_n = Cn1;

		// System.out.print("\noptimization finished, #iter = " + iter + "\n");
	}

	// return 1 if already optimal, return 0 otherwise
	int select_working_set(final int[] working_set)
	{
		// return i,j which maximize -grad(f)^T d , under constraint
		// if alpha_i == C, d != +1
		// if alpha_i == 0, d != -1

		double Gmax1 = -INF; // max { -grad(f)_i * d | y_i*d = +1 }
		int Gmax1_idx = -1;

		double Gmax2 = -INF; // max { -grad(f)_i * d | y_i*d = -1 }
		int Gmax2_idx = -1;

		for (int i = 0; i < active_size; i++)
			if (y[i] == +1) // mClass = +1
			{
				if (!is_upper_bound(i))
					if (-G[i] > Gmax1)
					{
						Gmax1 = -G[i];
						Gmax1_idx = i;
					}
				if (!is_lower_bound(i))
					if (G[i] > Gmax2)
					{
						Gmax2 = G[i];
						Gmax2_idx = i;
					}
			}
			else
			// mClass = -1
			{
				if (!is_upper_bound(i))
					if (-G[i] > Gmax2)
					{
						Gmax2 = -G[i];
						Gmax2_idx = i;
					}
				if (!is_lower_bound(i))
					if (G[i] > Gmax1)
					{
						Gmax1 = G[i];
						Gmax1_idx = i;
					}
			}

		if (Gmax1 + Gmax2 < eps)
			return 1;

		working_set[0] = Gmax1_idx;
		working_set[1] = Gmax2_idx;
		return 0;
	}

	void do_shrinking()
	{
		int i, j, k;
		final int[] working_set = new int[2];
		if (select_working_set(working_set) != 0)
			return;
		i = working_set[0];
		j = working_set[1];
		final double Gm1 = -y[j] * G[j];
		final double Gm2 = y[i] * G[i];

		// shrink

		for (k = 0; k < active_size; k++)
		{
			if (is_lower_bound(k))
			{
				if (y[k] == +1)
				{
					if (-G[k] >= Gm1)
						continue;
				}
				else if (-G[k] >= Gm2)
					continue;
			}
			else if (is_upper_bound(k))
			{
				if (y[k] == +1)
				{
					if (G[k] >= Gm2)
						continue;
				}
				else if (G[k] >= Gm1)
					continue;
			}
			else
				continue;

			--active_size;
			swap_index(k, active_size);
			--k; // look at the newcomer
		}

		// unshrink, check all variables again before final iterations

		if (unshrinked || (-(Gm1 + Gm2) > eps * 10))
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
				else if (-G[k] < Gm2)
					continue;
			}
			else if (is_upper_bound(k))
			{
				if (y[k] == +1)
				{
					if (G[k] < Gm2)
						continue;
				}
				else if (G[k] < Gm1)
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
		double r;
		int nr_free = 0;
		double ub = INF, lb = -INF, sum_free = 0;
		for (int i = 0; i < active_size; i++)
		{
			final double yG = y[i] * G[i];

			if (is_lower_bound(i))
			{
				if (y[i] > 0)
					ub = Math.min(ub, yG);
				else
					lb = Math.max(lb, yG);
			}
			else if (is_upper_bound(i))
			{
				if (y[i] < 0)
					ub = Math.min(ub, yG);
				else
					lb = Math.max(lb, yG);
			}
			else
			{
				++nr_free;
				sum_free += yG;
			}
		}

		if (nr_free > 0)
			r = sum_free / nr_free;
		else
			r = (ub + lb) / 2;

		return r;
	}

}