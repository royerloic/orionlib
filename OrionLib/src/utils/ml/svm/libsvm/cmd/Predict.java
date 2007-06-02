package utils.ml.svm.libsvm.cmd;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import utils.ml.svm.libsvm.Model;
import utils.ml.svm.libsvm.Node;
import utils.ml.svm.libsvm.Parameter;
import utils.ml.svm.libsvm.SVM;

class Predict
{
	private static double atof(final String s)
	{
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(final String s)
	{
		return Integer.parseInt(s);
	}

	private static void predict(final BufferedReader input,
															final DataOutputStream output,
															final Model model,
															final int predict_probability) throws IOException
	{
		int correct = 0;
		int total = 0;
		double error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

		final int svm_type = SVM.svmGetSvmType(model);
		final int nr_class = SVM.svmGetNumberOfClasses(model);
		final int[] labels = new int[nr_class];
		double[] prob_estimates = null;

		if (predict_probability == 1)
			if ((svm_type == Parameter.EPSILON_SVR) || (svm_type == Parameter.NU_SVR))
				System.out
						.print("Prob. model for test data: target mValue = predicted mValue + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="
								+ SVM.svmGetSvrProbability(model) + "\n");
			else
			{
				SVM.svmGetLabels(model, labels);
				prob_estimates = new double[nr_class];
				output.writeBytes("labels");
				for (int j = 0; j < nr_class; j++)
					output.writeBytes(" " + labels[j]);
				output.writeBytes("\n");
			}
		while (true)
		{
			final String line = input.readLine();
			if (line == null)
				break;

			final StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

			final double target = atof(st.nextToken());
			final int m = st.countTokens() / 2;
			final Node[] x = new Node[m];
			for (int j = 0; j < m; j++)
			{
				x[j] = new Node();
				x[j].mIndex = atoi(st.nextToken());
				x[j].mValue = atof(st.nextToken());
			}

			double v;
			if ((predict_probability == 1) && ((svm_type == Parameter.C_SVC) || (svm_type == Parameter.NU_SVC)))
			{
				v = SVM.svmPredictProbability(model, x, prob_estimates);
				output.writeBytes(v + " ");
				for (int j = 0; j < nr_class; j++)
					output.writeBytes(prob_estimates[j] + " ");
				output.writeBytes("\n");
			}
			else
			{
				v = SVM.svmPredict(model, x);
				output.writeBytes(v + "\n");
			}

			if (v == target)
				++correct;
			error += (v - target) * (v - target);
			sumv += v;
			sumy += target;
			sumvv += v * v;
			sumyy += target * target;
			sumvy += v * target;
			++total;
		}
		System.out.print("Accuracy = " + (double) correct / total * 100 + "% (" + correct + "/" + total
				+ ") (classification)\n");
		System.out.print("Mean squared error = " + error / total + " (regression)\n");
		System.out.print("Squared correlation coefficient = "
				+ ((total * sumvy - sumv * sumy) * (total * sumvy - sumv * sumy))
				/ ((total * sumvv - sumv * sumv) * (total * sumyy - sumy * sumy)) + " (regression)\n");
	}

	private static void exit_with_help()
	{
		System.err
				.print("usage: Predict [options] test_file model_file output_file\n"
						+ "options:\n"
						+ "-b probability_estimates: whether to predict probability estimates, 0 or 1 (default 0); one-class SVM not supported yet\n");
		System.exit(1);
	}

	public static void main(final String argv[]) throws IOException
	{
		int i, predict_probability = 0;

		// parse options
		for (i = 0; i < argv.length; i++)
		{
			if (argv[i].charAt(0) != '-')
				break;
			++i;
			switch (argv[i - 1].charAt(1))
			{
				case 'b':
					predict_probability = atoi(argv[i]);
					break;
				default:
					System.err.print("unknown option\n");
					exit_with_help();
			}
		}
		if (i >= argv.length)
			exit_with_help();
		try
		{
			final BufferedReader input = new BufferedReader(new FileReader(argv[i]));
			final DataOutputStream output = new DataOutputStream(new FileOutputStream(argv[i + 2]));
			final Model model = SVM.svmLoadModel(argv[i + 1]);
			predict(input, output, model, predict_probability);
		}
		catch (final FileNotFoundException e)
		{
			exit_with_help();
		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			exit_with_help();
		}
	}
}