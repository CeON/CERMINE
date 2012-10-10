package pl.edu.icm.cermine.tools.classification.metrics;

import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

/**
 *
 * @author Dominika Tkaczyk
 */
public interface FeatureVectorDistanceMetric {

    double getDistance(FeatureVector vector1, FeatureVector vector2);
    
}
