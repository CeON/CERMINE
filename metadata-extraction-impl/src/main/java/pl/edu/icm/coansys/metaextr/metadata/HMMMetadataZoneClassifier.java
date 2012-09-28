package pl.edu.icm.coansys.metaextr.metadata;

import pl.edu.icm.coansys.metaextr.structure.model.BxZone;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabelCategory;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import java.io.IOException;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.HMMService;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.HMMZoneClassifier;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.coansys.metaextr.structure.ZoneClassifier;

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
