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
import pl.edu.icm.cermine.tools.classification.clustering.CompleteLinkageClusterizer;
import pl.edu.icm.cermine.tools.classification.clustering.FeatureVectorClusterizer;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.SimpleFeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.metrics.FeatureVectorEuclideanMetric;

/**
 * Clustering-based bibliographic reference extractor.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ClusteringBibReferenceExtractor implements BibReferenceExtractor {

    private double maxDistance = 1.2;
    
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
            
        FeatureVectorClusterizer clusterizer = new FeatureVectorClusterizer();
        clusterizer.setClusterizer(new CompleteLinkageClusterizer());
        int[] clusters = clusterizer.clusterize(featureVectors.toArray(new FeatureVector[featureVectors.size()]), VECTOR_BUILDER, 
                new FeatureVectorEuclideanMetric(), maxDistance, false);

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
            
        return references.toArray(new String[]{});
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

}
