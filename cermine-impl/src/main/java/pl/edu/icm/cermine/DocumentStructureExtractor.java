package pl.edu.icm.cermine;

import java.io.InputStream;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Document structure extractor interface.
 * 
 * @author Dominika Tkaczyk
 */
public interface DocumentStructureExtractor {
	
    /**
     * Extracts geometric structure from the document.
     * 
     * @param stream
     * @return geometric structure of the document
     * @throws AnalysisException 
     */
	BxDocument extractStructure(InputStream stream) throws AnalysisException;
}
