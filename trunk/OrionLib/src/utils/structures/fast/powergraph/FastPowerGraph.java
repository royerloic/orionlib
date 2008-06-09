package utils.structures.fast.powergraph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import utils.structures.fast.graph.Edge;
import utils.structures.fast.graph.FastGraph;
import utils.structures.fast.graph.FastIntegerGraph;
import utils.structures.fast.set.FastSparseIntegerSet;

public class FastPowerGraph<N>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1;

	FastIntegerPowerGraph mFastIntegerPowerGraph;

	final HashMap<N, FastSparseIntegerSet> mNameToNodeMap = new HashMap<N, FastSparseIntegerSet>();
	final HashMap<FastSparseIntegerSet, N> mNodeToNameMap = new HashMap<FastSparseIntegerSet, N>();

	public FastPowerGraph()
	{
		super();
		mFastIntegerPowerGraph = new FastIntegerPowerGraph();
	}

	public FastPowerGraph(int pNumberOfNodes,
												int pNumberOfPowerNodes,
												int pNumberOfPowerEdges)
	{
		super();
		mFastIntegerPowerGraph = new FastIntegerPowerGraph(pNumberOfNodes,pNumberOfPowerNodes,pNumberOfPowerEdges);
	}

	protected FastIntegerPowerGraph getUnderlyingFastIntegerPowerGraph()
	{
		return mFastIntegerPowerGraph;
	}
	
	

}
