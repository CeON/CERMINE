package pl.edu.icm.yadda.analysis.jrlsimilarity.process;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.DoubleJournalSimilarity;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalPairMetaData;

/**
 * An implementation of a method that calculates similiraty on a basis
 * of pair of journals and theirs metadata.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class BasicSimilarityCaculateMethod implements SimilarityCalculateMethod {

	private JournalPairMetaData journalPair;
	@Override
	public DoubleJournalSimilarity calculate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setJournalPair(JournalPairMetaData journalPair) {
		this.journalPair = journalPair;
		
	}

}
