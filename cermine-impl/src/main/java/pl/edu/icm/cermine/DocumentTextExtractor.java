package pl.edu.icm.cermine;

import java.io.InputStream;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Document text extractor interface.
 * 
 * @author Dominika Tkaczyk
 * @param <T> a type of document text content
 */
public interface DocumentTextExtractor<T> {

    /**
     * Extracts text content from the document passed as InputStream.
     * 
     * @param stream
     * @return text content
     * @throws AnalysisException 
     */
	T extractText(InputStream stream) throws AnalysisException;
    
    /**
     * Extracts text content from the document.
     * 
     * @param document
     * @return text content
     * @throws AnalysisException 
     */
    T extractText(BxDocument document) throws AnalysisException;

}
