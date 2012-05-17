package pl.edu.icm.yadda.analysis.jrlsimilarity.repo.meta.connector;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.JournalId;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalMetaData;


/**
 * Interface for journal metadata repository connector object.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 * @param <T>
 */
public interface JMRepoConnector<T> {
	
	void addJournalMetaData(T journalMetadata);
	
	JournalMetaData getJournalMetaData(JournalId journalId);
}
