package pl.edu.icm.coansys.metaextr.classification.hmm;

import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabelCategory;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.classification.hmm.HMMService;
import pl.edu.icm.coansys.metaextr.classification.hmm.HMMStorage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureVector;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.tools.ZoneClassificationUtils;
import pl.edu.icm.coansys.metaextr.textr.HierarchicalReadingOrderResolver;
import pl.edu.icm.coansys.metaextr.textr.ReadingOrderResolver;
import pl.edu.icm.coansys.metaextr.textr.ZoneClassifier;

/**
 * Hidden Markov Models-based zone classifier.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class HMMZoneClassifier implements ZoneClassifier {

    private HMMService hmmService;
    private HMMProbabilityInfo<BxZoneLabel> labelProbabilities;
    private FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder;
    private List<BxZoneLabel> zoneLabels = BxZoneLabel.valuesOfCategory(BxZoneLabelCategory.CAT_GENERAL);


    public HMMZoneClassifier(HMMService hmmService, HMMStorage hmmStorage, String hmmId,
            FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) throws IOException {
        this.hmmService = hmmService;
        this.featureVectorBuilder = featureVectorBuilder;
        this.labelProbabilities = hmmStorage.getProbabilityInfo(hmmId);
    }

    public HMMZoneClassifier(HMMService hmmService, HMMProbabilityInfo<BxZoneLabel> labelProbabilities,
            FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
        this.hmmService = hmmService;
        this.labelProbabilities = labelProbabilities;
        this.featureVectorBuilder = featureVectorBuilder;
    }
    
    public HMMZoneClassifier(HMMService hmmService, HMMProbabilityInfo<BxZoneLabel> labelProbabilities,
            List<BxZoneLabel> zoneLabels, FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
        this.hmmService = hmmService;
        this.labelProbabilities = labelProbabilities;
        this.featureVectorBuilder = featureVectorBuilder;
        this.zoneLabels = zoneLabels;
    }

    /**
     * Sets labels of all zones in a document using Hidden Markov Models.
     *
     * @param document A document whose zones will be classified.
     * @throws AnalysisException analysis exception
     */
    @Override
    public BxDocument classifyZones(BxDocument document) throws AnalysisException {
        ZoneClassificationUtils.correctPagesBounds(document);
        
        ReadingOrderResolver ror = new HierarchicalReadingOrderResolver();
        document = ror.resolve(document);

        List<FeatureVector> featureVectors = new ArrayList<FeatureVector>();
        for (BxPage page : document.getPages()) {
            for (BxZone zone : page.getZones()) {
                featureVectors.add(featureVectorBuilder.getFeatureVector(zone, page));
            }
        }

        List<BxZoneLabel> labels = hmmService.viterbiMostProbableStates(labelProbabilities,
                    zoneLabels, featureVectors);

        int i = 0;
        for (BxPage page : document.getPages()) {
            for (BxZone zone : page.getZones()) {
                zone.setLabel(labels.get(i));
                i++;
            }
        }
        
        return document;
    }

    public void setFeatureVectorBuilder(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
        this.featureVectorBuilder = featureVectorBuilder;
    }

    public void setHmmService(HMMService hmmService) {
        this.hmmService = hmmService;
    }

    public void setLabelProbabilities(HMMProbabilityInfo<BxZoneLabel> labelProbabilities) {
        this.labelProbabilities = labelProbabilities;
    }
    
    public void setZoneLabels(List<BxZoneLabel> zoneLabels) {
        this.zoneLabels = zoneLabels;
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
