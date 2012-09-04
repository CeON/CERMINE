package pl.edu.icm.yadda.analysis.metadata;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

/**
 * Extracting metadata from labelled zones. 
 * 
 * @author Dominika Tkaczyk
 */
public interface MetadataExtractor<T> {
	
	public T extractMetadata(BxDocument document) throws AnalysisException;
}
