package org.royerloic.structures.dbcol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class ValuedIntegerStringBidiMap
{
	private static final Logger	cLogger									= Logger.getLogger(ValuedIntegerStringBidiMap.class);

	private boolean							mTemp;
	private Connection					mDatabaseConnection;
	private PreparedStatement		mInsertStatement;
	private PreparedStatement		mStringQueryStatement;
	private PreparedStatement		mIntegerQueryStatement;
	private PreparedStatement		mStringDeleteStatement;
	private PreparedStatement		mIntegerDeleteStatement;
	private PreparedStatement		mIntegerStringDeleteStatement;
	private PreparedStatement		mSizeStatement;
	private String							mName;
	private int									mMinStringSize;
	private int									mMaxStringSize;
	private int									mCounter								= 0;
	private int									mCommitPeriod						= 10000;
	private int									mMaximumNuberOfResults	= Integer.MAX_VALUE;

	public class ValuedEntry
	{
		public int		mInteger;
		public String	mString;
		public double	mValue;

		public ValuedEntry(int pInteger, String pString, double pValue)
		{
			super();
			mInteger = pInteger;
			mString = pString;
			mValue = pValue;
		}

		@Override
		public String toString()
		{
			return "{value=" + mValue + ", integer=" + mInteger + ", string=" + mString + "}";
		}
	}

	public ValuedIntegerStringBidiMap(Connection pDatabaseConnection)
	{
		this(pDatabaseConnection, generateRandomName());
		mTemp = true;
	}

	private static String generateRandomName()
	{
		return ValuedIntegerStringBidiMap.class.getSimpleName() + "(" + Math.random() + ")";
	}

	public ValuedIntegerStringBidiMap(Connection pDatabaseConnection, String pName)
	{
		this(pDatabaseConnection, pName, 0, 250);
	}

	public ValuedIntegerStringBidiMap(Connection pDatabaseConnection,
																		String pName,
																		int pMinStringSize,
																		int pMaxStringSize)
	{
		mDatabaseConnection = pDatabaseConnection;
		mName = pName;
		mMinStringSize = pMinStringSize;
		mMaxStringSize = pMaxStringSize;
		try
		{
			mDatabaseConnection.setAutoCommit(false);
			mDatabaseConnection.setTransactionIsolation(mDatabaseConnection.TRANSACTION_READ_UNCOMMITTED);
			mDatabaseConnection.commit();
		}
		catch (SQLException e)
		{
			loginfo(e);
		}
		createTable(mDatabaseConnection, mName, mMinStringSize, mMaxStringSize);
		createStatements(mDatabaseConnection, mName);
		commit(mDatabaseConnection);
		mCounter = 0;
	}

	private void createStatements(Connection pDatabaseConnection, String pTableName)
	{
		try
		{
			mInsertStatement = (PreparedStatement) mDatabaseConnection.prepareStatement("INSERT INTO " + mName
					+ " (fValue,fInteger,fString) VALUES(?,?,?)");
			mStringQueryStatement = (PreparedStatement) mDatabaseConnection
					.prepareStatement("SELECT fInteger, fValue FROM " + mName + " WHERE fString=? AND fvalue>=?");
			mIntegerQueryStatement = (PreparedStatement) mDatabaseConnection
					.prepareStatement("SELECT fString, fValue FROM " + mName + " WHERE fInteger=? AND fvalue>=?");
			mStringDeleteStatement = (PreparedStatement) mDatabaseConnection.prepareStatement("DELETE FROM "
					+ mName + " WHERE fString=?");
			mIntegerDeleteStatement = (PreparedStatement) mDatabaseConnection.prepareStatement("DELETE FROM "
					+ mName + " WHERE fInteger=?");
			mIntegerStringDeleteStatement = (PreparedStatement) mDatabaseConnection.prepareStatement("DELETE FROM "
					+ mName + " WHERE fInteger=? AND fString=?");
			mSizeStatement = (PreparedStatement) mDatabaseConnection
					.prepareStatement("SELECT COUNT(*) AS Count FROM " + mName);
		}
		catch (SQLException e)
		{
			logerror(e);
		}
	}

	private boolean createTable(Connection pDatabaseConnection,
															String pTableName,
															int pMinStringSize,
															int MaxStringSize)
	{
		try
		{
			String lSql = "CREATE TABLE " + pTableName
					+ " ( fId INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0,INCREMENT BY 1), "
					+ " fValue DOUBLE, " + " fInteger INTEGER, " + " fString "
					+ getStringType(pMinStringSize, MaxStringSize) + ", " + " PRIMARY KEY (fId))";
			// System.out.println(lSql);
			pDatabaseConnection.createStatement().execute(lSql);
			return true;
		}
		catch (SQLException e)
		{
			System.out.println("Table: " + pTableName + " allready created...");
			loginfo(e);
			return false;
		}
	}

	private boolean dropTable(Connection pDatabaseConnection, String pTableName)
	{
		try
		{
			pDatabaseConnection.createStatement().execute("DROP TABLE " + pTableName);
			return true;
		}
		catch (SQLException e)
		{
			logerror(e);
			return false;
		}
	}

	private boolean createIndices(Connection pDatabaseConnection, String pTableName)
	{
		try
		{
			mDatabaseConnection.commit();
			pDatabaseConnection.createStatement().execute(
					"CREATE INDEX " + pTableName + "INDEXINTEGER ON " + pTableName + " (fInteger ASC)");
			mDatabaseConnection.commit();
			pDatabaseConnection.createStatement().execute(
					"CREATE INDEX " + pTableName + "INDEXSTRING ON " + pTableName + " (fString ASC)");
			mDatabaseConnection.commit();
			return true;
		}
		catch (SQLException e)
		{
			logerror(e); // second time we run program
			return false;
		}
	}

	public boolean optimize()
	{
		return createIndices(mDatabaseConnection, mName);
	}

	private void automaticCommit()
	{
		if (mCounter % mCommitPeriod == 0)
			commit(mDatabaseConnection);
	}

	private boolean commit(Connection pDatabaseConnection)
	{
		try
		{
			pDatabaseConnection.commit();
			return true;
		}
		catch (SQLException e)
		{
			logerror(e);
			return false;
		}
	}

	public boolean commitChanges()
	{
		return commit(mDatabaseConnection);
	}

	private String getStringType(int pMinStringSize, int pMaxStringSize)
	{
		if (pMinStringSize == pMaxStringSize)
		{
			return "CHAR(" + pMinStringSize + ")";
		}
		else
		{
			return "VARCHAR(" + pMaxStringSize + ")";
		}
	}

	public int size()
	{
		try
		{
			ResultSet lResultSet = mSizeStatement.executeQuery();
			while (lResultSet.next())
			{
				int lSize;

				lSize = lResultSet.getInt("Count");

				return lSize;
			}
			lResultSet.close();
		}
		catch (SQLException e)
		{
			logerror(e);
		}
		throw new RuntimeException("Could not get size");
	}

	public boolean isEmpty()
	{
		return size() == 0;
	}

	public boolean containsString(String pString)
	{
		try
		{
			mStringQueryStatement.setString(1, pString);
			ResultSet lResultSet = mStringQueryStatement.executeQuery();
			return lResultSet.first();
		}
		catch (SQLException e)
		{
			logerror(e);
			return false;
		}
	}

	public boolean containsInteger(int pInteger)
	{
		try
		{
			mIntegerQueryStatement.setInt(1, pInteger);
			ResultSet lResultSet = mIntegerQueryStatement.executeQuery();
			boolean lContainsInteger = lResultSet.first();
			lResultSet.close();
			return lContainsInteger;
		}
		catch (SQLException e)
		{
			logerror(e);
			return false;
		}
	}

	public List<ValuedEntry> getIntegers(String pString)
	{
		return getIntegers(pString, 0);
	}

	public List<ValuedEntry> getIntegers(String pString, double pMinValue)
	{
		ArrayList<ValuedEntry> lValuedEntryList = new ArrayList<ValuedEntry>();

		try
		{
			mStringQueryStatement.setString(1, pString);
			mStringQueryStatement.setDouble(2, pMinValue);
			ResultSet lResultSet = mStringQueryStatement.executeQuery();
			int lCounter = 0;
			while (lResultSet.next())
				if (lCounter < mMaximumNuberOfResults)
				{
					int lInteger = lResultSet.getInt("fInteger");
					double lValue = lResultSet.getDouble("fValue");
					ValuedEntry lValuedEntry = new ValuedEntry(lInteger, pString, lValue);
					lValuedEntryList.add(lValuedEntry);
					lCounter++;
				}
			lResultSet.close();
			return lValuedEntryList;
		}
		catch (SQLException e)
		{
			logerror(e);
			return null;
		}
	}

	public List<ValuedEntry> getStrings(int pInteger)
	{
		return getStrings(pInteger, 1);
	}

	public List<ValuedEntry> getStrings(int pInteger, double pMinValue)
	{
		ArrayList<ValuedEntry> lValuedEntryList = new ArrayList<ValuedEntry>();

		try
		{
			mIntegerQueryStatement.setInt(1, pInteger);
			mIntegerQueryStatement.setDouble(2, pMinValue);
			ResultSet lResultSet = mIntegerQueryStatement.executeQuery();
			int lCounter = 0;
			while (lResultSet.next())
				if (lCounter < mMaximumNuberOfResults)
				{
					String lString = lResultSet.getString("fString");
					double lValue = lResultSet.getDouble("fValue");
					ValuedEntry lValuedEntry = new ValuedEntry(pInteger, lString, lValue);
					lValuedEntryList.add(lValuedEntry);
					lCounter++;
				}
			lResultSet.close();
			return lValuedEntryList;
		}
		catch (SQLException e)
		{
			logerror(e);
			return null;
		}
	}

	public boolean put(Integer pInteger, String pString)
	{
		return put(pInteger, pString, 0.0);
	}

	public boolean put(Integer pInteger, String pString, double pValue)
	{
		try
		{
			mInsertStatement.clearWarnings();
			mInsertStatement.clearParameters();
			mInsertStatement.setDouble(1, pValue);
			mInsertStatement.setInt(2, pInteger);
			mInsertStatement.setString(3, pString);
			mInsertStatement.executeUpdate();
			mCounter++;
			automaticCommit();
			return true;
		}
		catch (SQLException e)
		{
			logerror(e);
			return false;
		}
	}

	public int removeString(String pString)
	{
		try
		{
			mStringDeleteStatement.setString(1, pString);
			int lDeletedRows = mStringDeleteStatement.executeUpdate();
			mCounter += lDeletedRows;
			automaticCommit();
			return lDeletedRows;
		}
		catch (SQLException e)
		{
			logerror(e);
			return 0;
		}
	}

	public int removeInteger(int pInteger)
	{
		try
		{
			mIntegerDeleteStatement.setInt(1, pInteger);
			int lDeletedRows = mIntegerDeleteStatement.executeUpdate();
			mCounter += lDeletedRows;
			automaticCommit();
			return lDeletedRows;
		}
		catch (SQLException e)
		{
			logerror(e);
			return 0;
		}
	}

	public int removeIntegerString(int pInteger, String pString)
	{
		try
		{
			mIntegerStringDeleteStatement.setInt(1, pInteger);
			mIntegerStringDeleteStatement.setString(2, pString);
			int lDeletedRows = mIntegerStringDeleteStatement.executeUpdate();
			mCounter += lDeletedRows;
			automaticCommit();
			return lDeletedRows;
		}
		catch (SQLException e)
		{
			logerror(e);
			return 0;
		}
	}

	public void putAllIntegers(String pString, Set<Integer> pIntegerSet)
	{
		for (Integer lInteger : pIntegerSet)
		{
			put(lInteger, pString);
		}
	}

	public void putAllStrings(Integer pInteger, Set<String> pStringSet)
	{
		for (String lString : pStringSet)
		{
			put(pInteger, lString);
		}
	}

	public void clear()
	{
		commit(mDatabaseConnection);
		dropTable(mDatabaseConnection, mName);
		commit(mDatabaseConnection);
		createTable(mDatabaseConnection, mName, mMinStringSize, mMaxStringSize);
		commit(mDatabaseConnection);
	}

	public int getCommitPeriod()
	{
		return mCommitPeriod;
	}

	public void setCommitPeriod(int commitPeriod)
	{
		mCommitPeriod = commitPeriod;
	}

	public void close()
	{
		try
		{
			if (mTemp)
			{
				dropTable(mDatabaseConnection, mName);
			}
			else
			{
				commitChanges();
			}
			mInsertStatement.close();
			mIntegerDeleteStatement.close();
			mIntegerQueryStatement.close();
			mIntegerStringDeleteStatement.close();
			mSizeStatement.close();
			mStringDeleteStatement.close();
			mStringQueryStatement.close();
		}
		catch (SQLException e)
		{
			logerror(e);
		}
	}

	@Override
	protected void finalize() throws Throwable
	{
		close();
		super.finalize();
	}

	@Override
	public String toString()
	{
		return "{" + mDatabaseConnection + ", " + mName + " action counter=" + mCounter + "}";
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		else if (this.toString() == obj.toString())
			return true;
		else
			return false;
	}

	@Override
	public int hashCode()
	{
		return this.toString().hashCode();
	}

	private void logerror(Throwable e)
	{
		cLogger.error(e);
		debug(e);
	}

	private void loginfo(Throwable e)
	{
		cLogger.info(e);
	}

	private void debug(Throwable e)
	{
		// cLogger.e.printStackTrace();
	}
}
