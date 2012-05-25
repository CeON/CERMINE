package pl.edu.icm.yadda.analysis.bibref.manual;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.bibref.manual.search.SearchStrategy;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.client.indexing.IndexFields;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;
import pl.edu.icm.yadda.service.search.searching.ResultField;
import pl.edu.icm.yadda.service.search.searching.SearchResult;
import pl.edu.icm.yadda.tools.abbr.AbbreviationDirectory;
import pl.edu.icm.yadda.tools.bibref.model.AuthorSimpleMetadata;
import pl.edu.icm.yadda.tools.bibref.model.BibReferenceTriple;
import pl.edu.icm.yadda.tools.bibref.model.DocSimpleMetadata;
import pl.edu.icm.yadda.tools.bibref.model.SimpleMetadata;


/**
 * Bibliographic reference matcher based on metadata information,
 * such as authors, title, journal name and year of publication.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class MetadataBibReferenceMatcher implements BibReferenceMatcher {

    private static final Logger log = LoggerFactory.getLogger(MetadataBibReferenceMatcher.class);
    
    private final MetadataReader metadataReader = new MetadataReader(YConstants.EXT_SCHEMA_EUDML);
    private SearchStrategy searchStrategy;

    /**
     * Finds matching documents referenced by given document.
     *
     * @param nlm the document in NLM format
     * @return a set of matched references
     * @throws TransformationException
     */
    @Override
    public Set<BibReferenceTriple> matchBibReferencedIds(String nlm) throws TransformationException {
        DocSimpleMetadata docMetadata = metadataReader.readFromNLMFile(nlm);
        if (docMetadata == null) {
            return null;
        }

        Set<BibReferenceTriple> docTriples = new HashSet<BibReferenceTriple>();
        List<SimpleMetadata> references = docMetadata.getReferences();
        for (SimpleMetadata refMetadata : references) {
            Set<BibReferenceTriple> refTriples = matchByAuthorJournalYear(refMetadata);
            if (!refTriples.isEmpty()) {
                docTriples.add(refTriples.iterator().next());
                continue;
            }
            refTriples = matchByAuthorYear(refMetadata);
            if (!refTriples.isEmpty()) {
                docTriples.add(refTriples.iterator().next());
                continue;
            }
        }

        return docTriples;
    }

    /**
     * Finds matching documents referencing given document.
     *
     * @param nlm the document in NLM format
     * @return a set of matched references
     * @throws TransformationException
     */
    @Override
    public Set<BibReferenceTriple> matchBibReferencingIds(String nlm) throws TransformationException {
        DocSimpleMetadata docMetadata = metadataReader.readFromNLMFile(nlm);
        if (docMetadata == null) {
            return null;
        }

        Set<BibReferenceTriple> refTriples = new HashSet<BibReferenceTriple>();
        refTriples.addAll(matchByAuthorJournalYear(docMetadata));
        refTriples.addAll(matchByAuthorYear(docMetadata));

        return refTriples;
    }

    /**
     * Matches documents (both referenced and referencing) by authors, journal
     * and year.
     *
     * @param metadata metadata of referencing or referenced document
     * @return a set of matched references
     */
    private Set<BibReferenceTriple> matchByAuthorJournalYear(SimpleMetadata metadata) {
        Set<BibReferenceTriple> refTriples = new HashSet<BibReferenceTriple>();

        List<SearchResult> searchResults = searchStrategy.searchByAuthorJournalYear(metadata);

        for (SearchResult result : searchResults) {
            if (metadata.getPosition() > 0 && isDocResult(result) && matchesMetadata(metadata, result)) {
                BibReferenceTriple triple = new BibReferenceTriple(metadata.getDocId(), metadata.getPosition(), result.getDocId());
                if (!triple.getDocumentId().equals(triple.getBibReferenceId())) {
                    refTriples.add(triple);
                    log.debug("MetadataBibReferenceMatcher: referenced document found by authors, journal, year: {}", triple);
                }
            }
            if (metadata.getPosition() == 0 && isRefResult(result) && matchesMetadata(metadata, result)) {
                try {
                    BibReferenceTriple triple = new BibReferenceTriple(getOneValue(result, IndexFields.F_BIBREF_SOURCE),
                        Integer.parseInt(getOneValue(result, IndexFields.F_BIBREF_POSITION)),
                        metadata.getDocId());
                    if (!triple.getDocumentId().equals(triple.getBibReferenceId())) {
                        refTriples.add(triple);
                        log.debug("MetadataBibReferenceMatcher: referencin document found by authors, journal, year: {}", triple);
                    }
                } catch (NumberFormatException e) {}
            }
        }

        return refTriples;
    }

    /**
     * Matches documents (both referenced and referencing) by authors and year.
     *
     * @param metadata metadata of referencing or referenced document
     * @return a set of matched references
     */
    private Set<BibReferenceTriple> matchByAuthorYear(SimpleMetadata metadata) {
        Set<BibReferenceTriple> refTriples = new HashSet<BibReferenceTriple>();

        List<SearchResult> searchResults = searchStrategy.searchByAuthorYear(metadata);

        for (SearchResult result : searchResults) {
            if (metadata.getPosition() > 0 && isDocResult(result) && matchesMetadataWithTitle(metadata, result)) {
                BibReferenceTriple triple = new BibReferenceTriple(metadata.getDocId(), metadata.getPosition(), result.getDocId());
                if (!triple.getDocumentId().equals(triple.getBibReferenceId())) {
                    refTriples.add(triple);
                    log.debug("MetadataBibReferenceMatcher: referenced document found by authors, year: {}", triple);
                }
            }
            if (metadata.getPosition() == 0 && isRefResult(result) && matchesMetadataWithTitle(metadata, result)) {
                try {
                    BibReferenceTriple triple = new BibReferenceTriple(getOneValue(result, IndexFields.F_BIBREF_SOURCE),
                        Integer.parseInt(getOneValue(result, IndexFields.F_BIBREF_POSITION)),
                        metadata.getDocId());
                    if (!triple.getDocumentId().equals(triple.getBibReferenceId())) {
                        refTriples.add(triple);
                        log.debug("MetadataBibReferenceMatcher: referencing document found by authors, year: {}", triple);
                    }
                } catch (NumberFormatException e) {}
            }
        }

        return refTriples;
    }

    private boolean matchesMetadata(SimpleMetadata metadata, SearchResult searchResult) {
        List<String> authors = getAllValues(searchResult, IndexFields.F_AUTHOR_COAUTHOR_NORMALIZED);
        if (authors == null || authors.size() != metadata.getAuthors().size()) {
            return false;
        }
        for (AuthorSimpleMetadata author : metadata.getAuthors()) {
            if (!authors.contains(author.getNormalized())) {
                return false;
            }
        }
        if (!passesSubsequence(metadata.getJournal(), getAllValues(searchResult, IndexFields.F_JOURNAL_NAME))) {
            return false;
        }
        if (!passesExact(metadata.getVolume(), getAllValues(searchResult, IndexFields.F_VOLUME))) {
            return false;
        }
        if (!passesExact(metadata.getIssue(), getAllValues(searchResult, IndexFields.F_ISSUE))) {
            return false;
        }
        if (!passesExact(metadata.getYear(), getAllValues(searchResult, IndexFields.F_DATE_PUBLISHED_YEAR))) {
            return false;
        }

        return true;
    }

    private boolean matchesMetadataWithTitle(SimpleMetadata metadata, SearchResult searchResult) {
        if (!matchesMetadata(metadata, searchResult)) {
            return false;
        }
        if (!passesSimilarity(metadata.getTitle(), getAllValues(searchResult, IndexFields.F_DEF_NAME))) {
            return false;
        }
        return true;
    }

    private boolean passesExact(String text, List<String> fields) {
        if (text == null || fields == null || fields.isEmpty()) {
            return true;
        }
        text = text.toLowerCase(Locale.ENGLISH).trim();
        for (String field : fields) {
            field = field.toLowerCase(Locale.ENGLISH).trim();
            if (text.equals(field)) {
                return true;
            }
        }
        return false;
    }

    private boolean passesSubsequence(String text, List<String> fields) {
        if (text == null || fields == null || fields.isEmpty()) {
            return true;
        }
        text = text.replaceAll("[^\\p{L}0-9]++", "").toLowerCase(Locale.ENGLISH).trim();
        for (String field : fields) {
            field = field.replaceAll("[^\\p{L}0-9]++", "").toLowerCase(Locale.ENGLISH).trim();
            if (AbbreviationDirectory.checkIfSubsequence(text, field)
                    || AbbreviationDirectory.checkIfSubsequence(field, text)) {
                return true;
            }
        }
        return false;
    }

    private boolean passesSimilarity(String text, List<String> fields) {
        if (text == null || fields == null || fields.isEmpty()) {
            return true;
        }
        text = text.replaceAll("[^\\p{L}0-9]++", "").toLowerCase(Locale.ENGLISH).trim();
        for (String field : fields) {
            field = field.replaceAll("[^\\p{L}0-9]++", "").toLowerCase(Locale.ENGLISH).trim();
            if (AbbreviationDirectory.checkIfSubsequence(text, field)
                    || AbbreviationDirectory.checkIfSubsequence(field, text)) {
                return true;
            }
            if (text.length() > 20 && field.length() > 20 && StringUtils.getLevenshteinDistance(field, text) <= 5) {
                return true;
            }
        }
        return false;
    }

    private boolean isDocResult(SearchResult searchResult) {
        String position = getOneValue(searchResult, IndexFields.F_BIBREF_POSITION);
        try {
            return (position != null && Integer.parseInt(position) == 0);
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean isRefResult(SearchResult searchResult) {
        String sourceId = getOneValue(searchResult, IndexFields.F_BIBREF_SOURCE);
        String position = getOneValue(searchResult, IndexFields.F_BIBREF_POSITION);
        try {
            return (sourceId != null && position != null && Integer.parseInt(position) > 0);
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private String getOneValue(SearchResult searchResult, String name) {
        for (ResultField field : searchResult.getFields()) {
            if (field.getName().equals(name)) {
                if (field.getValues() == null || field.getValues().length == 0) {
                    return null;
                }
                return field.getValues()[0];
            }
        }
        return null;
    }

    private List<String> getAllValues(SearchResult searchResult, String name) {
        for (ResultField field : searchResult.getFields()) {
            if (field.getName().equals(name)) {
                if (field.getValues() == null || field.getValues().length == 0) {
                    return null;
                }
                return Arrays.asList(field.getValues());
            }
        }
        return null;
    }

    public void setSearchStrategy(SearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy; 
    }
}
