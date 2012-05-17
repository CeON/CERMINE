package pl.edu.icm.yadda.analysis.hmm.features;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple feature vector.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleFeatureVector implements FeatureVector {

    Map<String, Double> features = new HashMap<String, Double>();

    @Override
    public double getFeature(String name) {
        if (features.get(name) == null) {
            throw new IllegalArgumentException("Feature vector does not contain feature '" + name + "'.");
        }
        return features.get(name);
    }

    @Override
    public void addFeature(String name, double calculateFeatureValue) {
        features.put(name, calculateFeatureValue);
    }

}
