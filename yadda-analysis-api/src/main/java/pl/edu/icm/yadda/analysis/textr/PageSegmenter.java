package pl.edu.icm.yadda.analysis.textr;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

public interface PageSegmenter {
	
	public BxDocument segmentPages(BxDocument document) throws AnalysisException;
}
