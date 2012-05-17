package pl.edu.icm.yadda.analysis.jrlsimilarity.repo.meta.connector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import pl.edu.icm.yadda.analysis.jrlsimilarity.common.JournalId;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalMetaData;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.disambiguation.JournalDisambiguationMeta;
import pl.edu.icm.yadda.analysis.jrlsimilarity.repo.meta.PostgresJMRepo;

/**
 * Connector class for an implementation of
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class PostgresJMRepoConnector implements JMRepoConnector<JournalDisambiguationMeta>{

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private PostgresJMRepo repo;
	
	public PostgresJMRepoConnector(PostgresJMRepo repo){
		this.repo = repo;
	}
	
	/** It's purpose is to add to repo journal's metadada that does not exists yet.*/
	@Override
	public void addJournalMetaData(JournalDisambiguationMeta journalMetadata) {
		
//		System.out.println(insertJournalMetaSql);
		PreparedStatement pstmt = null;
		try {
			pstmt = repo.getConnection().prepareStatement("INSERT INTO journalmeta(issn, eissn, title, acrtitle, count) "+
					"VALUES (?,?,?,?,?)");
				pstmt.setString(1, journalMetadata.getIssn() == null ? "" : journalMetadata.getIssn());
				pstmt.setString(2, journalMetadata.geteIssn() == null ? "" : journalMetadata.geteIssn());
				pstmt.setString(3, journalMetadata.getTitle()== null ? "" : journalMetadata.getTitle());
				pstmt.setString(4, journalMetadata.getAcronymTitle() == null ? "" : journalMetadata.getAcronymTitle());
				pstmt.setInt(5, 1);
//			System.out.println(pstmt);
			if(!metaExistsInRepo(journalMetadata))
				pstmt.executeUpdate();
			else{
				PreparedStatement pstmt2 = repo.getConnection().prepareStatement("UPDATE journalmeta SET count=count+1 " +
						"WHERE issn = ? AND eissn = ? AND title = ? AND acrtitle = ?");
//					pstmt2.setInt(1, journalMetadata.getArticlesNr() == 0 ? 1 : journalMetadata.getArticlesNr());
					pstmt2.setString(1, journalMetadata.getIssn() == null ? "" : journalMetadata.getIssn());
					pstmt2.setString(2, journalMetadata.geteIssn() == null ? "" : journalMetadata.geteIssn());
					pstmt2.setString(3, journalMetadata.getTitle()== null ? "" : journalMetadata.getTitle());
					pstmt2.setString(4, journalMetadata.getAcronymTitle() == null ? "" : journalMetadata.getAcronymTitle());
				pstmt2.executeUpdate();
			}
		} catch (SQLException e) {
			log.info("SQLException in addJournalMetaData() method, query " + pstmt + ", : "+  e);
			e.printStackTrace();
		}
		

	}



	@Override
	public JournalMetaData getJournalMetaData(JournalId journalId) {
		
		return null;
	}

	/** Debug method that updates debug_journalmetaoccur table
	 * 	very ugly, indeed.*/
	public void addJournalDebugOccur(JournalDisambiguationMeta meta) {
		if(meta == null){
			log.error("META NUUUUUUUUUUUULLLLLLLLLLLLLLLL\nMETA NUUUUUUUUUUUULLLLLLLLLLLLLLLL\nMETA NUUUUUUUUUUUULLLLLLLLLLLLLLLL\nMETA NUUUUUUUUUUUULLLLLLLLLLLLLLLL\nMETA NUUUUUUUUUUUULLLLLLLLLLLLLLLL\n");
			return;
		}
		if(meta.getIssn() == null && meta.geteIssn() != null && meta.getTitle() != null)
			repo.executeInsertQuery("INSERT INTO debug_journalmetaoccur(occur_code) VALUES (1);");
		else if(meta.getIssn() != null && meta.geteIssn() == null && meta.getTitle() != null)
			repo.executeInsertQuery("INSERT INTO debug_journalmetaoccur(occur_code) VALUES (2);");
		else if(meta.getIssn() == null && meta.geteIssn() == null && meta.getTitle() != null)
			repo.executeInsertQuery("INSERT INTO debug_journalmetaoccur(occur_code) VALUES (3);");
		else if(meta.getIssn() != null && meta.geteIssn() != null && meta.getTitle() == null)
			repo.executeInsertQuery("INSERT INTO debug_journalmetaoccur(occur_code) VALUES (4);");
		else if(meta.getIssn() == null && meta.geteIssn() != null && meta.getTitle() == null)
			repo.executeInsertQuery("INSERT INTO debug_journalmetaoccur(occur_code) VALUES (5);");
		else if(meta.getIssn() != null && meta.geteIssn() == null && meta.getTitle() == null)
			repo.executeInsertQuery("INSERT INTO debug_journalmetaoccur(occur_code) VALUES (6);");
		else if(meta.getIssn() == null && meta.geteIssn() == null && meta.getTitle() == null)
			repo.executeInsertQuery("INSERT INTO debug_journalmetaoccur(occur_code) VALUES (7);");
		
	}
	
	
	
	
	private boolean metaExistsInRepo(JournalDisambiguationMeta meta) {
		
		ResultSet rs = null;
		try {
			PreparedStatement pstmt = repo.getConnection().prepareStatement("SELECT issn, eissn, title, acrtitle "+
					"FROM journalmeta WHERE (issn = ?) AND (eissn = ?) " + 
					"AND (title = ?) AND (acrtitle = ?)");
				
				pstmt.setString(1, meta.getIssn() == null ? "" : meta.getIssn());
				pstmt.setString(2, meta.geteIssn() == null ? "" : meta.geteIssn());
				pstmt.setString(3, meta.getTitle()== null ? "" : meta.getTitle());
				pstmt.setString(4, meta.getAcronymTitle() == null ? "" : meta.getAcronymTitle());
			rs = pstmt.executeQuery();
//			System.out.println(pstmt);
			return rs.next();
		} catch (SQLException e) {
			log.info("SQLException " + e);
			return false;
		}finally{
			try {
				rs.getStatement().close(); //closes both Statement and 
				
			} catch (SQLException e) {
				log.info("SQLException while closing ResultSet" + e );

			}
		}
	}



}
