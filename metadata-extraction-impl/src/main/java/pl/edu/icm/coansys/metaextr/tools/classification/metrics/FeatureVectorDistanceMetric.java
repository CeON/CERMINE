package pl.edu.icm.coansys.metaextr.tools.classification.metrics;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVector;

/**
 *
 * @author Dominika Tkaczyk
 */
public interface FeatureVectorDistanceMetric {

    public double getDistance(FeatureVector vector1, FeatureVector vector2);
    
}
