package pl.edu.icm.yadda.analysis.bibref;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.yadda.analysis.bibref.extraction.tools.BibRefExtractionUtils;
import pl.edu.icm.yadda.analysis.classification.clustering.CompleteLinkageClusterizer;
import pl.edu.icm.yadda.analysis.classification.clustering.FeatureVectorClusterizer;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.metrics.FeatureVectorEuclideanMetric;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;

/**
 * Clustering-based bibliographic reference extractor.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ClusteringBibReferenceExtractor implements BibReferenceExtractor {

    private FeatureVectorBuilder<BxLine, BxDocumentBibReferences> featureVectorBuilder;
    
    private double maxDistance = 1.2;

 
    public ClusteringBibReferenceExtractor(FeatureVectorBuilder<BxLine, BxDocumentBibReferences> featureVectorBuilder) {
        this.featureVectorBuilder = featureVectorBuilder;
    }

    @Override
    public String[] extractBibReferences(BxDocument document) throws AnalysisException {
        BxDocumentBibReferences documentReferences = BibRefExtractionUtils.extractBibRefLines(document);
           
        List<String> lines = new ArrayList<String>();
        List<FeatureVector> featureVectors = new ArrayList<FeatureVector>();
        for (BxLine line : documentReferences.getLines()) {
            featureVectors.add(featureVectorBuilder.getFeatureVector(line, documentReferences));
            lines.add(line.toText());
        }
            
        FeatureVectorClusterizer clusterizer = new FeatureVectorClusterizer();
        clusterizer.setClusterizer(new CompleteLinkageClusterizer());
        int[] clusters = clusterizer.clusterize(featureVectors.toArray(new FeatureVector[]{}), featureVectorBuilder, 
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

    public void setFeatureVectorBuilder(FeatureVectorBuilder<BxLine, BxDocumentBibReferences> featureVectorBuilder) {
        this.featureVectorBuilder = featureVectorBuilder;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

}
