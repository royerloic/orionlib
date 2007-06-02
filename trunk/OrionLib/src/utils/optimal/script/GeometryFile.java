/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *   
 */

package utils.optimal.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public class GeometryFile extends Vector
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8942281402647660871L;

	/**
	 * @author MSc. Ing. Loic Royer
	 * 
	 */
	public class Point extends Vector
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -5576308834875882579L;

		public final void addCoordinate(final double pCoordinate)
		{
			add(new Double(pCoordinate));
		};

		public final double getCoordinate(final int pIndex)
		{
			return ((Double) elementAt(pIndex)).doubleValue();
		}
	}

	/**
	 * @author MSc. Ing. Loic Royer
	 * 
	 */
	public class Line extends Vector
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -7603009795190553675L;

		public final void a(final double pCoordinate)
		{
			add(new Double(pCoordinate));
		};

		public final double getCoordinate(final int pIndex)
		{
			return ((Double) elementAt(pIndex)).doubleValue();
		}
	}

	/**
	 * @author MSc. Ing. Loic Royer
	 * 
	 */
	public class GeometryObject extends Vector
	{

		/**
		 * 
		 */
		private static final long	serialVersionUID	= -8515498979768191062L;
	}

	/**
	 * @author MSc. Ing. Loic Royer
	 * 
	 */
	public class PointList extends GeometryObject
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 3127293181449195730L;

		/**
		 * @param pPoint
		 */
		public final void addPoint(final Point pPoint)
		{
			add(pPoint);
		}

		/**
		 * @param pIndex
		 * @return
		 */
		public final Point getPointAt(final int pIndex)
		{
			return (Point) elementAt(pIndex);
		}

		private int	mPointPointer;

		/**
		 * @return
		 */
		public final int getPointPointer()
		{
			return mPointPointer;
		}

		/**
		 * @return
		 */
		public final Point firstPoint()
		{
			if (size() == 0)
				return null;
			else
			{
				mPointPointer = 0;
				return (Point) elementAt(mPointPointer);
			}
		}

		/**
		 * @return
		 */
		public final Point nextPoint()
		{
			if (size() == 0)
				return null;
			else
			{
				if (mPointPointer >= size())
					mPointPointer = 0;
				else if (mPointPointer < 0)
					mPointPointer = 0;
				else
				{
					mPointPointer++;
					if (mPointPointer >= size())
						mPointPointer = 0;
				}

				return (Point) elementAt(mPointPointer);
			}
		}

		/**
		 * @param pIndex
		 * @return
		 */
		public final int indexBefore(final int pIndex)
		{
			if (pIndex <= 0)
				return size() - 1;
			else
				return pIndex - 1;
		}

		/**
		 * @param pIndex
		 * @return
		 */
		public final int indexAfter(final int pIndex)
		{
			if (pIndex >= (size() - 1))
				return 0;
			else
				return pIndex + 1;
		}

	}

	/**
	 * @param pGeometryObject
	 */
	private void addGeometryObject(final GeometryObject pGeometryObject)
	{
		add(pGeometryObject);
	}

	/**
	 * @param pIndex
	 * @return
	 */
	public GeometryObject getGeometryObjectAt(final int pIndex)
	{
		return (GeometryObject) elementAt(pIndex);
	}

	/**
	 * Constructs a GeometryFile given a <code>.GEO</code> File.
	 */
	public GeometryFile(final String pFileName)
	{
		super();

		try
		{

			final File lInputFile = new File(pFileName);

			FileReader lFileReader;
			BufferedReader lBufferedReader;
			try
			{
				lFileReader = new FileReader(lInputFile);
				lBufferedReader = new BufferedReader(lFileReader);
			}
			catch (final FileNotFoundException e)
			{
				System.out.println("File: " + lInputFile + " not found.");
				throw e;
			}

			GeometryObject lGeometryObject = null;
			try
			{
				while ((lGeometryObject = readGeometryObject(lBufferedReader)) != null)
					addGeometryObject(lGeometryObject);
			}
			catch (final IOException e2)
			{
				System.out.println("Error while reading: " + e2.getCause());
			}
			finally
			{
				lFileReader.close();
			}
		}
		catch (final Exception any)
		{
			any.printStackTrace(System.out);

		}

	}

	private final GeometryObject readGeometryObject(final BufferedReader pBufferedReader) throws IOException
	{
		final String lFirstLine = pBufferedReader.readLine();
		if (lFirstLine == null)
			return null;

		final String lObjectTypeString = lFirstLine.substring(1);
		final int lObjectType = stringToInt(lObjectTypeString);

		if (lObjectType == 1)
			return readPointList(pBufferedReader);
		else
			return readPointList(pBufferedReader);

	}

	private final GeometryObject readPointList(final BufferedReader pBufferedReader) throws IOException
	{
		final PointList lPointList = new PointList();
		final String FirstLine = pBufferedReader.readLine();
		final Point lHeaderPoint = readPoint(FirstLine);
		final int lNumberOfPoints = (int) lHeaderPoint.getCoordinate(0);

		String lLineString;

		for (int i = 0; i < lNumberOfPoints; i++)
			if ((lLineString = pBufferedReader.readLine()) != null)
			{
				final Point lPoint = readPoint(lLineString);
				lPointList.addPoint(lPoint);
			}
			else
				break;

		return lPointList;
	}

	private final Point readPoint(final String pString)
	{
		final Point lPoint = new Point();
		final StringTokenizer lStringTokenizer = new StringTokenizer(pString, " ");
		while (lStringTokenizer.hasMoreTokens())
			lPoint.addCoordinate(stringToDouble(lStringTokenizer.nextToken()));
		return lPoint;
	}

	private final double stringToDouble(final String pString)
	{
		return Double.parseDouble(pString);
	}

	private final int stringToInt(final String pString)
	{
		return Integer.parseInt(pString);
	}

	public static double[] getVector(final String pFileName)
	{
		final Vector lVector = new Vector();

		try
		{

			final File lInputFile = new File(pFileName);

			FileReader lFileReader;
			BufferedReader lBufferedReader;
			try
			{
				lFileReader = new FileReader(lInputFile);
				lBufferedReader = new BufferedReader(lFileReader);
			}
			catch (final FileNotFoundException e)
			{
				System.out.println("File: " + lInputFile + " not found.");
				throw e;
			}

			String lLineString;
			try
			{
				while ((lLineString = lBufferedReader.readLine()) != null)
					lVector.add(Double.valueOf(lLineString));
			}
			catch (final IOException e2)
			{
				System.out.println("Error while reading: " + e2.getCause());
			}
			finally
			{
				lFileReader.close();
			}
		}
		catch (final Exception any)
		{
			any.printStackTrace(System.out);
		}

		final double[] lResult = new double[lVector.size()];
		for (int i = 0; i < lVector.size(); i++)
			lResult[i] = ((Double) lVector.elementAt(i)).doubleValue();
		return lResult;
	}

}
