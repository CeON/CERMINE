package pl.edu.icm.yadda.analysis.textr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.hmm.HMMService;
import pl.edu.icm.yadda.analysis.hmm.HMMStorage;
import pl.edu.icm.yadda.analysis.hmm.features.FeatureVector;
import pl.edu.icm.yadda.analysis.hmm.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.textr.tools.BxModelUtils;
import pl.edu.icm.yadda.analysis.zone.classification.tools.ZoneClassificationUtils;

/**
 * Hidden Markov Models-based zone classifier.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class HMMZoneClassifier implements ZoneClassifier {

    private HMMService hmmService;
    private HMMProbabilityInfo<BxZoneLabel, FeatureVector> labelProbabilities;
    private FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder;

    private double zoneSortTolerance = 5.0;

    public HMMZoneClassifier(HMMService hmmService, HMMStorage hmmStorage, String hmmId,
            FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) throws IOException {
        this.hmmService = hmmService;
        this.featureVectorBuilder = featureVectorBuilder;
        this.labelProbabilities = hmmStorage.getProbabilityInfo(hmmId);
    }

    public HMMZoneClassifier(HMMService hmmService, HMMProbabilityInfo<BxZoneLabel, FeatureVector> labelProbabilities,
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
    @Override
    public void classifyZones(BxDocument document) throws AnalysisException {
        ZoneClassificationUtils.correctPagesBounds(document);
        ZoneClassificationUtils.sortZones(document, zoneSortTolerance);

        List<FeatureVector> featureVectors = new ArrayList<FeatureVector>();
        for (BxPage page : document.getPages()) {
            for (BxZone zone : page.getZones()) {
                featureVectors.add(featureVectorBuilder.getFeatureVector(zone, page));
            }
        }

        List<BxZoneLabel> labels = hmmService.viterbiMostProbableStates(labelProbabilities,
                    Arrays.asList(BxZoneLabel.values()), featureVectors);

        int i = 0;
        for (BxPage page : document.getPages()) {
            for (BxZone zone : page.getZones()) {
                zone.setLabel(labels.get(i));
                i++;
            }
        }
    }

    public void setFeatureVectorBuilder(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
        this.featureVectorBuilder = featureVectorBuilder;
    }

    public void setHmmService(HMMService hmmService) {
        this.hmmService = hmmService;
    }

    public void setLabelProbabilities(HMMProbabilityInfo<BxZoneLabel, FeatureVector> labelProbabilities) {
        this.labelProbabilities = labelProbabilities;
    }

 }
