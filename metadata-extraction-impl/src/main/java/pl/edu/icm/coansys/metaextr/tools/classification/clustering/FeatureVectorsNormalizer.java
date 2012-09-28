package pl.edu.icm.coansys.metaextr.tools.classification.clustering;

import java.util.Set;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVector;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVectorBuilder;

/**
 *
 * @author Dominika Tkaczyk
 */
public class FeatureVectorsNormalizer {
    
    public static void normalize(FeatureVector[] vectors, FeatureVectorBuilder builder) {
        for (String feature : (Set<String>)builder.getFeatureNames()) {
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            
            for (FeatureVector vector : vectors) {
                if (vector.getFeature(feature) < min) {
                    min = vector.getFeature(feature);
                }
                if (vector.getFeature(feature) > max) {
                    max = vector.getFeature(feature);
                }
            }
            
            for (FeatureVector vector : vectors) {
                if (max - min == 0) {
                    vector.addFeature(feature, 0);
                } else {
                    vector.addFeature(feature, (vector.getFeature(feature) - min) / (max - min));
                }
            }
        }
    }
    
}
