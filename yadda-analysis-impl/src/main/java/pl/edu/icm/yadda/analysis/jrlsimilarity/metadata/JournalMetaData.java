package pl.edu.icm.yadda.analysis.jrlsimilarity.metadata;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.JournalId;

/**
 * Interface for encapsulating journal metadata such as keywords,  citations, identities, classification. Each object of class implementing JournalMetaData should contain  {@link #JournalMetaData} and a list of String values representing metadata.
 * @author  Michał Siemiończyk michsiem@icm.edu.pl
 */
public interface JournalMetaData {

	boolean isEmpty();
	
	void setEmpty(boolean isEmpty);

	JournalId getJournalId();

	void setJournalId(JournalId journalId);
}
