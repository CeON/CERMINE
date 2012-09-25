package pl.edu.icm.coansys.metaextr.textr;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;

/**
 * Determining the order in which the elements on all levels should be read. 
 *
 * @author Dominika Tkaczyk
 */
public interface ReadingOrderResolver {
    
    public BxDocument resolve(BxDocument document) throws AnalysisException;
    
}
