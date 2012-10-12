package pl.edu.icm.cermine.structure;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Classifying zones of the document.
 * 
 * @author Dominika Tkaczyk
 */
public interface ZoneClassifier {

	BxDocument classifyZones(BxDocument document) throws AnalysisException;
}
