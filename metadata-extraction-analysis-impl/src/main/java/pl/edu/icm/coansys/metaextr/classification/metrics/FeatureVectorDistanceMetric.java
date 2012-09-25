package pl.edu.icm.coansys.metaextr.classification.metrics;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureVector;

/**
 *
 * @author Dominika Tkaczyk
 */
public interface FeatureVectorDistanceMetric {

    public double getDistance(FeatureVector vector1, FeatureVector vector2);
    
}
