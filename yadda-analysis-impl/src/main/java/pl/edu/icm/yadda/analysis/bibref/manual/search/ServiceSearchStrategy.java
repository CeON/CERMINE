package pl.edu.icm.yadda.analysis.bibref.manual.search;

import java.util.ArrayList;
import java.util.List;

import pl.edu.icm.yadda.client.indexing.IndexFields;
import pl.edu.icm.yadda.service.search.query.SearchOperator;
import pl.edu.icm.yadda.service.search.query.SearchQuery;
import pl.edu.icm.yadda.service.search.query.criteria.BooleanCriterion;
import pl.edu.icm.yadda.service.search.query.criteria.FieldCriterion;
import pl.edu.icm.yadda.service.search.searching.FieldRequest;
import pl.edu.icm.yadda.service.search.searching.ResultsFormat;
import pl.edu.icm.yadda.service.search.searching.SearchResult;
import pl.edu.icm.yadda.service2.search.ISearchService;
import pl.edu.icm.yadda.service2.search.SearchIndexRequest;
import pl.edu.icm.yadda.tools.bibref.model.AuthorSimpleMetadata;
import pl.edu.icm.yadda.tools.bibref.model.SimpleMetadata;

/**
 * SearchStrategy implementation using Yadda Search Service.
 * 
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 */
public class ServiceSearchStrategy implements SearchStrategy {

	private ISearchService searchService;
	private String indexName;

	/**
	 * Queries the reference index searching for similar records based on
	 * author, journal and year.
	 * 
	 * @param metadata
	 *            metadata of referencing or referenced document
	 * @return A list of search results
	 */
	@Override
	public List<SearchResult> searchByAuthorJournalYear(SimpleMetadata metadata) {
		if (metadata.getJournal() == null || metadata.getYear() == null
				|| metadata.getAuthors() == null
				|| metadata.getAuthors().isEmpty()) {
			return new ArrayList<SearchResult>();
		}

		SearchQuery query = new SearchQuery();

		BooleanCriterion criterion = new BooleanCriterion();
		criterion.setOperator(SearchOperator.AND);
		criterion.addCriterion(new FieldCriterion(IndexFields.F_JOURNAL_HASH,
				metadata.getJournalHash()));
		criterion.addCriterion(new FieldCriterion(
				IndexFields.F_DATE_PUBLISHED_YEAR, metadata.getYear()));
		for (AuthorSimpleMetadata author : metadata.getAuthors()) {
			if (author.getSurname() != null) {
				criterion.addCriterion(new FieldCriterion(
						IndexFields.F_AUTHOR_COAUTHOR_SURNAME, author
								.getSurname()));
			}
		}
		query.addCriterion(criterion);

		return searchByQuery(query);
	}

	/**
	 * Queries the reference index searching for similar records based on author
	 * and year.
	 * 
	 * @param metadata
	 *            metadata of referencing or referenced document
	 * @return A list of search results
	 */
	@Override
	public List<SearchResult> searchByAuthorYear(SimpleMetadata metadata) {
		if (metadata.getYear() == null || metadata.getAuthors() == null
				|| metadata.getAuthors().isEmpty()) {
			return new ArrayList<SearchResult>();
		}

		SearchQuery query = new SearchQuery();

		BooleanCriterion criterion = new BooleanCriterion();
		criterion.setOperator(SearchOperator.AND);
		criterion.addCriterion(new FieldCriterion(
				IndexFields.F_DATE_PUBLISHED_YEAR, metadata.getYear()));
		for (AuthorSimpleMetadata author : metadata.getAuthors()) {
			if (author.getSurname() != null) {
				criterion.addCriterion(new FieldCriterion(
						IndexFields.F_AUTHOR_COAUTHOR_SURNAME, author
								.getSurname()));
			}
		}

		query.addCriterion(criterion);

		return searchByQuery(query);
	}

	/**
	 * Searches the index according to the given query.
	 * 
	 * @param query
	 *            a query
	 * @return A list of search results
	 */
	private List<SearchResult> searchByQuery(SearchQuery query) {
		ResultsFormat format = new ResultsFormat(new FieldRequest(
				IndexFields.F_BIBREF_POSITION), new FieldRequest(
				IndexFields.F_BIBREF_SOURCE), new FieldRequest(
				IndexFields.F_AUTHOR_COAUTHOR_NORMALIZED), new FieldRequest(
				IndexFields.F_DEF_NAME), new FieldRequest(
				IndexFields.F_JOURNAL_NAME), new FieldRequest(
				IndexFields.F_VOLUME), new FieldRequest(IndexFields.F_ISSUE),
				new FieldRequest(IndexFields.F_DATE_PUBLISHED_YEAR));

		SearchIndexRequest indexRequest = new SearchIndexRequest();
		indexRequest.setIndexName(indexName);
		indexRequest.setQuery(query);
		indexRequest.setResultsFormat(format);

		return searchService.search(indexRequest).getResult().getResults();
	}

	public void setSearchService(ISearchService searchService) {
		this.searchService = searchService;
	}

	public void setIndexName(String indexName) {
        this.indexName = indexName;
	}

}
