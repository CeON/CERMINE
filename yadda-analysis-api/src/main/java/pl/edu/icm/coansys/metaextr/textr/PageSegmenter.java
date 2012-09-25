package pl.edu.icm.coansys.metaextr.textr;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;

/**
 * Building hierarchical geometric structure containing: pages, zones, lines, words and characters.
 * 
 * @author Dominika Tkaczyk
 */
public interface PageSegmenter {
	
	public BxDocument segmentPages(BxDocument document) throws AnalysisException;
}
