package pl.edu.icm.cermine;

import java.io.InputStream;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Document content extractor interface.
 * 
 * @author Dominika Tkaczyk
 * @param <T> type of document content objects
 */
public interface DocumentContentExtractor<T> {
	
    /**
     * Extracts content from the document passed as InputStream.
     * 
     * @param stream
     * @return extracted content
     * @throws AnalysisException 
     */
	T extractContent(InputStream stream) throws AnalysisException;
    
    /**
     * Extracts content from the document.
     * 
     * @param document
     * @return extracted content
     * @throws AnalysisException 
     */
    T extractContent(BxDocument document) throws AnalysisException;

}
