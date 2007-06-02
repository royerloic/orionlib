package utils.bioinformatics.pubmed;

/**
 * 
 * @author Joerg Hakenberg
 * 
 */

public class DatabaseConstants
{

	public static final String	dbAccessDriver				= "com.mysql.jdbc.Driver";
	public static final String	dbAccessUrlYggdrasil	= "jdbc:mysql://yggdrasil:3306/";
	public static final String	dbAccessUrlMyserver		= "jdbc:mysql://myserver:3306/";
	public static final String	dbAccessUrlUserJoerg	= dbAccessUrlMyserver + "user_joerg";
	public static final String	dbAccessUser					= "joerg";
	public static final String	dbAccessPassY					= "lola03";
	public static final String	dbAccessPassM					= "lola03";
	public static final String	dbAccessPass					= "lola03";

}

/*
 * -- phpMyAdmin SQL Dump -- version 2.6.2-Debian-3sarge1 --
 * http://www.phpmyadmin.net -- -- Host: localhost -- Generation Time: Sep 01,
 * 2006 at 05:07 PM -- Server version: 4.1.11 -- PHP Version: 4.3.10-16 -- --
 * Database: `user_joerg` -- --
 * -------------------------------------------------------- -- -- Table
 * structure for table `GOA_ID2GO` --
 * 
 * CREATE TABLE GOA_ID2GO ( uid varchar(10) NOT NULL default '', go varchar(10)
 * NOT NULL default '', qual tinyint(4) default NULL ) ENGINE=MyISAM DEFAULT
 * CHARSET=utf8; -- -------------------------------------------------------- -- --
 * Table structure for table `GeneOntology_GO2GO` --
 * 
 * CREATE TABLE GeneOntology_GO2GO ( parent varchar(10) NOT NULL default '',
 * child varchar(10) NOT NULL default '', distance tinyint(4) NOT NULL default
 * '0' ) ENGINE=MyISAM DEFAULT CHARSET=utf8; --
 * -------------------------------------------------------- -- -- Table
 * structure for table `Inter` --
 * 
 * CREATE TABLE Inter ( parent varchar(10) NOT NULL default '', child
 * varchar(10) NOT NULL default '', distance tinyint(4) NOT NULL default '0' )
 * ENGINE=MyISAM DEFAULT CHARSET=utf8; --
 * -------------------------------------------------------- -- -- Table
 * structure for table `UniProt_ID2Disease` --
 * 
 * CREATE TABLE UniProt_ID2Disease ( uid varchar(6) NOT NULL default '', disease
 * text NOT NULL ) ENGINE=MyISAM DEFAULT CHARSET=utf8; --
 * -------------------------------------------------------- -- -- Table
 * structure for table `UniProt_ID2Function` --
 * 
 * CREATE TABLE UniProt_ID2Function ( uid varchar(6) NOT NULL default '',
 * `function` text NOT NULL ) ENGINE=MyISAM DEFAULT CHARSET=utf8; --
 * -------------------------------------------------------- -- -- Table
 * structure for table `UniProt_ID2GO` --
 * 
 * CREATE TABLE UniProt_ID2GO ( uid varchar(6) NOT NULL default '', goid
 * varchar(10) NOT NULL default '' ) ENGINE=MyISAM DEFAULT CHARSET=utf8; --
 * -------------------------------------------------------- -- -- Table
 * structure for table `UniProt_ID2ID` --
 * 
 * CREATE TABLE UniProt_ID2ID ( uid varchar(6) NOT NULL default '', nid
 * varchar(6) NOT NULL default '' ) ENGINE=MyISAM DEFAULT CHARSET=utf8; --
 * -------------------------------------------------------- -- -- Table
 * structure for table `UniProt_ID2Interaction` --
 * 
 * CREATE TABLE UniProt_ID2Interaction ( uid varchar(6) NOT NULL default '',
 * iuid varchar(6) NOT NULL default '', iisoform varchar(8) default NULL )
 * ENGINE=MyISAM DEFAULT CHARSET=utf8; --
 * -------------------------------------------------------- -- -- Table
 * structure for table `UniProt_ID2Keyword` --
 * 
 * CREATE TABLE UniProt_ID2Keyword ( uid varchar(6) NOT NULL default '', keyword
 * varchar(50) NOT NULL default '' ) ENGINE=MyISAM DEFAULT CHARSET=utf8; --
 * -------------------------------------------------------- -- -- Table
 * structure for table `UniProt_ID2Origin` --
 * 
 * CREATE TABLE UniProt_ID2Origin ( uid varchar(6) NOT NULL default '', origin
 * varchar(80) NOT NULL default '', taxid int(11) default '0' ) ENGINE=MyISAM
 * DEFAULT CHARSET=utf8; --
 * -------------------------------------------------------- -- -- Table
 * structure for table `UniProt_ID2PubMed` --
 * 
 * CREATE TABLE UniProt_ID2PubMed ( uid varchar(6) NOT NULL default '', pmid
 * varchar(10) NOT NULL default '' ) ENGINE=MyISAM DEFAULT CHARSET=utf8; --
 * -------------------------------------------------------- -- -- Table
 * structure for table `UniProt_ID2Synonym` --
 * 
 * CREATE TABLE UniProt_ID2Synonym ( uid varchar(6) NOT NULL default '', synonym
 * text NOT NULL, `type` tinyint(4) default NULL ) ENGINE=MyISAM DEFAULT
 * CHARSET=utf8;
 * 
 * 
 * 
 */