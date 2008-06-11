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

	public boolean init(final int pSize)
	{
		mMatrix = new float[pSize][];
		for (int i = 0; i < pSize; i++)
		{
			mMatrix[i] = new float[i + 1];
		}
		return true;
	}

	public int getOrCreateIndexFor(final Integer pInteger)
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

	public Integer getIndexFor(final Integer pInteger)
	{
		final Integer xi = mIndex2ValMap.getReverse(pInteger);
		return xi;
	}

	public int getCapacity()
	{
		return mMatrix.length;
	}

	public boolean set(final Integer x, final Integer y, final Float pValue)
	{
		check();

		final int xi = getOrCreateIndexFor(x);
		final int yi = getOrCreateIndexFor(y);

		if (xi >= yi)
		{
			mMatrix[xi][yi] = pValue;
		}
		else
		{
			mMatrix[yi][xi] = pValue;
		}

		return true;
	}

	public float get(final Integer x, final Integer y)
	{
		check();

		final Integer xi = getIndexFor(x);
		final Integer yi = getIndexFor(y);

		if (xi != null && yi != null)
		{
			if (xi >= yi)
			{
				return mMatrix[xi][yi];
			}
			else
			{
				return mMatrix[yi][xi];
			}
		}
		else
		{
			return 0;
		}
	}

	public ArrayList<Float> get(final Integer x, final Collection yr)
	{
		check();
		final ArrayList<Float> lColumn = new ArrayList<Float>();
		for (final Object lObject : yr)
		{
			final Integer y = (Integer) lObject;
			lColumn.add(get(x, y));
		}
		return lColumn;
	}

	public ArrayList<Float> get(final Collection xr, final Integer y)
	{
		check();
		final ArrayList<Float> lLine = new ArrayList<Float>();
		for (final Object lObject : xr)
		{
			final Integer x = (Integer) lObject;
			lLine.add(get(x, y));
		}
		return lLine;
	}

	public ArrayList<ArrayList<Float>> get(	final Collection xr,
																					final Collection yr)
	{
		check();
		final ArrayList<ArrayList<Float>> lSubMatrix = new ArrayList<ArrayList<Float>>();
		for (final Object lx : xr)
		{
			final ArrayList<Float> lColumn = new ArrayList<Float>();
			for (final Object ly : yr)
			{
				final Integer x = (Integer) lx;
				final Integer y = (Integer) ly;
				lColumn.add(get(x, y));
			}
			lSubMatrix.add(lColumn);
		}
		return lSubMatrix;
	}

	public ArrayList<ArrayList<Float>> get(final int... pList)
	{
		final ArrayList<Integer> lList = new ArrayList<Integer>();
		for (final Integer lInteger : pList)
		{
			lList.add(lInteger);
		}
		return get(lList);
	}

	public ArrayList<ArrayList<Float>> get(final Collection pIntRange)
	{
		final ArrayList<Integer> lList = new ArrayList<Integer>();
		for (final Object lObject : pIntRange)
		{
			final Integer lInteger = (Integer) lObject;
			lList.add(lInteger);
		}
		return get(lList);
	}

	public ArrayList<ArrayList<Float>> get(final List<Integer> pList)
	{
		check();
		final ArrayList<ArrayList<Float>> lSubMatrix = new ArrayList<ArrayList<Float>>();

		for (int i = 0; i < pList.size(); i++)
		{
			final ArrayList<Float> lColumn = new ArrayList<Float>();
			for (int j = 0; j <= i; j++)
			{
				final Integer x = pList.get(i);
				final Integer y = pList.get(j);

				final Float lValue = get(x, y);
				lColumn.add(lValue);
			}
			lSubMatrix.add(lColumn);
		}
		return lSubMatrix;
	}

	public boolean saveToFile(final File pFile) throws IOException
	{
		FileOutputStream lFileOutputStream = null;
		ObjectOutputStream lObjectOutputStream = null;

		lFileOutputStream = new FileOutputStream(pFile);
		final BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(lFileOutputStream,
																																								10000000);
		lObjectOutputStream = new ObjectOutputStream(lBufferedOutputStream);
		lObjectOutputStream.writeObject(this);
		lObjectOutputStream.close();

		return true;
	}

	public boolean loadFromFile(final File pFile) throws IOException
	{
		FileInputStream lFileInputStream = null;
		ObjectInputStream lObjectInputStream = null;

		lFileInputStream = new FileInputStream(pFile);
		final BufferedInputStream lBufferedInputStream = new BufferedInputStream(	lFileInputStream,
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
		catch (final ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		lObjectInputStream.close();
		return false;
	}

	private static final Pattern sTabDelPattern = Pattern.compile("\t");

	public boolean loadColumnFromTabDelFile(final Integer pColumnInteger,
																					final File pFile) throws IOException
	{
		final Integer x = pColumnInteger;
		for (final String lLine : LineReader.getLines(pFile))
		{
			if (lLine.length() > 0)
			{
				final String[] lArray = sTabDelPattern.split(lLine, -1);
				final Integer y = Integer.parseInt(lArray[0]);
				final Float val = Float.parseFloat(lArray[1]);

				set(x, y, val);
			}
		}
		return true;
	}

}
