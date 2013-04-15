package pl.edu.icm.cermine.metadata;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Interface for extracting metadata from labelled zones. 
 * 
 * @author Dominika Tkaczyk
 * @param <T> a type of metadata objects
 */
public interface MetadataExtractor<T> {

    /**
     * Extracts metadata from the document.
     * 
     * @param document
     * @return extracted metadata
     * @throws AnalysisException 
     */
	T extractMetadata(BxDocument document) throws AnalysisException;
}
