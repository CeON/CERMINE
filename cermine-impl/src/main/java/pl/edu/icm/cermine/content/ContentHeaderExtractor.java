package pl.edu.icm.cermine.content;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.content.model.BxDocContentStructure;
import pl.edu.icm.cermine.content.model.DocumentContentStructure;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.clustering.FeatureVectorClusterizer;
import pl.edu.icm.cermine.tools.classification.clustering.SingleLinkageClusterizer;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.knn.KnnClassifier;
import pl.edu.icm.cermine.tools.classification.knn.KnnModel;
import pl.edu.icm.cermine.tools.classification.knn.KnnTrainingSample;
import pl.edu.icm.cermine.tools.classification.metrics.FeatureVectorEuclideanMetric;

/**
 *
 * @author Dominika Tkaczyk
 */
public class ContentHeaderExtractor {

    /**
     * The number of nearest training samples used for line classification.
     */
    private int knnVoters = 3;
    
    /**
     * The maximum number of additional line following the first header line added as a part of the header.
     */
    private int maxAddedHeaderLines = 2;
    
    /**
     * The maximum difference between heights of lines belonging to the same header.
     */
    private double headerHeightTolerance = 0.01;
    
    /**
     * The minimum score for a line to be considered as a candidate for header expanding.
     */
    private int minHeaderCandidateScore = 1;
    
    private double headerLineWidthMultiplier = 0.7;
    
    private double headerLineSpacingMultiplier = 0.7;
    
    private int minHeaderLineScore = 1;
    
    private double maxHeaderLevelDistance = 1;
    
        
    public BxDocContentStructure extractHeaders(KnnModel<BxZoneLabel> model, 
            FeatureVectorBuilder<BxLine, BxPage> classVectorBuilder, 
            FeatureVectorBuilder<BxLine, BxPage> clustVectorBuilder,
            BxDocument document) throws AnalysisException {
        
        KnnClassifier<BxZoneLabel> classifier = new KnnClassifier<BxZoneLabel>();
        
        BxDocContentStructure contentStructure = new BxDocContentStructure();
        BxLine lastHeaderLine = null;
        for (BxPage page : document.getPages()) {
            for (BxZone zone : page.getZones()) {
                if (zone.getLabel().equals(BxZoneLabel.BODY_CONTENT) || zone.getLabel().equals(BxZoneLabel.BODY_JUNK)) {
                    for (BxLine line : zone.getLines()) {
                        FeatureVector featureVector = classVectorBuilder.getFeatureVector(line, page);
                        
                        BxZoneLabel label = classifier.classify(model, new FeatureVectorEuclideanMetric(), featureVector, knnVoters);
                        if (label.equals(BxZoneLabel.BODY_HEADER)) {
                            contentStructure.addFirstHeaderLine(page, line);
                            lastHeaderLine = line;
                        } else if (zone.getLabel().equals(BxZoneLabel.BODY_CONTENT)) {
                            contentStructure.addContentLine(lastHeaderLine, line);
                        }
                    }
                }
            }
        }
        
        completeLines(contentStructure);
        setLevelIds(contentStructure, clustVectorBuilder);
        
        return contentStructure;
    }
    
    public KnnModel<BxZoneLabel> buildModel(FeatureVectorBuilder<BxLine, BxPage> vectorBuilder,
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
                            || zone.getLabel().equals(BxZoneLabel.BODY_HEADER))) {
                        for (BxLine line : zone.getLines()) {
                            FeatureVector fv = vectorBuilder.getFeatureVector(line, page);
                            if (contentStr.containsHeaderFirstLineText(line.toText())) {
                                model.addTrainingSample(new KnnTrainingSample(fv, BxZoneLabel.BODY_HEADER));
                            } else {
                                model.addTrainingSample(new KnnTrainingSample(fv, BxZoneLabel.BODY_CONTENT));
                            }
                        }
                    }
                }
            }
        }
        
        return model;
    }

    private void setLevelIds(BxDocContentStructure contentStructure, FeatureVectorBuilder<BxLine, BxPage> clustVectorBuilder) {
        FeatureVectorClusterizer clusterizer = new FeatureVectorClusterizer();
        clusterizer.setClusterizer(new SingleLinkageClusterizer());
        int[] clusters = clusterizer.clusterize(contentStructure.getFirstHeaderFeatureVectors(clustVectorBuilder), 
                clustVectorBuilder, new FeatureVectorEuclideanMetric(), maxHeaderLevelDistance, true);
        contentStructure.setHeaderLevelIds(clusters);
    }
    
    private void completeLines(BxDocContentStructure contentStructure) {
        for (BxLine headerLine : contentStructure.getFirstHeaderLines()) {
            int added = 0;
            BxLine actLine = headerLine;
            List<BxLine> candidates = new ArrayList<BxLine>();
            while (actLine.hasNext()) {
                if (added > maxAddedHeaderLines) {
                    break;
                }
                actLine = actLine.getNext();
                if (contentStructure.containsFirstHeaderLine(actLine)) {
                    break;
                }
                int score = 0;
                if (Math.abs(actLine.getHeight() - headerLine.getHeight()) < headerHeightTolerance) {
                    score++;
                }
                if (score >= minHeaderCandidateScore) {
                    candidates.add(actLine);
                    added++;
                } else {
                    break;
                }
            }

            if (added == 0) {
                continue;
            }
            int score = 0;
            BxLine firstCandidate = candidates.get(0);
            BxLine lastCandidate = candidates.get(added-1);
            if (lastCandidate.getWidth() < headerLine.getWidth() * headerLineWidthMultiplier) {
                score++;
            }
            if (lastCandidate.hasNext() 
                    && Math.abs(headerLine.getY() - firstCandidate.getY()) 
                  < Math.abs(lastCandidate.getY() - lastCandidate.getNext().getY()) * headerLineSpacingMultiplier) {
                score++;
            }
            if (score >= minHeaderLineScore) {
                for (BxLine candidate : candidates) {
                    contentStructure.addAdditionalHeaderLine(headerLine, candidate);
                }
            }
        }

    }
    
}
