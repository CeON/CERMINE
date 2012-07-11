package pl.edu.icm.yadda.analysis.textr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.bibref.BibEntry;
import pl.edu.icm.yadda.analysis.bibref.BibReferenceParser;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.yadda.analysis.bibref.parsing.tools.CitationUtils;
import pl.edu.icm.yadda.analysis.classification.hmm.HMMService;
import pl.edu.icm.yadda.analysis.classification.hmm.HMMStorage;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.tools.ZoneClassificationUtils;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneGeneralLabel;

/**
 * Hidden Markov Models-based citation parser.
 * 
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */
public class HMMZoneGeneralClassifier {

    private HMMService hmmService;
    private HMMProbabilityInfo<BxZoneGeneralLabel> labelProbabilities;
    private FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder;

    private double zoneSortTolerance = 5.0;

    public HMMZoneGeneralClassifier(HMMService hmmService, HMMStorage hmmStorage, String hmmId,
            FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) throws IOException {
        this.hmmService = hmmService;
        this.featureVectorBuilder = featureVectorBuilder;
        this.labelProbabilities = hmmStorage.getProbabilityInfo(hmmId);
    }

    public HMMZoneGeneralClassifier(HMMService hmmService, HMMProbabilityInfo<BxZoneGeneralLabel> labelProbabilities,
            FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
        this.hmmService = hmmService;
        this.labelProbabilities = labelProbabilities;
        this.featureVectorBuilder = featureVectorBuilder;
    }

    /**
     * Sets labels of all zones in a document using Hidden Markov Models.
     *
     * @param document A document whose zones will be classified.
     * @throws AnalysisException analysis exception
     */

    public List<BxZoneGeneralLabel> classifyZones(BxDocument document) throws AnalysisException {
        ZoneClassificationUtils.correctPagesBounds(document);
        ZoneClassificationUtils.sortZones(document, zoneSortTolerance);

        List<FeatureVector> featureVectors = new ArrayList<FeatureVector>();
        for (BxPage page : document.getPages()) {
            for (BxZone zone : page.getZones()) {
                featureVectors.add(featureVectorBuilder.getFeatureVector(zone, page));
            }
        }

        List<BxZoneGeneralLabel> labels = hmmService.viterbiMostProbableStates(labelProbabilities,
                    Arrays.asList(BxZoneGeneralLabel.values()), featureVectors);
        return labels;
    }

    public void setFeatureVectorBuilder(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
        this.featureVectorBuilder = featureVectorBuilder;
    }

    public void setHmmService(HMMService hmmService) {
        this.hmmService = hmmService;
    }

    public void setLabelProbabilities(HMMProbabilityInfo<BxZoneGeneralLabel> labelProbabilities) {
        this.labelProbabilities = labelProbabilities;
    }


}