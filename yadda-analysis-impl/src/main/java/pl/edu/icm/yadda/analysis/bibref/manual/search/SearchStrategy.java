package pl.edu.icm.yadda.analysis.bibref.manual.search;

import java.util.List;

import pl.edu.icm.yadda.service.search.searching.SearchResult;
import pl.edu.icm.yadda.tools.bibref.model.SimpleMetadata;
/**
 * Interface containing all the search methods needed by MetadataBibReferenceMatcher.
 * 
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 *
 */
public interface SearchStrategy {
	public List<SearchResult> searchByAuthorJournalYear(SimpleMetadata metadata);
	public List<SearchResult> searchByAuthorYear(SimpleMetadata metadata);
}
