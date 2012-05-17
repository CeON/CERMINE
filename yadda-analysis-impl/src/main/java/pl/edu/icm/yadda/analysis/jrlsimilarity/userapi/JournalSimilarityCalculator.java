package pl.edu.icm.yadda.analysis.jrlsimilarity.userapi;

import java.util.List;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.DoubleJournalSimilarity;
import pl.edu.icm.yadda.analysis.jrlsimilarity.common.JournalId;
import pl.edu.icm.yadda.analysis.jrlsimilarity.repo.sim.connector.SimMatrixRepoConnector;

/**
 * User's API for calculating journal similarity feature.
 * It comes with two main static methods that provide desired funcionality
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */


public class JournalSimilarityCalculator {
	
	private SimMatrixRepoConnector connector = null;
	
	
	public List<JournalId> getSimiliarJournals(JournalId journalid){
		return null;
		//TODO finish!
	}
	
	public DoubleJournalSimilarity calculateSimilarity(JournalId journalId1, JournalId journalId2){
		return null;
		//TODO finish!
	}
}
