package pl.edu.icm.cermine.structure;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Determining the order in which the elements on all levels should be read. 
 *
 * @author Dominika Tkaczyk
 */
public interface ReadingOrderResolver {
    
    /**
     * Resolves the reading order of the document's objects.
     * 
     * @param document
     * @return the document with the reading order set.
     * @throws AnalysisException 
     */
    BxDocument resolve(BxDocument document) throws AnalysisException;
    
}
