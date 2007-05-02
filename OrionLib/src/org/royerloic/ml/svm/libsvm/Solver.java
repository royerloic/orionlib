/*
 * Created on 04.10.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.royerloic.ml.svm.libsvm;

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
		return (this.y[i] > 0) ? this.Cp : this.Cn;
	}

	void update_alpha_status(final int i)
	{
		if (this.alpha[i] >= get_C(i))
			this.alpha_status[i] = UPPER_BOUND;
		else if (this.alpha[i] <= 0)
			this.alpha_status[i] = LOWER_BOUND;
		else
			this.alpha_status[i] = FREE;
	}

	boolean is_upper_bound(final int i)
	{
		return this.alpha_status[i] == UPPER_BOUND;
	}

	boolean is_lower_bound(final int i)
	{
		return this.alpha_status[i] == LOWER_BOUND;
	}

	boolean is_free(final int i)
	{
		return this.alpha_status[i] == FREE;
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
		this.Q.swap_index(i, j);
		do
		{
			final byte _ = this.y[i];
			this.y[i] = this.y[j];
			this.y[j] = _;
		}
		while (false);
		do
		{
			final double _ = this.G[i];
			this.G[i] = this.G[j];
			this.G[j] = _;
		}
		while (false);
		do
		{
			final byte _ = this.alpha_status[i];
			this.alpha_status[i] = this.alpha_status[j];
			this.alpha_status[j] = _;
		}
		while (false);
		do
		{
			final double _ = this.alpha[i];
			this.alpha[i] = this.alpha[j];
			this.alpha[j] = _;
		}
		while (false);
		do
		{
			final double _ = this.b[i];
			this.b[i] = this.b[j];
			this.b[j] = _;
		}
		while (false);
		do
		{
			final int _ = this.active_set[i];
			this.active_set[i] = this.active_set[j];
			this.active_set[j] = _;
		}
		while (false);
		do
		{
			final double _ = this.G_bar[i];
			this.G_bar[i] = this.G_bar[j];
			this.G_bar[j] = _;
		}
		while (false);
	}

	void reconstruct_gradient()
	{
		// reconstruct inactive elements of G from G_bar and free variables

		if (this.active_size == this.l)
			return;

		int i;
		for (i = this.active_size; i < this.l; i++)
			this.G[i] = this.G_bar[i] + this.b[i];

		for (i = 0; i < this.active_size; i++)
			if (is_free(i))
			{
				final float[] Q_i = this.Q.get_Q(i, this.l);
				final double alpha_i = this.alpha[i];
				for (int j = this.active_size; j < this.l; j++)
					this.G[j] += alpha_i * Q_i[j];
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
		this.l = l1;
		this.Q = Q1;
		this.b = b_.clone();
		this.y = y_.clone();
		this.alpha = alpha_.clone();
		this.Cp = Cp1;
		this.Cn = Cn1;
		this.eps = eps1;
		this.unshrinked = false;

		// initialize alpha_status
		{
			this.alpha_status = new byte[l1];
			for (int i = 0; i < l1; i++)
				update_alpha_status(i);
		}

		// initialize active set (for shrinking)
		{
			this.active_set = new int[l1];
			for (int i = 0; i < l1; i++)
				this.active_set[i] = i;
			this.active_size = l1;
		}

		// initialize gradient
		{
			this.G = new double[l1];
			this.G_bar = new double[l1];
			int i;
			for (i = 0; i < l1; i++)
			{
				this.G[i] = this.b[i];
				this.G_bar[i] = 0;
			}
			for (i = 0; i < l1; i++)
				if (!is_lower_bound(i))
				{
					final float[] Q_i = Q1.get_Q(i, l1);
					final double alpha_i = this.alpha[i];
					int j;
					for (j = 0; j < l1; j++)
						this.G[j] += alpha_i * Q_i[j];
					if (is_upper_bound(i))
						for (j = 0; j < l1; j++)
							this.G_bar[j] += get_C(i) * Q_i[j];
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
				this.active_size = l1;
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

			float[] Q_i = Q1.get_Q(i, this.active_size);
			float[] Q_j = Q1.get_Q(j, this.active_size);

			final double C_i = get_C(i);
			final double C_j = get_C(j);

			final double old_alpha_i = this.alpha[i];
			final double old_alpha_j = this.alpha[j];

			if (this.y[i] != this.y[j])
			{
				final double delta = (-this.G[i] - this.G[j]) / Math.max(Q_i[i] + Q_j[j] + 2 * Q_i[j], 0);
				double diff = this.alpha[i] - this.alpha[j];
				this.alpha[i] += delta;
				this.alpha[j] += delta;

				if (diff > 0)
				{
					if (this.alpha[j] < 0)
					{
						this.alpha[j] = 0;
						this.alpha[i] = diff;
					}
				}
				else if (this.alpha[i] < 0)
				{
					this.alpha[i] = 0;
					this.alpha[j] = -diff;
				}
				if (diff > C_i - C_j)
				{
					if (this.alpha[i] > C_i)
					{
						this.alpha[i] = C_i;
						this.alpha[j] = C_i - diff;
					}
				}
				else if (this.alpha[j] > C_j)
				{
					this.alpha[j] = C_j;
					this.alpha[i] = C_j + diff;
				}
			}
			else
			{
				final double delta = (this.G[i] - this.G[j]) / Math.max(Q_i[i] + Q_j[j] - 2 * Q_i[j], 0);
				final double sum = this.alpha[i] + this.alpha[j];
				this.alpha[i] -= delta;
				this.alpha[j] += delta;
				if (sum > C_i)
				{
					if (this.alpha[i] > C_i)
					{
						this.alpha[i] = C_i;
						this.alpha[j] = sum - C_i;
					}
				}
				else if (this.alpha[j] < 0)
				{
					this.alpha[j] = 0;
					this.alpha[i] = sum;
				}
				if (sum > C_j)
				{
					if (this.alpha[j] > C_j)
					{
						this.alpha[j] = C_j;
						this.alpha[i] = sum - C_j;
					}
				}
				else if (this.alpha[i] < 0)
				{
					this.alpha[i] = 0;
					this.alpha[j] = sum;
				}
			}

			// update G

			final double delta_alpha_i = this.alpha[i] - old_alpha_i;
			final double delta_alpha_j = this.alpha[j] - old_alpha_j;

			for (int k = 0; k < this.active_size; k++)
				this.G[k] += Q_i[k] * delta_alpha_i + Q_j[k] * delta_alpha_j;

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
							this.G_bar[k] -= C_i * Q_i[k];
					else
						for (k = 0; k < l1; k++)
							this.G_bar[k] += C_i * Q_i[k];
				}

				if (uj != is_upper_bound(j))
				{
					Q_j = Q1.get_Q(j, l1);
					if (uj)
						for (k = 0; k < l1; k++)
							this.G_bar[k] -= C_j * Q_j[k];
					else
						for (k = 0; k < l1; k++)
							this.G_bar[k] += C_j * Q_j[k];
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
				v += this.alpha[i] * (this.G[i] + this.b[i]);

			si.obj = v / 2;
		}

		// put back the solution
		{
			for (int i = 0; i < l1; i++)
				alpha_[this.active_set[i]] = this.alpha[i];
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

		for (int i = 0; i < this.active_size; i++)
			if (this.y[i] == +1) // mClass = +1
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
			// mClass = -1
			{
				if (!is_upper_bound(i))
					if (-this.G[i] > Gmax2)
					{
						Gmax2 = -this.G[i];
						Gmax2_idx = i;
					}
				if (!is_lower_bound(i))
					if (this.G[i] > Gmax1)
					{
						Gmax1 = this.G[i];
						Gmax1_idx = i;
					}
			}

		if (Gmax1 + Gmax2 < this.eps)
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
		final double Gm1 = -this.y[j] * this.G[j];
		final double Gm2 = this.y[i] * this.G[i];

		// shrink

		for (k = 0; k < this.active_size; k++)
		{
			if (is_lower_bound(k))
			{
				if (this.y[k] == +1)
				{
					if (-this.G[k] >= Gm1)
						continue;
				}
				else if (-this.G[k] >= Gm2)
					continue;
			}
			else if (is_upper_bound(k))
			{
				if (this.y[k] == +1)
				{
					if (this.G[k] >= Gm2)
						continue;
				}
				else if (this.G[k] >= Gm1)
					continue;
			}
			else
				continue;

			--this.active_size;
			swap_index(k, this.active_size);
			--k; // look at the newcomer
		}

		// unshrink, check all variables again before final iterations

		if (this.unshrinked || (-(Gm1 + Gm2) > this.eps * 10))
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
				else if (-this.G[k] < Gm2)
					continue;
			}
			else if (is_upper_bound(k))
			{
				if (this.y[k] == +1)
				{
					if (this.G[k] < Gm2)
						continue;
				}
				else if (this.G[k] < Gm1)
					continue;
			}
			else
				continue;

			swap_index(k, this.active_size);
			this.active_size++;
			++k; // look at the newcomer
		}
	}

	double calculate_rho()
	{
		double r;
		int nr_free = 0;
		double ub = INF, lb = -INF, sum_free = 0;
		for (int i = 0; i < this.active_size; i++)
		{
			final double yG = this.y[i] * this.G[i];

			if (is_lower_bound(i))
			{
				if (this.y[i] > 0)
					ub = Math.min(ub, yG);
				else
					lb = Math.max(lb, yG);
			}
			else if (is_upper_bound(i))
			{
				if (this.y[i] < 0)
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