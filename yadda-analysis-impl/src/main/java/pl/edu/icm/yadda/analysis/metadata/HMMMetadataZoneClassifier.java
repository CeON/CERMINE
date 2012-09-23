package pl.edu.icm.yadda.analysis.metadata;

import java.io.IOException;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.HMMService;
import pl.edu.icm.yadda.analysis.classification.hmm.HMMZoneClassifier;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.yadda.analysis.textr.ZoneClassifier;
import pl.edu.icm.yadda.analysis.textr.model.*;

/**
 * Classifying zones as TITLE, AUTHOR, AFFILIATION, etc. 
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class HMMMetadataZoneClassifier implements ZoneClassifier {

    private HMMZoneClassifier hmmZoneClassifier;
    

    public HMMMetadataZoneClassifier(HMMService hmmService, HMMProbabilityInfo<BxZoneLabel> labelProbabilities,
            FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
        this.hmmZoneClassifier = new HMMZoneClassifier(hmmService, labelProbabilities, 
                BxZoneLabel.valuesOfCategory(BxZoneLabelCategory.CAT_METADATA), featureVectorBuilder);
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