package pl.edu.icm.coansys.metaextr.structure;

import pl.edu.icm.coansys.metaextr.structure.ZoneClassifier;
import java.io.IOException;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.HMMService;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.HMMZoneClassifier;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;

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
}