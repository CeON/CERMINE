package pl.edu.icm.coansys.metaextr;

import java.io.InputStream;
import pl.edu.icm.coansys.metaextr.exception.AnalysisException;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;

/**
 * @author Dominika Tkaczyk
 */
public interface DocumentReferencesExtractor<T> {
	
	public T[] extractReferences(InputStream stream) throws AnalysisException;
    
    public T[] extractReferences(BxDocument document) throws AnalysisException;
}
