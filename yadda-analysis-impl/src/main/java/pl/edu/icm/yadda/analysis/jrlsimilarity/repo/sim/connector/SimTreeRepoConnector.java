package pl.edu.icm.yadda.analysis.jrlsimilarity.repo.sim.connector;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.DoubleJournalSimilarity;
import pl.edu.icm.yadda.analysis.jrlsimilarity.common.JournalId;
import pl.edu.icm.yadda.analysis.jrlsimilarity.repo.sim.SimTreeRepo;


/**
 * Connector class which provides method for contacting with
 * Journal Similatiry Repository built on tree structure.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class SimTreeRepoConnector implements SimRepoConnector {

	private SimTreeRepo repoInstance;
	
	@Override
	public boolean isSimilaritySet(DoubleJournalSimilarity similarity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSimilaritySet(JournalId journalId1, JournalId journalId2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DoubleJournalSimilarity getSimilarity(JournalId journalId1,
			JournalId journalId2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DoubleJournalSimilarity getSimilarity(
			DoubleJournalSimilarity similarity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addNewSimilarity(DoubleJournalSimilarity journalSimilarity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addNewSimilarity(JournalId journalId1, JournalId journalId2,
			double simValue) {
		// TODO Auto-generated method stub
		return false;
	}

}
