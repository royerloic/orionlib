package utils.structures.dbcol;

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
	private static final Logger cLogger = Logger.getLogger(ValuedIntegerStringBidiMap.class);

	private boolean mTemp;
	private Connection mDatabaseConnection;
	private PreparedStatement mInsertStatement;
	private PreparedStatement mStringQueryStatement;
	private PreparedStatement mIntegerQueryStatement;
	private PreparedStatement mStringDeleteStatement;
	private PreparedStatement mIntegerDeleteStatement;
	private PreparedStatement mIntegerStringDeleteStatement;
	private PreparedStatement mSizeStatement;
	private String mName;
	private int mMinStringSize;
	private int mMaxStringSize;
	private int mCounter = 0;
	private int mCommitPeriod = 10000;
	private final int mMaximumNuberOfResults = Integer.MAX_VALUE;

	public class ValuedEntry
	{
		public int mInteger;
		public String mString;
		public double mValue;

		public ValuedEntry(	final int pInteger,
												final String pString,
												final double pValue)
		{
			super();
			mInteger = pInteger;
			mString = pString;
			mValue = pValue;
		}

		@Override
		public String toString()
		{
			return "{value=" + mValue
							+ ", integer="
							+ mInteger
							+ ", string="
							+ mString
							+ "}";
		}
	}

	public ValuedIntegerStringBidiMap(final Connection pDatabaseConnection)
	{
		this(pDatabaseConnection, generateRandomName());
		mTemp = true;
	}

	private static String generateRandomName()
	{
		return ValuedIntegerStringBidiMap.class.getSimpleName() + "("
						+ Math.random()
						+ ")";
	}

	public ValuedIntegerStringBidiMap(final Connection pDatabaseConnection,
																		final String pName)
	{
		this(pDatabaseConnection, pName, 0, 250);
	}

	public ValuedIntegerStringBidiMap(final Connection pDatabaseConnection,
																		final String pName,
																		final int pMinStringSize,
																		final int pMaxStringSize)
	{
		mDatabaseConnection = pDatabaseConnection;
		mName = pName;
		mMinStringSize = pMinStringSize;
		mMaxStringSize = pMaxStringSize;
		try
		{
			mDatabaseConnection.setAutoCommit(false);
			mDatabaseConnection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			mDatabaseConnection.commit();
		}
		catch (final SQLException e)
		{
			loginfo(e);
		}
		createTable(mDatabaseConnection, mName, mMinStringSize, mMaxStringSize);
		createStatements(mDatabaseConnection, mName);
		commit(mDatabaseConnection);
		mCounter = 0;
	}

	private void createStatements(final Connection pDatabaseConnection,
																final String pTableName)
	{
		try
		{
			mInsertStatement = mDatabaseConnection.prepareStatement("INSERT INTO " + mName
																															+ " (fValue,fInteger,fString) VALUES(?,?,?)");
			mStringQueryStatement = mDatabaseConnection.prepareStatement("SELECT fInteger, fValue FROM " + mName
																																		+ " WHERE fString=? AND fvalue>=?");
			mIntegerQueryStatement = mDatabaseConnection.prepareStatement("SELECT fString, fValue FROM " + mName
																																		+ " WHERE fInteger=? AND fvalue>=?");
			mStringDeleteStatement = mDatabaseConnection.prepareStatement("DELETE FROM " + mName
																																		+ " WHERE fString=?");
			mIntegerDeleteStatement = mDatabaseConnection.prepareStatement("DELETE FROM " + mName
																																			+ " WHERE fInteger=?");
			mIntegerStringDeleteStatement = mDatabaseConnection.prepareStatement("DELETE FROM " + mName
																																						+ " WHERE fInteger=? AND fString=?");
			mSizeStatement = mDatabaseConnection.prepareStatement("SELECT COUNT(*) AS Count FROM " + mName);
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
													+ " fValue DOUBLE, "
													+ " fInteger INTEGER, "
													+ " fString "
													+ getStringType(pMinStringSize, MaxStringSize)
													+ ", "
													+ " PRIMARY KEY (fId))";
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

	private boolean dropTable(final Connection pDatabaseConnection,
														final String pTableName)
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

	private boolean createIndices(final Connection pDatabaseConnection,
																final String pTableName)
	{
		try
		{
			mDatabaseConnection.commit();
			pDatabaseConnection	.createStatement()
													.execute("CREATE INDEX " + pTableName
																		+ "INDEXINTEGER ON "
																		+ pTableName
																		+ " (fInteger ASC)");
			mDatabaseConnection.commit();
			pDatabaseConnection	.createStatement()
													.execute("CREATE INDEX " + pTableName
																		+ "INDEXSTRING ON "
																		+ pTableName
																		+ " (fString ASC)");
			mDatabaseConnection.commit();
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
		return createIndices(mDatabaseConnection, mName);
	}

	private void automaticCommit()
	{
		if (mCounter % mCommitPeriod == 0)
			commit(mDatabaseConnection);
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
		return commit(mDatabaseConnection);
	}

	private String getStringType(	final int pMinStringSize,
																final int pMaxStringSize)
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
			final ResultSet lResultSet = mSizeStatement.executeQuery();
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
			mStringQueryStatement.setString(1, pString);
			final ResultSet lResultSet = mStringQueryStatement.executeQuery();
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
			mIntegerQueryStatement.setInt(1, pInteger);
			final ResultSet lResultSet = mIntegerQueryStatement.executeQuery();
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

	public List<ValuedEntry> getIntegers(	final String pString,
																				final double pMinValue)
	{
		final ArrayList<ValuedEntry> lValuedEntryList = new ArrayList<ValuedEntry>();

		try
		{
			mStringQueryStatement.setString(1, pString);
			mStringQueryStatement.setDouble(2, pMinValue);
			final ResultSet lResultSet = mStringQueryStatement.executeQuery();
			int lCounter = 0;
			while (lResultSet.next())
				if (lCounter < mMaximumNuberOfResults)
				{
					final int lInteger = lResultSet.getInt("fInteger");
					final double lValue = lResultSet.getDouble("fValue");
					final ValuedEntry lValuedEntry = new ValuedEntry(	lInteger,
																														pString,
																														lValue);
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
			mIntegerQueryStatement.setInt(1, pInteger);
			mIntegerQueryStatement.setDouble(2, pMinValue);
			final ResultSet lResultSet = mIntegerQueryStatement.executeQuery();
			int lCounter = 0;
			while (lResultSet.next())
				if (lCounter < mMaximumNuberOfResults)
				{
					final String lString = lResultSet.getString("fString");
					final double lValue = lResultSet.getDouble("fValue");
					final ValuedEntry lValuedEntry = new ValuedEntry(	pInteger,
																														lString,
																														lValue);
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

	public boolean put(	final Integer pInteger,
											final String pString,
											final double pValue)
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
			mStringDeleteStatement.setString(1, pString);
			final int lDeletedRows = mStringDeleteStatement.executeUpdate();
			mCounter += lDeletedRows;
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
			mIntegerDeleteStatement.setInt(1, pInteger);
			final int lDeletedRows = mIntegerDeleteStatement.executeUpdate();
			mCounter += lDeletedRows;
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
			mIntegerStringDeleteStatement.setInt(1, pInteger);
			mIntegerStringDeleteStatement.setString(2, pString);
			final int lDeletedRows = mIntegerStringDeleteStatement.executeUpdate();
			mCounter += lDeletedRows;
			automaticCommit();
			return lDeletedRows;
		}
		catch (final SQLException e)
		{
			logerror(e);
			return 0;
		}
	}

	public void putAllIntegers(	final String pString,
															final Set<Integer> pIntegerSet)
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

	public void setCommitPeriod(final int commitPeriod)
	{
		mCommitPeriod = commitPeriod;
	}

	public void close()
	{
		try
		{
			if (mTemp)
				dropTable(mDatabaseConnection, mName);
			else
				commitChanges();
			mInsertStatement.close();
			mIntegerDeleteStatement.close();
			mIntegerQueryStatement.close();
			mIntegerStringDeleteStatement.close();
			mSizeStatement.close();
			mStringDeleteStatement.close();
			mStringQueryStatement.close();
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
		return "{" + mDatabaseConnection
						+ ", "
						+ mName
						+ " action counter="
						+ mCounter
						+ "}";
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
