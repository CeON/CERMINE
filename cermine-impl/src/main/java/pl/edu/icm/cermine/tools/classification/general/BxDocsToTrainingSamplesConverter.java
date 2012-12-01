package pl.edu.icm.cermine.tools.classification.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.zoneclassification.tools.ZoneClassificationUtils;
import pl.edu.icm.cermine.structure.HierarchicalReadingOrderResolver;
import pl.edu.icm.cermine.structure.ReadingOrderResolver;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;

/**
 * BxDocument objects to HMM training elements converter node. The observations
 * emitted by resulting training elements are vectors of features.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public abstract class BxDocsToTrainingSamplesConverter {

    public static List<TrainingSample<BxZoneLabel>> getTrainingSamples(List<BxDocument> documents, 
            FeatureVectorBuilder<BxZone, BxPage> vectorBuilder, Map<BxZoneLabel, BxZoneLabel> labelMap) throws AnalysisException {
        List<TrainingSample<BxZoneLabel>> trainingList = new ArrayList<TrainingSample<BxZoneLabel>>(documents.size());
        ReadingOrderResolver ror = new HierarchicalReadingOrderResolver();
        
        for (BxDocument doc : documents) {
            ZoneClassificationUtils.correctPagesBounds(doc);
            doc = ror.resolve(doc);

            if (labelMap != null) {
                ZoneClassificationUtils.mapZoneLabels(doc, labelMap);
            }

            for (BxPage page : doc.getPages()) {
                for (BxZone zone : page.getZones()) {
                    FeatureVector featureVector = vectorBuilder.getFeatureVector(zone, page);
                    TrainingSample<BxZoneLabel> element = new TrainingSample<BxZoneLabel>(featureVector, zone.getLabel());
                    trainingList.add(element);
                }
            }
        }
        return trainingList;
    }
    
    public static List<TrainingSample<BxZoneLabel>> getTrainingSamples(List<BxDocument> documents, 
            FeatureVectorBuilder<BxZone, BxPage> vectorBuilder) throws AnalysisException {
        return getTrainingSamples(documents, vectorBuilder, null);
    }
    
    public static List<TrainingSample<BxZoneLabel>> getTrainingSamples(BxDocument document, 
            FeatureVectorBuilder<BxZone, BxPage> vectorBuilder, Map<BxZoneLabel, BxZoneLabel> labelMap) throws AnalysisException {
        return getTrainingSamples(Arrays.asList(document), vectorBuilder, labelMap);
    }
    
    public static List<TrainingSample<BxZoneLabel>> getTrainingSamples(BxDocument document, 
            FeatureVectorBuilder<BxZone, BxPage> vectorBuilder) throws AnalysisException {
        return getTrainingSamples(Arrays.asList(document), vectorBuilder, null);
    }
    
}
