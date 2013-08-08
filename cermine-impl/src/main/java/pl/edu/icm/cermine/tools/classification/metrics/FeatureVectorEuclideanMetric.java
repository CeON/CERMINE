package pl.edu.icm.cermine.tools.classification.metrics;

import com.google.common.collect.Sets;
import java.util.List;
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
        
        if (Sets.newHashSet(featureNames1).equals(Sets.newHashSet(featureNames2))) {
            for (String feature : featureNames1) {
                sum += Math.pow(vector1.getValue(feature) - vector2.getValue(feature), 2);
            }
        } else {
            for (int i = 0; i < vector1.size(); i++) {
                sum += Math.pow(vector1.getValue(i) - vector2.getValue(i), 2);
            }
        }

        return Math.sqrt(sum);
    }
}
