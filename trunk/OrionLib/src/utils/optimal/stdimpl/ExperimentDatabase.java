/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package utils.optimal.stdimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import utils.math.INumericalVector;
import utils.math.stdimpl.NumericalVector;
import utils.optimal.interf.IExperiment;
import utils.optimal.interf.IExperimentDatabase;
import utils.optimal.interf.IObjectiveFunction;

/**
 * ExperimentDatabase is an abstract class containing generic methods and
 * abstract methods that need to be implemented in derived classes. A
 * ExperimentDatabase contains Experiments in chronological order (iteration
 * order).
 * 
 * @see IExperiment
 * 
 * @author MSc. Ing. Loic Royer
 */
public class ExperimentDatabase implements IExperimentDatabase
{

	private IObjectiveFunction mObjectiveFunction;

	private List mExperimentList;

	private List mBestExperimentList;

	private List mBestExperimentValuesList;

	private INumericalVector mMinimumValuesVector;

	private INumericalVector mMaximumValuesVector;

	/**
	 * 
	 */
	public ExperimentDatabase()
	{
		super();
		mExperimentList = Collections.synchronizedList(new ArrayList());
		mBestExperimentList = new ArrayList();
		mBestExperimentValuesList = new ArrayList();
	}

	/**
	 * @see utils.optimal.interf.IExperimentDatabase#setObjectiveFunction(utils.optimal.interf.IObjectiveFunction)
	 */
	public void setObjectiveFunction(final IObjectiveFunction pObjectiveFunction)
	{
		mObjectiveFunction = pObjectiveFunction;
	}

	/**
	 * @see ExperimentDatabase#getSize()
	 */
	public int getNumberOfExperiments()
	{
		return mExperimentList.size();
	}

	/**
	 * @see ExperimentDatabase#addExperiment(Experiment)
	 */
	public boolean addExperiment(final IExperiment pExperiment)
	{
		synchronized (mExperimentList)
		{
			if (!this.contains(pExperiment))
			{
				mExperimentList.add(pExperiment);
				calculateMinMaxValues();
			}

			final double lNewValue = mObjectiveFunction.evaluate(pExperiment.getOutput());
			boolean lBetter = true;

			for (int i = 0; i < getNumberOfExperiments(); i++)
			{
				final IExperiment lExperiment = getExperiment(i);
				final double lValue = mObjectiveFunction.evaluate(lExperiment.getOutput());
				lBetter = lBetter && (lNewValue >= lValue);
			}

			if (lBetter || (mBestExperimentList.size() == 0))
				mBestExperimentList.add(pExperiment);
			else
			{
				final IExperiment lExperiment = (IExperiment) mBestExperimentList.get(mBestExperimentList.size() - 1);
				mBestExperimentList.add(lExperiment);
			}

			return lBetter;
		}
	}

	/**
	 * @param pExperiment
	 * @return
	 */
	public boolean contains(final IExperiment pExperiment)
	{
		return mExperimentList.contains(pExperiment);
	}

	/**
	 * @see utils.optimal.interf.IExperimentDatabase#findExperiment(utils.optimal.interf.IExperiment)
	 */
	public int findExperiment(final IExperiment pExperiment)
	{
		return mExperimentList.indexOf(pExperiment);
	}

	/**
	 * @see ExperimentDatabase#getExperiment(int)
	 */
	public IExperiment getExperiment(final int pIndex)
	{
		return (IExperiment) mExperimentList.get(pIndex);
	}

	/**
	 * Returns the experiment whose input vector is closest to 'pVector'.
	 * 
	 * @param pVector
	 *          from which we want a neighboor.
	 * @return neigbooring experiment.
	 */
	public final IExperiment getNeighboor(final INumericalVector pVector)
	{
		/**
		 * Initialize and start the search loop.
		 */
		IExperiment lResult = null;
		double lMinDistance = Double.POSITIVE_INFINITY;

		synchronized (mExperimentList)
		{
			for (int i = 0; i < getNumberOfExperiments(); i++)
			{
				final IExperiment lExperiment = getExperiment(i);
				final double lCurrentDistance = pVector.euclideanDistanceTo(lExperiment.getInput());
				if (lCurrentDistance < lMinDistance)
				{
					lResult = lExperiment;
					lMinDistance = lCurrentDistance;
				}
			}
		}
		return lResult;
	}

	/**
	 * @see ExperimentDatabase#isExperiment(MVector)
	 */
	public boolean containsInputVector(final INumericalVector pVector)
	{
		boolean lResult = false;
		synchronized (mExperimentList)
		{
			for (int i = 0; i < getNumberOfExperiments(); i++)
				if (getExperiment(i).getInput().equals(pVector))
				{
					lResult = true;
					break;
				}
		}
		return lResult;
	}

