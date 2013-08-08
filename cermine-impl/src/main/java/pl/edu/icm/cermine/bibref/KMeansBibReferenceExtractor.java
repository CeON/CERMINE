package pl.edu.icm.cermine.bibref;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.edu.icm.cermine.bibref.extraction.features.*;
import pl.edu.icm.cermine.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.cermine.bibref.extraction.tools.BibRefExtractionUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.tools.classification.clustering.KMeansWithInitialCentroids;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.metrics.FeatureVectorDistanceMetric;
import pl.edu.icm.cermine.tools.classification.metrics.FeatureVectorEuclideanMetric;

/**
 * Clustering-based bibliographic reference extractor.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class KMeansBibReferenceExtractor implements BibReferenceExtractor {

    private static final FeatureVectorBuilder<BxLine, BxDocumentBibReferences> VECTOR_BUILDER =
                new FeatureVectorBuilder<BxLine, BxDocumentBibReferences>();
    static {
        VECTOR_BUILDER.setFeatureCalculators(Arrays.<FeatureCalculator<BxLine, BxDocumentBibReferences>>asList(
                new PrevEndsWithDotFeature(),
                new PrevRelativeLengthFeature(),
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
        List<FeatureVector> instances = new ArrayList<FeatureVector>();
        FeatureVectorDistanceMetric metric = new FeatureVectorEuclideanMetric();
        FeatureVector farthestInstance = null;
        double farthestDistance = 0;
        for (BxLine line : documentReferences.getLines()) {
            lines.add(line.toText());
            FeatureVector featureVector = VECTOR_BUILDER.getFeatureVector(line, documentReferences);
            instances.add(featureVector);
            if (farthestInstance == null) {
                farthestInstance = instances.get(0);
            }
            double distance = metric.getDistance(instances.get(0), featureVector);
            if (distance > farthestDistance) {
                farthestInstance = featureVector;
            }
        }
        if (lines.size() <= 1) {
            return lines.toArray(new String[lines.size()]);
        }
        
        KMeansWithInitialCentroids clusterer = new KMeansWithInitialCentroids(2);
        FeatureVector[] centroids = new FeatureVector[2];
        centroids[0] = instances.get(0);
        centroids[1] = farthestInstance;
        clusterer.setCentroids(centroids);
        List<FeatureVector>[] clusters = clusterer.cluster(instances);
        
        int firstInstanceClusterNum = 0;
        if (clusters[1].contains(instances.get(0))) {
            firstInstanceClusterNum = 1;
        }

        List<String> references = new ArrayList<String>();
        String actRef = "";
        for (int i = 0; i < lines.size(); i++) {
            if (clusters[firstInstanceClusterNum].contains(instances.get(i))) {
                if (!actRef.isEmpty() && actRef.matches(".*[0-9].*") && actRef.matches(".*[a-zA-Z].*")) {
                    references.add(actRef);
                }
                actRef = lines.get(i);
            } else {
                actRef += " ";
                actRef += lines.get(i);
            }
        }
        if (!actRef.isEmpty() && actRef.matches(".*[0-9].*") && actRef.matches(".*[a-zA-Z].*")) {
            references.add(actRef);
        }
        
        return references.toArray(new String[references.size()]);
    }

}
