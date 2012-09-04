package pl.edu.icm.yadda.analysis.textr;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

/**
 * Building hierarchical geometric structure containing: pages, zones, lines, words and characters.
 * 
 * @author Dominika Tkaczyk
 */
public interface PageSegmenter {
	
	public BxDocument segmentPages(BxDocument document) throws AnalysisException;
}
