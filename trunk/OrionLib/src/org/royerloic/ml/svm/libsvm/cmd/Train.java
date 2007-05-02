package org.royerloic.ml.svm.libsvm.cmd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.royerloic.ml.svm.libsvm.Model;
import org.royerloic.ml.svm.libsvm.Node;
import org.royerloic.ml.svm.libsvm.Parameter;
import org.royerloic.ml.svm.libsvm.Problem;
import org.royerloic.ml.svm.libsvm.SVM;

class Train
{
	private Parameter	param;									// set by parse_command_line

	private Problem		prob;									// set by read_problem

	private Model			model;

	private String		input_file_name;				// set by parse_command_line

	private String		model_file_name;				// set by parse_command_line

	private String		error_msg;

	private int				cross_validation	= 0;

	private int				nr_fold;

	private static void exit_with_help()
	{
		System.out
				.print("Usage: Train [options] training_set_file [model_file]\n"
						+ "options:\n"
						+ "-s svm_type : set type of SVM (default 0)\n"
						+ "	0 -- C-SVC\n"
						+ "	1 -- nu-SVC\n"
						+ "	2 -- one-class SVM\n"
						+ "	3 -- epsilon-SVR\n"
						+ "	4 -- nu-SVR\n"
						+ "-t kernel_type : set type of kernel function (default 2)\n"
						+ "	0 -- linear: u'*v\n"
						+ "	1 -- polynomial: (gamma*u'*v + coef0)^degree\n"
						+ "	2 -- radial basis function: exp(-gamma*|u-v|^2)\n"
						+ "	3 -- sigmoid: tanh(gamma*u'*v + coef0)\n"
						+ "-d degree : set degree in kernel function (default 3)\n"
						+ "-g gamma : set gamma in kernel function (default 1/k)\n"
						+ "-r coef0 : set coef0 in kernel function (default 0)\n"
						+ "-c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)\n"
						+ "-n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)\n"
						+ "-p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)\n"
						+ "-m cachesize : set cache memory size in MB (default 40)\n"
						+ "-e epsilon : set tolerance of termination criterion (default 0.001)\n"
						+ "-h shrinking: whether to use the shrinking heuristics, 0 or 1 (default 1)\n"
						+ "-b probability_estimates: whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)\n"
						+ "-wi weight: set the parameter C of class i to weight*C, for C-SVC (default 1)\n"
						+ "-v n: n-fold cross validation mode\n");
		System.exit(1);
	}

	private void do_cross_validation()
	{
		int i;
		int total_correct = 0;
		double total_error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
		final double[] target = new double[this.prob.mNumberOfVectors];

		SVM.svmCrossValidation(this.prob, this.param, this.nr_fold, target);
		if ((this.param.svm_type == Parameter.EPSILON_SVR) || (this.param.svm_type == Parameter.NU_SVR))
		{
			for (i = 0; i < this.prob.mNumberOfVectors; i++)
			{
				final double y = this.prob.mClass[i];
				final double v = target[i];
				total_error += (v - y) * (v - y);
				sumv += v;
				sumy += y;
				sumvv += v * v;
				sumyy += y * y;
				sumvy += v * y;
			}
			System.out.print("Cross Validation Mean squared error = " + total_error / this.prob.mNumberOfVectors + "\n");
			System.out.print("Cross Validation Squared correlation coefficient = "
					+ ((this.prob.mNumberOfVectors * sumvy - sumv * sumy) * (this.prob.mNumberOfVectors * sumvy - sumv * sumy))
					/ ((this.prob.mNumberOfVectors * sumvv - sumv * sumv) * (this.prob.mNumberOfVectors * sumyy - sumy * sumy))
					+ "\n");
		}
		else
			for (i = 0; i < this.prob.mNumberOfVectors; i++)
				if (target[i] == this.prob.mClass[i])
					++total_correct;
		System.out.print("Cross Validation Accuracy = " + 100.0 * total_correct / this.prob.mNumberOfVectors + "%\n");
	}

	private void run(final String argv[]) throws IOException
	{
		parse_command_line(argv);
		read_problem();
		this.error_msg = SVM.svmCheckParameter(this.prob, this.param);

		if (this.error_msg != null)
		{
			System.err.print("Error: " + this.error_msg + "\n");
			System.exit(1);
		}

		if (this.cross_validation != 0)
			do_cross_validation();
		else
		{
			this.model = SVM.svmTrain(this.prob, this.param);
			SVM.svmSaveModel(this.model_file_name, this.model);
		}
	}

	public static void main(final String argv[]) throws IOException
	{
		final Train t = new Train();
		t.run(argv);
	}

