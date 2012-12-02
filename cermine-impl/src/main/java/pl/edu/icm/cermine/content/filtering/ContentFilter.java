package pl.edu.icm.cermine.content.filtering;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 *
 * @author Dominika Tkaczyk
 */
public interface ContentFilter {
    
    BxDocument filter(BxDocument document) throws AnalysisException;

}
