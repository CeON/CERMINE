package pl.edu.icm.cermine;

import java.io.InputStream;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Document metadata extractor interface.
 * 
 * @author Dominika Tkaczyk
 * @param <T> type of document metadata objects
 */
public interface DocumentMetadataExtractor<T> {

    /**
     * Extracts metadata from the document passed as InputStream.
     * 
     * @param stream
     * @return extracted metadata
     * @throws AnalysisException 
     */
	T extractMetadata(InputStream stream) throws AnalysisException;
    
    /**
     * Extracts metadata from the document.
     * 
     * @param document
     * @return extracted metadata
     * @throws AnalysisException 
     */
    T extractMetadata(BxDocument document) throws AnalysisException;
}
