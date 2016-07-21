/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.tools.classification.general;

import java.util.*;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.zoneclassification.tools.ZoneClassificationUtils;
import pl.edu.icm.cermine.structure.model.*;

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

        int i = 0;
        while (documents.hasNext()) {
            BxDocument doc  = documents.next();
            if (labelMap != null) {
                ZoneClassificationUtils.mapZoneLabels(doc, labelMap);
            }

            for (BxPage page : doc) {
                for (BxZone zone : page) {
                    FeatureVector featureVector = vectorBuilder.getFeatureVector(zone, page);
                    TrainingSample<BxZoneLabel> element = new TrainingSample<BxZoneLabel>(featureVector, zone.getLabel());
                    element.setData(zone.toText());
                    trainingList.add(element);
                }
            }
            System.out.println("Converting document: "+(++i));
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

            for (BxPage page : doc) {
                for (BxZone zone : page) {
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

            for (BxPage page : doc) {
                for (BxZone zone : page) {
                    for (BxLine line : zone) {
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
