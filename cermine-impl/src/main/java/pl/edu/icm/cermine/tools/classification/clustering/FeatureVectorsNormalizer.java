package pl.edu.icm.cermine.tools.classification.clustering;

import java.util.ArrayList;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;

/**
 *
 * @author Dominika Tkaczyk
 */
public final class FeatureVectorsNormalizer {
    
    public static void normalize(FeatureVector[] vectors, FeatureVectorBuilder builder) {
        for (String feature : (ArrayList<String>)builder.getFeatureNames()) {
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            
            for (FeatureVector vector : vectors) {
                if (vector.getValue(feature) < min) {
                    min = vector.getValue(feature);
                }
                if (vector.getValue(feature) > max) {
                    max = vector.getValue(feature);
                }
            }
            
            for (FeatureVector vector : vectors) {
                if (max - min == 0) {
                    vector.addFeature(feature, 0);
                } else {
                    vector.addFeature(feature, (vector.getValue(feature) - min) / (max - min));
                }
            }
        }
    }

    private FeatureVectorsNormalizer() {}
    
}
