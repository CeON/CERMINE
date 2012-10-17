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
    
    public static final int DEFAULT_KNN_VOTERS = 3;
    
    public static final int DEFAULT_MAX_ADDED_LINES = 2;
    
    public static final double DEFAULT_HEADER_HEIGHT_TOL = 0.01;
    
    public static final int DEFAULT_MIN_HEADER_SCORE = 1;
    
    public static final double DEFAULT_HEADER_LINE_WIDTH_MULT = 0.7;
    
    public static final double DEFAULT_HEADER_LINE_MULT = 0.7;
    
    public static final int DEFAULT_MIN_HEADER_LINE_SCORE = 1;
    
    public static final double DEFAULT_MAX_HEADER_LEV_DIST = 1;
    

    /**
     * The number of nearest training samples used for line classification.
     */
    private int knnVoters = DEFAULT_KNN_VOTERS;
    
    /**
     * The maximum number of additional line following the first header line added as a part of the header.
     */
    private int maxAddedHeaderLines = DEFAULT_MAX_ADDED_LINES;
    
    /**
     * The maximum difference between heights of lines belonging to the same header.
     */
    private double headerHeightTolerance = DEFAULT_HEADER_HEIGHT_TOL;
    
    /**
     * The minimum score for a line to be considered as a candidate for header expanding.
     */
    private int minHeaderCandidateScore = DEFAULT_MIN_HEADER_SCORE;
    
    private double headerLineWidthMultiplier = DEFAULT_HEADER_LINE_WIDTH_MULT;
    
    private double headerLineSpacingMultiplier = DEFAULT_HEADER_LINE_MULT;
    
    private int minHeaderLineScore = DEFAULT_MIN_HEADER_LINE_SCORE;
    
    private double maxHeaderLevelDistance = DEFAULT_MAX_HEADER_LEV_DIST;
    
        
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

    public void setHeaderHeightTolerance(double headerHeightTolerance) {
        this.headerHeightTolerance = headerHeightTolerance;
    }

    public void setHeaderLineSpacingMultiplier(double headerLineSpacingMultiplier) {
        this.headerLineSpacingMultiplier = headerLineSpacingMultiplier;
    }

    public void setHeaderLineWidthMultiplier(double headerLineWidthMultiplier) {
        this.headerLineWidthMultiplier = headerLineWidthMultiplier;
    }

    public void setKnnVoters(int knnVoters) {
        this.knnVoters = knnVoters;
    }

    public void setMaxAddedHeaderLines(int maxAddedHeaderLines) {
        this.maxAddedHeaderLines = maxAddedHeaderLines;
    }

    public void setMaxHeaderLevelDistance(double maxHeaderLevelDistance) {
        this.maxHeaderLevelDistance = maxHeaderLevelDistance;
    }

    public void setMinHeaderCandidateScore(int minHeaderCandidateScore) {
        this.minHeaderCandidateScore = minHeaderCandidateScore;
    }

    public void setMinHeaderLineScore(int minHeaderLineScore) {
        this.minHeaderLineScore = minHeaderLineScore;
    }
    
}
