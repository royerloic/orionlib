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
	private final int									mMaximumNuberOfResults	= Integer.MAX_VALUE;

	public class ValuedEntry
	{
		public int		mInteger;
		public String	mString;
		public double	mValue;

		public ValuedEntry(final int pInteger, final String pString, final double pValue)
		{
			super();
			this.mInteger = pInteger;
			this.mString = pString;
			this.mValue = pValue;
		}

		@Override
		public String toString()
		{
			return "{value=" + this.mValue + ", integer=" + this.mInteger + ", string=" + this.mString + "}";
		}
	}

	public ValuedIntegerStringBidiMap(final Connection pDatabaseConnection)
	{
		this(pDatabaseConnection, generateRandomName());
		this.mTemp = true;
	}

	private static String generateRandomName()
	{
		return ValuedIntegerStringBidiMap.class.getSimpleName() + "(" + Math.random() + ")";
	}

	public ValuedIntegerStringBidiMap(final Connection pDatabaseConnection, final String pName)
	{
		this(pDatabaseConnection, pName, 0, 250);
	}

	public ValuedIntegerStringBidiMap(final Connection pDatabaseConnection,
																		final String pName,
																		final int pMinStringSize,
																		final int pMaxStringSize)
	{
		this.mDatabaseConnection = pDatabaseConnection;
		this.mName = pName;
		this.mMinStringSize = pMinStringSize;
		this.mMaxStringSize = pMaxStringSize;
		try
		{
			this.mDatabaseConnection.setAutoCommit(false);
			this.mDatabaseConnection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			this.mDatabaseConnection.commit();
		}
		catch (final SQLException e)
		{
			loginfo(e);
		}
		createTable(this.mDatabaseConnection, this.mName, this.mMinStringSize, this.mMaxStringSize);
		createStatements(this.mDatabaseConnection, this.mName);
		commit(this.mDatabaseConnection);
		this.mCounter = 0;
	}

	private void createStatements(final Connection pDatabaseConnection, final String pTableName)
	{
		try
		{
			this.mInsertStatement = this.mDatabaseConnection.prepareStatement("INSERT INTO " + this.mName
					+ " (fValue,fInteger,fString) VALUES(?,?,?)");
			this.mStringQueryStatement = this.mDatabaseConnection
					.prepareStatement("SELECT fInteger, fValue FROM " + this.mName + " WHERE fString=? AND fvalue>=?");
			this.mIntegerQueryStatement = this.mDatabaseConnection
					.prepareStatement("SELECT fString, fValue FROM " + this.mName + " WHERE fInteger=? AND fvalue>=?");
			this.mStringDeleteStatement = this.mDatabaseConnection.prepareStatement("DELETE FROM "
					+ this.mName + " WHERE fString=?");
			this.mIntegerDeleteStatement = this.mDatabaseConnection.prepareStatement("DELETE FROM "
					+ this.mName + " WHERE fInteger=?");
			this.mIntegerStringDeleteStatement = this.mDatabaseConnection.prepareStatement("DELETE FROM "
					+ this.mName + " WHERE fInteger=? AND fString=?");
			this.mSizeStatement = this.mDatabaseConnection
					.prepareStatement("SELECT COUNT(*) AS Count FROM " + this.mName);
		}
		catch (final SQLException e)
		{
			logerror(e);
		}
	}

	private boolean createTable(final Connection pDatabaseConnection,
															final String pTableName,
															final int pMinStringSize,
															final int MaxStringSize)
	{
		try
		{
			final String lSql = "CREATE TABLE " + pTableName
					+ " ( fId INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0,INCREMENT BY 1), "
					+ " fValue DOUBLE, " + " fInteger INTEGER, " + " fString "
					+ getStringType(pMinStringSize, MaxStringSize) + ", " + " PRIMARY KEY (fId))";
			// System.out.println(lSql);
			pDatabaseConnection.createStatement().execute(lSql);
			return true;
		}
		catch (final SQLException e)
		{
			System.out.println("Table: " + pTableName + " allready created...");
			loginfo(e);
			return false;
		}
	}

	private boolean dropTable(final Connection pDatabaseConnection, final String pTableName)
	{
		try
		{
			pDatabaseConnection.createStatement().execute("DROP TABLE " + pTableName);
			return true;
		}
		catch (final SQLException e)
		{
			logerror(e);
			return false;
		}
	}

	private boolean createIndices(final Connection pDatabaseConnection, final String pTableName)
	{
		try
		{
			this.mDatabaseConnection.commit();
			pDatabaseConnection.createStatement().execute(
					"CREATE INDEX " + pTableName + "INDEXINTEGER ON " + pTableName + " (fInteger ASC)");
			this.mDatabaseConnection.commit();
			pDatabaseConnection.createStatement().execute(
					"CREATE INDEX " + pTableName + "INDEXSTRING ON " + pTableName + " (fString ASC)");
			this.mDatabaseConnection.commit();
			return true;
		}
		catch (final SQLException e)
		{
			logerror(e); // second time we run program
			return false;
		}
	}

	public boolean optimize()
	{
		return createIndices(this.mDatabaseConnection, this.mName);
	}

	private void automaticCommit()
	{
		if (this.mCounter % this.mCommitPeriod == 0)
			commit(this.mDatabaseConnection);
	}

	private boolean commit(final Connection pDatabaseConnection)
	{
		try
		{
			pDatabaseConnection.commit();
			return true;
		}
		catch (final SQLException e)
		{
			logerror(e);
			return false;
		}
	}

	public boolean commitChanges()
	{
		return commit(this.mDatabaseConnection);
	}

	private String getStringType(final int pMinStringSize, final int pMaxStringSize)
	{
		if (pMinStringSize == pMaxStringSize)
			return "CHAR(" + pMinStringSize + ")";
		else
			return "VARCHAR(" + pMaxStringSize + ")";
	}

	public int size()
	{
		try
		{
			final ResultSet lResultSet = this.mSizeStatement.executeQuery();
			while (lResultSet.next())
			{
				int lSize;

				lSize = lResultSet.getInt("Count");

				return lSize;
			}
			lResultSet.close();
		}
		catch (final SQLException e)
		{
			logerror(e);
		}
		throw new RuntimeException("Could not get size");
	}

	public boolean isEmpty()
	{
		return size() == 0;
	}

	public boolean containsString(final String pString)
	{
		try
		{
			this.mStringQueryStatement.setString(1, pString);
			final ResultSet lResultSet = this.mStringQueryStatement.executeQuery();
			return lResultSet.first();
		}
		catch (final SQLException e)
		{
			logerror(e);
			return false;
		}
	}

	public boolean containsInteger(final int pInteger)
	{
		try
		{
			this.mIntegerQueryStatement.setInt(1, pInteger);
			final ResultSet lResultSet = this.mIntegerQueryStatement.executeQuery();
			final boolean lContainsInteger = lResultSet.first();
			lResultSet.close();
			return lContainsInteger;
		}
		catch (final SQLException e)
		{
			logerror(e);
			return false;
		}
	}

	public List<ValuedEntry> getIntegers(final String pString)
	{
		return getIntegers(pString, 0);
	}

	public List<ValuedEntry> getIntegers(final String pString, final double pMinValue)
	{
		final ArrayList<ValuedEntry> lValuedEntryList = new ArrayList<ValuedEntry>();

		try
		{
			this.mStringQueryStatement.setString(1, pString);
			this.mStringQueryStatement.setDouble(2, pMinValue);
			final ResultSet lResultSet = this.mStringQueryStatement.executeQuery();
			int lCounter = 0;
			while (lResultSet.next())
				if (lCounter < this.mMaximumNuberOfResults)
				{
					final int lInteger = lResultSet.getInt("fInteger");
					final double lValue = lResultSet.getDouble("fValue");
					final ValuedEntry lValuedEntry = new ValuedEntry(lInteger, pString, lValue);
					lValuedEntryList.add(lValuedEntry);
					lCounter++;
				}
			lResultSet.close();
			return lValuedEntryList;
		}
		catch (final SQLException e)
		{
			logerror(e);
			return null;
		}
	}

	public List<ValuedEntry> getStrings(final int pInteger)
	{
		return getStrings(pInteger, 1);
	}

	public List<ValuedEntry> getStrings(final int pInteger, final double pMinValue)
	{
		final ArrayList<ValuedEntry> lValuedEntryList = new ArrayList<ValuedEntry>();

		try
		{
			this.mIntegerQueryStatement.setInt(1, pInteger);
			this.mIntegerQueryStatement.setDouble(2, pMinValue);
			final ResultSet lResultSet = this.mIntegerQueryStatement.executeQuery();
			int lCounter = 0;
			while (lResultSet.next())
				if (lCounter < this.mMaximumNuberOfResults)
				{
					final String lString = lResultSet.getString("fString");
					final double lValue = lResultSet.getDouble("fValue");
					final ValuedEntry lValuedEntry = new ValuedEntry(pInteger, lString, lValue);
					lValuedEntryList.add(lValuedEntry);
					lCounter++;
				}
			lResultSet.close();
			return lValuedEntryList;
		}
		catch (final SQLException e)
		{
			logerror(e);
			return null;
		}
	}

	public boolean put(final Integer pInteger, final String pString)
	{
		return put(pInteger, pString, 0.0);
	}

	public boolean put(final Integer pInteger, final String pString, final double pValue)
	{
		try
		{
			this.mInsertStatement.clearWarnings();
			this.mInsertStatement.clearParameters();
			this.mInsertStatement.setDouble(1, pValue);
			this.mInsertStatement.setInt(2, pInteger);
			this.mInsertStatement.setString(3, pString);
			this.mInsertStatement.executeUpdate();
			this.mCounter++;
			automaticCommit();
			return true;
		}
		catch (final SQLException e)
		{
			logerror(e);
			return false;
		}
	}

	public int removeString(final String pString)
	{
		try
		{
			this.mStringDeleteStatement.setString(1, pString);
			final int lDeletedRows = this.mStringDeleteStatement.executeUpdate();
			this.mCounter += lDeletedRows;
			automaticCommit();
			return lDeletedRows;
		}
		catch (final SQLException e)
		{
			logerror(e);
			return 0;
		}
	}

	public int removeInteger(final int pInteger)
	{
		try
		{
			this.mIntegerDeleteStatement.setInt(1, pInteger);
			final int lDeletedRows = this.mIntegerDeleteStatement.executeUpdate();
			this.mCounter += lDeletedRows;
			automaticCommit();
			return lDeletedRows;
		}
		catch (final SQLException e)
		{
			logerror(e);
			return 0;
		}
	}

	public int removeIntegerString(final int pInteger, final String pString)
	{
		try
		{
			this.mIntegerStringDeleteStatement.setInt(1, pInteger);
			this.mIntegerStringDeleteStatement.setString(2, pString);
			final int lDeletedRows = this.mIntegerStringDeleteStatement.executeUpdate();
			this.mCounter += lDeletedRows;
			automaticCommit();
			return lDeletedRows;
		}
		catch (final SQLException e)
		{
			logerror(e);
			return 0;
		}
	}

	public void putAllIntegers(final String pString, final Set<Integer> pIntegerSet)
	{
		for (final Integer lInteger : pIntegerSet)
			put(lInteger, pString);
	}

	public void putAllStrings(final Integer pInteger, final Set<String> pStringSet)
	{
		for (final String lString : pStringSet)
			put(pInteger, lString);
	}

	public void clear()
	{
		commit(this.mDatabaseConnection);
		dropTable(this.mDatabaseConnection, this.mName);
		commit(this.mDatabaseConnection);
		createTable(this.mDatabaseConnection, this.mName, this.mMinStringSize, this.mMaxStringSize);
		commit(this.mDatabaseConnection);
	}

	public int getCommitPeriod()
	{
		return this.mCommitPeriod;
	}

	public void setCommitPeriod(final int commitPeriod)
	{
		this.mCommitPeriod = commitPeriod;
	}

	public void close()
	{
		try
		{
			if (this.mTemp)
				dropTable(this.mDatabaseConnection, this.mName);
			else
				commitChanges();
			this.mInsertStatement.close();
			this.mIntegerDeleteStatement.close();
			this.mIntegerQueryStatement.close();
			this.mIntegerStringDeleteStatement.close();
			this.mSizeStatement.close();
			this.mStringDeleteStatement.close();
			this.mStringQueryStatement.close();
		}
		catch (final SQLException e)
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
		return "{" + this.mDatabaseConnection + ", " + this.mName + " action counter=" + this.mCounter + "}";
	}

	@Override
	public boolean equals(final Object obj)
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

	private void logerror(final Throwable e)
	{
		cLogger.error(e);
		debug(e);
	}

	private void loginfo(final Throwable e)
	{
		cLogger.info(e);
	}

	private void debug(final Throwable e)
	{
		// cLogger.e.printStackTrace();
	}
}
