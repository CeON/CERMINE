package pl.edu.icm.yadda.analysis.textr;

import java.io.IOException;
import java.util.List;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.classification.hmm.training.TrainingElement;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;

/**
 * Classifying zones of the document.
 * 
 * @author Dominika Tkaczyk
 */
public interface ZoneClassifier {

	public BxDocument classifyZones(BxDocument document) throws AnalysisException;
//	public void classifyZones(List<BxZone> elements);
	public void loadModel(String modelPath) throws IOException;
	public void saveModel(String modelPath) throws IOException;
}
