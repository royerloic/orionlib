package org.royerloic.ml.svm.libsvm;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class SVM
{
	//
	// construct and solve various formulations
	//
	private static void solve_c_svc(Problem prob,
																	Parameter param,
																	double[] alpha,
																	Solver.SolutionInfo si,
																	double Cp,
																	double Cn)
	{
		int l = prob.mNumberOfVectors;
		double[] minus_ones = new double[l];
		byte[] y = new byte[l];

		int i;

		for (i = 0; i < l; i++)
		{
			alpha[i] = 0;
			minus_ones[i] = -1;
			if (prob.mClass[i] > 0)
				y[i] = +1;
			else
				y[i] = -1;
		}

		Solver s = new Solver();
		s.Solve(l, new SVC_Q(prob, param, y), minus_ones, y, alpha, Cp, Cn, param.eps, si, param.shrinking);

		double sum_alpha = 0;
		for (i = 0; i < l; i++)
			sum_alpha += alpha[i];

		// if (Cp == Cn)
		// System.out.print("nu = " + sum_alpha / (Cp * prob.mNumberOfVectors) +
		// "\n");

		for (i = 0; i < l; i++)
			alpha[i] *= y[i];
	}

	private static void solve_nu_svc(Problem prob, Parameter param, double[] alpha, Solver.SolutionInfo si)
	{
		int i;
		int l = prob.mNumberOfVectors;
		double nu = param.nu;

		byte[] y = new byte[l];

		for (i = 0; i < l; i++)
			if (prob.mClass[i] > 0)
				y[i] = +1;
			else
				y[i] = -1;

		double sum_pos = nu * l / 2;
		double sum_neg = nu * l / 2;

		for (i = 0; i < l; i++)
			if (y[i] == +1)
			{
				alpha[i] = Math.min(1.0, sum_pos);
				sum_pos -= alpha[i];
			}
			else
			{
				alpha[i] = Math.min(1.0, sum_neg);
				sum_neg -= alpha[i];
			}

		double[] zeros = new double[l];

		for (i = 0; i < l; i++)
			zeros[i] = 0;

		Solver_NU s = new Solver_NU();
		s.Solve(l, new SVC_Q(prob, param, y), zeros, y, alpha, 1.0, 1.0, param.eps, si, param.shrinking);
		double r = si.r;

		// System.out.print("C = " + 1 / r + "\n");

		for (i = 0; i < l; i++)
			alpha[i] *= y[i] / r;

		si.rho /= r;
		si.obj /= (r * r);
		si.upper_bound_p = 1 / r;
		si.upper_bound_n = 1 / r;
	}

	private static void solve_one_class(Problem prob, Parameter param, double[] alpha, Solver.SolutionInfo si)
	{
		int l = prob.mNumberOfVectors;
		double[] zeros = new double[l];
		byte[] ones = new byte[l];
		int i;

		int n = (int) (param.nu * prob.mNumberOfVectors); // # of alpha's at upper
		// bound

		for (i = 0; i < n; i++)
			alpha[i] = 1;
		alpha[n] = param.nu * prob.mNumberOfVectors - n;
		for (i = n + 1; i < l; i++)
			alpha[i] = 0;

		for (i = 0; i < l; i++)
		{
			zeros[i] = 0;
			ones[i] = 1;
		}

		Solver s = new Solver();
		s.Solve(l, new ONE_CLASS_Q(prob, param), zeros, ones, alpha, 1.0, 1.0, param.eps, si, param.shrinking);
	}

	private static void solve_epsilon_svr(Problem prob, Parameter param, double[] alpha, Solver.SolutionInfo si)
	{
		int l = prob.mNumberOfVectors;
		double[] alpha2 = new double[2 * l];
		double[] linear_term = new double[2 * l];
		byte[] y = new byte[2 * l];
		int i;

		for (i = 0; i < l; i++)
		{
			alpha2[i] = 0;
			linear_term[i] = param.p - prob.mClass[i];
			y[i] = 1;

			alpha2[i + l] = 0;
			linear_term[i + l] = param.p + prob.mClass[i];
			y[i + l] = -1;
		}

		Solver s = new Solver();
		s.Solve(2 * l, new SVR_Q(prob, param), linear_term, y, alpha2, param.C, param.C, param.eps, si,
				param.shrinking);

		double sum_alpha = 0;
		for (i = 0; i < l; i++)
		{
			alpha[i] = alpha2[i] - alpha2[i + l];
			sum_alpha += Math.abs(alpha[i]);
		}
		// System.out.print("nu = " + sum_alpha / (param.C * l) + "\n");
	}

	private static void solve_nu_svr(Problem prob, Parameter param, double[] alpha, Solver.SolutionInfo si)
	{
		int l = prob.mNumberOfVectors;
		double C = param.C;
		double[] alpha2 = new double[2 * l];
		double[] linear_term = new double[2 * l];
		byte[] y = new byte[2 * l];
		int i;

		double sum = C * param.nu * l / 2;
		for (i = 0; i < l; i++)
		{
			alpha2[i] = alpha2[i + l] = Math.min(sum, C);
			sum -= alpha2[i];

			linear_term[i] = -prob.mClass[i];
			y[i] = 1;

			linear_term[i + l] = prob.mClass[i];
			y[i + l] = -1;
		}

		Solver_NU s = new Solver_NU();
		s.Solve(2 * l, new SVR_Q(prob, param), linear_term, y, alpha2, C, C, param.eps, si, param.shrinking);

		// System.out.print("epsilon = " + (-si.r) + "\n");

		for (i = 0; i < l; i++)
			alpha[i] = alpha2[i] - alpha2[i + l];
	}

	//
	// decision_function
	//
	static class decision_function
	{
		double[]	alpha;

		double		rho;
	};

	static decision_function svmTrainOne(Problem prob, Parameter param, double Cp, double Cn)
	{
		double[] alpha = new double[prob.mNumberOfVectors];
		Solver.SolutionInfo si = new Solver.SolutionInfo();
		switch (param.svm_type)
		{
			case Parameter.C_SVC:
				solve_c_svc(prob, param, alpha, si, Cp, Cn);
				break;
			case Parameter.NU_SVC:
				solve_nu_svc(prob, param, alpha, si);
				break;
			case Parameter.ONE_CLASS:
				solve_one_class(prob, param, alpha, si);
				break;
			case Parameter.EPSILON_SVR:
				solve_epsilon_svr(prob, param, alpha, si);
				break;
			case Parameter.NU_SVR:
				solve_nu_svr(prob, param, alpha, si);
				break;
		}

		// System.out.print("obj = " + si.obj + ", mRho = " + si.rho + "\n");

		// output SVs

		int nSV = 0;
		int nBSV = 0;
		for (int i = 0; i < prob.mNumberOfVectors; i++)
		{
			if (Math.abs(alpha[i]) > 0)
			{
				++nSV;
				if (prob.mClass[i] > 0)
				{
					if (Math.abs(alpha[i]) >= si.upper_bound_p)
						++nBSV;
				}
				else
				{
					if (Math.abs(alpha[i]) >= si.upper_bound_n)
						++nBSV;
				}
			}
		}

		// System.out.print("mNumberOfSupportVectorsPerClass = "+ nSV + ", nBSV = "
		// + nBSV + "\n");

		decision_function f = new decision_function();
		f.alpha = alpha;
		f.rho = si.rho;
		return f;
	}

	// Platt's binary SVM Probablistic Output: an improvement from Lin et al.
	private static void sigmoid_train(int l, double[] dec_values, double[] labels, double[] probAB)
	{
		double A, B;
		double prior1 = 0, prior0 = 0;
		int i;

		for (i = 0; i < l; i++)
			if (labels[i] > 0)
				prior1 += 1;
			else
				prior0 += 1;

		int max_iter = 100; // Maximal number of iterations
		double min_step = 1e-10; // Minimal step taken in line search
		double sigma = 1e-3; // For numerically strict PD of Hessian
		double eps = 1e-5;
		double hiTarget = (prior1 + 1.0) / (prior1 + 2.0);
		double loTarget = 1 / (prior0 + 2.0);
		double[] t = new double[l];
		double fApB, p, q, h11, h22, h21, g1, g2, det, dA, dB, gd, stepsize;
		double newA, newB, newf, d1, d2;
		int iter;

		// Initial Point and Initial Fun Value
		A = 0.0;
		B = Math.log((prior0 + 1.0) / (prior1 + 1.0));
		double fval = 0.0;

		for (i = 0; i < l; i++)
		{
			if (labels[i] > 0)
				t[i] = hiTarget;
			else
				t[i] = loTarget;
			fApB = dec_values[i] * A + B;
			if (fApB >= 0)
				fval += t[i] * fApB + Math.log(1 + Math.exp(-fApB));
			else
				fval += (t[i] - 1) * fApB + Math.log(1 + Math.exp(fApB));
		}
		for (iter = 0; iter < max_iter; iter++)
		{
			// Update Gradient and Hessian (use H' = H + sigma I)
			h11 = sigma; // numerically ensures strict PD
			h22 = sigma;
			h21 = 0.0;
			g1 = 0.0;
			g2 = 0.0;
			for (i = 0; i < l; i++)
			{
				fApB = dec_values[i] * A + B;
				if (fApB >= 0)
				{
					p = Math.exp(-fApB) / (1.0 + Math.exp(-fApB));
					q = 1.0 / (1.0 + Math.exp(-fApB));
				}
				else
				{
					p = 1.0 / (1.0 + Math.exp(fApB));
					q = Math.exp(fApB) / (1.0 + Math.exp(fApB));
				}
				d2 = p * q;
				h11 += dec_values[i] * dec_values[i] * d2;
				h22 += d2;
				h21 += dec_values[i] * d2;
				d1 = t[i] - p;
				g1 += dec_values[i] * d1;
				g2 += d1;
			}

			// Stopping Criteria
			if (Math.abs(g1) < eps && Math.abs(g2) < eps)
				break;

			// Finding Newton direction: -inv(H') * g
			det = h11 * h22 - h21 * h21;
			dA = -(h22 * g1 - h21 * g2) / det;
			dB = -(-h21 * g1 + h11 * g2) / det;
			gd = g1 * dA + g2 * dB;

			stepsize = 1; // Line Search
			while (stepsize >= min_step)
			{
				newA = A + stepsize * dA;
				newB = B + stepsize * dB;

				// New function mValue
				newf = 0.0;
				for (i = 0; i < l; i++)
				{
					fApB = dec_values[i] * newA + newB;
					if (fApB >= 0)
						newf += t[i] * fApB + Math.log(1 + Math.exp(-fApB));
					else
						newf += (t[i] - 1) * fApB + Math.log(1 + Math.exp(fApB));
				}
				// Check sufficient decrease
				if (newf < fval + 0.0001 * stepsize * gd)
				{
					A = newA;
					B = newB;
					fval = newf;
					break;
				}
				else
					stepsize = stepsize / 2.0;
			}

			if (stepsize < min_step)
			{
				System.err.print("Line search fails in two-class probability estimates\n");
				break;
			}
		}

		if (iter >= max_iter)
			System.err.print("Reaching maximal iterations in two-class probability estimates\n");
		probAB[0] = A;
		probAB[1] = B;
	}

	private static double sigmoid_predict(double decision_value, double A, double B)
	{
		double fApB = decision_value * A + B;
		if (fApB >= 0)
			return Math.exp(-fApB) / (1.0 + Math.exp(-fApB));
		else
			return 1.0 / (1 + Math.exp(fApB));
	}

	// Method 2 from the multiclass_prob paper by Wu, Lin, and Weng
	private static void multiclass_probability(int k, double[][] r, double[] p)
	{
		int t;
		int iter = 0, max_iter = 100;
		double[][] Q = new double[k][k];
		double[] Qp = new double[k];
		double pQp, eps = 0.001;

		for (t = 0; t < k; t++)
		{
			p[t] = 1.0 / k; // Valid if k = 1
			Q[t][t] = 0;
			for (int j = 0; j < t; j++)
			{
				Q[t][t] += r[j][t] * r[j][t];
				Q[t][j] = Q[j][t];
			}
			for (int j = t + 1; j < k; j++)
			{
				Q[t][t] += r[j][t] * r[j][t];
				Q[t][j] = -r[j][t] * r[t][j];
			}
		}
		for (iter = 0; iter < max_iter; iter++)
		{
			// stopping condition, recalculate QP,pQP for numerical accuracy
			pQp = 0;
			for (t = 0; t < k; t++)
			{
				Qp[t] = 0;
				for (int j = 0; j < k; j++)
					Qp[t] += Q[t][j] * p[j];
				pQp += p[t] * Qp[t];
			}
			double max_error = 0;
			for (t = 0; t < k; t++)
			{
				double error = Math.abs(Qp[t] - pQp);
				if (error > max_error)
					max_error = error;
			}
			if (max_error < eps)
				break;

			for (t = 0; t < k; t++)
			{
				double diff = (-Qp[t] + pQp) / Q[t][t];
				p[t] += diff;
				pQp = (pQp + diff * (diff * Q[t][t] + 2 * Qp[t])) / (1 + diff) / (1 + diff);
				for (int j = 0; j < k; j++)
				{
					Qp[j] = (Qp[j] + diff * Q[t][j]) / (1 + diff);
					p[j] /= (1 + diff);
				}
			}
		}
		if (iter >= max_iter)
			System.err.print("Exceeds max_iter in multiclass_prob\n");
	}

	// Cross-validation decision values for probability estimates
	private static void svm_binary_svc_probability(	Problem prob,
																									Parameter param,
																									double Cp,
																									double Cn,
																									double[] probAB)
	{
		int i;
		int nr_fold = 5;
		int[] perm = new int[prob.mNumberOfVectors];
		double[] dec_values = new double[prob.mNumberOfVectors];

		// random shuffle
		for (i = 0; i < prob.mNumberOfVectors; i++)
			perm[i] = i;
		for (i = 0; i < prob.mNumberOfVectors; i++)
		{
			int j = i + (int) (Math.random() * (prob.mNumberOfVectors - i));
			do
			{
				int _ = perm[i];
				perm[i] = perm[j];
				perm[j] = _;
			}
			while (false);
		}
		for (i = 0; i < nr_fold; i++)
		{
			int begin = i * prob.mNumberOfVectors / nr_fold;
			int end = (i + 1) * prob.mNumberOfVectors / nr_fold;
			int j, k;
			Problem subprob = new Problem();

			subprob.mNumberOfVectors = prob.mNumberOfVectors - (end - begin);
			subprob.mVectorsTable = new Node[subprob.mNumberOfVectors][];
			subprob.mClass = new double[subprob.mNumberOfVectors];

			k = 0;
			for (j = 0; j < begin; j++)
			{
				subprob.mVectorsTable[k] = prob.mVectorsTable[perm[j]];
				subprob.mClass[k] = prob.mClass[perm[j]];
				++k;
			}
			for (j = end; j < prob.mNumberOfVectors; j++)
			{
				subprob.mVectorsTable[k] = prob.mVectorsTable[perm[j]];
				subprob.mClass[k] = prob.mClass[perm[j]];
				++k;
			}
			int p_count = 0, n_count = 0;
			for (j = 0; j < k; j++)
				if (subprob.mClass[j] > 0)
					p_count++;
				else
					n_count++;

			if (p_count == 0 && n_count == 0)
				for (j = begin; j < end; j++)
					dec_values[perm[j]] = 0;
			else if (p_count > 0 && n_count == 0)
				for (j = begin; j < end; j++)
					dec_values[perm[j]] = 1;
			else if (p_count == 0 && n_count > 0)
				for (j = begin; j < end; j++)
					dec_values[perm[j]] = -1;
			else
			{
				Parameter subparam = (Parameter) param.clone();
				subparam.probability = 0;
				subparam.C = 1.0;
				subparam.nr_weight = 2;
				subparam.weight_label = new int[2];
				subparam.weight = new double[2];
				subparam.weight_label[0] = +1;
				subparam.weight_label[1] = -1;
				subparam.weight[0] = Cp;
				subparam.weight[1] = Cn;
				Model submodel = svmTrain(subprob, subparam);
				for (j = begin; j < end; j++)
				{
					double[] dec_value = new double[1];
					svmPredictValues(submodel, prob.mVectorsTable[perm[j]], dec_value);
					dec_values[perm[j]] = dec_value[0];
					// ensure +1 -1 order; reason not using CV subroutine
					dec_values[perm[j]] *= submodel.mSVCLabelsTable[0];
				}
			}
		}
		sigmoid_train(prob.mNumberOfVectors, dec_values, prob.mClass, probAB);
	}

	// Return parameter of a Laplace distribution
	private static double svm_svr_probability(Problem prob, Parameter param)
	{
		int i;
		int nr_fold = 5;
		double[] ymv = new double[prob.mNumberOfVectors];
		double mae = 0;

		Parameter newparam = (Parameter) param.clone();
		newparam.probability = 0;
		svmCrossValidation(prob, newparam, nr_fold, ymv);
		for (i = 0; i < prob.mNumberOfVectors; i++)
		{
			ymv[i] = prob.mClass[i] - ymv[i];
			mae += Math.abs(ymv[i]);
		}
		mae /= prob.mNumberOfVectors;
		double std = Math.sqrt(2 * mae * mae);
		int count = 0;
		mae = 0;
		for (i = 0; i < prob.mNumberOfVectors; i++)
			if (Math.abs(ymv[i]) > 5 * std)
				count = count + 1;
			else
				mae += Math.abs(ymv[i]);
		mae /= (prob.mNumberOfVectors - count);
		System.err
				.print("Prob. model for test data: target mValue = predicted mValue + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="
						+ mae + "\n");
		return mae;
	}

	//
	// Interface functions
	//
	public static Model svmTrain(Problem prob, Parameter param)
	{
		Model model = new Model();
		model.mParameter = param;

		if (param.svm_type == Parameter.ONE_CLASS || param.svm_type == Parameter.EPSILON_SVR
				|| param.svm_type == Parameter.NU_SVR)
		{
			// regression or one-class-SVM
			model.mNumberOfClasses = 2;
			model.mSVCLabelsTable = null;
			model.mNumberOfSupportVectorsPerClass = null;
			model.mPairwiseProbability = null;
			model.probB = null;
			model.mSupportVectorCoeficientsTable = new double[1][];

			if (param.probability == 1
					&& (param.svm_type == Parameter.EPSILON_SVR || param.svm_type == Parameter.NU_SVR))
			{
				model.mPairwiseProbability = new double[1];
				model.mPairwiseProbability[0] = svm_svr_probability(prob, param);
			}

			decision_function f = svmTrainOne(prob, param, 0, 0);
			model.mRho = new double[1];
			model.mRho[0] = f.rho;

			int nSV = 0;
			int i;
			for (i = 0; i < prob.mNumberOfVectors; i++)
				if (Math.abs(f.alpha[i]) > 0)
					++nSV;
			model.mNumberOfSupportVectors = nSV;
			model.mSupportVectorsTable = new Node[nSV][];
			model.mSupportVectorCoeficientsTable[0] = new double[nSV];
			int j = 0;
			for (i = 0; i < prob.mNumberOfVectors; i++)
				if (Math.abs(f.alpha[i]) > 0)
				{
					model.mSupportVectorsTable[j] = prob.mVectorsTable[i];
					model.mSupportVectorCoeficientsTable[0][j] = f.alpha[i];
					++j;
				}
		}
		else
		{
			// classification
			// find out the number of classes
			int l = prob.mNumberOfVectors;
			int max_nr_class = 16;
			int nr_class = 0;
			int[] label = new int[max_nr_class];
			int[] count = new int[max_nr_class];
			int[] index = new int[l];

			int i;
			for (i = 0; i < l; i++)
			{
				int this_label = (int) prob.mClass[i];
				int j;
				for (j = 0; j < nr_class; j++)
					if (this_label == label[j])
					{
						++count[j];
						break;
					}
				index[i] = j;
				if (j == nr_class)
				{
					if (nr_class == max_nr_class)
					{
						max_nr_class *= 2;
						int[] new_data = new int[max_nr_class];
						System.arraycopy(label, 0, new_data, 0, label.length);
						label = new_data;

						new_data = new int[max_nr_class];
						System.arraycopy(count, 0, new_data, 0, count.length);
						count = new_data;
					}
					label[nr_class] = this_label;
					count[nr_class] = 1;
					++nr_class;
				}
			}

			// group training data of the same class

			int[] start = new int[nr_class];
			start[0] = 0;
			for (i = 1; i < nr_class; i++)
				start[i] = start[i - 1] + count[i - 1];

			Node[][] x = new Node[l][];

			for (i = 0; i < l; i++)
			{
				x[start[index[i]]] = prob.mVectorsTable[i];
				++start[index[i]];
			}

			start[0] = 0;
			for (i = 1; i < nr_class; i++)
				start[i] = start[i - 1] + count[i - 1];

			// calculate weighted C

			double[] weighted_C = new double[nr_class];
			for (i = 0; i < nr_class; i++)
				weighted_C[i] = param.C;
			for (i = 0; i < param.nr_weight; i++)
			{
				int j;
				for (j = 0; j < nr_class; j++)
					if (param.weight_label[i] == label[j])
						break;
				if (j == nr_class)
					System.err.print("warning: class mSVCLabelsTable " + param.weight_label[i]
							+ " specified in weight is not found\n");
				else
					weighted_C[j] *= param.weight[i];
			}

			// train k*(k-1)/2 models

			boolean[] nonzero = new boolean[l];
			for (i = 0; i < l; i++)
				nonzero[i] = false;
			decision_function[] f = new decision_function[nr_class * (nr_class - 1) / 2];

			double[] probA = null, probB = null;
			if (param.probability == 1)
			{
				probA = new double[nr_class * (nr_class - 1) / 2];
				probB = new double[nr_class * (nr_class - 1) / 2];
			}

			int p = 0;
			for (i = 0; i < nr_class; i++)
				for (int j = i + 1; j < nr_class; j++)
				{
					Problem sub_prob = new Problem();
					int si = start[i], sj = start[j];
					int ci = count[i], cj = count[j];
					sub_prob.mNumberOfVectors = ci + cj;
					sub_prob.mVectorsTable = new Node[sub_prob.mNumberOfVectors][];
					sub_prob.mClass = new double[sub_prob.mNumberOfVectors];
					int k;
					for (k = 0; k < ci; k++)
					{
						sub_prob.mVectorsTable[k] = x[si + k];
						sub_prob.mClass[k] = +1;
					}
					for (k = 0; k < cj; k++)
					{
						sub_prob.mVectorsTable[ci + k] = x[sj + k];
						sub_prob.mClass[ci + k] = -1;
					}

					if (param.probability == 1)
					{
						double[] probAB = new double[2];
						svm_binary_svc_probability(sub_prob, param, weighted_C[i], weighted_C[j], probAB);
						probA[p] = probAB[0];
						probB[p] = probAB[1];
					}

					f[p] = svmTrainOne(sub_prob, param, weighted_C[i], weighted_C[j]);
					for (k = 0; k < ci; k++)
						if (!nonzero[si + k] && Math.abs(f[p].alpha[k]) > 0)
							nonzero[si + k] = true;
					for (k = 0; k < cj; k++)
						if (!nonzero[sj + k] && Math.abs(f[p].alpha[ci + k]) > 0)
							nonzero[sj + k] = true;
					++p;
				}

			// build output

			model.mNumberOfClasses = nr_class;

			model.mSVCLabelsTable = new int[nr_class];
			for (i = 0; i < nr_class; i++)
				model.mSVCLabelsTable[i] = label[i];

			model.mRho = new double[nr_class * (nr_class - 1) / 2];
			for (i = 0; i < nr_class * (nr_class - 1) / 2; i++)
				model.mRho[i] = f[i].rho;

			if (param.probability == 1)
			{
				model.mPairwiseProbability = new double[nr_class * (nr_class - 1) / 2];
				model.probB = new double[nr_class * (nr_class - 1) / 2];
				for (i = 0; i < nr_class * (nr_class - 1) / 2; i++)
				{
					model.mPairwiseProbability[i] = probA[i];
					model.probB[i] = probB[i];
				}
			}
			else
			{
				model.mPairwiseProbability = null;
				model.probB = null;
			}

			int nnz = 0;
			int[] nz_count = new int[nr_class];
			model.mNumberOfSupportVectorsPerClass = new int[nr_class];
			for (i = 0; i < nr_class; i++)
			{
				int nSV = 0;
				for (int j = 0; j < count[i]; j++)
					if (nonzero[start[i] + j])
					{
						++nSV;
						++nnz;
					}
				model.mNumberOfSupportVectorsPerClass[i] = nSV;
				nz_count[i] = nSV;
			}

			// System.out.print("Total mNumberOfSupportVectorsPerClass = " + nnz +
			// "\n");

			model.mNumberOfSupportVectors = nnz;
			model.mSupportVectorsTable = new Node[nnz][];
			p = 0;
			for (i = 0; i < l; i++)
				if (nonzero[i])
					model.mSupportVectorsTable[p++] = x[i];

			int[] nz_start = new int[nr_class];
			nz_start[0] = 0;
			for (i = 1; i < nr_class; i++)
				nz_start[i] = nz_start[i - 1] + nz_count[i - 1];

			model.mSupportVectorCoeficientsTable = new double[nr_class - 1][];
			for (i = 0; i < nr_class - 1; i++)
				model.mSupportVectorCoeficientsTable[i] = new double[nnz];

			p = 0;
			for (i = 0; i < nr_class; i++)
				for (int j = i + 1; j < nr_class; j++)
				{
					// classifier (i,j): coefficients with
					// i are in mSupportVectorCoeficientsTable[j-1][nz_start[i]...],
					// j are in mSupportVectorCoeficientsTable[i][nz_start[j]...]

					int si = start[i];
					int sj = start[j];
					int ci = count[i];
					int cj = count[j];

					int q = nz_start[i];
					int k;
					for (k = 0; k < ci; k++)
						if (nonzero[si + k])
							model.mSupportVectorCoeficientsTable[j - 1][q++] = f[p].alpha[k];
					q = nz_start[j];
					for (k = 0; k < cj; k++)
						if (nonzero[sj + k])
							model.mSupportVectorCoeficientsTable[i][q++] = f[p].alpha[ci + k];
					++p;
				}
		}
		return model;
	}

	public static void svmCrossValidation(Problem prob, Parameter param, int nr_fold, double[] target)
	{
		int i;
		int[] perm = new int[prob.mNumberOfVectors];

		// random shuffle
		for (i = 0; i < prob.mNumberOfVectors; i++)
			perm[i] = i;
		for (i = 0; i < prob.mNumberOfVectors; i++)
		{
			int j = i + (int) (Math.random() * (prob.mNumberOfVectors - i));
			do
			{
				int _ = perm[i];
				perm[i] = perm[j];
				perm[j] = _;
			}
			while (false);
		}
		for (i = 0; i < nr_fold; i++)
		{
			int begin = i * prob.mNumberOfVectors / nr_fold;
			int end = (i + 1) * prob.mNumberOfVectors / nr_fold;
			int j, k;
			Problem subprob = new Problem();

			subprob.mNumberOfVectors = prob.mNumberOfVectors - (end - begin);
			subprob.mVectorsTable = new Node[subprob.mNumberOfVectors][];
			subprob.mClass = new double[subprob.mNumberOfVectors];

			k = 0;
			for (j = 0; j < begin; j++)
			{
				subprob.mVectorsTable[k] = prob.mVectorsTable[perm[j]];
				subprob.mClass[k] = prob.mClass[perm[j]];
				++k;
			}
			for (j = end; j < prob.mNumberOfVectors; j++)
			{
				subprob.mVectorsTable[k] = prob.mVectorsTable[perm[j]];
				subprob.mClass[k] = prob.mClass[perm[j]];
				++k;
			}
			Model submodel = svmTrain(subprob, param);
			if (param.probability == 1 && (param.svm_type == Parameter.C_SVC || param.svm_type == Parameter.NU_SVC))
			{
				double[] prob_estimates = new double[svmGetNumberOfClasses(submodel)];
				for (j = begin; j < end; j++)
					target[perm[j]] = svmPredictProbability(submodel, prob.mVectorsTable[perm[j]], prob_estimates);
			}
			else
				for (j = begin; j < end; j++)
					target[perm[j]] = svmPredict(submodel, prob.mVectorsTable[perm[j]]);
		}
	}

	public static int svmGetSvmType(Model model)
	{
		return model.mParameter.svm_type;
	}

	public static int svmGetNumberOfClasses(Model model)
	{
		return model.mNumberOfClasses;
	}

	public static void svmGetLabels(Model model, int[] label)
	{
		if (model.mSVCLabelsTable != null)
			for (int i = 0; i < model.mNumberOfClasses; i++)
				label[i] = model.mSVCLabelsTable[i];
	}

	public static double svmGetSvrProbability(Model model)
	{
		if ((model.mParameter.svm_type == Parameter.EPSILON_SVR || model.mParameter.svm_type == Parameter.NU_SVR)
				&& model.mPairwiseProbability != null)
			return model.mPairwiseProbability[0];
		else
		{
			System.err.print("Model doesn't contain information for SVR probability inference\n");
			return 0;
		}
	}

	public static void svmPredictValues(Model model, Node[] x, double[] dec_values)
	{
		if (model.mParameter.svm_type == Parameter.ONE_CLASS
				|| model.mParameter.svm_type == Parameter.EPSILON_SVR
				|| model.mParameter.svm_type == Parameter.NU_SVR)
		{
			double[] sv_coef = model.mSupportVectorCoeficientsTable[0];
			double sum = 0;
			for (int i = 0; i < model.mNumberOfSupportVectors; i++)
				sum += sv_coef[i] * Kernel.k_function(x, model.mSupportVectorsTable[i], model.mParameter);
			sum -= model.mRho[0];
			dec_values[0] = sum;
		}
		else
		{
			int i;
			int nr_class = model.mNumberOfClasses;
			int l = model.mNumberOfSupportVectors;

			double[] kvalue = new double[l];
			for (i = 0; i < l; i++)
				kvalue[i] = Kernel.k_function(x, model.mSupportVectorsTable[i], model.mParameter);

			int[] start = new int[nr_class];
			start[0] = 0;
			for (i = 1; i < nr_class; i++)
				start[i] = start[i - 1] + model.mNumberOfSupportVectorsPerClass[i - 1];

			int p = 0;
			int pos = 0;
			for (i = 0; i < nr_class; i++)
				for (int j = i + 1; j < nr_class; j++)
				{
					double sum = 0;
					int si = start[i];
					int sj = start[j];
					int ci = model.mNumberOfSupportVectorsPerClass[i];
					int cj = model.mNumberOfSupportVectorsPerClass[j];

					int k;
					double[] coef1 = model.mSupportVectorCoeficientsTable[j - 1];
					double[] coef2 = model.mSupportVectorCoeficientsTable[i];
					for (k = 0; k < ci; k++)
						sum += coef1[si + k] * kvalue[si + k];
					for (k = 0; k < cj; k++)
						sum += coef2[sj + k] * kvalue[sj + k];
					sum -= model.mRho[p++];
					dec_values[pos++] = sum;
				}
		}
	}

	public static double svmPredict(Model model, Node[] x)
	{
		if (model.mParameter.svm_type == Parameter.ONE_CLASS
				|| model.mParameter.svm_type == Parameter.EPSILON_SVR
				|| model.mParameter.svm_type == Parameter.NU_SVR)
		{
			double[] res = new double[1];
			svmPredictValues(model, x, res);

			if (model.mParameter.svm_type == Parameter.ONE_CLASS)
				return (res[0] > 0) ? 1 : -1;
			else
				return res[0];
		}
		else
		{
			int i;
			int nr_class = model.mNumberOfClasses;
			double[] dec_values = new double[nr_class * (nr_class - 1) / 2];
			svmPredictValues(model, x, dec_values);

			int[] vote = new int[nr_class];
			for (i = 0; i < nr_class; i++)
				vote[i] = 0;
			int pos = 0;
			for (i = 0; i < nr_class; i++)
				for (int j = i + 1; j < nr_class; j++)
				{
					if (dec_values[pos++] > 0)
						++vote[i];
					else
						++vote[j];
				}

			int vote_max_idx = 0;
			for (i = 1; i < nr_class; i++)
				if (vote[i] > vote[vote_max_idx])
					vote_max_idx = i;
			return model.mSVCLabelsTable[vote_max_idx];
		}
	}

	public static double svmPredictProbability(Model model, Node[] x, double[] prob_estimates)
	{
		if ((model.mParameter.svm_type == Parameter.C_SVC || model.mParameter.svm_type == Parameter.NU_SVC)
				&& model.mPairwiseProbability != null && model.probB != null)
		{
			int i;
			int nr_class = model.mNumberOfClasses;
			double[] dec_values = new double[nr_class * (nr_class - 1) / 2];
			svmPredictValues(model, x, dec_values);

			double min_prob = 1e-7;
			double[][] pairwise_prob = new double[nr_class][nr_class];

			int k = 0;
			for (i = 0; i < nr_class; i++)
				for (int j = i + 1; j < nr_class; j++)
				{
					pairwise_prob[i][j] = Math.min(Math.max(sigmoid_predict(dec_values[k],
							model.mPairwiseProbability[k], model.probB[k]), min_prob), 1 - min_prob);
					pairwise_prob[j][i] = 1 - pairwise_prob[i][j];
					k++;
				}
			multiclass_probability(nr_class, pairwise_prob, prob_estimates);

			int prob_max_idx = 0;
			for (i = 1; i < nr_class; i++)
				if (prob_estimates[i] > prob_estimates[prob_max_idx])
					prob_max_idx = i;
			return model.mSVCLabelsTable[prob_max_idx];
		}
		else
			return svmPredict(model, x);
	}

	static final String	svm_type_table[]		=
																					{ "c_svc", "nu_svc", "one_class", "epsilon_svr", "nu_svr", };

	static final String	kernel_type_table[]	=
																					{ "linear", "polynomial", "rbf", "sigmoid", };

	public static void svmSaveModel(String model_file_name, Model model) throws IOException
	{
		DataOutputStream fp = new DataOutputStream(new FileOutputStream(model_file_name));

		Parameter param = model.mParameter;

		fp.writeBytes("svm_type " + svm_type_table[param.svm_type] + "\n");
		fp.writeBytes("kernel_type " + kernel_type_table[param.kernel_type] + "\n");

		if (param.kernel_type == Parameter.POLY)
			fp.writeBytes("degree " + param.degree + "\n");

		if (param.kernel_type == Parameter.POLY || param.kernel_type == Parameter.RBF
				|| param.kernel_type == Parameter.SIGMOID)
			fp.writeBytes("gamma " + param.gamma + "\n");

		if (param.kernel_type == Parameter.POLY || param.kernel_type == Parameter.SIGMOID)
			fp.writeBytes("coef0 " + param.coef0 + "\n");

		int nr_class = model.mNumberOfClasses;
		int l = model.mNumberOfSupportVectors;
		fp.writeBytes("mNumberOfClasses " + nr_class + "\n");
		fp.writeBytes("total_sv " + l + "\n");

		{
			fp.writeBytes("mRho");
			for (int i = 0; i < nr_class * (nr_class - 1) / 2; i++)
				fp.writeBytes(" " + model.mRho[i]);
			fp.writeBytes("\n");
		}

		if (model.mSVCLabelsTable != null)
		{
			fp.writeBytes("mSVCLabelsTable");
			for (int i = 0; i < nr_class; i++)
				fp.writeBytes(" " + model.mSVCLabelsTable[i]);
			fp.writeBytes("\n");
		}

		if (model.mPairwiseProbability != null) // regression has
		// mPairwiseProbability only
		{
			fp.writeBytes("mPairwiseProbability");
			for (int i = 0; i < nr_class * (nr_class - 1) / 2; i++)
				fp.writeBytes(" " + model.mPairwiseProbability[i]);
			fp.writeBytes("\n");
		}
		if (model.probB != null)
		{
			fp.writeBytes("probB");
			for (int i = 0; i < nr_class * (nr_class - 1) / 2; i++)
				fp.writeBytes(" " + model.probB[i]);
			fp.writeBytes("\n");
		}

		if (model.mNumberOfSupportVectorsPerClass != null)
		{
			fp.writeBytes("nr_sv");
			for (int i = 0; i < nr_class; i++)
				fp.writeBytes(" " + model.mNumberOfSupportVectorsPerClass[i]);
			fp.writeBytes("\n");
		}

		fp.writeBytes("mSupportVectorsTable\n");
		double[][] sv_coef = model.mSupportVectorCoeficientsTable;
		Node[][] SV = model.mSupportVectorsTable;

		for (int i = 0; i < l; i++)
		{
			for (int j = 0; j < nr_class - 1; j++)
				fp.writeBytes(sv_coef[j][i] + " ");

			Node[] p = SV[i];
			for (int j = 0; j < p.length; j++)
				fp.writeBytes(p[j].mIndex + ":" + p[j].mValue + " ");
			fp.writeBytes("\n");
		}

		fp.close();
	}

	private static double atof(String s)
	{
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s)
	{
		return Integer.parseInt(s);
	}

	public static Model svmLoadModel(String model_file_name) throws IOException
	{
		BufferedReader fp = new BufferedReader(new FileReader(model_file_name));

		// read parameters

		Model model = new Model();
		Parameter param = new Parameter();
		model.mParameter = param;
		model.mRho = null;
		model.mPairwiseProbability = null;
		model.probB = null;
		model.mSVCLabelsTable = null;
		model.mNumberOfSupportVectorsPerClass = null;

		while (true)
		{
			String cmd = fp.readLine();
			String arg = cmd.substring(cmd.indexOf(' ') + 1);

			if (cmd.startsWith("svm_type"))
			{
				int i;
				for (i = 0; i < svm_type_table.length; i++)
				{
					if (arg.indexOf(svm_type_table[i]) != -1)
					{
						param.svm_type = i;
						break;
					}
				}
				if (i == svm_type_table.length)
				{
					System.err.print("unknown SVM type.\n");
					return null;
				}
			}
			else if (cmd.startsWith("kernel_type"))
			{
				int i;
				for (i = 0; i < kernel_type_table.length; i++)
				{
					if (arg.indexOf(kernel_type_table[i]) != -1)
					{
						param.kernel_type = i;
						break;
					}
				}
				if (i == kernel_type_table.length)
				{
					System.err.print("unknown kernel function.\n");
					return null;
				}
			}
			else if (cmd.startsWith("degree"))
				param.degree = atof(arg);
			else if (cmd.startsWith("gamma"))
				param.gamma = atof(arg);
			else if (cmd.startsWith("coef0"))
				param.coef0 = atof(arg);
			else if (cmd.startsWith("mNumberOfClasses"))
				model.mNumberOfClasses = atoi(arg);
			else if (cmd.startsWith("total_sv"))
				model.mNumberOfSupportVectors = atoi(arg);
			else if (cmd.startsWith("mRho"))
			{
				int n = model.mNumberOfClasses * (model.mNumberOfClasses - 1) / 2;
				model.mRho = new double[n];
				StringTokenizer st = new StringTokenizer(arg);
				for (int i = 0; i < n; i++)
					model.mRho[i] = atof(st.nextToken());
			}
			else if (cmd.startsWith("mSVCLabelsTable"))
			{
				int n = model.mNumberOfClasses;
				model.mSVCLabelsTable = new int[n];
				StringTokenizer st = new StringTokenizer(arg);
				for (int i = 0; i < n; i++)
					model.mSVCLabelsTable[i] = atoi(st.nextToken());
			}
			else if (cmd.startsWith("mPairwiseProbability"))
			{
				int n = model.mNumberOfClasses * (model.mNumberOfClasses - 1) / 2;
				model.mPairwiseProbability = new double[n];
				StringTokenizer st = new StringTokenizer(arg);
				for (int i = 0; i < n; i++)
					model.mPairwiseProbability[i] = atof(st.nextToken());
			}
			else if (cmd.startsWith("probB"))
			{
				int n = model.mNumberOfClasses * (model.mNumberOfClasses - 1) / 2;
				model.probB = new double[n];
				StringTokenizer st = new StringTokenizer(arg);
				for (int i = 0; i < n; i++)
					model.probB[i] = atof(st.nextToken());
			}
			else if (cmd.startsWith("nr_sv"))
			{
				int n = model.mNumberOfClasses;
				model.mNumberOfSupportVectorsPerClass = new int[n];
				StringTokenizer st = new StringTokenizer(arg);
				for (int i = 0; i < n; i++)
					model.mNumberOfSupportVectorsPerClass[i] = atoi(st.nextToken());
			}
			else if (cmd.startsWith("mSupportVectorsTable"))
			{
				break;
			}
			else
			{
				System.err.print("unknown text in model file\n");
				return null;
			}
		}

		// read mSupportVectorCoeficientsTable and mSupportVectorsTable

		int m = model.mNumberOfClasses - 1;
		int l = model.mNumberOfSupportVectors;
		model.mSupportVectorCoeficientsTable = new double[m][l];
		model.mSupportVectorsTable = new Node[l][];

		for (int i = 0; i < l; i++)
		{
			String line = fp.readLine();
			StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

			for (int k = 0; k < m; k++)
				model.mSupportVectorCoeficientsTable[k][i] = atof(st.nextToken());
			int n = st.countTokens() / 2;
			model.mSupportVectorsTable[i] = new Node[n];
			for (int j = 0; j < n; j++)
			{
				model.mSupportVectorsTable[i][j] = new Node();
				model.mSupportVectorsTable[i][j].mIndex = atoi(st.nextToken());
				model.mSupportVectorsTable[i][j].mValue = atof(st.nextToken());
			}
		}

		fp.close();
		return model;
	}

	public static String svmCheckParameter(Problem prob, Parameter param)
	{
		// svm_type

		int svm_type = param.svm_type;
		if (svm_type != Parameter.C_SVC && svm_type != Parameter.NU_SVC && svm_type != Parameter.ONE_CLASS
				&& svm_type != Parameter.EPSILON_SVR && svm_type != Parameter.NU_SVR)
			return "unknown SVM type";

		// kernel_type

		int kernel_type = param.kernel_type;
		if (kernel_type != Parameter.LINEAR && kernel_type != Parameter.POLY && kernel_type != Parameter.RBF
				&& kernel_type != Parameter.SIGMOID)
			return "unknown kernel type";

		// cache_size,eps,C,nu,p,shrinking

		if (param.cache_size <= 0)
			return "cache_size <= 0";

		if (param.eps <= 0)
			return "eps <= 0";

		if (svm_type == Parameter.C_SVC || svm_type == Parameter.EPSILON_SVR || svm_type == Parameter.NU_SVR)
			if (param.C <= 0)
				return "C <= 0";

		if (svm_type == Parameter.NU_SVC || svm_type == Parameter.ONE_CLASS || svm_type == Parameter.NU_SVR)
			if (param.nu < 0 || param.nu > 1)
				return "nu < 0 or nu > 1";

		if (svm_type == Parameter.EPSILON_SVR)
			if (param.p < 0)
				return "p < 0";

		if (param.shrinking != 0 && param.shrinking != 1)
			return "shrinking != 0 and shrinking != 1";

		if (param.probability != 0 && param.probability != 1)
			return "probability != 0 and probability != 1";

		if (param.probability == 1 && svm_type == Parameter.ONE_CLASS)
			return "one-class SVM probability output not supported yet";

		// check whether nu-svc is feasible

		if (svm_type == Parameter.NU_SVC)
		{
			int l = prob.mNumberOfVectors;
			int max_nr_class = 16;
			int nr_class = 0;
			int[] label = new int[max_nr_class];
			int[] count = new int[max_nr_class];

			int i;
			for (i = 0; i < l; i++)
			{
				int this_label = (int) prob.mClass[i];
				int j;
				for (j = 0; j < nr_class; j++)
					if (this_label == label[j])
					{
						++count[j];
						break;
					}

				if (j == nr_class)
				{
					if (nr_class == max_nr_class)
					{
						max_nr_class *= 2;
						int[] new_data = new int[max_nr_class];
						System.arraycopy(label, 0, new_data, 0, label.length);
						label = new_data;

						new_data = new int[max_nr_class];
						System.arraycopy(count, 0, new_data, 0, count.length);
						count = new_data;
					}
					label[nr_class] = this_label;
					count[nr_class] = 1;
					++nr_class;
				}
			}

			for (i = 0; i < nr_class; i++)
			{
				int n1 = count[i];
				for (int j = i + 1; j < nr_class; j++)
				{
					int n2 = count[j];
					if (param.nu * (n1 + n2) / 2 > Math.min(n1, n2))
						return "specified nu is infeasible";
				}
			}
		}

		return null;
	}

	public static int svmCheckProbabilityModel(Model model)
	{
		if (((model.mParameter.svm_type == Parameter.C_SVC || model.mParameter.svm_type == Parameter.NU_SVC)
				&& model.mPairwiseProbability != null && model.probB != null)
				|| ((model.mParameter.svm_type == Parameter.EPSILON_SVR || model.mParameter.svm_type == Parameter.NU_SVR) && model.mPairwiseProbability != null))
			return 1;
		else
			return 0;
	}
}