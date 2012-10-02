package pl.edu.icm.coansys.metaextr.metadata;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.structure.HMMZoneClassifier;
import pl.edu.icm.coansys.metaextr.structure.ZoneClassifier;
import pl.edu.icm.coansys.metaextr.structure.model.*;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.HMMService;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.model.HMMProbabilityInfo;

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
