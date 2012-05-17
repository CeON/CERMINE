package pl.edu.icm.yadda.analysis.jrlsimilarity.repo.sim.connector;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.DoubleJournalSimilarity;
import pl.edu.icm.yadda.analysis.jrlsimilarity.common.JournalId;

/**
 * Connector-interface for journal similarity repository. 
 * It provides all necessary methods for reaching the repository 
 * from user's API as well as backend processes.
 * 
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public interface SimRepoConnector {
	
	boolean isSimilaritySet(DoubleJournalSimilarity similarity);
	
	boolean isSimilaritySet(JournalId journalId1, JournalId journalId2);
	
	DoubleJournalSimilarity getSimilarity(JournalId journalId1, JournalId journalId2);
	
	DoubleJournalSimilarity getSimilarity(DoubleJournalSimilarity similarity);
	
	boolean addNewSimilarity(DoubleJournalSimilarity journalSimilarity);
	
	boolean addNewSimilarity(JournalId journalId1, JournalId journalId2, double simValue);
}
