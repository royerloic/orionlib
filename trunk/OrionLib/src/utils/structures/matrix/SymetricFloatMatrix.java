package utils.structures.matrix;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import utils.io.LineReader;
import utils.structures.map.BijectiveBidiHashMap;

public class SymetricFloatMatrix implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	float[][] mMatrix = null;

	BijectiveBidiHashMap<Integer, Integer> mIndex2ValMap = new BijectiveBidiHashMap<Integer, Integer>();

	int mNextNewIndex = 0;

	public SymetricFloatMatrix()
	{
		super();
	}

	private void check()
	{
		if (mMatrix == null)
		{
			throw new UnsupportedOperationException("Matrix not initialized");
		}
	}

	public boolean init(int pSize)
	{
		mMatrix = new float[pSize][];
		for (int i = 0; i < pSize; i++)
		{
			mMatrix[i] = new float[i + 1];
		}
		return true;
	}

	public int getOrCreateIndexFor(Integer pInteger)
	{
		Integer xi = mIndex2ValMap.getReverse(pInteger);
		if (xi == null)
		{
			if (mNextNewIndex + 1 > mMatrix.length - 1)
			{
				throw new UnsupportedOperationException("Matrix too small, ");
			}
			xi = mNextNewIndex;
			mIndex2ValMap.put(xi, pInteger);
			mNextNewIndex++;
		}
		return xi;
	}

	public Integer getIndexFor(Integer pInteger)
	{
		Integer xi = mIndex2ValMap.getReverse(pInteger);
		return xi;
	}

	public int getCapacity()
	{
		return mMatrix.length;
	}

	public boolean set(Integer x, Integer y, Float pValue)
	{
		check();

		int xi = getOrCreateIndexFor(x);
		int yi = getOrCreateIndexFor(y);

		if (xi >= yi)
			mMatrix[xi][yi] = pValue;
		else
			mMatrix[yi][xi] = pValue;

		return true;
	}

	public float get(Integer x, Integer y)
	{
		check();

		Integer xi = getIndexFor(x);
		Integer yi = getIndexFor(y);

		if (xi != null && yi != null)
		{
			if (xi >= yi)
				return mMatrix[xi][yi];
			else
				return mMatrix[yi][xi];
		}
		else
		{
			return 0;
		}
	}

	public ArrayList<Float> get(Integer x, Collection yr)
	{
		check();
		ArrayList<Float> lColumn = new ArrayList<Float>();
		for (Object lObject : yr)
		{
			Integer y = (Integer) lObject;
			lColumn.add(get(x, y));
		}
		return lColumn;
	}

	public ArrayList<Float> get(Collection xr, Integer y)
	{
		check();
		ArrayList<Float> lLine = new ArrayList<Float>();
		for (Object lObject : xr)
		{
			Integer x = (Integer) lObject;
			lLine.add(get(x, y));
		}
		return lLine;
	}

	public ArrayList<ArrayList<Float>> get(Collection xr, Collection yr)
	{
		check();
		ArrayList<ArrayList<Float>> lSubMatrix = new ArrayList<ArrayList<Float>>();
		for (Object lx : xr)
		{
			ArrayList<Float> lColumn = new ArrayList<Float>();
			for (Object ly : yr)
			{
				Integer x = (Integer) lx;
				Integer y = (Integer) ly;
				lColumn.add(get(x, y));
			}
			lSubMatrix.add(lColumn);
		}
		return lSubMatrix;
	}

	public ArrayList<ArrayList<Float>> get(int... pList)
	{
		ArrayList<Integer> lList = new ArrayList<Integer>();
		for (Integer lInteger : pList)
		{
			lList.add(lInteger);
		}
		return get(lList);
	}

	public ArrayList<ArrayList<Float>> get(Collection pIntRange)
	{
		ArrayList<Integer> lList = new ArrayList<Integer>();
		for (Object lObject : pIntRange)
		{
			Integer lInteger = (Integer) lObject;
			lList.add(lInteger);
		}
		return get(lList);
	}

	public ArrayList<ArrayList<Float>> get(List<Integer> pList)
	{
		check();
		ArrayList<ArrayList<Float>> lSubMatrix = new ArrayList<ArrayList<Float>>();

		for (int i = 0; i < pList.size(); i++)
		{
			ArrayList<Float> lColumn = new ArrayList<Float>();
			for (int j = 0; j <= i; j++)
			{
				Integer x = pList.get(i);
				Integer y = pList.get(j);

				Float lValue = get(x, y);
				lColumn.add(lValue);
			}
			lSubMatrix.add(lColumn);
		}
		return lSubMatrix;
	}

	public boolean saveToFile(File pFile) throws IOException
	{
		FileOutputStream lFileOutputStream = null;
		ObjectOutputStream lObjectOutputStream = null;

		lFileOutputStream = new FileOutputStream(pFile);
		BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(lFileOutputStream,
																																					10000000);
		lObjectOutputStream = new ObjectOutputStream(lBufferedOutputStream);
		lObjectOutputStream.writeObject(this);
		lObjectOutputStream.close();

		return true;
	}

	public boolean loadFromFile(File pFile) throws IOException
	{
		FileInputStream lFileInputStream = null;
		ObjectInputStream lObjectInputStream = null;

		lFileInputStream = new FileInputStream(pFile);
		BufferedInputStream lBufferedInputStream = new BufferedInputStream(	lFileInputStream,
																																				10000000);
		lObjectInputStream = new ObjectInputStream(lBufferedInputStream);
		SymetricFloatMatrix lSymetricFloatMatrix;
		try
		{
			lSymetricFloatMatrix = (SymetricFloatMatrix) lObjectInputStream.readObject();
			mMatrix = lSymetricFloatMatrix.mMatrix;
			mIndex2ValMap = lSymetricFloatMatrix.mIndex2ValMap;
			return true;
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		lObjectInputStream.close();
		return false;
	}

	private static final Pattern sTabDelPattern = Pattern.compile("\t");

	public boolean loadColumnFromTabDelFile(Integer pColumnInteger, File pFile) throws IOException
	{
		Integer x = pColumnInteger;
		for (String lLine : LineReader.getLines(pFile))
			if (lLine.length() > 0)
			{
				String[] lArray = sTabDelPattern.split(lLine, -1);
				Integer y = Integer.parseInt(lArray[0]);
				Float val = Float.parseFloat(lArray[1]);

				set(x, y, val);
			}
		return true;
	}

}
