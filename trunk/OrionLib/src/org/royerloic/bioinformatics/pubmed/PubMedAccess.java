package org.royerloic.bioinformatics.pubmed;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

/**
 * 
 * @author Joerg Hakenberg
 * 
 */

public class PubMedAccess
{
	public static final Integer	cMaxPubMedId	= 17000000;
	public static final Random	cRandom				= new Random(System.currentTimeMillis());
	static String								dbAccessUrl		= DatabaseConstants.dbAccessUrlMyserver + "yggDoc_Medline";
	static String								dbTable				= "`medline_citation`";

	static
	{
		try
		{
			Class.forName(DatabaseConstants.dbAccessDriver);
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static Connection						connection;
	static
	{
		try
		{
			connection = DriverManager.getConnection(dbAccessUrl, DatabaseConstants.dbAccessUser,
					DatabaseConstants.dbAccessPass);
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static PreparedStatement		statement;
	static
	{
		String table = dbTable;
		try
		{
			statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE pmid=?");
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Returns the abtract of a PubMed citation. Concatenates title and text.
	 * 
	 * @param pubmedID
	 * @return
	 */
	public static String getAbstractForPubMedID(Integer pubmedID)
	{
		String res = "";

		ResultSet resultset = null;
		try
		{
			statement.setInt(1, pubmedID);
			resultset = statement.executeQuery();
			resultset.first();
			String title = resultset.getString("article_title");
			String text = resultset.getString("abstract_text");

			if (title != null)
				res = title.trim();

			// add the text to the full citation
			if (text != null)
			{
				if (res.endsWith(".") || res.endsWith("?") || res.endsWith("!"))
					res += " " + text;
				else
					res += ". " + text;
			}

		}
		catch (java.sql.SQLException sqle)
		{
			// System.err.println("No citation found for " + pubmedID);
			// sqle.printStackTrace();
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
		finally
		{
			try
			{
				if (resultset != null)
					resultset.close();
			}
			catch (Exception e)
			{
			}
		}

		return res;
	}

	/**
	 * Returns the abtract of a PubMed citation. Concatenates title and text.
	 * 
	 * @param pubmedID
	 * @return
	 */
	public static String getRandomAbstract()
	{
		return getAbstractForPubMedID(cRandom.nextInt(cMaxPubMedId));
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{

		for (int i = 0; i < 1000; i++)
		{
			System.out.println(getRandomAbstract());
		}
	}

}
