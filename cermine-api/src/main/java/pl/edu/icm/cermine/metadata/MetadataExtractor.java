package pl.edu.icm.cermine.metadata;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Extracting metadata from labelled zones. 
 * 
 * @author Dominika Tkaczyk
 */
public interface MetadataExtractor<T> {
	
	T extractMetadata(BxDocument document) throws AnalysisException;
}
