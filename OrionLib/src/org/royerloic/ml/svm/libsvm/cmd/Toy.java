package org.royerloic.ml.svm.libsvm.cmd;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.royerloic.ml.svm.libsvm.Model;
import org.royerloic.ml.svm.libsvm.Node;
import org.royerloic.ml.svm.libsvm.Parameter;
import org.royerloic.ml.svm.libsvm.Problem;
import org.royerloic.ml.svm.libsvm.SVM;

public class Toy extends Applet
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1549842888476096352L;

	static final String	DEFAULT_PARAM	= "-t 2 -c 100";

	int									XLEN;

	int									YLEN;

	// off-screen buffer

	Image								buffer;

	Graphics						buffer_gc;

	// pre-allocated colors

	final static Color	colors[]			=
																		{ new Color(0, 0, 0), new Color(0, 120, 120), new Color(120, 120, 0),
			new Color(120, 0, 120), new Color(0, 200, 200), new Color(200, 200, 0), new Color(200, 0, 200) };

	class point
	{
		point(final double x, final double y, final byte value)
		{
			this.x = x;
			this.y = y;
			this.value = value;
		}

		double	x, y;

		byte		value;
	}

	Vector	point_list		= new Vector();

	byte		current_value	= 1;

	@Override
	public void init()
	{
		final Button button_change = new Button("Change");
		final Button button_run = new Button("Run");
		final Button button_clear = new Button("Clear");
		final Button button_save = new Button("Save");
		final Button button_load = new Button("Load");
		final TextField input_line = new TextField(DEFAULT_PARAM);

		final BorderLayout layout = new BorderLayout();
		this.setLayout(layout);

		final Panel p = new Panel();
		final GridBagLayout gridbag = new GridBagLayout();
		p.setLayout(gridbag);

		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridwidth = 1;
		gridbag.setConstraints(button_change, c);
		gridbag.setConstraints(button_run, c);
		gridbag.setConstraints(button_clear, c);
		gridbag.setConstraints(button_save, c);
		gridbag.setConstraints(button_load, c);
		c.weightx = 5;
		c.gridwidth = 5;
		gridbag.setConstraints(input_line, c);

		p.add(button_change);
		p.add(button_run);
		p.add(button_clear);
		p.add(button_save);
		p.add(button_load);
		p.add(input_line);
		this.add(p, BorderLayout.SOUTH);

		button_change.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				button_change_clicked();
			}
		});

		button_run.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				button_run_clicked(input_line.getText());
			}
		});

		button_clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				button_clear_clicked();
			}
		});

		button_save.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				button_save_clicked();
			}
		});

		button_load.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				button_load_clicked();
			}
		});

		input_line.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				button_run_clicked(input_line.getText());
			}
		});

		this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}

	void draw_point(final point p)
	{
		final Color c = colors[p.value + 3];

		final Graphics window_gc = getGraphics();
		buffer_gc.setColor(c);
		buffer_gc.fillRect((int) (p.x * XLEN), (int) (p.y * YLEN), 4, 4);
		window_gc.setColor(c);
		window_gc.fillRect((int) (p.x * XLEN), (int) (p.y * YLEN), 4, 4);
	}

	void clear_all()
	{
		point_list.removeAllElements();
		if (buffer != null)
		{
			buffer_gc.setColor(colors[0]);
			buffer_gc.fillRect(0, 0, XLEN, YLEN);
		}
		repaint();
	}

	void draw_all_points()
	{
		final int n = point_list.size();
		for (int i = 0; i < n; i++)
			draw_point((point) point_list.elementAt(i));
	}

	void button_change_clicked()
	{
		++current_value;
		if (current_value > 3)
			current_value = 1;
	}

	private static double atof(final String s)
	{
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(final String s)
	{
		return Integer.parseInt(s);
	}

	void button_run_clicked(final String args)
	{
		// guard
		if (point_list.isEmpty())
			return;

		final Parameter param = new Parameter();

		// default values
		param.svm_type = Parameter.C_SVC;
		param.kernel_type = Parameter.RBF;
		param.degree = 3;
		param.gamma = 0;
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 40;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];

		// parse options
		final StringTokenizer st = new StringTokenizer(args);
		final String[] argv = new String[st.countTokens()];
		for (int i = 0; i < argv.length; i++)
			argv[i] = st.nextToken();

		for (int i = 0; i < argv.length; i++)
		{
			if (argv[i].charAt(0) != '-')
				break;
			++i;
			switch (argv[i - 1].charAt(1))
			{
				case 's':
					param.svm_type = atoi(argv[i]);
					break;
				case 't':
					param.kernel_type = atoi(argv[i]);
					break;
				case 'd':
					param.degree = atof(argv[i]);
					break;
				case 'g':
					param.gamma = atof(argv[i]);
					break;
				case 'r':
					param.coef0 = atof(argv[i]);
					break;
				case 'n':
					param.nu = atof(argv[i]);
					break;
				case 'm':
					param.cache_size = atof(argv[i]);
					break;
				case 'c':
					param.C = atof(argv[i]);
					break;
				case 'e':
					param.eps = atof(argv[i]);
					break;
				case 'p':
					param.p = atof(argv[i]);
					break;
				case 'h':
					param.shrinking = atoi(argv[i]);
					break;
				case 'w':
					++param.nr_weight;
					{
						final int[] old = param.weight_label;
						param.weight_label = new int[param.nr_weight];
						System.arraycopy(old, 0, param.weight_label, 0, param.nr_weight - 1);
					}

					{
						final double[] old = param.weight;
						param.weight = new double[param.nr_weight];
						System.arraycopy(old, 0, param.weight, 0, param.nr_weight - 1);
					}

					param.weight_label[param.nr_weight - 1] = atoi(argv[i - 1].substring(2));
					param.weight[param.nr_weight - 1] = atof(argv[i]);
					break;
				default:
					System.err.print("unknown option\n");
			}
		}

		// build problem
		final Problem prob = new Problem();
		prob.mNumberOfVectors = point_list.size();
		prob.mClass = new double[prob.mNumberOfVectors];

		if ((param.svm_type == Parameter.EPSILON_SVR) || (param.svm_type == Parameter.NU_SVR))
		{
			if (param.gamma == 0)
				param.gamma = 1;
			prob.mVectorsTable = new Node[prob.mNumberOfVectors][1];
			for (int i = 0; i < prob.mNumberOfVectors; i++)
			{
				final point p = (point) point_list.elementAt(i);
				prob.mVectorsTable[i][0] = new Node();
				prob.mVectorsTable[i][0].mIndex = 1;
				prob.mVectorsTable[i][0].mValue = p.x;
				prob.mClass[i] = p.y;
			}

			// build model & classify
			final Model model = SVM.svmTrain(prob, param);
			final Node[] x = new Node[1];
			x[0] = new Node();
			x[0].mIndex = 1;
			final int[] j = new int[XLEN];

			final Graphics window_gc = getGraphics();
			for (int i = 0; i < XLEN; i++)
			{
				x[0].mValue = (double) i / XLEN;
				j[i] = (int) (YLEN * SVM.svmPredict(model, x));
			}

			buffer_gc.setColor(colors[0]);
			buffer_gc.drawLine(0, 0, 0, YLEN - 1);
			window_gc.setColor(colors[0]);
			window_gc.drawLine(0, 0, 0, YLEN - 1);

			final int p = (int) (param.p * YLEN);
			for (int i = 1; i < XLEN; i++)
			{
				buffer_gc.setColor(colors[0]);
				buffer_gc.drawLine(i, 0, i, YLEN - 1);
				window_gc.setColor(colors[0]);
				window_gc.drawLine(i, 0, i, YLEN - 1);

				buffer_gc.setColor(colors[5]);
				window_gc.setColor(colors[5]);
				buffer_gc.drawLine(i - 1, j[i - 1], i, j[i]);
				window_gc.drawLine(i - 1, j[i - 1], i, j[i]);

				if (param.svm_type == Parameter.EPSILON_SVR)
				{
					buffer_gc.setColor(colors[2]);
					window_gc.setColor(colors[2]);
					buffer_gc.drawLine(i - 1, j[i - 1] + p, i, j[i] + p);
					window_gc.drawLine(i - 1, j[i - 1] + p, i, j[i] + p);

					buffer_gc.setColor(colors[2]);
					window_gc.setColor(colors[2]);
					buffer_gc.drawLine(i - 1, j[i - 1] - p, i, j[i] - p);
					window_gc.drawLine(i - 1, j[i - 1] - p, i, j[i] - p);
				}
			}
		}
		else
		{
			if (param.gamma == 0)
				param.gamma = 0.5;
			prob.mVectorsTable = new Node[prob.mNumberOfVectors][2];
			for (int i = 0; i < prob.mNumberOfVectors; i++)
			{
				final point p = (point) point_list.elementAt(i);
				prob.mVectorsTable[i][0] = new Node();
				prob.mVectorsTable[i][0].mIndex = 1;
				prob.mVectorsTable[i][0].mValue = p.x;
				prob.mVectorsTable[i][1] = new Node();
				prob.mVectorsTable[i][1].mIndex = 2;
				prob.mVectorsTable[i][1].mValue = p.y;
				prob.mClass[i] = p.value;
			}

			// build model & classify
			final Model model = SVM.svmTrain(prob, param);
			final Node[] x = new Node[2];
			x[0] = new Node();
			x[1] = new Node();
			x[0].mIndex = 1;
			x[1].mIndex = 2;

			final Graphics window_gc = getGraphics();
			for (int i = 0; i < XLEN; i++)
				for (int j = 0; j < YLEN; j++)
				{
					x[0].mValue = (double) i / XLEN;
					x[1].mValue = (double) j / YLEN;
					double d = SVM.svmPredict(model, x);
					if ((param.svm_type == Parameter.ONE_CLASS) && (d < 0))
						d = 2;
					buffer_gc.setColor(colors[(int) d]);
					window_gc.setColor(colors[(int) d]);
					buffer_gc.drawLine(i, j, i, j);
					window_gc.drawLine(i, j, i, j);
				}
		}

		draw_all_points();
	}

	void button_clear_clicked()
	{
		clear_all();
	}

	void button_save_clicked()
	{
		final FileDialog dialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
		dialog.setVisible(true);
		final String filename = dialog.getFile();
		if (filename == null)
			return;
		try
		{
			final DataOutputStream fp = new DataOutputStream(new FileOutputStream(filename));
			final int n = point_list.size();
			for (int i = 0; i < n; i++)
			{
				final point p = (point) point_list.elementAt(i);
				fp.writeBytes(p.value + " 1:" + p.x + " 2:" + p.y + "\n");
			}
			fp.close();
		}
		catch (final IOException e)
		{
			System.err.print(e);
		}
	}

	void button_load_clicked()
	{
		final FileDialog dialog = new FileDialog(new Frame(), "Load", FileDialog.LOAD);
		dialog.setVisible(true);
		final String filename = dialog.getFile();
		if (filename == null)
			return;
		clear_all();
		try
		{
			final BufferedReader fp = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = fp.readLine()) != null)
			{
				final StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");
				final byte value = (byte) atoi(st.nextToken());
				st.nextToken();
				final double x = atof(st.nextToken());
				st.nextToken();
				final double y = atof(st.nextToken());
				point_list.addElement(new point(x, y, value));
			}
			fp.close();
		}
		catch (final IOException e)
		{
			System.err.print(e);
		}
		draw_all_points();
	}

	@Override
	protected void processMouseEvent(final MouseEvent e)
	{
		if (e.getID() == MouseEvent.MOUSE_PRESSED)
		{
			if ((e.getX() >= XLEN) || (e.getY() >= YLEN))
				return;
			final point p = new point((double) e.getX() / XLEN, (double) e.getY() / YLEN, current_value);
			point_list.addElement(p);
			draw_point(p);
		}
	}

	@Override
	public void paint(final Graphics g)
	{
		// create buffer first time
		if (buffer == null)
		{
			buffer = this.createImage(XLEN, YLEN);
			buffer_gc = buffer.getGraphics();
			buffer_gc.setColor(colors[0]);
			buffer_gc.fillRect(0, 0, XLEN, YLEN);
		}
		g.drawImage(buffer, 0, 0, this);
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(XLEN, YLEN + 50);
	}

	@Override
	public void resize(final Dimension d)
	{
		resize(d.width, d.height);
	}

	@Override
	public void resize(final int w, final int h)
	{
		super.resize(w, h);
		XLEN = w;
		YLEN = h - 50;
		clear_all();
	}

	public static void main(final String[] argv)
	{
		new AppletFrame("Toy", new Toy(), 500, 500 + 50);
	}
}

class AppletFrame extends Frame
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5168173625514849854L;

	AppletFrame(final String title, final Applet applet, final int width, final int height)
	{
		super(title);
		applet.init();
		applet.resize(width, height);
		applet.start();
		this.add(applet);
		this.pack();
		this.setVisible(true);
	}
}