package pl.edu.icm.cermine.tools.classification.metrics;

import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

/**
 * Feature vector distance metric interface.
 *
 * @author Dominika Tkaczyk
 */
public interface FeatureVectorDistanceMetric {

    /**
     * Calculates distance between two feature vectors.
     * 
     * @param vector1
     * @param vector2
     * @return the distance
     */
    double getDistance(FeatureVector vector1, FeatureVector vector2);
    
}
