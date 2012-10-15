package pl.edu.icm.cermine.structure;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Interface for classifying zones of the document.
 * 
 * @author Dominika Tkaczyk
 */
public interface ZoneClassifier {

    /**
     * Sets labels for the document's zones.
     * 
     * @param document
     * @return a documents with labels set
     * @throws AnalysisException 
     */
	BxDocument classifyZones(BxDocument document) throws AnalysisException;
}