	private static double atof(final String s)
	{
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(final String s)
	{
		return Integer.parseInt(s);
	}

	private void parse_command_line(final String argv[])
	{
		int i;

		this.param = new Parameter();
		// default values
		this.param.svm_type = Parameter.C_SVC;
		this.param.kernel_type = Parameter.RBF;
		this.param.degree = 3;
		this.param.gamma = 0; // 1/k
		this.param.coef0 = 0;
		this.param.nu = 0.5;
		this.param.cache_size = 40;
		this.param.C = 1;
		this.param.eps = 1e-3;
		this.param.p = 0.1;
		this.param.shrinking = 1;
		this.param.probability = 0;
		this.param.nr_weight = 0;
		this.param.weight_label = new int[0];
		this.param.weight = new double[0];

		// parse options
		for (i = 0; i < argv.length; i++)
		{
			if (argv[i].charAt(0) != '-')
				break;
			++i;
			switch (argv[i - 1].charAt(1))
			{
				case 's':
					this.param.svm_type = atoi(argv[i]);
					break;
				case 't':
					this.param.kernel_type = atoi(argv[i]);
					break;
				case 'd':
					this.param.degree = atof(argv[i]);
					break;
				case 'g':
					this.param.gamma = atof(argv[i]);
					break;
				case 'r':
					this.param.coef0 = atof(argv[i]);
					break;
				case 'n':
					this.param.nu = atof(argv[i]);
					break;
				case 'm':
					this.param.cache_size = atof(argv[i]);
					break;
				case 'c':
					this.param.C = atof(argv[i]);
					break;
				case 'e':
					this.param.eps = atof(argv[i]);
					break;
				case 'p':
					this.param.p = atof(argv[i]);
					break;
				case 'h':
					this.param.shrinking = atoi(argv[i]);
					break;
				case 'b':
					this.param.probability = atoi(argv[i]);
					break;
				case 'v':
					this.cross_validation = 1;
					this.nr_fold = atoi(argv[i]);
					if (this.nr_fold < 2)
					{
						System.err.print("n-fold cross validation: n must >= 2\n");
						exit_with_help();
					}
					break;
				case 'w':
					++this.param.nr_weight;
					{
						final int[] old = this.param.weight_label;
						this.param.weight_label = new int[this.param.nr_weight];
						System.arraycopy(old, 0, this.param.weight_label, 0, this.param.nr_weight - 1);
					}

					{
						final double[] old = this.param.weight;
						this.param.weight = new double[this.param.nr_weight];
						System.arraycopy(old, 0, this.param.weight, 0, this.param.nr_weight - 1);
					}

					this.param.weight_label[this.param.nr_weight - 1] = atoi(argv[i - 1].substring(2));
					this.param.weight[this.param.nr_weight - 1] = atof(argv[i]);
					break;
				default:
					System.err.print("unknown option\n");
					exit_with_help();
			}
		}

		// determine filenames

		if (i >= argv.length)
			exit_with_help();

		this.input_file_name = argv[i];

		if (i < argv.length - 1)
			this.model_file_name = argv[i + 1];
		else
		{
			int p = argv[i].lastIndexOf('/');
			++p; // whew...
			this.model_file_name = argv[i].substring(p) + ".model";
		}
	}

	// read in a problem (in svmlight format)

	private void read_problem() throws IOException
	{
		final BufferedReader fp = new BufferedReader(new FileReader(this.input_file_name));
		final Vector vy = new Vector();
		final Vector vx = new Vector();
		int max_index = 0;

		while (true)
		{
			final String line = fp.readLine();
			if (line == null)
				break;

			final StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

			vy.addElement(st.nextToken());
			final int m = st.countTokens() / 2;
			final Node[] x = new Node[m];
			for (int j = 0; j < m; j++)
			{
				x[j] = new Node();
				x[j].mIndex = atoi(st.nextToken());
				x[j].mValue = atof(st.nextToken());
			}
			if (m > 0)
				max_index = Math.max(max_index, x[m - 1].mIndex);
			vx.addElement(x);
		}

		this.prob = new Problem();
		this.prob.mNumberOfVectors = vy.size();
		this.prob.mVectorsTable = new Node[this.prob.mNumberOfVectors][];
		for (int i = 0; i < this.prob.mNumberOfVectors; i++)
			this.prob.mVectorsTable[i] = (Node[]) vx.elementAt(i);
		this.prob.mClass = new double[this.prob.mNumberOfVectors];
		for (int i = 0; i < this.prob.mNumberOfVectors; i++)
			this.prob.mClass[i] = atof((String) vy.elementAt(i));

		if (this.param.gamma == 0)
			this.param.gamma = 1.0 / max_index;

		fp.close();
	}
}