package pl.edu.icm.cermine.bibref;

import java.util.*;
import pl.edu.icm.cermine.bibref.extraction.features.*;
import pl.edu.icm.cermine.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.cermine.bibref.extraction.tools.BibRefExtractionUtils;
import pl.edu.icm.cermine.bibref.extraction.tools.BibRefLinesClusteringEvaluator;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.tools.classification.clustering.CompleteLinkageClusterizer;
import pl.edu.icm.cermine.tools.classification.clustering.FeatureVectorClusterizer;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.SimpleFeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.metrics.FeatureVectorDistanceMetric;
import pl.edu.icm.cermine.tools.classification.metrics.FeatureVectorEuclideanMetric;

/**
 * Clustering-based bibliographic reference extractor.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ClusteringBibReferenceExtractor implements BibReferenceExtractor {

    public static final double DEFAULT_BEST_REF_RATIO = 0.4;
    
    public static final int DEFAULT_MAX_REF_LINES = 10;
    
    private static final double MIN_CANDIDATE_WINDOW = 0.05;
    
    private double bestRefRatio = DEFAULT_BEST_REF_RATIO;
    
    private int maxRefLines = DEFAULT_MAX_REF_LINES;
    
    private static final FeatureVectorBuilder<BxLine, BxDocumentBibReferences> VECTOR_BUILDER =
                new SimpleFeatureVectorBuilder<BxLine, BxDocumentBibReferences>();
    static {
        VECTOR_BUILDER.setFeatureCalculators(Arrays.<FeatureCalculator<BxLine, BxDocumentBibReferences>>asList(
                new PrevEndsWithDotFeature(),
                new RelativeLengthFeature(),
                new RelativeStartTresholdFeature(),
                new SpaceBetweenLinesFeature(),
                new StartsWithNumberFeature()
                ));
    }

    /**
     * Extracts individual bibliographic references from the document. The references lines
     * are clustered based on feature vector computed for them. The cluster containing the first line
     * is then assumed to be the set of all first lines, which allows for splitting references blocks
     * into individual references.
     * 
     * @param document
     * @return an array of extracted references
     * @throws AnalysisException 
     */
    @Override
    public String[] extractBibReferences(BxDocument document) throws AnalysisException {
        BxDocumentBibReferences documentReferences = BibRefExtractionUtils.extractBibRefLines(document);
        
        List<String> lines = new ArrayList<String>();
        List<FeatureVector> featureVectors = new ArrayList<FeatureVector>();
        for (BxLine line : documentReferences.getLines()) {
            featureVectors.add(VECTOR_BUILDER.getFeatureVector(line, documentReferences));
            lines.add(line.toText());
        }
        
        if (lines.isEmpty()) {
            return new String[]{};
        }
        
        double bestDistance = findBestDistance(featureVectors);
        
        FeatureVectorClusterizer clusterizer = new FeatureVectorClusterizer();
        clusterizer.setClusterizer(new CompleteLinkageClusterizer(new BibRefLinesClusteringEvaluator()));
        int[] clusters = clusterizer.clusterize(featureVectors.toArray(new FeatureVector[featureVectors.size()]), VECTOR_BUILDER, 
                new FeatureVectorEuclideanMetric(), bestDistance, false);
        
        List<String> references = new ArrayList<String>();
        int firstLineClust = clusters[0];
        String actRef = "";
        for (int i = 0; i < clusters.length; i++) {
            if (clusters[i] == firstLineClust) {
                if (!actRef.isEmpty()) {
                    references.add(actRef);
                }
                actRef = lines.get(i);
            } else {
                actRef += " ";
                actRef += lines.get(i);
            }
        }
        if (!actRef.isEmpty()) {
            references.add(actRef);
        }
        
        return references.toArray(new String[references.size()]);
    }

    private double findBestDistance(List<FeatureVector> featureVectors) {
        FeatureVectorDistanceMetric metric = new FeatureVectorEuclideanMetric();
        FeatureVector first = featureVectors.get(0);

        List<Double> distanceList = new ArrayList<Double>();
        for (FeatureVector featureVector : featureVectors) {
            distanceList.add(metric.getDistance(first, featureVector));
        }
        Collections.sort(distanceList);
        
        Set<Double> candidates = new HashSet<Double>();
        
        Double prevDist = null;
        for (Double dist : distanceList) {
            if (prevDist != null) {
                if (dist - prevDist > MIN_CANDIDATE_WINDOW)
                    candidates.add(prevDist + (dist - prevDist) / 2);
            }
            prevDist = dist;
        }
                
        double bestDist = 0;
        double bestMeasure = 0;
        
        for (double candidate : candidates) {
            int firstCount = 0;
            int prevFirstIndex = 0;
            int maxLineCount = 0;
        
            for (FeatureVector featureVector : featureVectors) {
                if (new FeatureVectorEuclideanMetric().getDistance(first, featureVector) < candidate) {
                    firstCount++;
                    if (featureVectors.indexOf(featureVector) - prevFirstIndex > maxLineCount)
                        maxLineCount = featureVectors.indexOf(featureVector) - prevFirstIndex;
                    prevFirstIndex = featureVectors.indexOf(featureVector);
                }
            }
            if (featureVectors.size() - prevFirstIndex > maxLineCount) {
                maxLineCount = featureVectors.size() - prevFirstIndex;
            }
        
            if (maxLineCount <= maxRefLines 
                    && Math.abs(bestRefRatio - (double)firstCount/(double)featureVectors.size()) < Math.abs(bestRefRatio - bestMeasure)) {
                bestDist = candidate;
                bestMeasure = (double)firstCount/(double)featureVectors.size();
            }
        }
        
        return bestDist;
    }

    public void setBestRefRatio(double bestRefRatio) {
        this.bestRefRatio = bestRefRatio;
    }

    public void setMaxRefLines(int maxRefLines) {
        this.maxRefLines = maxRefLines;
    }
    
}
