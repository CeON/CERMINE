package pl.edu.icm.cermine.tools.classification.general;

import java.util.*;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.zoneclassification.tools.ZoneClassificationUtils;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;

/**
 * BxDocument objects to HMM training elements converter node. The observations
 * emitted by resulting training elements are vectors of features.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public abstract class BxDocsToTrainingSamplesConverter {

    public static List<TrainingSample<BxZoneLabel>> getZoneTrainingSamples(Iterator<BxDocument> documents, 
            FeatureVectorBuilder<BxZone, BxPage> vectorBuilder, Map<BxZoneLabel, BxZoneLabel> labelMap) throws AnalysisException {
        List<TrainingSample<BxZoneLabel>> trainingList = new ArrayList<TrainingSample<BxZoneLabel>>();

        while (documents.hasNext()) {
            BxDocument doc  = documents.next();
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
    
    public static List<TrainingSample<BxZoneLabel>> getZoneTrainingSamples(List<BxDocument> documents, 
            FeatureVectorBuilder<BxZone, BxPage> vectorBuilder, Map<BxZoneLabel, BxZoneLabel> labelMap) throws AnalysisException {
        List<TrainingSample<BxZoneLabel>> trainingList = new ArrayList<TrainingSample<BxZoneLabel>>(documents.size());
        
        for (BxDocument doc : documents) {
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
    
    public static List<TrainingSample<BxZoneLabel>> getZoneTrainingSamples(List<BxDocument> documents, 
            FeatureVectorBuilder<BxZone, BxPage> vectorBuilder) throws AnalysisException {
        return getZoneTrainingSamples(documents, vectorBuilder, null);
    }
    
    public static List<TrainingSample<BxZoneLabel>> getZoneTrainingSamples(BxDocument document, 
            FeatureVectorBuilder<BxZone, BxPage> vectorBuilder, Map<BxZoneLabel, BxZoneLabel> labelMap) throws AnalysisException {
        return getZoneTrainingSamples(Arrays.asList(document), vectorBuilder, labelMap);
    }
    
    public static List<TrainingSample<BxZoneLabel>> getZoneTrainingSamples(BxDocument document, 
            FeatureVectorBuilder<BxZone, BxPage> vectorBuilder) throws AnalysisException {
        return getZoneTrainingSamples(Arrays.asList(document), vectorBuilder, null);
    }
    
    public static List<TrainingSample<BxZoneLabel>> getLineTrainingSamples(List<BxDocument> documents, 
            FeatureVectorBuilder<BxLine, BxPage> vectorBuilder, Map<BxZoneLabel, BxZoneLabel> labelMap) throws AnalysisException {
        List<TrainingSample<BxZoneLabel>> trainingList = new ArrayList<TrainingSample<BxZoneLabel>>(documents.size());
       
        for (BxDocument doc : documents) {
            if (labelMap != null) {
                ZoneClassificationUtils.mapZoneLabels(doc, labelMap);
            }

            for (BxPage page : doc.getPages()) {
                for (BxZone zone : page.getZones()) {
                    for (BxLine line : zone.getLines()) {
                        FeatureVector featureVector = vectorBuilder.getFeatureVector(line, page);
                        TrainingSample<BxZoneLabel> element = new TrainingSample<BxZoneLabel>(featureVector, zone.getLabel());
                        trainingList.add(element);
                    }
                }
            }
        }
        return trainingList;
    }
    
    public static List<TrainingSample<BxZoneLabel>> getLineTrainingSamples(List<BxDocument> documents, 
            FeatureVectorBuilder<BxLine, BxPage> vectorBuilder) throws AnalysisException {
        return getLineTrainingSamples(documents, vectorBuilder, null);
    }
    
    public static List<TrainingSample<BxZoneLabel>> getLineTrainingSamples(BxDocument document, 
            FeatureVectorBuilder<BxLine, BxPage> vectorBuilder, Map<BxZoneLabel, BxZoneLabel> labelMap) throws AnalysisException {
        return getLineTrainingSamples(Arrays.asList(document), vectorBuilder, labelMap);
    }
    
    public static List<TrainingSample<BxZoneLabel>> getLineTrainingSamples(BxDocument document, 
            FeatureVectorBuilder<BxLine, BxPage> vectorBuilder) throws AnalysisException {
        return getLineTrainingSamples(Arrays.asList(document), vectorBuilder, null);
    }
    
}
