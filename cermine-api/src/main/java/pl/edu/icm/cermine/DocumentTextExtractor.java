package pl.edu.icm.cermine;

import java.io.InputStream;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * @author Dominika Tkaczyk
 */
public interface DocumentTextExtractor<T> {
	
	T extractText(InputStream stream) throws AnalysisException;
    
    T extractText(BxDocument document) throws AnalysisException;

}
