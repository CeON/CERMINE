package pl.edu.icm.yadda.analysis.jrlsimilarity.process;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.JournalSimilarity;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalPairMetaData;

;

/**
 * Interface that provides basic methods for calculating similarity between 
 * journals. It is used in processing node in process that calculates similarity
 * value from specific journal metadata.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public interface SimilarityCalculateMethod {
	
	JournalSimilarity<?> calculate();
	
	void setJournalPair(JournalPairMetaData journalPair);
	
	
}
