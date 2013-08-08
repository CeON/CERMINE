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

package pl.edu.icm.cermine.content.headers;

import java.util.List;
import pl.edu.icm.cermine.content.model.BxDocContentStructure;
import pl.edu.icm.cermine.content.model.DocumentContentStructure;
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
public class KnnContentHeadersExtractor implements ContentHeadersExtractor {

    public static final int DEFAULT_KNN_VOTERS = 3;

    private int knnVoters = DEFAULT_KNN_VOTERS;

    private KnnModel<BxZoneLabel> model;
    
    private FeatureVectorBuilder<BxLine, BxPage> classVectorBuilder;
    
    private KnnClassifier<BxZoneLabel> classifier;
    
    private HeadersClusterizer headersClusterizer;
    
    private HeaderLinesCompletener headerLinesCompletener;

    public KnnContentHeadersExtractor(KnnModel<BxZoneLabel> model, KnnClassifier<BxZoneLabel> classifier) {
        this.model = model;
        this.classVectorBuilder = HeaderExtractingTools.EXTRACT_VB;
        this.classifier = classifier;
        this.headersClusterizer = new HeadersClusterizer();
        this.headerLinesCompletener = new HeaderLinesCompletener();
    }

    private boolean isHeader(BxLine line, BxPage page) {
        FeatureVector featureVector = classVectorBuilder.getFeatureVector(line, page);

        BxZoneLabel label = classifier.classify(model, new FeatureVectorEuclideanMetric(), featureVector, knnVoters);
        return label.equals(BxZoneLabel.BODY_HEADING);
    }
    
    @Override
    public BxDocContentStructure extractHeaders(BxDocument document) throws AnalysisException {

        BxDocContentStructure contentStructure = new BxDocContentStructure();
        BxLine lastHeaderLine = null;
        for (BxPage page : document.getPages()) {
            for (BxZone zone : page.getZones()) {
                if (zone.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_BODY)) {
                    for (BxLine line : zone.getLines()) {
                        if (isHeader(line, page)) {
                            contentStructure.addFirstHeaderLine(page, line);
                            lastHeaderLine = line;
                        } else if (zone.getLabel().equals(BxZoneLabel.BODY_CONTENT) || zone.getLabel().equals(BxZoneLabel.GEN_BODY)) {
                            contentStructure.addContentLine(lastHeaderLine, line);
                        }
                    }
                }
            }
        }
        
        headersClusterizer.clusterHeaders(contentStructure);
        headerLinesCompletener.completeLines(contentStructure);
        
        return contentStructure;
    }
    
    public static KnnModel<BxZoneLabel> buildModel(FeatureVectorBuilder<BxLine, BxPage> vectorBuilder,
            List<BxDocument> documents, List<DocumentContentStructure> headers) {
        KnnModel<BxZoneLabel> model = new KnnModel<BxZoneLabel>();
      
        for (int i = 0; i < Math.min(documents.size(), headers.size()); i++) {
            BxDocument doc = documents.get(i);
            DocumentContentStructure contentStr = headers.get(i);

            for (BxPage page : doc.getPages()) {
                for (BxZone zone : page.getZones()) {
                    if (zone.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_BODY)
                            && (zone.getLabel().equals(BxZoneLabel.GEN_BODY)
                            || zone.getLabel().equals(BxZoneLabel.BODY_CONTENT) 
                            || zone.getLabel().equals(BxZoneLabel.BODY_HEADING))) {
                        for (BxLine line : zone.getLines()) {
                            FeatureVector fv = vectorBuilder.getFeatureVector(line, page);
                            if (contentStr.containsHeaderFirstLineText(line.toText())) {
                                model.addTrainingSample(new TrainingSample(fv, BxZoneLabel.BODY_HEADING));
                            } else {
                                model.addTrainingSample(new TrainingSample(fv, BxZoneLabel.BODY_CONTENT));
                            }
                        }
                    }
                }
            }
        }
        
        return model;
    }
    
    public static KnnModel<BxZoneLabel> buildModel(List<BxDocument> documents, List<DocumentContentStructure> headers) {
        return buildModel(HeaderExtractingTools.EXTRACT_VB, documents, headers);
    }
}
