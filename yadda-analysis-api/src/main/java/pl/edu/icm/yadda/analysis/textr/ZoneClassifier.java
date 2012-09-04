package pl.edu.icm.yadda.analysis.textr;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

/**
 * Classifying zones of the document.
 * 
 * @author Dominika Tkaczyk
 */
public interface ZoneClassifier {

	public BxDocument classifyZones(BxDocument document) throws AnalysisException;
}
