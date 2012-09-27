package pl.edu.icm.coansys.metaextr.textr;

import java.io.IOException;
import java.util.List;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.classification.hmm.training.TrainingElement;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;

/**
 * Classifying zones of the document.
 * 
 * @author Dominika Tkaczyk
 */
public interface ZoneClassifier {

	public BxDocument classifyZones(BxDocument document) throws AnalysisException;
}
