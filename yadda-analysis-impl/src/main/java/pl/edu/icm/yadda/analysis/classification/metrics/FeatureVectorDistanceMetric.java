package pl.edu.icm.yadda.analysis.classification.metrics;

import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;

/**
 *
 * @author Dominika Tkaczyk
 */
public interface FeatureVectorDistanceMetric {

    public double getDistance(FeatureVector vector1, FeatureVector vector2);
    
}
