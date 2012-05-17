package pl.edu.icm.yadda.analysis.jrlsimilarity.metadata;


/**
 * Interface that specyfies basic Metadata parser abilities (Keywords, classifications,
 * citations and identities)
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public interface JournalMetaDataParser<T> {

	void setSource(T sourceElement);
	boolean parse();
}
