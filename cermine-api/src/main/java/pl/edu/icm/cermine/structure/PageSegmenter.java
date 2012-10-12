package pl.edu.icm.cermine.structure;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Building hierarchical geometric structure containing: pages, zones, lines, words and characters.
 * 
 * @author Dominika Tkaczyk
 */
public interface PageSegmenter {
	
	BxDocument segmentPages(BxDocument document) throws AnalysisException;
}
