package pl.edu.icm.coansys.metaextr.tools.classification.clustering;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVector;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.metrics.FeatureVectorDistanceMetric;

/**
 *
 * @author Dominika Tkaczyk
 */
public class FeatureVectorClusterizer {
   
    private Clusterizer clusterizer;
    
    public int[] clusterize(FeatureVector[] vectors, FeatureVectorBuilder builder, FeatureVectorDistanceMetric metric, 
            double maxDistance, boolean normalize) {
        if (normalize){
            FeatureVectorsNormalizer.normalize(vectors, builder);
        }
        double distanceMatrix[][] = new double[vectors.length][vectors.length];
        for (int i = 0; i < vectors.length; i++) {
            for (int j = 0; j < vectors.length; j++) {
                distanceMatrix[i][j] = metric.getDistance(vectors[i], vectors[j]);
                distanceMatrix[j][i] = metric.getDistance(vectors[i], vectors[j]);
            }
        }
        
        return clusterizer.clusterize(distanceMatrix, maxDistance);
    }

    public void setClusterizer(Clusterizer clusterizer) {
        this.clusterizer = clusterizer;
    }
    
}
