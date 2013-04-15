package pl.edu.icm.cermine;

import java.io.InputStream;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Document references extractor interface.
 * 
 * @author Dominika Tkaczyk
 * @param <T> a type of a reference
 */
public interface DocumentReferencesExtractor<T> {
	
    /**
     * Extracts references from a document passed as InputStream.
     * 
     * @param stream
     * @return an array of extracted references.
     * @throws AnalysisException 
     */
	T[] extractReferences(InputStream stream) throws AnalysisException;
    
    /**
     * Extracts references from a document.
     * 
     * @param document
     * @return an array of extracted references.
     * @throws AnalysisException 
     */
    T[] extractReferences(BxDocument document) throws AnalysisException;
}
