package pl.edu.icm.yadda.analysis.textr;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

public interface ZoneSegmenter {
	
	public void splitWords(BxDocument document) throws AnalysisException;
}
