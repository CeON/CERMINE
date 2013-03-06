package pl.edu.icm.cermine.tools.classification.metrics;

import java.util.List;
import java.util.Set;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

/**
 *
 * @author Dominika Tkaczyk
 */
public class FeatureVectorEuclideanMetric implements FeatureVectorDistanceMetric {

    @Override
    public double getDistance(FeatureVector vector1, FeatureVector vector2) {
        double sum = 0;
        List<String> featureNames1 = vector1.getFeatureNames();
        List<String> featureNames2 = vector2.getFeatureNames();
            
        for (String feature : featureNames1) {
            if (featureNames2.contains(feature)) {
                sum += Math.pow(vector1.getFeatureValue(feature) - vector2.getFeatureValue(feature), 2);
            }
        }
           
        return Math.sqrt(sum);
    }
}
