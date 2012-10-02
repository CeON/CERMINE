package pl.edu.icm.coansys.metaextr.structure;

import pl.edu.icm.coansys.metaextr.exception.AnalysisException;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;

/**
 * Building hierarchical geometric structure containing: pages, zones, lines, words and characters.
 * 
 * @author Dominika Tkaczyk
 */
public interface PageSegmenter {
	
	public BxDocument segmentPages(BxDocument document) throws AnalysisException;
}
