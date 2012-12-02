package pl.edu.icm.cermine.content.headers;

import pl.edu.icm.cermine.content.model.BxDocContentStructure;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 *
 * @author Dominika Tkaczyk
 */
public interface ContentHeadersExtractor {
    
    BxDocContentStructure extractHeaders(BxDocument document) throws AnalysisException;

}
