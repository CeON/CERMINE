package pl.edu.icm.yadda.analysis.jrlsimilarity.repo.meta;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.JournalId;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalMetaData;
import pl.edu.icm.yadda.analysis.jrlsimilarity.repo.meta.connector.JMRepoConnector;
import pl.edu.icm.yadda.analysis.jrlsimilarity.repo.meta.connector.PostgresJMRepoConnector;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of postgres based journal metadada repository.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class PostgresJMRepo implements JMRepo<PostgresJMRepoConnector> {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	private static PostgresJMRepo instance = null;
	
	public synchronized static PostgresJMRepo getInstance(){
		if (instance == null) instance = new PostgresJMRepo(postgresUrl);
		return instance;
	}
	
	private static String postgresUrl = "jdbc:postgresql://localhost:5432/journalmetarepo";
	
	
	private static Connection dbConnection = null;
	
	public Connection getConnection(){
		return dbConnection;
	}
	
	public synchronized boolean executeUpdate(String query){
		Statement st;
		boolean rs = false;
		try {
			st = dbConnection.createStatement();
			rs = st.execute(query);
			st.close();
		} catch (SQLException e) {
			log.error("SQLException in executeUpdate(() method: " + query + " -  " + e);
		}
		return rs;
	}
	
	public synchronized int executeInsertQuery(String query){
		Statement st;
		int rs = 0;
		try {
			st = dbConnection.createStatement();
			rs = st.executeUpdate(query);
			st.close();
		} catch (SQLException e) {
			log.error("SQLException in executeInsertQuery(() method: " + query + " -  " + e);
		}
		return rs;
		
	}
	
	/** Executes select query on Postgres Repo 
	 * After executing that query the resulting ResultSet and Statement that was
	 * used for obtaining that data should be both closed.*/
	public synchronized ResultSet executeSelectQuery(String query){
		Statement st;
		ResultSet rs = null;
		try {
			st = dbConnection.createStatement();
			rs = st.executeQuery(query); 
			
		} catch (SQLException e) {
			log.error("SQLException in executeSelectQuery(() method, query " + query + ", : "+  e);
		}
		return rs;
	}
	
	@Override
	public PostgresJMRepoConnector getConnector() {
		return new PostgresJMRepoConnector(getInstance());
			
			
	}
	
	protected void finalize() throws Throwable {
		try{
			dbConnection.close();
		} finally{
			super.finalize();
		}
	}
	
	private void initializeConnection(){
		try {
//			System.out.println("-----------\n INITIALIZE");
			Class.forName("org.postgresql.Driver");
			dbConnection = DriverManager.getConnection(this.postgresUrl, "yadda", "yadda");
//			System.out.println(dbConnection);
		} catch (ClassNotFoundException e) {
			log.error("Can not load org.postgresql.Driver");
//			System.out.println("INITIALIZEERRORCLASS + " + e);
		} catch (SQLException e) {
			log.error("SQL Exception " +e);
//			System.out.println("INITIALIZEERRORSQL + " + e);
		}

		
	}
	//FIXME To ensure that it works even if table journalmeta/debug_xxx does NOT exits 
	private void debug_deleteTables(boolean journalMetaTable, boolean journalDisOccurTable) {
		if(journalMetaTable){
			executeUpdate("DROP TABLE journalmeta;CREATE TABLE journalmeta (" +
				"journalmeta_id		serial," +
				"issn		varchar(140)," +
				"eissn		varchar(140)," +
				"title		varchar(250), "+
				"acrtitle varchar(40),"+
				"count		integer," +
				"CONSTRAINT journalmeta_pkey PRIMARY KEY (journalmeta_id)"+
			");");
			log.warn("DROP TABLE DONE DONE DONE DONE DONE!!!!!!!!!!!!!!!!!!!\nDROP TABLE DONE DONE DONE DONE DONE!!!!!!!!!!!!!!!!!!!\nDROP TABLE DONE DONE DONE DONE DONE!!!!!!!!!!!!!!!!!!!\nDROP TABLE DONE DONE DONE DONE DONE!!!!!!!!!!!!!!!!!!!\nDROP TABLE DONE DONE DONE DONE DONE!!!!!!!!!!!!!!!!!!!\nDROP TABLE DONE DONE DONE DONE DONE!!!!!!!!!!!!!!!!!!!\nDROP TABLE DONE DONE DONE DONE DONE!!!!!!!!!!!!!!!!!!!\n");
		}
		if(journalDisOccurTable)
			executeUpdate("DROP TABLE debug_journalmetaoccur;CREATE TABLE debug_journalmetaoccur("+
					"debug_journalmetaoccur_id	serial,"+
					"occur_code		int,"+
					"CONSTRAINT debug_journalmetaoccur_pkey PRIMARY KEY (debug_journalmetaoccur_id)"+
			");");
		
	}
	
	private PostgresJMRepo(String postgresURL){
		initializeConnection();	
		debug_deleteTables(true, true);
		
	}
	


}
