package pl.edu.icm.yadda.analysis.textr;

import java.io.IOException;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.HMMService;
import pl.edu.icm.yadda.analysis.classification.hmm.HMMZoneClassifier;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;

/**
 * Classifying zones as: METADATA, BODY, REFERENCES, OTHER. 
 * 
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class HMMInitialZoneClassifier implements ZoneClassifier {

    private HMMZoneClassifier hmmZoneClassifier;
    

    public HMMInitialZoneClassifier(HMMService hmmService, HMMProbabilityInfo<BxZoneLabel> labelProbabilities,
            FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
        this.hmmZoneClassifier = new HMMZoneClassifier(hmmService, labelProbabilities, featureVectorBuilder);
    }

    @Override
    public BxDocument classifyZones(BxDocument document) throws AnalysisException {
        return hmmZoneClassifier.classifyZones(document);
    }

	@Override
	public void loadModel(String modelPath) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveModel(String modelPath) throws IOException {
		// TODO Auto-generated method stub
		
	}

}