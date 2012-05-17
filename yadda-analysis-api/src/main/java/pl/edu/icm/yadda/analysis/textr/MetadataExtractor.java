package pl.edu.icm.yadda.analysis.textr;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

public interface MetadataExtractor<T> {
	
	public T extractMetadata(BxDocument document) throws AnalysisException;
}
