/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

package pl.edu.icm.cermine.content.filtering;

import java.util.List;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.knn.KnnClassifier;
import pl.edu.icm.cermine.tools.classification.knn.KnnModel;
import pl.edu.icm.cermine.tools.classification.metrics.FeatureVectorEuclideanMetric;

/**
 *
 * @author Dominika Tkaczyk
 */
public class KnnContentFilter implements ContentFilter {

    public static final int DEFAULT_KNN_VOTERS = 3;
    
    /**
     * The number of nearest training samples used for line classification.
     */
    private int knnVoters = DEFAULT_KNN_VOTERS;
    
    private FeatureVectorBuilder<BxZone, BxPage> vectorBuilder;
    
    private KnnModel<BxZoneLabel> knnModel;

    public KnnContentFilter(KnnModel<BxZoneLabel> knnModel) {
        this.vectorBuilder = ContentFilterTools.VECTOR_BUILDER;
        this.knnModel = knnModel;
    }
    
    public KnnContentFilter(KnnModel<BxZoneLabel> knnModel, FeatureVectorBuilder<BxZone, BxPage> vectorBuilder) {
        this.vectorBuilder = vectorBuilder;
        this.knnModel = knnModel;
    }
    
    
    @Override
    public BxDocument filter(BxDocument document) throws AnalysisException {
        KnnClassifier<BxZoneLabel> classifier = new KnnClassifier<BxZoneLabel>();
        
        for (BxPage page : document.getPages()) {
            for (BxZone zone : page.getZones()) {
                if (zone.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_BODY)) {
                    FeatureVector featureVector = vectorBuilder.getFeatureVector(zone, page);
                    BxZoneLabel label = classifier.classify(knnModel, new FeatureVectorEuclideanMetric(), featureVector, knnVoters);
                    zone.setLabel(label);
                }
            }
        }

        return document;
    }

    public static KnnModel<BxZoneLabel> buildModel(FeatureVectorBuilder<BxZone, BxPage> vBuilder,
            List<BxDocument> documents) {
        KnnModel<BxZoneLabel> model = new KnnModel<BxZoneLabel>();
      
        for (BxDocument document : documents) {
            for (BxPage page : document.getPages()) {
                for (BxZone zone : page.getZones()) {
                    if (zone.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_BODY)) {
                        FeatureVector fv = vBuilder.getFeatureVector(zone, page);
                        if (zone.getLabel().equals(BxZoneLabel.BODY_JUNK) 
                                || zone.getLabel().equals(BxZoneLabel.BODY_EQUATION)
                                || zone.getLabel().equals(BxZoneLabel.BODY_EQUATION_LABEL)
                                || zone.getLabel().equals(BxZoneLabel.BODY_FIGURE)
                                || zone.getLabel().equals(BxZoneLabel.BODY_FIGURE_CAPTION)
                                || zone.getLabel().equals(BxZoneLabel.BODY_TABLE)
                                || zone.getLabel().equals(BxZoneLabel.BODY_TABLE_CAPTION)) {
                            model.addTrainingSample(new TrainingSample(fv, BxZoneLabel.BODY_JUNK));
                        } else {
                            model.addTrainingSample(new TrainingSample(fv, BxZoneLabel.BODY_CONTENT));
                        }
                    }
                }
            }
        }
        
        return model;
    }
    
    public static KnnModel<BxZoneLabel> buildModel(List<BxDocument> documents) {
        return buildModel(ContentFilterTools.VECTOR_BUILDER, documents);
    }

    public void setKnnVoters(int knnVoters) {
        this.knnVoters = knnVoters;
    }

}
