package pl.edu.icm.coansys.metaextr.metadata;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;

/**
 * Extracting metadata from labelled zones. 
 * 
 * @author Dominika Tkaczyk
 */
public interface MetadataExtractor<T> {
	
	public T extractMetadata(BxDocument document) throws AnalysisException;
}
