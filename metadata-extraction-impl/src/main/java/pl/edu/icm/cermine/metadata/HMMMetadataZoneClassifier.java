package pl.edu.icm.cermine.metadata;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.HMMZoneClassifier;
import pl.edu.icm.cermine.structure.ZoneClassifier;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.hmm.HMMService;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMProbabilityInfo;

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
}
