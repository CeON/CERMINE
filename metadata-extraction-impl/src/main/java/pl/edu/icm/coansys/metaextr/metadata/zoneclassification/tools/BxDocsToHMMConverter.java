package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import pl.edu.icm.coansys.metaextr.exception.AnalysisException;
import pl.edu.icm.coansys.metaextr.structure.HierarchicalReadingOrderResolver;
import pl.edu.icm.coansys.metaextr.structure.ReadingOrderResolver;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVector;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.training.SimpleTrainingElement;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.training.TrainingElement;

/**
 * BxDocument objects to HMM training elements converter node. The observations
 * emitted by resulting training elements are vectors of features.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BxDocsToHMMConverter {

    private FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder;

    private Map<BxZoneLabel, BxZoneLabel> labelMap;

    public BxDocsToHMMConverter() {}

    public BxDocsToHMMConverter(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder, Map<BxZoneLabel, BxZoneLabel> labelMap) {
    	this.featureVectorBuilder = featureVectorBuilder;
    	this.labelMap = labelMap;
    }
    
    public List<TrainingElement<BxZoneLabel>> process(List<BxDocument> documents) throws AnalysisException {
        List<TrainingElement<BxZoneLabel>> trainingList =
                new ArrayList<TrainingElement<BxZoneLabel>>(documents.size());
        ReadingOrderResolver ror = new HierarchicalReadingOrderResolver();
        
        for (BxDocument doc : documents) {
            ZoneClassificationUtils.correctPagesBounds(doc);
            doc = ror.resolve(doc);

            if (labelMap != null) {
                ZoneClassificationUtils.mapZoneLabels(doc, labelMap);
            }

            SimpleTrainingElement<BxZoneLabel> prev = null;
            for (BxPage page : doc.getPages()) {
                for (BxZone zone : page.getZones()) {
                    FeatureVector featureVector = featureVectorBuilder.getFeatureVector(zone, page);
                    SimpleTrainingElement<BxZoneLabel> element =
                            new SimpleTrainingElement<BxZoneLabel>(featureVector, zone.getLabel(), prev == null);
                    trainingList.add(element);

                    if (prev != null) {
                        prev.setNextLabel(zone.getLabel());
                    }
                    prev = element;
                }
            }
        }
        return trainingList;
    }

    public void setFeatureVectorBuilder(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
        this.featureVectorBuilder = featureVectorBuilder;
    }

    public void setLabelMap(Map<BxZoneLabel, BxZoneLabel> labelMap) {
        this.labelMap = labelMap;
    }
    
}
