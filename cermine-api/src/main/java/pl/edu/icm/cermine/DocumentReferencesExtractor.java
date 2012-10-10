package pl.edu.icm.cermine;

import java.io.InputStream;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * @author Dominika Tkaczyk
 */
public interface DocumentReferencesExtractor<T> {
	
	T[] extractReferences(InputStream stream) throws AnalysisException;
    
    T[] extractReferences(BxDocument document) throws AnalysisException;
}