	/**
	 * @see utils.java.IObject#clone()
	 */
	@Override
	public Object clone()
	{
		final ExperimentDatabase lExperimentDatabase = new ExperimentDatabase();
		lExperimentDatabase.mExperimentList = mExperimentList;
		return lExperimentDatabase;
	}

	/**
	 * @see utils.java.IObject#copyFrom(java.lang.Object)
	 */
	public void copyFrom(final Object pObject)
	{
		if (pObject instanceof ExperimentDatabase)
		{
			final ExperimentDatabase lExperimentDatabase = (ExperimentDatabase) pObject;
			mExperimentList = lExperimentDatabase.mExperimentList;
		}
	}

	private void calculateMinMaxValues()
	{
		synchronized (mExperimentList)
		{
			final int lNumberOfExperiments = getNumberOfExperiments();
			if (lNumberOfExperiments != 0)
			{
				final int lDimension = getExperiment(0).getOutput().getDimension();

				final double[] lMin = new double[lDimension];
				final double[] lMax = new double[lDimension];

				for (int i = 0; i < lDimension; i++)
				{
					lMin[i] = Double.POSITIVE_INFINITY;
					lMax[i] = Double.NEGATIVE_INFINITY;

					for (int j = 0; j < lNumberOfExperiments; j++)
					{
						final double lValue = getExperiment(j).getOutput().get(i);
						lMin[i] = Math.min(lMin[i], lValue);
						lMax[i] = Math.max(lMax[i], lValue);
					}

				}

				mMinimumValuesVector = new NumericalVector(lMin);
				mMaximumValuesVector = new NumericalVector(lMax);
			}
		}
	}

	/**
	 * @see utils.optimal.interf.IExperimentDatabase#getMinimumInputValuesVector()
	 */
	public INumericalVector getMinimumInputValuesVector()
	{
		return null;
	}

	/**
	 * @see utils.optimal.interf.IExperimentDatabase#getMaximumInputValuesVector()
	 */
	public INumericalVector getMaximumInputValuesVector()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see utils.optimal.interf.IExperimentDatabase#getMinimumOutputValuesVector()
	 */
	public INumericalVector getMinimumOutputValuesVector()
	{
		synchronized (mExperimentList)
		{
			return mMinimumValuesVector;
		}
	}

	/**
	 * @see utils.optimal.interf.IExperimentDatabase#getMaximumOutputValuesVector()
	 */
	public INumericalVector getMaximumOutputValuesVector()
	{
		synchronized (mExperimentList)
		{
			return mMaximumValuesVector;
		}
	}

	/**
	 * @see utils.optimal.interf.IExperimentDatabase#getBestExperiment()
	 */
	public IExperiment getBestExperiment()
	{
		return (IExperiment) mBestExperimentList.get(getNumberOfExperiments() - 1);
	}

	/**
	 * @see utils.optimal.interf.IExperimentDatabase#getListOfBestExperiments()
	 */
	public List getListOfBestExperiments()
	{
		return mBestExperimentList;
	}

	/**
	 * @see utils.optimal.interf.IExperimentDatabase#getListOfBestExperimentValues()
	 */
	public List getListOfBestExperimentValues()
	{
		synchronized (mBestExperimentList)
		{
			mBestExperimentValuesList.clear();
			for (int i = 0; i < mBestExperimentList.size(); i++)
			{
				final IExperiment lExperiment = (IExperiment) mBestExperimentList.get(i);
				final double lValue = mObjectiveFunction.evaluate(lExperiment.getOutput());
				mBestExperimentValuesList.add(new Double(lValue));
			}
		}
		return mBestExperimentValuesList;
	}

	/**
	 * Returns true if the Database has not seen a better experiment added for the
	 * last 'pTime' added experiments. If there are less than 'pStart'
	 * experiments, we return false, considering that the question is inapropriate
	 * and that the database cannot be considered as stagnating at this point.
	 * 
	 * @param pStart
	 *          minimum number of experiments.
	 * @param pTime
	 *          number of experiments with no increase of the maximum.
	 * @return true if stagnating, false otherwise.
	 */
	public final boolean stagnating(final int pStart, final int pTime)
	{
		synchronized (mBestExperimentList)
		{
			int lTime = pTime;
			final List lList = getListOfBestExperimentValues();

			/**
			 * If we dont have enough points we cannot say that the database is
			 * stagnating
			 */
			if ((getNumberOfExperiments() - pStart) < lTime)
				return false;

			/** There is no point in asking for a too short stagnation window. */
			if (lTime < 2)
				lTime = 2;

			boolean lStagnating = true;
			int lStart = getNumberOfExperiments() - lTime;
			final int lEnd = getNumberOfExperiments() - 1;

			if (lStart < 0)
				lStart = 0;

			for (int i = lStart; i < lEnd; i++)
				lStagnating &= ((((Double) lList.get(i + 1)).doubleValue() - ((Double) lList.get(i)).doubleValue()) == 0);

			return lStagnating;
		}
	}
}