package pl.edu.icm.yadda.analysis.textr;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

/**
 * Determining the order in which the elements on all levels should be read. 
 *
 * @author Dominika Tkaczyk
 */
public interface ReadingOrderResolver {
    
    public BxDocument resolve(BxDocument document) throws AnalysisException;
    
}
