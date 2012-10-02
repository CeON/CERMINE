package pl.edu.icm.coansys.metaextr;

import java.io.InputStream;
import pl.edu.icm.coansys.metaextr.exception.AnalysisException;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;

/**
 * @author Dominika Tkaczyk
 */
public interface DocumentMetadataExtractor<T> {
	
	public T extractMetadata(InputStream stream) throws AnalysisException;
    
    public T extractMetadata(BxDocument document) throws AnalysisException;
}
